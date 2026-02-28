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

import javax.swing.table.DefaultTableModel;

/**
 * A DefaultTableModel that is not editable.
 *
 * @since 0.1
 */
public class UneditableTableModel extends DefaultTableModel {

   public UneditableTableModel() {
      super();
   }

   /**
    * Return true if a cell is editable.
    *
    * @param row the row
    * @param col the column
    * @return false
    */
   @Override
   public boolean isCellEditable(int row, int col) {
      return false;
   }
}
