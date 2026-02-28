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

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.mdiutil.xml.tree.XMLNode;

/**
 * The wrapper representant of a left or right node in the tree. It will be used as tree nodes associated Objects for the left and the right trees.
 *
 * @since 0.1
 */
public class NodeTreeRep implements NodeState {
   private final boolean isLeft;
   private final XMLNode node;
   private final String name;
   private ComparedElement element;
   private DefaultMutableTreeNode treeNode;
   private NodeTreeRep parentRep = null;
   private int index = -1;
   private final List<NodeTreeRep> children = new ArrayList<>();

   /**
    * Constructor when the associated XML node exists.
    *
    * @param isLeft true for a left node wrapper
    * @param node the associated XML node
    */
   public NodeTreeRep(boolean isLeft, XMLNode node) {
      this.isLeft = isLeft;
      this.node = node;
      this.name = node.getCompleteName();
   }

   /**
    * Constructor when the associated XML node does not exists.
    *
    * @param isLeft true for a left node wrapper
    * @param name the associated node name
    */
   public NodeTreeRep(boolean isLeft, String name) {
      this.isLeft = isLeft;
      this.node = null;
      this.name = name;
   }

   /**
    * Return the parent wrapper.
    *
    * @return the parent wrapper
    */
   public NodeTreeRep getParent() {
      return parentRep;
   }

   /**
    * Return the previous sibling. Return null if there is no previous sibling.
    *
    * @return the previous sibling
    */
   public NodeTreeRep getPreviousSibling() {
      if (parentRep == null) {
         return null;
      } else if (index == 0) {
         return null;
      } else {
         return parentRep.getChild(index - 1);
      }
   }

   /**
    * Return the index of this wrapper in its parent.
    *
    * @return the index
    */
   public int getIndexInParent() {
      return index;
   }

   /**
    * Return true if this wrapper is the first in its parent.
    *
    * @return true if this wrapper is the first in its parent
    */
   public boolean isFirstInParent() {
      return index == 0;
   }

   /**
    * Return true if this is a left wrapper.
    *
    * @return true if this is a left wrapper
    */
   public boolean isLeft() {
      return isLeft;
   }

   /**
    * Set the associated compared element.
    *
    * @param element the compared element
    */
   public void setComparedElement(ComparedElement element) {
      this.element = element;
   }

   /**
    * Return the associated compared element.
    *
    * @return the compared element
    */
   public ComparedElement getComparedElement() {
      return element;
   }

   /**
    * Add a child wrapper to this element.
    *
    * @param child the child
    */
   public void addChild(NodeTreeRep child) {
      children.add(child);
      child.parentRep = this;
      child.index = children.size() - 1;
   }

   /**
    * Return the children wrappers for this element.
    *
    * @return the children
    */
   public List<NodeTreeRep> getChildren() {
      return children;
   }

   /**
    * Return the child wrapper of a specified index.
    *
    * @param index the index
    * @return the wrapper
    */
   public NodeTreeRep getChild(int index) {
      return children.get(index);
   }

   /**
    * Count the number of children.
    *
    * @return the number o children
    */
   public int countChildren() {
      return children.size();
   }

   /**
    * Return true if this wrapper has children.
    *
    * @return true if this wrapper has children
    */
   public boolean hasChildren() {
      return !children.isEmpty();
   }

   /**
    * Set the associated tree node.
    *
    * @param treeNode the tree node
    */
   public void setTreeNode(DefaultMutableTreeNode treeNode) {
      this.treeNode = treeNode;
   }

   /**
    * Return the associated tree node.
    *
    * @return the tree node
    */
   public DefaultMutableTreeNode getTreeNode() {
      return treeNode;
   }

   /**
    * Return the associated node name.
    *
    * @return the node name
    */
   public String getNodeName() {
      return name;
   }

   /**
    * Return true if this wrapper is associated to a concrete XML node (which means that there is an XML node for the associated left or right tree).
    *
    * @return true if this wrapper is associated to a concrete XML node
    */
   public boolean hasXMLNode() {
      return node != null;
   }

   /**
    * Return the associated node key.
    *
    * @return the node key
    */
   public XMLNodeKey getNodeKey() {
      return element.getNodeKey();
   }

   @Override
   public String toString() {
      if (node == null) {
         return " ";
      } else {
         return name;
      }
   }

   /**
    * Return the associated XML node (can be null)
    *
    * @return the XML node
    */
   public XMLNode getNode() {
      return node;
   }

   /**
    * Return the associated XML node on the other side (can be null)
    *
    * @return the XML node
    */
   public XMLNode getOtherNode() {
      if (isLeft) {
         return element.getRightNode();
      } else {
         return element.getLeftNode();
      }
   }

   /**
    * Return true if the wrapper associated element has children which are different beteween left and right.
    *
    * @return true if the wrapper associated element has children which are different
    */
   public boolean hasDifferentChildren() {
      if (element != null) {
         return element.hasDifferentChildren();
      } else {
         return false;
      }
   }

   /**
    * Return the wrapper associated element state.
    *
    * @return the wrapper associated element state
    * @see ComparedElement#getState()
    * @see NodeState
    */
   public char getState() {
      if (element != null) {
         return element.getState();
      } else if (isLeft) {
         return NodeState.STATE_NEW;
      } else {
         return NodeState.STATE_DELETED;
      }
   }

   /**
    * Return the wrapper associated element moved state.
    *
    * @return the wrapper associated element moved state
    * @see ComparedElement#getMovedState()
    * @see NodeMoveState
    */   
   public char getMovedState() {
      if (element != null) {
         char movedState = element.getMovedState();
         if (movedState == NodeMoveState.MOVED_UNCHANGED) {
            return movedState;
         } else if (isLeft) {
            return movedState;
         } else {
            switch (movedState) {
               case NodeMoveState.MOVED_DOWN:
                  return NodeMoveState.MOVED_UP;
               case NodeMoveState.MOVED_UP:
                  return NodeMoveState.MOVED_DOWN;
               case NodeMoveState.MOVED_UP_THEN_UPDATED:
                  return NodeMoveState.MOVED_DOWN_THEN_UPDATED;
               case NodeMoveState.MOVED_DOWN_THEN_UPDATED:
                  return NodeMoveState.MOVED_UP_THEN_UPDATED;
               case NodeMoveState.MOVED_UP_AND_UPDATED:
                  return NodeMoveState.MOVED_DOWN_AND_UPDATED;
               case NodeMoveState.MOVED_DOWN_AND_UPDATED:
                  return NodeMoveState.MOVED_UP_AND_UPDATED;
               default:
                  return movedState;
            }
         }
      } else {
         return NodeMoveState.MOVED_UNCHANGED;
      }
   }

}
