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
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.DesktopUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

class PluginsPanel {

	private final DefaultListModel<Plugin> tmodel = new DefaultListModel<>();

	PluginsPanel(PreferencesDialog preferencesDialog) {
		preferencesDialog.model.addElement("Manage plugins");

		JList<Plugin> plugins = new JList<>(tmodel);
		plugins.setCellRenderer(new PluginsListCellRenderer());

		JPanel sectionPanel = new JPanel(new BorderLayout(15, 15));

		sectionPanel.add("North", new JLabel("<html><font style=\"font-size: 16px;\">Manage plugins"
				+ "</big><br><font style=\"font-size: 9px; color: gray;\">"
				+ "Here you can load plugins that extend functionality or add new generator types.<br>"
				+ "It is recommended to restart MCreator after adding new plugins!"));
		sectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

		JToolBar opts = new JToolBar();
		opts.setFloatable(false);

		JButton add = new JButton("Load plugin... ");
		add.setIcon(UIRES.get("16px.add.gif"));
		opts.add(add);
		opts.add(new JEmptyBox(5, 5));

		add.addActionListener(e -> {
			File[] files = FileDialogs.getMultiOpenDialog(preferencesDialog, new String[] { ".zip" });
			if (files != null && files.length > 0) {
				Arrays.stream(files).forEach(f -> FileIO
						.copyFile(f, new File(UserFolderManager.getFileFromUserFolder("plugins"), f.getName())));
				PluginLoader.initInstance(); // reload plugin loader
				reloadPluginList();
			}
		});

		JButton explorePlugins = new JButton("Explore plugins");
		explorePlugins.setIcon(UIRES.get("16px.search"));
		opts.add(explorePlugins);
		opts.add(new JEmptyBox(5, 5));

		explorePlugins.addActionListener(e -> DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/plugins"));

		reloadPluginList();

		sectionPanel.add("Center", PanelUtils.northAndCenterElement(opts, new JScrollPane(plugins), 5, 5));

		preferencesDialog.preferences.add(PanelUtils.pullElementUp(sectionPanel), "Manage plugins");
	}

	private void reloadPluginList() {
		tmodel.removeAllElements();
		PluginLoader.INSTANCE.getPlugins().stream().sorted(Comparator.comparing(Plugin::getWeight).reversed())
				.sorted(Comparator.comparing(Plugin::isBuiltin).reversed()).forEach(tmodel::addElement);
	}

	static class PluginsListCellRenderer extends JLabel implements ListCellRenderer<Plugin> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Plugin> list, Plugin value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));

			setOpaque(false);

			if (value.isBuiltin()) {
				setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
			} else {
				setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			}

			ComponentUtils.deriveFont(this, 12);

			String loaded;
			if(value.isLoaded()){
				loaded = "true";
			} else {
				loaded = "false";
			}

			if (value.getInfo().getAuthor() != null)
				setText("<html>" + value.getInfo().getName() + "<br><small>ID: " + value.getID() + ", version: " + value
						.getPluginVersion() + ", author: " + value.getInfo().getAuthor() + ", loaded: " +
						(value.isLoaded() ?  loaded :  loaded));
			else
				setText("<html>" + value.getInfo().getName() + "<br><small>ID: " + value.getID() + ", version: " + value
						.getPluginVersion() + ", loaded: " + (value.isLoaded() ? loaded :  loaded));

			setToolTipText(value.getInfo().getDescription());
			setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
			return this;
		}
	}

}
