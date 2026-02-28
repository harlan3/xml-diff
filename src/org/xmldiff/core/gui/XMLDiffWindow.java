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

import java.awt.BorderLayout;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.xmldiff.core.comparator.XMLComparator;
import org.xmldiff.core.model.ComparisonModel;
import org.xmldiff.core.model.NodeTreeRep;

/**
 * The window class for the xmldiff tool.
 *
 * @version 0.6
 */
public class XMLDiffWindow extends JPanel {
   private JSplitPane split = null;
   private File leftFile = null;
   private File rightFile = null;
   private XMLDiffFilePanel leftFilePanel = null;
   private XMLDiffFilePanel rightFilePanel = null;
   private XMLComparator comparator = null;
   private ComparisonModel compModel = null;

   public XMLDiffWindow() {
      super();
      this.setLayout(new BorderLayout());
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
    * Set the panel divider location;
    *
    * @param width the width
    * @param height the height
    */
   public void setDividerLocation(int width, int height) {
      split.setDividerLocation(width / 2);
      leftFilePanel.setDividerLocation(2 * height / 3);
      rightFilePanel.setDividerLocation(2 * height / 3);
   }

   void setFilePanelDivider(XMLDiffFilePanel panel) {
      if (leftFilePanel != panel) {
         leftFilePanel.setDividerLocation(panel.getDividerLocation());
      }
      if (rightFilePanel != panel) {
         rightFilePanel.setDividerLocation(panel.getDividerLocation());
      }
   }

   /**
    * Set the left and right files.
    *
    * @param leftFile the left file
    * @param rightFile the right file
    */
   public void setFiles(File leftFile, File rightFile) {
      this.leftFile = leftFile;
      this.rightFile = rightFile;
      split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
      leftFilePanel = new XMLDiffFilePanel(this, true);
      rightFilePanel = new XMLDiffFilePanel(this, false);
      split.setLeftComponent(leftFilePanel);
      split.setRightComponent(rightFilePanel);
      this.add(split, BorderLayout.CENTER);
      this.revalidate();
      this.repaint();
      leftFilePanel.setFile(leftFile);
      rightFilePanel.setFile(rightFile);
   }

   /**
    * Run the comparison
    *
    * @return the comparison model
    */
   public ComparisonModel runCompare() {
      comparator = new XMLComparator();
      comparator.setFiles(leftFile, rightFile);
      comparator.runCompare();
      compModel = comparator.getModel();
      leftFilePanel.load(compModel);
      rightFilePanel.load(compModel);
      return comparator.getModel();
   }

   /**
    * Reload the comparison.
    *
    * @return the comparison model
    */
   public ComparisonModel reload() {
      comparator.reload();

      comparator.runCompare();
      compModel = comparator.getModel();

      int leftDivider = leftFilePanel.getDividerLocation();
      int rightDivider = rightFilePanel.getDividerLocation();

      leftFilePanel.load(compModel);
      rightFilePanel.load(compModel);

      leftFilePanel.setDividerLocation(leftDivider);
      rightFilePanel.setDividerLocation(rightDivider);

      return comparator.getModel();
   }

   void expandNode(NodeTreeRep treeRep, XMLDiffFilePanel panel) {
      XMLDiffFilePanel otherPanel;
      if (panel == leftFilePanel) {
         otherPanel = rightFilePanel;
         treeRep = treeRep.getComparedElement().getRightNodeTreeRep();
      } else {
         otherPanel = leftFilePanel;
         treeRep = treeRep.getComparedElement().getLeftNodeTreeRep();
      }
      otherPanel.disableTreeSelection();

      otherPanel.forceExpand(treeRep);
      otherPanel.enableTreeSelection();
   }

   void collapseNode(NodeTreeRep treeRep, XMLDiffFilePanel panel) {
      XMLDiffFilePanel otherPanel;
      if (panel == leftFilePanel) {
         otherPanel = rightFilePanel;
         treeRep = treeRep.getComparedElement().getRightNodeTreeRep();
      } else {
         otherPanel = leftFilePanel;
         treeRep = treeRep.getComparedElement().getLeftNodeTreeRep();
      }
      otherPanel.disableTreeSelection();

      otherPanel.forceCollapse(treeRep);
      otherPanel.enableTreeSelection();
   }

   void selectNode(NodeTreeRep treeRep, XMLDiffFilePanel panel) {
      XMLDiffFilePanel otherPanel;
      if (panel == leftFilePanel) {
         otherPanel = rightFilePanel;
         treeRep = treeRep.getComparedElement().getRightNodeTreeRep();
      } else {
         otherPanel = leftFilePanel;
         treeRep = treeRep.getComparedElement().getLeftNodeTreeRep();
      }
      otherPanel.disableTreeSelection();

      otherPanel.forceSelection(treeRep);
      otherPanel.enableTreeSelection();
   }
}
