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
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.mdiutil.xml.tree.XMLNode;
import org.xmldiff.core.gui.xmlviewer.XMLViewer;
import org.xmldiff.core.model.ComparisonModel;
import org.xmldiff.core.model.NodeTreeRep;

/**
 * Represents one file panel used in the xmldiff tool.
 *
 * @version 0.6
 */
public class XMLDiffFilePanel extends JPanel {
   private static final int LABEL_HEIGHT;

   static {
      JLabel label = new JLabel("TOTO");
      LABEL_HEIGHT = label.getPreferredSize().height;
   }
   private File file;
   // see https://stackoverflow.com/questions/24604233/expand-and-collapse-two-trees-with-same-structure-simultaneously
   private final XMLDiffWindow diffWindow;
   private final JTextField tf = new JTextField(50);
   private DefaultMutableTreeNode root;
   private DefaultTreeModel treeModel;
   private JTree tree;
   private final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
   private final NodePropertiesPanel propertiesPanel = new NodePropertiesPanel();
   private boolean disableTreeSelection = false;
   private final boolean isLeftPanel;
   private ComparisonModel compModel = null;
   private NodeTreeRep selectionRep = null;

   /**
    * A panel showing the content of the left or right tree.
    *
    * @param diffWindow the associated differences window
    * @param isLeftPanel true if the panel shows the cntent of the left tree
    */
   public XMLDiffFilePanel(XMLDiffWindow diffWindow, boolean isLeftPanel) {
      super();
      this.diffWindow = diffWindow;
      this.isLeftPanel = isLeftPanel;
   }

   private void setupTree() {
      tree.addTreeExpansionListener(new TreeExpansionListener() {
         @Override
         public void treeExpanded(TreeExpansionEvent e) {
            TreePath path = e.getPath();
            Object o = path.getLastPathComponent();
            o = ((DefaultMutableTreeNode) o).getUserObject();
            if (o instanceof NodeTreeRep) {
               expandNode((NodeTreeRep) o);
            }
         }

         @Override
         public void treeCollapsed(TreeExpansionEvent e) {
            TreePath path = e.getPath();
            Object o = path.getLastPathComponent();
            o = ((DefaultMutableTreeNode) o).getUserObject();
            if (o instanceof NodeTreeRep) {
               collapseNode((NodeTreeRep) o);
            }
         }
      });

      tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
         @Override
         public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            Object o = path.getLastPathComponent();
            o = ((DefaultMutableTreeNode) o).getUserObject();
            if (o instanceof NodeTreeRep) {
               NodeTreeRep nodeRep = (NodeTreeRep) o;
               selectionRep = nodeRep;
               propertiesPanel.setNode(nodeRep.getNode(), nodeRep.getOtherNode());
               selectNode(nodeRep);
            }
         }
      });

      tree.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (selectionRep != null && e.getButton() == MouseEvent.BUTTON3) {
               showPopupMenu(e.getX(), e.getY());
            }
         }
      });
   }

   private void showPopupMenu(int x, int y) {
      JPopupMenu menu = new JPopupMenu();
      JMenuItem item = new JMenuItem("Show in File");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            showInFile();
         }
      });      
      menu.add(item);
      
      menu.addSeparator();
      item = new JMenuItem("Copy to Clipboard");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            copyToClipboard();
         }
      });
      menu.add(item);
      menu.show(tree, x, y);
   }

   /**
    * Return true if the panel shows the cntent of the left tree.
    *
    * @return true if the panel shows the cntent of the left tree
    */
   public boolean isLeftPanel() {
      return isLeftPanel;
   }

   /**
    * Temporarily disable the selection in the tree.
    */
   void disableTreeSelection() {
      this.disableTreeSelection = true;
   }

   /**
    * Re-enable the selection in the tree after it has been temporarily disabled.
    */
   void enableTreeSelection() {
      this.disableTreeSelection = false;
   }

   private void expandNode(NodeTreeRep treeRep) {
      if (!disableTreeSelection) {
         diffWindow.expandNode(treeRep, this);
      }
   }

   private void collapseNode(NodeTreeRep treeRep) {
      if (!disableTreeSelection) {
         diffWindow.collapseNode(treeRep, this);
      }
   }

   private void selectNode(NodeTreeRep treeRep) {
      if (!disableTreeSelection) {
         diffWindow.selectNode(treeRep, this);
      }
   }

   /**
    * Set the divider location between the tree and the properties table.
    *
    * @param location the divider location
    */
   public void setDividerLocation(int location) {
      splitPane.setDividerLocation(location);
   }

   /**
    * Return the divider location between the tree and the properties table.
    *
    * @return the divider location
    */
   public int getDividerLocation() {
      return splitPane.getDividerLocation();
   }

   private void fireDividerLocationModification() {
      diffWindow.setFilePanelDivider(this);
   }

   /**
    * Return the root node of the tree.
    *
    * @return the root node
    */
   public DefaultMutableTreeNode getRoot() {
      return root;
   }

   /**
    * Return the associated file.
    *
    * @return the file
    */
   public File getFile() {
      return file;
   }

   /**
    * Set the associated file.
    *
    * @param file the file
    */
   public void setFile(File file) {
      this.file = file;
      tf.setText(file.getAbsolutePath());
   }

   /**
    * Clear the content of the tree.
    */
   public void clear() {
      root.removeAllChildren();
      treeModel.reload(root);
      this.revalidate();
   }

   /**
    * Force expand the tree for a node associated to a wrapper.
    *
    * @param treeRep the wrapper
    */
   public void forceExpand(NodeTreeRep treeRep) {
      DefaultMutableTreeNode treeNode = treeRep.getTreeNode();
      TreePath path = new TreePath(treeNode.getPath());
      tree.expandPath(path);
   }

   /**
    * Force collapse the tree for a node associated to a wrapper.
    *
    * @param treeRep the wrapper
    */
   public void forceCollapse(NodeTreeRep treeRep) {
      DefaultMutableTreeNode treeNode = treeRep.getTreeNode();
      TreePath path = new TreePath(treeNode.getPath());
      tree.collapsePath(path);
   }

   /**
    * Force select a node in the tree associated to a wrapper.
    *
    * @param treeRep the wrapper
    */
   public void forceSelection(NodeTreeRep treeRep) {
      DefaultMutableTreeNode treeNode = treeRep.getTreeNode();
      TreePath path = new TreePath(treeNode.getPath());
      tree.setSelectionPath(path);
      tree.scrollPathToVisible(path);
      tree.makeVisible(path);
      propertiesPanel.setNode(treeRep.getNode(), treeRep.getOtherNode());
   }
   
   private void showInFile() {
      if (selectionRep != null) {
         XMLNode leftNode = selectionRep.getNode();
         XMLNode rightNode = selectionRep.getOtherNode();
         int leftLine = -1;
         if (leftNode != null) {
            leftLine = leftNode.getLineNumber();
         }
         int rightLine = -1;
         if (rightNode != null) {
            rightLine = rightNode.getLineNumber();
         } 
         XMLViewer viewer = XMLViewer.getInstance();
         File leftFile = viewer.getLeftFile();
         File rightFile = viewer.getRightFile();
         viewer.setVisible(true);
         boolean leftDifferent = (leftFile == null) || !leftFile.equals(diffWindow.getLeftFile());
         boolean rightDifferent = (rightFile == null) || !rightFile.equals(diffWindow.getRightFile());
         if (leftDifferent || rightDifferent) {
            viewer.load(compModel, leftLine, rightLine);
         } else {
            viewer.scrollTo(leftLine, rightLine);
         }
      }
   }

   private void copyToClipboard() {
      Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
      if (selectionRep != null) {
         String value = selectionRep.getNodeName();
         StringSelection stringSelection = new StringSelection(value);
         clpbrd.setContents(stringSelection, null);
      }
   }

   /**
    * Load a comparison model.
    *
    * @param compModel the model
    */
   public void load(ComparisonModel compModel) {
      selectionRep = null;
      this.compModel = compModel;
      root = compModel.getComparedTreeNode(isLeftPanel);
      treeModel = compModel.getTreeModel(isLeftPanel);
      tree = new JTree(treeModel);
      compModel.setTree(tree, isLeftPanel);
      tree.setRootVisible(true);
      tree.setExpandsSelectedPaths(true);
      tree.setCellRenderer(new XMLTreeCellRenderer(isLeftPanel));
      this.setLayout(new BorderLayout());
      JPanel tfPanel = new JPanel();
      tfPanel.setLayout(new BoxLayout(tfPanel, BoxLayout.X_AXIS));
      tf.setEditable(false);
      tfPanel.add(tf);
      tfPanel.add(Box.createHorizontalGlue());
      this.add(tfPanel, BorderLayout.NORTH);
      Dimension prefSize = tf.getPreferredSize();
      Dimension dim = new Dimension(prefSize.width, LABEL_HEIGHT);
      tf.setPreferredSize(dim);
      tf.setMaximumSize(dim);
      this.add(Box.createVerticalStrut(5));
      tree.expandRow(0);
      JPanel treePanel = new JPanel();
      treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.X_AXIS));
      treePanel.add(new JScrollPane(tree));
      treePanel.add(Box.createHorizontalGlue());

      splitPane.setTopComponent(treePanel);
      splitPane.setBottomComponent(propertiesPanel);
      this.add(splitPane, BorderLayout.CENTER);

      ToolTipManager toolTipmanager = ToolTipManager.sharedInstance();
      toolTipmanager.registerComponent(tree);

      splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
         @Override
         public void propertyChange(PropertyChangeEvent pce) {
            fireDividerLocationModification();
         }
      });
      setupTree();
      this.revalidate();
      tree.expandRow(0);
   }
}
