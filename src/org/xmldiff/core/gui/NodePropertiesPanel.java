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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.mdiutil.xml.tree.SortableQName;
import org.mdiutil.xml.tree.XMLNode;

/**
 * Shows the properties for an XML node.
 *
 * @version 0.6
 */
public class NodePropertiesPanel extends JPanel {
   private XMLNode xmlNode;
   private DefaultTableModel propertiesTableModel = new UneditableTableModel();
   private final JTable propertiesTable = new PropertiesTable(propertiesTableModel);
   private DefaultTableModel cdataTableModel = new UneditableTableModel();
   private final JTable cdataTable = new PropertiesTable(cdataTableModel);
   private final JLabel cdataLabel = new JLabel("CDATA");
   private int selectedRow = -1;
   private final XMLPropertiesRenderer propertiesRenderer = new XMLPropertiesRenderer();

   public NodePropertiesPanel() {
      super();
      setup();
   }

   private void setup() {
      // properties table
      propertiesTableModel.addColumn("Name");
      propertiesTableModel.addColumn("Value");
      TableColumnModel tcpModel = propertiesTable.getColumnModel();
      TableColumn tc = tcpModel.getColumn(1);
      tc.setCellRenderer(propertiesRenderer);

      // CDATA table
      cdataTableModel.addColumn("Value");
      TableColumnModel tccModel = cdataTable.getColumnModel();
      TableColumn tcc = tccModel.getColumn(0);
      tcc.setCellRenderer(propertiesRenderer);

      this.setLayout(new BorderLayout());
      JPanel allPanel = new JPanel();
      allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.Y_AXIS));
      allPanel.add(propertiesTable);
      allPanel.add(Box.createVerticalStrut(5));
      allPanel.add(cdataLabel);
      allPanel.add(Box.createVerticalStrut(5));
      allPanel.add(cdataTable);
      cdataTable.setVisible(false);
      cdataLabel.setVisible(false);

      this.add(new JScrollPane(allPanel), BorderLayout.CENTER);

      propertiesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      propertiesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(ListSelectionEvent e) {
            selectedRow = e.getFirstIndex();
         }
      });
      propertiesTable.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (selectedRow != -1 && e.getButton() == MouseEvent.BUTTON3) {
               showPopupMenu(e.getX(), e.getY());
            }
         }
      });
   }

   private void showPopupMenu(int x, int y) {
      JPopupMenu menu = new JPopupMenu();
      JMenuItem item = new JMenuItem("Copy to Clipboard");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            copyToClipboard();
         }
      });
      menu.add(item);
      menu.show(propertiesTable, x, y);
   }

   /**
    * Return the XML node which is used for the properties (can be null).
    *
    * @return the XML node
    */
   public XMLNode getXMLNode() {
      return xmlNode;
   }

   /**
    * Set the XML node which is used for the properties.
    *
    * @param node the XML node (can be null)
    * @param otherNode the other node (can be null)
    */
   public void setNode(XMLNode node, XMLNode otherNode) {
      this.xmlNode = node;
      // properties table
      propertiesTableModel = new UneditableTableModel();
      propertiesTableModel.addColumn("Name");
      propertiesTableModel.addColumn("Value");
      if (node != null) {
         Iterator<Entry<SortableQName, String>> it = node.getAttributes().entrySet().iterator();
         while (it.hasNext()) {
            Entry<SortableQName, String> entry = it.next();
            Vector v = new Vector<>();
            String attrName = entry.getKey().toString();
            String attrValue = entry.getValue();
            v.add(attrName);
            boolean isUpdated;
            if (otherNode == null) {
               isUpdated = true;
            } else if (!otherNode.hasAttribute(attrName)) {
               isUpdated = true;
            } else if (!attrValue.equals(otherNode.getAttributeValue(attrName))) {
               isUpdated = true;
            } else {
               isUpdated = false;
            }
            v.add(new EntryValue(attrValue, isUpdated));
            propertiesTableModel.addRow(v);
         }
      }
      this.selectedRow = -1;
      propertiesTable.setModel(propertiesTableModel);
      TableColumnModel tcModel = propertiesTable.getColumnModel();
      TableColumn tc = tcModel.getColumn(1);
      tc.setCellRenderer(propertiesRenderer);

      // CDATA table
      cdataTableModel = new UneditableTableModel();
      cdataTableModel.addColumn("Value");
      if (node != null) {
         cdataTable.setVisible(false);
         cdataLabel.setVisible(false);
         if (node.hasCDATA()) {
            cdataTable.setVisible(true);
            cdataLabel.setVisible(true);
         }
         String cdata = node.getCDATAValueAsString();
         Vector v = new Vector<>();
         boolean isUpdated;
         if (otherNode == null) {
            isUpdated = true;
         } else if (node.hasCDATA()) {
            if (!otherNode.hasCDATA()) {
               isUpdated = true;
            } else if (!cdata.equals(otherNode.getCDATAValueAsString())) {
               isUpdated = true;
            } else {
               isUpdated = false;
            }
         } else if (otherNode.hasCDATA()) {
            isUpdated = true;
         } else {
            isUpdated = false;
         }
         v.add(new EntryValue(cdata, isUpdated));
         cdataTableModel.addRow(v);
      } else {
         cdataTable.setVisible(false);
         cdataLabel.setVisible(false);
      }
      cdataTable.setModel(cdataTableModel);
      TableColumnModel tccModel = cdataTable.getColumnModel();
      TableColumn tcc = tccModel.getColumn(0);
      tcc.setCellRenderer(propertiesRenderer);
   }

   private void copyToClipboard() {
      Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
      if (selectedRow != -1) {
         String value = propertiesTableModel.getValueAt(selectedRow, 1).toString();
         StringSelection stringSelection = new StringSelection(value);
         clpbrd.setContents(stringSelection, null);
      }
   }

   /**
    * Represents one value in the properties table.
    */
   public static class EntryValue {
      private final String value;
      private final boolean isUpdated;

      private EntryValue(String value, boolean isUpdated) {
         this.value = value;
         this.isUpdated = isUpdated;
      }

      public boolean isUpdated() {
         return isUpdated;
      }

      @Override
      public String toString() {
         return value;
      }
   }
}
