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

package net.mcreator.ui.dialogs;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ITabContainedElement;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.impl.workspace.RegenerateCodeAction;
import net.mcreator.ui.component.ReordarableListTransferHandler;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.SmallIconModListRender;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ElementOrderEditor {

	public static void openElementOrderEditor(MCreator mcreator) {
		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

		JPanel top = new JPanel(new BorderLayout());

		top.add("West", L10N.label("dialog.element_order.instructions"));

		mainPanel.add("North", top);

		LinkedHashMap<String, DefaultListModel<ModElement>> tabEditors = new LinkedHashMap<>();
		JTabbedPane tabs = new JTabbedPane();
		tabs.setBorder(BorderFactory.createEmptyBorder());
		tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
			@Override protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
			}
		});

		mcreator.getWorkspace().getModElements().stream().sorted(Comparator.comparingInt(ModElement::getSortID))
				.forEach(modElement -> {
					GeneratableElement generatableElement = modElement.getGeneratableElement();
					if (generatableElement instanceof ITabContainedElement) {
						ITabContainedElement element = (ITabContainedElement) generatableElement;
						if (element.getCreativeTab() == null || element.getCreativeTab().getUnmappedValue()
								.equals("No creative tab entry")) {
							return;
						}

						if (tabEditors.get(element.getCreativeTab().getUnmappedValue()) == null) {
							DefaultListModel<ModElement> model = new DefaultListModel<ModElement>() {
								@Override public void add(int idx, ModElement element) {
									super.add(idx, element);
									element.setWorkspace(mcreator.getWorkspace());
									element.reinit();
								}
							};
							JList<ModElement> list = new JList<>(model);
							list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
							list.setVisibleRowCount(-1);
							list.setTransferHandler(new ReordarableListTransferHandler());
							list.setDropMode(DropMode.INSERT);
							list.setDragEnabled(true);
							list.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

							list.setCellRenderer(new SmallIconModListRender(false));
							tabs.addTab(element.getCreativeTab().getUnmappedValue(), new JScrollPane(list));

							tabEditors.put(element.getCreativeTab().getUnmappedValue(), model);
						}
						tabEditors.get(element.getCreativeTab().getUnmappedValue()).addElement(modElement);
					}
				});

		mainPanel.add("Center", tabs);
		mainPanel.setPreferredSize(new Dimension(748, 320));

		int resultval = JOptionPane.showOptionDialog(mcreator, mainPanel, L10N.t("dialog.element_order.editor_title"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] { "Save layout", "Cancel" },
				"");

		if (resultval == 0) {
			int currid = 1;

			Map<ModElement, Integer> idmap = new HashMap<>();
			for (Map.Entry<String, DefaultListModel<ModElement>> entry : tabEditors.entrySet()) {
				for (int i = 0; i < entry.getValue().size(); i++) {
					ModElement element = entry.getValue().getElementAt(i);
					idmap.put(element, currid);
					currid++;
				}
			}

			for (ModElement element : mcreator.getWorkspace().getModElements()) {
				if (idmap.get(element) != null)
					element.setSortID(idmap.get(element));
				else
					element.setSortID(currid++);
			}

			JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.element_order.change_message"),
					L10N.t("dialog.element_order.change_title"), JOptionPane.INFORMATION_MESSAGE);

			RegenerateCodeAction.regenerateCode(mcreator, true, false);
		}
	}

}
