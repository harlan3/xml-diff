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
package org.xmldiff.core.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.mdiutil.xml.tree.SortableQName;
import org.mdiutil.xml.tree.XMLNode;
import org.xmldiff.core.model.NodeMoveState;
import org.xmldiff.core.model.NodeState;
import org.xmldiff.core.model.NodeTreeRep;

/**
 * The TreeCellRenderer for an XML file tree.
 *
 * @version 0.6
 */
public class XMLTreeCellRenderer extends DefaultTreeCellRenderer {
   private static final Icon PARENT_UPDATE_ICON;
   private static final Icon MOVED_UP_ICON;
   private static final Icon MOVED_DOWN_ICON;
   private static final Icon MOVED_UP_UPDATED_ICON;
   private static final Icon MOVED_DOWN_UPDATED_ICON;
   private final boolean isLeftPanel;
   private static final Map<Integer, String> EMPTY_STR = new HashMap<>();

   static {
      PARENT_UPDATE_ICON = new ImageIcon(XMLTreeCellRenderer.class.getResource("diff.png"));
      MOVED_UP_ICON = new ImageIcon(XMLTreeCellRenderer.class.getResource("up-arrow-simple.png"));
      MOVED_DOWN_ICON = new ImageIcon(XMLTreeCellRenderer.class.getResource("down-arrow-simple.png"));
      MOVED_UP_UPDATED_ICON = new ImageIcon(XMLTreeCellRenderer.class.getResource("up-arrow.png"));
      MOVED_DOWN_UPDATED_ICON = new ImageIcon(XMLTreeCellRenderer.class.getResource("down-arrow.png"));
   }

   /**
    * Constructor.
    *
    * @param isLeftPanel true if this is the left panel
    */
   public XMLTreeCellRenderer(boolean isLeftPanel) {
      super();
      this.isLeftPanel = isLeftPanel;
   }

   @Override
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean setFocus) {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, setFocus);
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      Object o = node.getUserObject();

      this.setIcon(null);
      if (o instanceof NodeTreeRep) {
         NodeTreeRep treeRep = (NodeTreeRep) o;
         XMLNode theNode = treeRep.getNode();
         if (theNode != null) {
            this.setText(theNode.getCompleteName());
         } else {
            // see https://stackoverflow.com/questions/2804827/create-a-string-with-n-characters
            int length = treeRep.getOtherNode().getCompleteName().length();
            if (EMPTY_STR.containsKey(length)) {
               this.setText(EMPTY_STR.get(length));
            } else {
               char[] charArray = new char[length];
               Arrays.fill(charArray, ' ');
               String str = new String(charArray);
               this.setText(str);
               EMPTY_STR.put(length, str);
            }
         }
         setToolTip(theNode);
         boolean isUnchangedParent;
         if (treeRep.hasChildren() && treeRep.hasDifferentChildren() && treeRep.getState() == NodeState.STATE_UNCHANGED) {
            this.setIcon(PARENT_UPDATE_ICON);
            isUnchangedParent = false;
         } else {
            this.setIcon(null);
            isUnchangedParent = true;
         }
         switch (treeRep.getState()) {
            case NodeState.STATE_UNCHANGED:
               if (isUnchangedParent) {
                  this.setForeground(Color.GRAY);
               } else {
                  this.setForeground(Color.BLACK);
               }
               if (isUnchangedParent) {
                  switch (treeRep.getMovedState()) {
                     case NodeMoveState.MOVED_UP: {
                        this.setIcon(MOVED_UP_ICON);
                        break;
                     }
                     case NodeMoveState.MOVED_DOWN: {
                        this.setIcon(MOVED_DOWN_ICON);
                        break;
                     }
                  }
               }
               break;
            case NodeState.STATE_NEW:
               if (!isLeftPanel) {
                  this.setForeground(Color.BLUE);
               }
               break;
            case NodeState.STATE_UPDATED:
               this.setForeground(Color.RED);
               switch (treeRep.getMovedState()) {
                  case NodeMoveState.MOVED_UP_AND_UPDATED:
                  case NodeMoveState.MOVED_UP: {
                     this.setIcon(MOVED_UP_UPDATED_ICON);
                  }
                  break;
                  case NodeMoveState.MOVED_DOWN:
                  case NodeMoveState.MOVED_DOWN_AND_UPDATED: {
                     this.setIcon(MOVED_DOWN_UPDATED_ICON);
                  }
                  break;
                  case NodeMoveState.MOVED_UP_THEN_UPDATED:
                     this.setIcon(MOVED_UP_UPDATED_ICON);
                     this.setForeground(Color.BLACK);
                     break;
                  case NodeMoveState.MOVED_DOWN_THEN_UPDATED:
                     this.setIcon(MOVED_DOWN_UPDATED_ICON);
                     this.setForeground(Color.BLACK);
                     break;                     
               }
               break;
            case NodeState.STATE_DELETED:
               if (isLeftPanel) {
                  this.setForeground(Color.BLUE);
               }
               break;
            default:
               this.setForeground(Color.BLACK);
         }
      }
      return this;
   }

   private void setToolTip(XMLNode node) {
      if (node != null) {
         StringBuilder buf = new StringBuilder();
         buf.append(node.getLineNumber()).append(": ");
         buf.append("<").append(node.getCompleteName());
         Iterator<Map.Entry<SortableQName, String>> it = node.getAttributes().entrySet().iterator();
         while (it.hasNext()) {
            Map.Entry<SortableQName, String> entry = it.next();
            String attrName = entry.getKey().toString();
            String attrValue = entry.getValue();
            buf.append(" ").append(attrName).append("=\"").append(attrValue).append("\"");
         }
         buf.append(" />");
         this.setToolTipText(buf.toString());
      }
   }
}
