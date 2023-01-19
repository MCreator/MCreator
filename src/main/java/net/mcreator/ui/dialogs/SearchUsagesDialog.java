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

package net.mcreator.ui.dialogs;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.types.interfaces.IDataListEntriesProvider;
import net.mcreator.element.types.interfaces.IXMLProvider;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.ModElementManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class SearchUsagesDialog {

	// TODO: Probably add an enum for possible elementType arguments
	public static void open(MCreator mcreator, Object searchTarget, String elementType) {
		String searchQuery;
		if (searchTarget instanceof String str)
			searchQuery = str;
		else if (searchTarget instanceof ModElement modEl)
			searchQuery = new DataListEntry.Custom(modEl).getName();
		else
			return; // can't recognize type of the object we are looking for

		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.search_usages.title", searchTarget), true);

		mcreator.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		Set<ModElement> referencingMods = new HashSet<>();
		for (ModElement modEl : mcreator.getWorkspace().getModElements()) {
			if (modEl.isCodeLocked() || !mcreator.getModElementManager().hasModElementGeneratableElement(modEl))
				continue;

			GeneratableElement genEl = modEl.getGeneratableElement();

			if (genEl instanceof IDataListEntriesProvider dle && dle.getUsedDataListEntries().contains(searchQuery))
				referencingMods.add(modEl);

			if (genEl instanceof IXMLProvider provider && provider.getXML().contains(searchQuery))
				referencingMods.add(modEl);
		}

		mcreator.setCursor(Cursor.getDefaultCursor());

		JList<ModElement> referencingElements = new JList<>(referencingMods.toArray(ModElement[]::new));
		referencingElements.setFixedCellHeight(40);
		referencingElements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		referencingElements.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		referencingElements.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
			JLabel label = L10N.label("dialog.search_usages.list.item", value.getName(),
					value.getType().getReadableName());
			label.setOpaque(true);
			label.setIcon(ModElementManager.getModElementIcon(value/*.getType().getIcon()*/));
			label.setBackground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.GRAY_COLOR") :
					(Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
			label.setForeground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.DARK_ACCENT") :
					(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			return label;
		});

		referencingElements.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					editSelected(referencingElements.getSelectedValue(), mcreator, dialog);
			}
		});
		referencingElements.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					editSelected(referencingElements.getSelectedValue(), mcreator, dialog);
			}
		});

		JScrollPane sp = new JScrollPane(referencingElements);
		sp.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		sp.setPreferredSize(new Dimension(150, 140));

		JButton edit = L10N.button("dialog.search_usages.open_selected");
		JButton close = L10N.button("dialog.search_usages.close");

		edit.addActionListener(e -> {
			if (edit.isEnabled() && !referencingElements.isSelectionEmpty())
				editSelected(referencingElements.getSelectedValue(), mcreator, dialog);
		});
		close.addActionListener(e -> dialog.setVisible(false));

		JPanel list = new JPanel(new BorderLayout(10, 10));
		list.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		if (referencingMods.isEmpty()) {
			list.add("North", L10N.label("dialog.search_usages.list.empty", elementType, searchTarget));
			list.add("South", PanelUtils.centerInPanel(close));
		} else {
			referencingElements.setSelectedIndex(0);
			list.add("North", L10N.label("dialog.search_usages.list", elementType, searchTarget));
			list.add("Center", sp);
			list.add("South", PanelUtils.join(edit, close));
		}

		dialog.getContentPane().add(list);
		dialog.setSize(new Dimension(400, 300));
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);
	}

	private static void editSelected(ModElement element, MCreator mcreator, MCreatorDialog dialog) {
		ModElementGUI<?> gui = element.getType().getModElementGUI(mcreator, element, true);
		if (gui != null) {
			gui.showView();
			dialog.setVisible(false);
		}
	}
}
