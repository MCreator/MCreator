/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.action;

import javax.swing.*;

public class ActionUtils {

	public static JMenuItem hideableMenu(Action action) {
		JMenuItem item = new JMenuItem(action);
		item.setVisible(action.isEnabled());
		action.addPropertyChangeListener(evt -> {
			if ("enabled".equals(evt.getPropertyName()))
				item.setVisible((boolean) evt.getNewValue());
		});
		return item;
	}

	public static void hideableButton(JButton actionButton) {
		Action action = actionButton.getAction();
		actionButton.setVisible(action.isEnabled());
		action.addPropertyChangeListener(evt -> {
			if ("enabled".equals(evt.getPropertyName()))
				actionButton.setVisible((boolean) evt.getNewValue());
		});
	}

}
