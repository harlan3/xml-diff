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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

/**
 * The renderer for the properties table.
 *
 * @since 0.1
 */
public class XMLPropertiesRenderer extends JTextArea implements TableCellRenderer {

	public XMLPropertiesRenderer() {
		this.setLineWrap(true);
		this.setEditable(false);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		NodePropertiesPanel.EntryValue entryValue = (NodePropertiesPanel.EntryValue) value;
		String text = value.toString();
		setText(text);
		setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
		if (table.getRowHeight(row) < getPreferredSize().height) {
			table.setRowHeight(row, getPreferredSize().height);
		}

		if (entryValue.isUpdated()) {
			setForeground(Color.RED);
		} else {
			setForeground(Color.BLACK);
		}

		return this;
	}
}
