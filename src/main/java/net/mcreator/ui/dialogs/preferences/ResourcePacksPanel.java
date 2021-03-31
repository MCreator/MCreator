/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.ui.dialogs.preferences;

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.resourcepacks.ResourcePack;
import net.mcreator.resourcepacks.ResourcePackLoader;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;

public class ResourcePacksPanel {

	private final DefaultListModel<ResourcePack> tmodel = new DefaultListModel<>();
	private final JComboBox<String> packIDs;

	public ResourcePacksPanel(PreferencesDialog dialog) {
		dialog.model.addElement(L10N.t("dialog.preferences.page_resource_packs"));
		JList<ResourcePack> packs = new JList<>(tmodel);
		packs.setCellRenderer(new ResourcePacksPanel.ResourcePacksListCellRenderer());

		JPanel top = new JPanel(new GridLayout(1, 2));
		top.add(new JLabel("bonjour"));
		packIDs = new JComboBox<>(ResourcePackLoader.getIDs().toArray(new String[0]));
		packIDs.setSelectedItem(PreferencesManager.PREFERENCES.hidden.resourcePack);
		packIDs.addActionListener(e -> dialog.apply.setEnabled(true));
		top.add(packIDs);

		JPanel sectionPanel = new JPanel(new BorderLayout(15, 15));

		reloadResourcePacksList();

		sectionPanel.add("Center", PanelUtils.northAndCenterElement(top, new JScrollPane(packs), 5, 5));

		dialog.preferences.add(sectionPanel, L10N.t("dialog.preferences.page_resource_packs"));
	}

	private void reloadResourcePacksList() {
		tmodel.removeAllElements();
		ResourcePackLoader.getResourcePacks().stream().sorted(Comparator.comparing(ResourcePack::getID))
				.forEach(tmodel::addElement);
	}

	public String getResourcePack() {
		return (String) packIDs.getSelectedItem();
	}

	static class ResourcePacksListCellRenderer extends JLabel implements ListCellRenderer<ResourcePack> {
		@Override
		public Component getListCellRendererComponent(JList<? extends ResourcePack> list, ResourcePack value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));

			setOpaque(false);

			ComponentUtils.deriveFont(this, 12);

			String text = "<html><b>" + value.getName() + "</b>";
			if (value.getDescription() != null)
				text += "<br>" + value.getDescription();
			text += "<br><small>ID: " + value.getID();
			if (value.getVersion() != null)
				text += ", version: " + value.getVersion();
			if (value.getCredits() != null)
				text += ", credits: " + value.getCredits();
			setText(text);

			setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
			return this;
		}
	}
}
