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

package net.mcreator.ui.ide;

import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.laf.renderer.MinecraftCompletionCellRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;

class AutocompleteStyle {

	private static final Logger LOG = LogManager.getLogger("ACS");

	static void installStyle(AutoCompletion ac, RSyntaxTextArea te) {

		ac.setListCellRenderer(new MinecraftCompletionCellRenderer(te));

		ac.addAutoCompletionListener(autoCompletionEvent -> {
			try {
				Class<?> treeNodeClass = Class.forName("org.fife.ui.autocomplete.AutoCompletion");
				Field field = treeNodeClass.getDeclaredField("popupWindow");
				field.setAccessible(true);

				Object autoCompletePopupWindowRaw = field.get(ac);
				Class<?> treeNodeClass2 = Class.forName("org.fife.ui.autocomplete.AutoCompletePopupWindow");
				Field field2 = treeNodeClass2.getDeclaredField("list");
				field2.setAccessible(true);
				JList list = (JList) field2.get(autoCompletePopupWindowRaw);
				list.setForeground(new Color(0xD9D9D9));
				list.setSelectionForeground(Color.white);
				list.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));

				JWindow autoCompletePopupWindow = (JWindow) field.get(ac);
				autoCompletePopupWindow.setOpacity(0.93f);
				((JPanel) autoCompletePopupWindow.getContentPane()).setBorder(null);
				Component[] components = autoCompletePopupWindow.getContentPane().getComponents();
				for (Component com : components) {
					if (com instanceof JComponent)
						((JComponent) com).setBorder(null);
					if (com instanceof JScrollPane) {
						JScrollPane pane = (JScrollPane) com;
						pane.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
						pane.setBorder(BorderFactory
								.createMatteBorder(0, 3, 0, 0, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")));
						pane.getVerticalScrollBar()
								.setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
										(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"),
										pane.getVerticalScrollBar()));
						pane.getHorizontalScrollBar()
								.setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
										(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"),
										pane.getHorizontalScrollBar()));
						pane.getVerticalScrollBar().setPreferredSize(new Dimension(7, 0));
						pane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 7));
						JPanel dummyCorner = new JPanel();
						dummyCorner.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
						pane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, dummyCorner);
					}
				}

				// style the doc window
				Field descWindowField = treeNodeClass2.getDeclaredField("descWindow");
				descWindowField.setAccessible(true);

				JWindow descWindow = (JWindow) descWindowField.get(autoCompletePopupWindowRaw);
				Arrays.stream(descWindow.getContentPane().getComponents()).forEach(component -> {
					if (component instanceof JPanel)
						descWindow.getContentPane().remove(component);
				});

				descWindow.setOpacity(0.85f);
				descWindow.setSize(390, 220);
				((JPanel) descWindow.getContentPane())
						.setBorder(BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT")));

			} catch (ClassNotFoundException | SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e1) {
				LOG.error(e1.getMessage(), e1);
			}
		});

	}

}
