/*
 * Copyright (c) 2024 Herve Girod. All rights reserved.
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

 If you have any questions about this project, you can visit
 the project website at the project page on https://sourceforge.net/projects/xmldiff/
 */
package org.xmldiff.core.comparator;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.mdiutil.xml.tree.XMLNode;
import org.mdiutil.xml.tree.XMLNodeUtilities;
import org.xmldiff.core.config.NodeRules;
import org.xmldiff.core.config.XMLDiffConfiguration;
import org.xmldiff.core.model.ComparedElement;
import org.xmldiff.core.model.ComparisonModel;
import org.xmldiff.core.model.NodeMoveState;
import org.xmldiff.core.model.NodeState;
import org.xmldiff.core.model.NodeTreeRep;
import org.xmldiff.core.model.XMLNodesUtil;

/**
 * This class allows to compare two XML files.
 *
 * @version 0.6
 */
public class XMLComparator {
   private File leftFile = null;
   private File rightFile = null;
   private DefaultMutableTreeNode leftRoot;
   private DefaultMutableTreeNode rightRoot;
   private NodeTreeRep leftRootRep = null;
   private NodeTreeRep rightRootRep = null;
   private ComparisonModel compModel = null;
   private ComparedElement comparedRoot = null;
   private NodeRules nodeRules;
   private static final XMLNodesUtil nodesUtils = XMLNodesUtil.getInstance();

   public XMLComparator() {
      this.nodeRules = XMLDiffConfiguration.getInstance().getNodeRules();
      nodesUtils.setNodeRules(nodeRules);
   }

   /**
    * Reload the node rules.
    */
   public void reload() {
      this.nodeRules = XMLDiffConfiguration.getInstance().getNodeRules();
      nodesUtils.setNodeRules(nodeRules);
   }

   /**
    * Set the URLs to compare.
    *
    * @param leftURL the left URL
    * @param rightURL the right URL
    */
   public void setFiles(URL leftURL, URL rightURL) {
      try {
         this.leftFile = new File(leftURL.toURI().getPath());
         this.rightFile = new File(rightURL.toURI().getPath());
      } catch (URISyntaxException ex) {
      }

   }

   /**
    * Set the files to compare.
    *
    * @param leftFile the left file
    * @param rightFile the right file
    */
   public void setFiles(File leftFile, File rightFile) {
      this.leftFile = leftFile;
      this.rightFile = rightFile;
   }

   /**
    * Return the left file.
    *
    * @return the left file
    */
   public File getLeftFile() {
      return leftFile;
   }

   /**
    * Return the right file.
    *
    * @return the right file
    */   
   public File getRightFile() {
      return rightFile;
   }

   /**
    * Return the model which is the result of the comparison.
    *
    * @return the model
    */
   public ComparisonModel getModel() {
      return compModel;
   }

   private void createTrees() {
      int options = XMLNodeUtilities.NAMESPACE_AWARE | XMLNodeUtilities.KEEP_LINE_NUMBERS;
      XMLNode leftNode = XMLNodeUtilities.getNode(leftFile, options);
      XMLNode rightNode = XMLNodeUtilities.getNode(rightFile, options);
      leftRootRep = new NodeTreeRep(true, leftNode);
      rightRootRep = new NodeTreeRep(false, rightNode);
      leftRoot = new DefaultMutableTreeNode(leftRootRep);
      leftRootRep.setTreeNode(leftRoot);
      addNodes(leftRoot, leftRootRep, true);
      rightRoot = new DefaultMutableTreeNode(rightRootRep);
      rightRootRep.setTreeNode(rightRoot);
      addNodes(rightRoot, rightRootRep, true);
   }

   /**
    * Run the comparison.
    */
   public void runCompare() {
      createTrees();
      compareTrees();
   }

   private void compareTrees() {
      this.comparedRoot = new ComparedElement(leftRootRep, rightRootRep);
      compModel = new ComparisonModel(leftFile, rightFile, comparedRoot);
      DefaultTreeModel leftTreeModel = new DefaultTreeModel(leftRoot);
      compModel.setLeftTreeModel(leftTreeModel);
      DefaultTreeModel rightTreeModel = new DefaultTreeModel(rightRoot);
      compModel.setRightTreeModel(rightTreeModel);
      XMLNode leftNode = leftRootRep.getNode();
      XMLNode rightNode = rightRootRep.getNode();
      if (!nodesUtils.equals(leftNode, rightNode)) {
         comparedRoot.setState(NodeState.STATE_UPDATED);
      }
      compareUnderNode(comparedRoot);
      leftTreeModel.reload();
      rightTreeModel.reload();
   }

   private void compareUnderNode(ComparedElement element) {
      NodeTreeRep leftRep = element.getLeftNodeTreeRep();
      NodeTreeRep rightRep = element.getRightNodeTreeRep();
      if (!leftRep.hasChildren()) {
         return;
      }
      XMLNode leftNode = leftRep.getNode();
      XMLNode rightNode = rightRep.getNode();
      if (rightNode == null) {
         // first case, there is no right node parent
         Iterator<NodeTreeRep> it = leftRep.getChildren().iterator();
         while (it.hasNext()) {
            NodeTreeRep childRep = it.next();
            NodeTreeRep rightChildRep = new NodeTreeRep(false, childRep.getNodeName());
            ComparedElement childElement = new ComparedElement(childRep, rightChildRep);
            DefaultMutableTreeNode rightParentTreeNode = rightRep.getTreeNode();
            DefaultMutableTreeNode rightTreeNode = new DefaultMutableTreeNode(rightChildRep);
            rightChildRep.setTreeNode(rightTreeNode);
            rightParentTreeNode.add(rightTreeNode);
            childElement.setState(NodeState.STATE_DELETED);
            compModel.addComparedElement(childElement);
            element.addChild(childElement);
            rightRep.getTreeNode().add(rightTreeNode);
            compareUnderNode(childElement);
         }
      } else {
         // general case, there are both left and right node parents
         List<NodeTreeRep> leftNodeChildren = leftRep.getChildren();
         List<NodeTreeRep> rightNodeChildren = rightRep.getChildren();
         int sizeLeft = leftNodeChildren.size();
         int sizeRight = rightNodeChildren.size();
         Set<Integer> addedLeft = new HashSet<>();
         Set<Integer> addedRight = new HashSet<>();
         int indexLeft = 0;
         while (true) {
            NodeTreeRep leftChildRep = leftNodeChildren.get(indexLeft);
            NodeResult result = getFirstNode(leftChildRep, addedRight, rightRep);
            if (result == null) {
               NodeTreeRep rightChildRep = new NodeTreeRep(false, leftChildRep.getNodeName());
               ComparedElement childElement = new ComparedElement(leftChildRep, rightChildRep);
               childElement.setState(NodeState.STATE_DELETED);
               DefaultMutableTreeNode rightTreeNode = new DefaultMutableTreeNode(rightChildRep);
               rightChildRep.setTreeNode(rightTreeNode);
               compModel.addComparedElement(childElement);
               element.addChild(childElement);
               addToTree(childElement, rightTreeNode, false);
               compareUnderNode(childElement);
               indexLeft++;
            } else {
               NodeTreeRep rightChildRep = result.treeRep;
               addedRight.add(result.index);
               ComparedElement childElement = new ComparedElement(leftChildRep, rightChildRep);
               element.addChild(childElement);
               compareUnderNode(childElement);
               this.setElementState(childElement, result.state, result.movedState);
               compModel.addComparedElement(childElement);
               indexLeft++;
            }
            if (indexLeft >= sizeLeft) {
               break;
            }
         }
         DefaultMutableTreeNode leftParentTreeNode = leftRep.getTreeNode();
         for (int i = 0; i < sizeRight; i++) {
            if (!addedRight.contains(i)) {
               NodeTreeRep rightChildRep = rightNodeChildren.get(i);
               NodeTreeRep leftChildRep = new NodeTreeRep(true, rightChildRep.getNodeName());
               ComparedElement childElement = new ComparedElement(leftChildRep, rightChildRep);
               DefaultMutableTreeNode leftTreeNode = new DefaultMutableTreeNode(leftChildRep);
               leftChildRep.setTreeNode(leftTreeNode);
               leftParentTreeNode.add(leftTreeNode);
               childElement.setState(NodeState.STATE_NEW);
               compModel.addComparedElement(childElement);
               element.addChild(childElement);
               addToTree(childElement, leftTreeNode, true);
               compareUnderNode(childElement);
            }
         }
      }
   }

   private void addToTree(ComparedElement childElement, DefaultMutableTreeNode nodeToInsert, boolean isLeft) {
      NodeTreeRep refTreeRep = childElement.getNodeTreeRep(!isLeft);
      DefaultTreeModel treeModel = compModel.getTreeModel(isLeft);
      if (refTreeRep.getIndexInParent() == 0) {
         NodeTreeRep parentRep = refTreeRep.getParent();
         ComparedElement comparedElt = parentRep.getComparedElement();
         DefaultMutableTreeNode otherParentNode = comparedElt.getNodeTreeRep(isLeft).getTreeNode();
         treeModel.insertNodeInto(nodeToInsert, otherParentNode, 0);
      } else {
         NodeTreeRep previousSibling = refTreeRep.getPreviousSibling();
         ComparedElement comparedElt = previousSibling.getComparedElement();
         DefaultMutableTreeNode otherSiblingNode = comparedElt.getNodeTreeRep(isLeft).getTreeNode();
         DefaultMutableTreeNode otherParentNode = (DefaultMutableTreeNode) otherSiblingNode.getParent();
         int index = otherParentNode.getIndex(otherSiblingNode);
         treeModel.insertNodeInto(nodeToInsert, otherParentNode, index + 1);
      }
   }

   private void setElementState(ComparedElement childElement, char status, char movedState) {
      switch (status) {
         case NodeState.STATE_UNCHANGED:
            childElement.setState(NodeState.STATE_UNCHANGED);
            childElement.setMovedState(movedState);
            break;
         case NodeState.STATE_DELETED:
            childElement.setState(NodeState.STATE_DELETED);
            childElement.setMovedState(NodeMoveState.MOVED_UNCHANGED);
            break;
         case NodeState.STATE_NEW:
            childElement.setState(NodeState.STATE_NEW);
            childElement.setMovedState(NodeMoveState.MOVED_UNCHANGED);
            break;
         case NodeState.STATE_UPDATED:
            childElement.setState(NodeState.STATE_UPDATED);
            childElement.setMovedState(movedState);
            break;
      }
   }

   private NodeResult getFirstNode(NodeTreeRep refTreeRep, Set<Integer> addedRight, NodeTreeRep parentTreeRep) {
      int index = 0;
      int countChildren = parentTreeRep.countChildren();
      while (index < countChildren) {
         if (!addedRight.contains(index)) {
            NodeTreeRep otherTreeRep = parentTreeRep.getChild(index);
            char status = nodesUtils.compare(refTreeRep.getNode(), otherTreeRep.getNode());
            if (status != NodeState.STATE_DELETED) {
               switch (status) {
                  case NodeState.STATE_UNCHANGED:
                  case NodeState.STATE_UPDATED: {
                     NodeResult result = new NodeResult(otherTreeRep, status, index);
                     return result;
                  }
                  case NodeMoveState.MOVED_UP:
                  case NodeMoveState.MOVED_DOWN: {
                     NodeResult result = new NodeResult(otherTreeRep, NodeState.STATE_UNCHANGED, index);
                     result.movedState = status;
                     return result;
                  }
                  case NodeMoveState.MOVED_UP_AND_UPDATED:
                  case NodeMoveState.MOVED_DOWN_AND_UPDATED:
                  case NodeMoveState.MOVED_UP_THEN_UPDATED:
                  case NodeMoveState.MOVED_DOWN_THEN_UPDATED: {
                     NodeResult result = new NodeResult(otherTreeRep, NodeState.STATE_UPDATED, index);
                     result.movedState = status;
                     return result;
                  }
               }
            }
         }
         index++;
      }
      return null;
   }

   private void addNodes(DefaultMutableTreeNode parentNode, NodeTreeRep nodeRep, boolean isRoot) {
      DefaultMutableTreeNode childNode;
      if (!isRoot) {
         childNode = new DefaultMutableTreeNode(nodeRep);
         nodeRep.setTreeNode(childNode);
         parentNode.add(childNode);
      } else {
         childNode = parentNode;
      }

      boolean isLeft = nodeRep.isLeft();
      XMLNode xmlNode = nodeRep.getNode();
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childXMLNode = it.next();
         NodeTreeRep childRep = new NodeTreeRep(isLeft, childXMLNode);
         nodeRep.addChild(childRep);
         addNodes(childNode, childRep, false);
      }
   }

   private static class NodeResult {
      private NodeTreeRep treeRep;
      private int index;
      private char state;
      private char movedState;

      private NodeResult(NodeTreeRep treeRep, char state, int index) {
         this.treeRep = treeRep;
         this.state = state;
         this.movedState = state;
         this.index = index;
      }

      @Override
      public String toString() {
         StringBuilder buf = new StringBuilder();
         switch (state) {
            case NodeState.STATE_UNCHANGED:
               buf.append("STATE_UNCHANGED");
               break;
            case NodeState.STATE_UPDATED:
               buf.append("STATE_UPDATED");
               break;
            case NodeState.STATE_NEW:
               buf.append("STATE_NEW");
               break;
            case NodeState.STATE_DELETED:
               buf.append("STATE_DELETED");
               break;
            default:
               buf.append("Undefined State");
               break;
         }
         buf.append(" ");
         switch (movedState) {
            case NodeMoveState.MOVED_UNCHANGED:
               buf.append("MOVED_UNCHANGED");
               break;
            case NodeMoveState.MOVED_DOWN:
               buf.append("MOVED_DOWN");
               break;
            case NodeMoveState.MOVED_UP:
               buf.append("MOVED_UP");
               break;
            case NodeMoveState.MOVED_DOWN_AND_UPDATED:
               buf.append("MOVED_DOWN_AND_UPDATED");
               break;
            case NodeMoveState.MOVED_UP_AND_UPDATED:
               buf.append("MOVED_UP_AND_UPDATED");
               break;
            case NodeMoveState.MOVED_UP_THEN_UPDATED:
               buf.append("MOVED_UP_THEN_UPDATED");
               break;
            case NodeMoveState.MOVED_DOWN_THEN_UPDATED:
               buf.append("MOVED_UP_THEN_UPDATED");
               break;
            default:
               buf.append("Undefined Moved State");
               break;
         }
         return buf.toString();
      }
   }
}
