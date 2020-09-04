/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.ui.component.util;

import javax.swing.*;

public class KeyStrokes {

	public static void registerKeyStroke(KeyStroke stroke, JComponent component, Action a) {
		registerKeyStroke(stroke, component, a, JComponent.WHEN_FOCUSED);
	}

	public static void registerKeyStroke(KeyStroke stroke, JComponent component, Action a, int condition) {
		InputMap inputMap = component.getInputMap(condition);
		ActionMap actionMap = component.getActionMap();
		inputMap.put(stroke, stroke.hashCode());
		actionMap.put(stroke.hashCode(), a);
		component.setActionMap(actionMap);
	}

}
