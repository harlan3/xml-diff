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

import java.awt.Component;
import java.awt.Insets;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

/**
 * The table used for properties.
 *
 * @version 0.6
 */
public class PropertiesTable extends JTable {

   public PropertiesTable() {
      super();
   }

   @Override
   public boolean isCellEditable(int row, int column) {
      return false;
   }
   
   public PropertiesTable(TableModel tablemodel) {
      super(tablemodel);
   }

   @Override
   public void doLayout() {
      TableColumn col = getColumnModel().getColumn(0);
      for (int row = 0; row < getRowCount(); row++) {
         if (col.getCellRenderer() != null) {
            Component c = prepareRenderer(col.getCellRenderer(), row, 0);
            if (c instanceof JTextArea) {
               JTextArea a = (JTextArea) c;
               int h = getPreferredHeight(a) + getIntercellSpacing().height;
               if (getRowHeight(row) != h) {
                  setRowHeight(row, h);
               }
            }
         }
      }
      super.doLayout();
   }

   private int getPreferredHeight(JTextComponent c) {
      Insets insets = c.getInsets();
      View view = c.getUI().getRootView(c).getView(0);
      int preferredHeight = (int) view.getPreferredSpan(View.Y_AXIS);
      return preferredHeight + insets.top + insets.bottom;
   }
}
