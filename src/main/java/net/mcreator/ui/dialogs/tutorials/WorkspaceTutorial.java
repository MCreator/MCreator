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

package net.mcreator.ui.dialogs.tutorials;

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.awt.*;

public class WorkspaceTutorial {

	public static void newDialog(Window window, String text){
		if(PreferencesManager.PREFERENCES.ui.tutorials) {
			MCreatorDialog dialog = new MCreatorDialog(window, "Tutorial", true);

			dialog.setLayout(new BorderLayout(10, 10));

			dialog.add("Center", panelWithLabel(text));
			JButton next = new JButton("Finish");
			byte b = 0;
			next.addActionListener(e -> {
				dialog.setVisible(false);
			});
			dialog.add("South", next);

			dialog.setSize(700, 250);
			dialog.setLocationRelativeTo(window);
			dialog.setVisible(true);
		}
	}

	private static JPanel panelWithLabel(String key, Object... parameter){
		JPanel panel = new JPanel();
		panel.add(L10N.label(key, parameter));
		panel.setVisible(true);
		return panel;
	}
}
