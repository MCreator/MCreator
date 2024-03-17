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
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.laf.themes.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ThemesPanel {

	private final DefaultListModel<Theme> tmodel = new DefaultListModel<>();

	public ThemesPanel(PreferencesDialog dialog) {
		dialog.model.addElement(L10N.t("dialog.preferences.page_themes"));
		JList<Theme> themes = new JList<>(tmodel);
		themes.setCellRenderer(new ThemesListCellRenderer());

		JPanel sectionPanel = new JPanel(new BorderLayout(0, 0));

		JComponent titlebar = L10N.label("dialog.preferences.themes");
		titlebar.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 10));
		sectionPanel.add("North", titlebar);

		JPanel top = new JPanel(new BorderLayout());

		String themeName = L10N.t("preferences.themes.select_theme");
		String themeDescription = L10N.t("preferences.themes.select_theme.description");
		top.add("West", L10N.label("dialog.preferences.entry_description", themeName, themeDescription));

		JComboBox<String> themeIDs = new JComboBox<>(
				ThemeManager.getThemes().stream().map(Theme::getID).toArray(String[]::new));
		themeIDs.setPreferredSize(new Dimension(250, 0));
		themeIDs.setSelectedItem(PreferencesManager.PREFERENCES.hidden.uiTheme.get());
		themeIDs.addActionListener(e -> dialog.markChanged());

		themeIDs.addActionListener(e -> {
			if (themeIDs.getSelectedItem() != null)
				PreferencesManager.PREFERENCES.hidden.uiTheme.set((String) themeIDs.getSelectedItem());
		});

		top.add("East", themeIDs);

		reloadThemesList();

		JComponent main = PanelUtils.northAndCenterElement(top,
				PanelUtils.northAndCenterElement(L10N.label("dialog.preferences.themes.list"), new JScrollPane(themes)),
				5, 5);
		main.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		sectionPanel.add("Center", main);

		dialog.preferences.add(sectionPanel, L10N.t("dialog.preferences.page_themes"));
	}

	private void reloadThemesList() {
		tmodel.removeAllElements();
		ThemeManager.getThemes().stream().sorted(Comparator.comparing(Theme::getID)).forEach(tmodel::addElement);
	}

	static class ThemesListCellRenderer extends JPanel implements ListCellRenderer<Theme> {

		private final JLabel name = new JLabel();
		private final JLabel description = new JLabel();
		private final JLabel icon = new JLabel();

		public ThemesListCellRenderer() {
			setLayout(new BorderLayout(8, 0));

			add("West", icon);

			JComponent text = PanelUtils.northAndCenterElement(name, description, 0, 2);
			text.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
			add("Center", text);

			ComponentUtils.deriveFont(name, 14);
			ComponentUtils.deriveFont(description, 11);

			setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
			setOpaque(false);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Theme> list, Theme value, int index,
				boolean isSelected, boolean cellHasFocus) {
			name.setText(value.getName());

			List<String> descriptors = new ArrayList<>();
			if (value.getDescription() != null)
				descriptors.add(value.getDescription());
			if (value.getVersion() != null)
				descriptors.add("Version: " + value.getVersion());
			if (value.getCredits() != null)
				descriptors.add("Credits: " + value.getCredits());

			description.setText(String.join(", ", descriptors));

			icon.setIcon(value.getIcon());

			return this;
		}
	}
}
