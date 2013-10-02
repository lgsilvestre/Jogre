/*
 * JOGRE (Java Online Gaming Real-time Engine) - Spades
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
 * http://jogre.sourceforge.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jogre.spades.client;

import javax.swing.table.DefaultTableModel;

/**
 * Spades score and bags table model that extends the default table model. This
 * class was mainly created to avoid cells of the table to be editable.
 *
 * @author Garrett Lehman (Gman)
 * @version Alpha 0.2.3
 */
public class SpadesScoreAndBagsTableModel extends DefaultTableModel {

	/**
	 * Default contructor
	 *
	 * @param data Initial data to be placed into table
	 * @param columnNames Initial columns to be placed into table
	 */
	public SpadesScoreAndBagsTableModel(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
