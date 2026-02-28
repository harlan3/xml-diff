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
import org.mdiutil.xml.tree.XMLNode;

/**
 * The representant of a left and right node comparison in the tree.
 *
 * @version 0.5
 */
public class ComparedElement implements NodeState {
   public NodeTreeRep leftRep;
   public NodeTreeRep rightRep;
   private char state = STATE_UNCHANGED;
   private char movedState = NodeMoveState.MOVED_UNCHANGED;
   private XMLNodeKey nodeKey = null;
   private boolean hasDifferentChildren = false;
   private ComparedElement parentElement = null;
   public final List<ComparedElement> children = new ArrayList<>();

   /**
    * Constructor.
    *
    * @param leftRep the left node wrapper
    * @param rightRep the right node wrapper
    */
   public ComparedElement(NodeTreeRep leftRep, NodeTreeRep rightRep) {
      setLeftNodeTreeRep(leftRep);
      setRightNodeTreeRep(rightRep);
   }

   /**
    * Set the left node wrapper.
    *
    * @param leftRep the left node wrapper
    */
   public final void setLeftNodeTreeRep(NodeTreeRep leftRep) {
      this.leftRep = leftRep;
      leftRep.setComparedElement(this);
   }

   /**
    * Set the right node wrapper.
    *
    * @param rightRep the right node wrapper
    */
   public final void setRightNodeTreeRep(NodeTreeRep rightRep) {
      this.rightRep = rightRep;
      rightRep.setComparedElement(this);
   }

   /**
    * Return the node wrapper for the left or right tree.
    *
    * @param isLeftTree true for the left tree
    * @return the node wrapper
    */
   public NodeTreeRep getNodeTreeRep(boolean isLeftTree) {
      if (isLeftTree) {
         return getLeftNodeTreeRep();
      } else {
         return getRightNodeTreeRep();
      }
   }

   /**
    * Return the left node wrapper.
    *
    * @return the left node wrapper
    */
   public NodeTreeRep getLeftNodeTreeRep() {
      return leftRep;
   }

   /**
    * Return the right node wrapper.
    *
    * @return the right node wrapper
    */
   public NodeTreeRep getRightNodeTreeRep() {
      return rightRep;
   }

   /**
    * Set the parent element.
    *
    * @param parentElement the parent element
    */
   public void setParentElement(ComparedElement parentElement) {
      this.parentElement = parentElement;
   }

   /**
    * Set if the element has children which are different beteween left and right.
    *
    * @param hasDifferentChildren true if the element has children which are different
    */
   public void setHasDifferentChildren(boolean hasDifferentChildren) {
      this.hasDifferentChildren = hasDifferentChildren;
      if (parentElement != null) {
         parentElement.setHasDifferentChildren(true);
      }
   }

   /**
    * Return the node name for the element.
    *
    * @return the node name
    */
   public String getNodeName() {
      if (leftRep != null) {
         return leftRep.getNodeName();
      } else {
         return rightRep.getNodeName();
      }
   }

   /**
    * Return true if the element has children which are different beteween left and right.
    *
    * @return true if the element has children which are different
    */
   public boolean hasDifferentChildren() {
      return hasDifferentChildren;
   }

   /**
    * Return the key of the node.
    *
    * @return the key
    */
   public XMLNodeKey getNodeKey() {
      if (nodeKey == null) {
         if (leftRep.hasXMLNode()) {
            this.nodeKey = new XMLNodeKey(leftRep.getNode().getNodePath().getPathArray(), leftRep.getNode().getPrefixedName());
         } else {
            this.nodeKey = new XMLNodeKey(rightRep.getNode().getNodePath().getPathArray(), rightRep.getNode().getPrefixedName());
         }
      }
      return nodeKey;
   }

   @Override
   public String toString() {
      if (leftRep.hasXMLNode()) {
         return leftRep.getNodeName();
      } else {
         return rightRep.getNodeName();
      }
   }

   /**
    * Return the associated left node (can be null) if there is no left node for this element).
    *
    * @return the associated left node
    */
   public XMLNode getLeftNode() {
      return leftRep.getNode();
   }

   /**
    * Return true if there is a left node and this node has children.
    *
    * @return true if there is a left node and this node has children
    */
   public boolean hasLeftChildren() {
      return leftRep.hasXMLNode() && leftRep.getNode().hasChildren();
   }

   /**
    * Return true if there is a right node and this node has children.
    *
    * @return true if there is a right node and this node has children
    */
   public boolean hasRightChildren() {
      return rightRep.hasXMLNode() && rightRep.getNode().hasChildren();
   }

   /**
    * Return the associated right node (can be null) if there is no right node for this element).
    *
    * @return the associated right node
    */
   public XMLNode getRightNode() {
      return rightRep.getNode();
   }

   /**
    * Set the state of this element. It indicates if the left and right node are different.
    *
    * @param state the state
    * @see NodeState
    */
   public void setState(char state) {
      this.state = state;
      if (state != NodeState.STATE_UNCHANGED && parentElement != null) {
         parentElement.setHasDifferentChildren(true);
      }
   }

   /**
    * Return the state of this element. It indicates if the left and right node are different.
    *
    * @return the state
    * @see NodeState
    */
   public char getState() {
      return state;
   }

   /**
    * Set the moved state of this element. It indicates if the left and right node have been moved.
    *
    * @param state the moved state
    * @see NodeMoveState
    */
   public void setMovedState(char state) {
      this.movedState = state;
   }

   /**
    * Return the moved state of this element. It indicates if the left and right node have been moved.
    *
    * @return the moved state
    * @see NodeMoveState
    */
   public char getMovedState() {
      return movedState;
   }

   /**
    * Add a child element for this element.
    *
    * @param child the child element
    */
   public void addChild(ComparedElement child) {
      children.add(child);
      child.setParentElement(this);
      if (child.getState() != NodeState.STATE_UNCHANGED) {
         this.setHasDifferentChildren(true);
      }
   }

   /**
    * Return the children of the element.
    *
    * @return the children
    */
   public List<ComparedElement> getChildren() {
      return children;
   }

   /**
    * Return true if the element has children.
    *
    * @return true if the element has children
    */
   public boolean hasChildren() {
      return !children.isEmpty();
   }
}
