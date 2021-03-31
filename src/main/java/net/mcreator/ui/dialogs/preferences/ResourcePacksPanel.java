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
		dialog.model.addElement("Resource packs");
		JList<ResourcePack> packs = new JList<>(tmodel);
		packs.setCellRenderer(new ResourcePacksPanel.ResourcePacksListCellRenderer());

		JPanel sectionPanel = new JPanel(new BorderLayout(15, 15));

		sectionPanel.add("North", L10N.label("dialog.preferences.resource_packs"));
		sectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

		JPanel top = new JPanel(new BorderLayout());

		String name = L10N.t("preferences.resource_packs.select_resource_pack");
		String description = L10N.t("preferences.resource_packs.select_resource_pack.description");
		top.add("West", L10N.label("dialog.preferences.entry_description", name, description));

		packIDs = new JComboBox<>(ResourcePackLoader.getIDs().toArray(new String[0]));
		packIDs.setPreferredSize(new Dimension(250, 0));
		packIDs.setSelectedItem(PreferencesManager.PREFERENCES.hidden.resourcePack);
		packIDs.addActionListener(e -> dialog.apply.setEnabled(true));
		top.add("East", packIDs);

		reloadResourcePacksList();

		sectionPanel.add("Center", PanelUtils.northAndCenterElement(top, PanelUtils
						.northAndCenterElement(L10N.label("dialog.preferences.resource_packs.list"), new JScrollPane(packs)), 5,
				5));

		dialog.preferences.add(sectionPanel, "Resource packs");
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

			String text = "<html>" + value.getName();
			if (value.getDescription() != null)
				text += "<br><i>" + value.getDescription() + "</i>";
			text += "<br><small>ID: " + value.getID();
			if (value.getVersion() != null)
				text += ", version: " + value.getVersion();
			if (value.getCredits() != null)
				text += ", credits: " + value.getCredits();
			setText(text);
			setIcon(value.getIcon());

			setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
			return this;
		}
	}
}
