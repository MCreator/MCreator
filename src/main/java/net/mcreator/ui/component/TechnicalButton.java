/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.component;

import net.mcreator.ui.component.entries.JEntriesList;

import javax.swing.*;

/**
 * This is a special subtype of regular buttons instances of which used as components of
 * {@link net.mcreator.ui.modgui.ModElementGUI ModElementGUIs} perform technical operations when pressed (for instance,
 * add fresh entries to a {@link JEntriesList JEntriesList} or import missing resources)
 * and should not trigger {@link net.mcreator.ui.modgui.ModElementChangedListener ModElementChangedListeners}.
 */
public class TechnicalButton extends JButton implements ITechnicalComponent {

	public TechnicalButton(Icon icon) {
		super(icon);
	}

	public TechnicalButton(String text) {
		super(text);
	}

}
