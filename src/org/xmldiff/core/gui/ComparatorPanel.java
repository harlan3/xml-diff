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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.xmldiff.core.model.ComparedElement;
import org.xmldiff.core.model.ComparisonModel;

/**
 * This class is a panel allowing to navigate in the differences.
 *
 * @since 0.1
 */
public class ComparatorPanel extends JPanel {
   private JButton nextDifference = null;
   private JLabel diffCountLabel = null;
   private JButton previousDifference = null;
   private ComparisonModel model = null;
   private static URL nextDiffIcon = null;
   private static URL previousDiffIcon = null;

   static {
      Class clazz = ComparatorPanel.class;
      nextDiffIcon = clazz.getResource("nextdiff.png");
      previousDiffIcon = clazz.getResource("previousdiff.png");
   }

   public ComparatorPanel() {
      super();
      setup();
   }

   /**
    * Set the comparison model and update the content of the panel.
    *
    * @param model the comparison model
    */
   public void setComparisonModel(ComparisonModel model) {
      this.model = model;
      if (model == null) {
         this.previousDifference.setEnabled(false);
         this.nextDifference.setEnabled(false);
         this.diffCountLabel.setText("");
      } else {
         this.model.initCurrentDifference();
         this.previousDifference.setEnabled(model.hasPreviousDifference());
         this.nextDifference.setEnabled(model.hasNextDifference());
         updateCount();
      }
   }

   private void updateCount() {
      int countDifferences = model.countDifferences();
      int currentDifference = model.getCurrentDifference();
      if (currentDifference < 0) {
         this.diffCountLabel.setText("" + countDifferences);
      } else {
         this.diffCountLabel.setText((currentDifference + 1) + " / " + countDifferences);
      }
   }

   private ImageIcon getIcon(URL url) {
      ImageIcon icon = new ImageIcon(url);
      return icon;
   }

   private void setup() {
      this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

      previousDifference = new JButton(getIcon(previousDiffIcon));
      previousDifference.setEnabled(false);
      previousDifference.setBorderPainted(false);
      previousDifference.setContentAreaFilled(false);
      previousDifference.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            previousDifference();
         }
      });
      this.add(previousDifference);
      this.add(Box.createHorizontalStrut(5));

      diffCountLabel = new JLabel();
      this.add(diffCountLabel);

      this.add(Box.createHorizontalStrut(5));
      nextDifference = new JButton(getIcon(nextDiffIcon));
      nextDifference.setEnabled(false);
      nextDifference.setBorderPainted(false);
      nextDifference.setContentAreaFilled(false);
      nextDifference.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            nextDifference();
         }
      });
      this.add(nextDifference);
   }

   private void highlight(boolean isLeft, ComparedElement element) {
      DefaultMutableTreeNode treeNode;
      JTree tree;
      if (isLeft) {
         treeNode = element.getLeftNodeTreeRep().getTreeNode();
         tree = model.getLeftTree();
      } else {
         treeNode = element.getRightNodeTreeRep().getTreeNode();
         tree = model.getRightTree();
      }
      TreePath path = new TreePath(treeNode.getPath());
      tree.setSelectionPath(path);
      tree.scrollPathToVisible(path);
      tree.makeVisible(path);
   }

   private void previousDifference() {
      if (model == null) {
         return;
      }
      ComparedElement element = model.previousDifference();
      if (element != null) {
         highlight(true, element);
         highlight(false, element);
      }
      updateCount();
      this.previousDifference.setEnabled(model.hasPreviousDifference());
      this.nextDifference.setEnabled(model.hasNextDifference());
   }

   private void nextDifference() {
      if (model == null) {
         return;
      }
      ComparedElement element = model.nextDifference();
      if (element != null) {
         highlight(true, element);
         highlight(false, element);
      }
      updateCount();
      this.previousDifference.setEnabled(model.hasPreviousDifference());
      this.nextDifference.setEnabled(model.hasNextDifference());
   }

}
