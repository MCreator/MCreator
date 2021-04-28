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
import net.mcreator.themes.Theme;
import net.mcreator.themes.ThemeLoader;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;

public class ThemesPanel {

	private final DefaultListModel<Theme> tmodel = new DefaultListModel<>();
	private final JComboBox<String> packIDs;

	public ThemesPanel(PreferencesDialog dialog) {
		dialog.model.addElement("Themes");
		JList<Theme> packs = new JList<>(tmodel);
		packs.setCellRenderer(new ThemesPanel.ResourcePacksListCellRenderer());

		JPanel sectionPanel = new JPanel(new BorderLayout(15, 15));

		sectionPanel.add("North", L10N.label("dialog.preferences.themes"));
		sectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

		JPanel top = new JPanel(new BorderLayout());

		String themeName = L10N.t("preferences.resource_packs.select_theme");
		String themeDescription = L10N.t("preferences.resource_packs.select_theme.description");
		top.add("West", L10N.label("dialog.preferences.entry_description", themeName, themeDescription));

		packIDs = new JComboBox<>(ThemeLoader.getIDs().toArray(new String[0]));
		packIDs.setPreferredSize(new Dimension(250, 0));
		packIDs.setSelectedItem(PreferencesManager.PREFERENCES.hidden.uiTheme);
		packIDs.addActionListener(e -> dialog.markChanged());
		top.add("East", packIDs);

		reloadResourcePacksList();

		sectionPanel.add("Center", PanelUtils.northAndCenterElement(top,
				PanelUtils.northAndCenterElement(L10N.label("dialog.preferences.themes.list"), new JScrollPane(packs)),
				5, 5));

		dialog.preferences.add(sectionPanel, "Themes");
	}

	private void reloadResourcePacksList() {
		tmodel.removeAllElements();
		ThemeLoader.getThemes().stream().sorted(Comparator.comparing(Theme::getID)).forEach(tmodel::addElement);
	}

	public String getResourcePack() {
		return (String) packIDs.getSelectedItem();
	}

	static class ResourcePacksListCellRenderer extends JLabel implements ListCellRenderer<Theme> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Theme> list, Theme value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));

			setOpaque(false);

			ComponentUtils.deriveFont(this, 12);

			String text = "<html>" + value.getName();
			if (value.getColorScheme() != null)
				text += "<br><small>Color theme: " + value.getColorScheme().getID();
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
