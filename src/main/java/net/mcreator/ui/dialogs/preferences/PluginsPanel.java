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

package net.mcreator.ui.dialogs.preferences;

import net.mcreator.io.FileIO;
import net.mcreator.io.UserFolderManager;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.DesktopUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

class PluginsPanel {

	private final DefaultListModel<Plugin> tmodel = new DefaultListModel<>();

	PluginsPanel(PreferencesDialog preferencesDialog) {
		preferencesDialog.model.addElement(L10N.t("dialog.preferences.page_plugins"));

		JList<Plugin> plugins = new JList<>(tmodel);
		plugins.setCellRenderer(new PluginsListCellRenderer());

		JPanel sectionPanel = new JPanel(new BorderLayout(15, 15));

		sectionPanel.add("North", L10N.label("dialog.preferences.manage_plugins"));
		sectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

		JPanel opts = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

		JButton add = L10N.button("dialog.preferences.load_plugins");
		add.setIcon(UIRES.get("16px.add"));
		opts.add(add);
		opts.add(new JEmptyBox(5, 5));

		add.addActionListener(e -> {
			File[] files = FileDialogs.getMultiOpenDialog(preferencesDialog, new String[] { ".zip" });
			if (files != null && files.length > 0) {
				Arrays.stream(files).forEach(f -> FileIO.copyFile(f,
						new File(UserFolderManager.getFileFromUserFolder("plugins"), f.getName())));
				PluginLoader.initInstance(); // reload plugin loader
				reloadPluginList();
			}
		});

		JButton explorePlugins = L10N.button("dialog.preferences.explore_plugins");
		explorePlugins.setIcon(UIRES.get("16px.search"));
		opts.add(explorePlugins);
		opts.add(new JEmptyBox(5, 5));

		explorePlugins.addActionListener(e -> DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/plugins"));

		reloadPluginList();

		JButton openPluginFolder = L10N.button("dialog.preferences.open_folder",
				L10N.t("dialog.preferences.plugins").toLowerCase());
		openPluginFolder.setIcon(UIRES.get("16px.open"));
		opts.add(openPluginFolder);
		opts.add(new JEmptyBox(5, 5));

		JCheckBox box = L10N.checkbox("dialog.preferences.java_plugins");
		box.setSelected(PreferencesManager.PREFERENCES.hidden.enableJavaPlugins.get());

		box.addActionListener(e -> PreferencesManager.PREFERENCES.hidden.enableJavaPlugins.set(box.isSelected()));

		openPluginFolder.addActionListener(
				e -> DesktopUtils.openSafe(UserFolderManager.getFileFromUserFolder("plugins")));

		sectionPanel.add("Center", PanelUtils.northAndCenterElement(PanelUtils.northAndCenterElement(opts, box, 10, 10),
				PanelUtils.northAndCenterElement(L10N.label("dialog.preferences.plugins_list"),
						new JScrollPane(plugins), 3, 3), 10, 10));

		preferencesDialog.preferences.add(sectionPanel, L10N.t("dialog.preferences.page_plugins"));
	}

	private void reloadPluginList() {
		tmodel.removeAllElements();
		PluginLoader.INSTANCE.getPlugins().stream().sorted(Comparator.comparing(Plugin::getWeight).reversed())
				.sorted(Comparator.comparing(Plugin::isBuiltin)).forEach(tmodel::addElement);
	}

	static class PluginsListCellRenderer extends JLabel implements ListCellRenderer<Plugin> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Plugin> list, Plugin value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setBackground(Theme.current().getForegroundColor());

			setOpaque(false);

			if (value.isBuiltin()) {
				setForeground(Theme.current().getAltForegroundColor());
			} else {
				setForeground(Theme.current().getForegroundColor());
			}

			ComponentUtils.deriveFont(this, 12);

			if ((value.getInfo().getAuthor() != null) && (value.getInfo().getName() != null) && (value.getInfo()
					.getCredits().equals("None"))) {
				setText("<html>" + value.getInfo().getName() + "<br><small>ID: " + value.getID() + ", version: "
						+ value.getPluginVersion() + ", author: " + value.getInfo().getAuthor() + ", fully loaded: " + (
						value.isLoaded() ?
								"<html><font color=#a7ed1a>yes</font>" :
								"<html><font color=#f24122>no</font>"));
			} else if (value.getInfo().getAuthor() != null) {
				setText("<html>" + value.getInfo().getName() + "<br><small>ID: " + value.getID() + ", version: "
						+ value.getPluginVersion() + ", author: " + value.getInfo().getAuthor() + ", credit: "
						+ value.getInfo().getCredits() + ", fully loaded: " + (value.isLoaded() ?
						"<html><font color=#a7ed1a>yes</font>" :
						"<html><font color=#f24122>no</font>"));
			} else {
				setText("<html>" + value.getInfo().getName() + "<br><small>ID: " + value.getID() + ", version: "
						+ value.getPluginVersion() + ", fully loaded: " + (value.isLoaded() ?
						"<html><font color=#a7ed1a>yes</font>" :
						"<html><font color=#f24122>no</font>"));
			}

			setToolTipText(value.getInfo().getDescription());
			setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
			return this;
		}
	}

}
