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

import org.mdiutil.xml.tree.XMLNode;
import org.xmldiff.core.config.NodeRules;
import org.xmldiff.core.config.Rule;

/**
 * Utilities for XML nodes used in the xmldiff tool.
 *
 * @since 0.1
 */
public class XMLNodesUtil {
   private static XMLNodesUtil nodesUtils = null;
   private NodeRules nodeRules = null;

   private XMLNodesUtil() {
   }

   /**
    * Set the node rules.
    *
    * @param nodeRules the rules
    */
   public void setNodeRules(NodeRules nodeRules) {
      this.nodeRules = nodeRules;
   }

   /**
    * Return the unique instance.
    *
    * @return the instance
    */
   public static XMLNodesUtil getInstance() {
      if (nodesUtils == null) {
         nodesUtils = new XMLNodesUtil();
      }
      return nodesUtils;
   }

   /**
    * Compare two nodes. The returned states use all the values for {@link NodeState} and {@link NodeMoveState}.
    *
    * @param node1 the first node
    * @param node2 the second node
    * @return the state
    */
   public char compare(XMLNode node1, XMLNode node2) {
      if (isSameNode(node1, node2)) {
         char status = nodeRules.getStatus(node1, node2);
         switch (status) {
            case Rule.STATUS_DIFFERENT:
               return NodeState.STATE_DELETED;
            case Rule.STATUS_UPDATED: {
               int node1Index = node1.getIndexInParent();
               int node2Index = node2.getIndexInParent();
               if (node1Index != node2Index) {
                  Rule rule = nodeRules.getRule(node1);
                  if (rule.isOrderSignificant()) {
                     if (node1Index < node2Index) {
                        return NodeMoveState.MOVED_DOWN_AND_UPDATED;
                     } else {
                        return NodeMoveState.MOVED_UP_AND_UPDATED;
                     }
                  } else {
                     if (node1Index < node2Index) {
                        return NodeMoveState.MOVED_DOWN_THEN_UPDATED;
                     } else {
                        return NodeMoveState.MOVED_UP_THEN_UPDATED;
                     }
                  }
               } else {
                  return NodeState.STATE_UPDATED;
               }
            }
            default:
               int node1Index = node1.getIndexInParent();
               int node2Index = node2.getIndexInParent();
               if (node1Index != node2Index) {
                  Rule rule = nodeRules.getRule(node1);
                  if (node1Index < node2Index) {
                     if (rule.isOrderSignificant()) {
                        return NodeMoveState.MOVED_DOWN_THEN_UPDATED;
                     } else {
                        return NodeMoveState.MOVED_DOWN;
                     }
                  } else {
                     if (rule.isOrderSignificant()) {
                        return NodeMoveState.MOVED_UP_THEN_UPDATED;
                     } else {
                        return NodeMoveState.MOVED_UP;
                     }
                  }
               } else {
                  return NodeState.STATE_UNCHANGED;
               }
         }
      } else {
         return NodeState.STATE_DELETED;
      }
   }

   private boolean isSameNode(XMLNode node1, XMLNode node2) {
      if (node1 != null && node2 == null) {
         return false;
      } else if (node1 == null && node2 != null) {
         return false;
      } else if (node1.getPrefixedName().equals(node2.getPrefixedName())) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * Return true if two nodes are equal.
    *
    * @param node1 the first node
    * @param node2 the second node
    * @return true if the two nodes are equal
    */
   public boolean equals(XMLNode node1, XMLNode node2) {
      Rule rule = nodeRules.getRule(node1);
      return rule.equals(node1, node2);
   }

}
