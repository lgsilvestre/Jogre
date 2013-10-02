/*
 * JOGRE (Java Online Gaming Real-time Engine) - Ninety Nine
 * Copyright (C) 2006  Richard Walter (rwalter42@yahoo.com)
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
package org.jogre.ninetynine.client;

// An generic interface for an object that can receive alerts from
// the upgrade dialog
public interface IUpgradeDialogAlertee {

	/**
	 * Signal that the user clicked on the don't upgrade button
	 *
	 */
	public void noUpgradeButtonClicked () ;


	/**
	 * Signal that the user clicked on one of the upgrade buttons
	 *
	 * @param	leadPlayer		PlayerID who should be the lead player
	 */
	public void upgradeButtonClicked (int leadPlayer) ;
}
