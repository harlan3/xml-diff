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
package org.xmldiff.core.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.mdiutil.xml.tree.XMLNode;

/**
 * The model which is the result of one comparison.
 *
 * @version 0.6
 */
public class ComparisonModel {
   private final File leftFile;
   private final File rightFile;
   private DefaultTreeModel leftTreeModel;
   private DefaultTreeModel rightTreeModel;
   private JTree leftTree;
   private JTree rightTree;
   private final ComparedElement comparedRoot;
   private final Map<XMLNodeKey, ComparedElement> keyToCompElement = new HashMap<>();
   private int currentDifference = -1;
   private char comparisonState = NodeState.STATE_UNCHANGED;
   private final List<ComparedElement> differences = new ArrayList<>();

   /**
    * Constructor.
    *
    * @param leftFile the left file
    * @param rightFile the right file
    * @param comparedRoot the root element of the comparison
    */
   public ComparisonModel(File leftFile, File rightFile, ComparedElement comparedRoot) {
      this.leftFile = leftFile;
      this.rightFile = rightFile;
      this.comparedRoot = comparedRoot;
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
    * Set the tree model for the left or right tree.
    *
    * @param treeModel the tree model
    * @param isLeftTree true for the left tree
    */
   public void setTreeModel(DefaultTreeModel treeModel, boolean isLeftTree) {
      if (isLeftTree) {
         setLeftTreeModel(treeModel);
      } else {
         setRightTreeModel(treeModel);
      }
   }

   /**
    * Set the tree model for the left tree
    *
    * @param leftTreeModel the tree model
    */
   public void setLeftTreeModel(DefaultTreeModel leftTreeModel) {
      this.leftTreeModel = leftTreeModel;
   }

   /**
    * Set the tree model for the right tree
    *
    * @param rightTreeModel the tree model
    */
   public void setRightTreeModel(DefaultTreeModel rightTreeModel) {
      this.rightTreeModel = rightTreeModel;
   }

   /**
    * Return the tree model for the left or right tree.
    *
    * @param isLeftTree true for the left tree
    * @return the tree model
    */
   public DefaultTreeModel getTreeModel(boolean isLeftTree) {
      if (isLeftTree) {
         return getLeftTreeModel();
      } else {
         return getRightTreeModel();
      }
   }

   /**
    * Return the tree model for the left tree
    *
    * @return leftTreeModel the tree model
    */
   public DefaultTreeModel getLeftTreeModel() {
      return leftTreeModel;
   }

   /**
    * Return the tree model for the right tree
    *
    * @return leftTreeModel the tree model
    */
   public DefaultTreeModel getRightTreeModel() {
      return rightTreeModel;
   }

   /**
    * Set the left or right tree.
    *
    * @param tree the tree
    * @param isLeftTree true for the left tree
    */
   public void setTree(JTree tree, boolean isLeftTree) {
      if (isLeftTree) {
         setLeftTree(tree);
      } else {
         setRightTree(tree);
      }
   }

   /**
    * Set the left tree.
    *
    * @param leftTree the left tree
    */
   public void setLeftTree(JTree leftTree) {
      this.leftTree = leftTree;
   }

   /**
    * Return the left tree.
    *
    * @return the left tree
    */
   public JTree getLeftTree() {
      return leftTree;
   }

   /**
    * Set the right tree.
    *
    * @param rightTree the left right
    */
   public void setRightTree(JTree rightTree) {
      this.rightTree = rightTree;
   }

   /**
    * Return the right tree.
    *
    * @return the right tree
    */
   public JTree getRightTree() {
      return rightTree;
   }

   /**
    * Return the left root node of the comparison.
    *
    * @return the left root node
    */
   public XMLNode getLeftRootNode() {
      return comparedRoot.getLeftNode();
   }

   /**
    * Return the right root node of the comparison.
    *
    * @return the right root node
    */
   public XMLNode getRightRootNode() {
      return comparedRoot.getRightNode();
   }

   /**
    * Return the root compared element.
    *
    * @return the root compared element
    */
   public ComparedElement getComparedElement() {
      return comparedRoot;
   }

   /**
    * Return the root tree node associated with the left or right root node of the comparison.
    *
    * @param isLeft true for the left node
    * @return the root tree node
    */
   public DefaultMutableTreeNode getComparedTreeNode(boolean isLeft) {
      if (isLeft) {
         return comparedRoot.getLeftNodeTreeRep().getTreeNode();
      } else {
         return comparedRoot.getRightNodeTreeRep().getTreeNode();
      }
   }

   /**
    * Add a compared element which is in the comparison.
    *
    * @param compElement the element
    */
   public void addComparedElement(ComparedElement compElement) {
      keyToCompElement.put(compElement.getNodeKey(), compElement);
      if (compElement.getState() != NodeState.STATE_UNCHANGED) {
         comparisonState = NodeState.STATE_UPDATED;
         differences.add(compElement);
      }
   }

   /**
    * Return the comparison state. It can be {@link NodeState#STATE_UNCHANGED} or {@link NodeState#STATE_UPDATED}.
    *
    * @return the comparison state
    */
   public char getComparisonState() {
      return comparisonState;
   }

   /**
    * Return the list of differences in the comparison result.
    *
    * @return the list of differences
    */
   public List<ComparedElement> getDifferences() {
      return differences;
   }

   /**
    * Initialize the current difference index.
    */
   public void initCurrentDifference() {
      this.currentDifference = -1;
   }

   /**
    * Count the number of differences.
    *
    * @return the number of differences
    */
   public int countDifferences() {
      return differences.size();
   }

   /**
    * Return true if there are differences.
    *
    * @return true if there are differences
    */
   public boolean hasDifferences() {
      return !differences.isEmpty();
   }

   /**
    * Return the current diffeence index.
    *
    * @return the current diffeence index
    */
   public int getCurrentDifference() {
      return currentDifference;
   }

   /**
    * Return true if there is a previous difference.
    *
    * @return true if there is a previous difference
    */
   public boolean hasPreviousDifference() {
      if (currentDifference > 0) {
         return currentDifference > 0;
      } else {
         return false;
      }
   }

   /**
    * Return the previous difference.
    *
    * @return the previous difference
    */
   public ComparedElement previousDifference() {
      if (currentDifference > 0) {
         currentDifference--;
         return differences.get(currentDifference);
      }
      return null;
   }

   /**
    * Return true if there is a next difference.
    *
    * @return true if there is a next difference
    */
   public boolean hasNextDifference() {
      return currentDifference < differences.size() - 1;
   }

   /**
    * Return the next difference.
    *
    * @return the next difference
    */
   public ComparedElement nextDifference() {
      if (currentDifference < differences.size() - 1) {
         currentDifference++;
         return differences.get(currentDifference);
      }
      return null;
   }

   /**
    * Return the map of keys to elements.
    *
    * @return the map
    */
   public Map<XMLNodeKey, ComparedElement> getKeyToComparedElement() {
      return keyToCompElement;
   }

   /**
    * Return the element corresponding to a key.
    *
    * @param key the key
    * @return the element
    */
   public ComparedElement getKeyToComparedElement(XMLNodeKey key) {
      return keyToCompElement.get(key);
   }
}
