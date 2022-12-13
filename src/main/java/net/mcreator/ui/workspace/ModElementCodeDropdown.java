/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.ui.workspace;

import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.generator.GeneratorTemplatesList;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.ide.ProjectFileOpener;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.FileIcons;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

class ModElementCodeDropdown extends JPopupMenu {
	private final MCreator mcreator;

	ModElementCodeDropdown(MCreator mcreator, List<GeneratorTemplate> modElementFiles,
			List<GeneratorTemplate> modElementGlobalFiles, List<GeneratorTemplatesList> modElementListFiles) {
		this.mcreator = mcreator;
		setBorder(BorderFactory.createEmptyBorder());
		setBackground(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker());

		// add regular files to the dropdown
		for (GeneratorTemplate modElementFile : modElementFiles)
			add(newModElementFileMenuItem(modElementFile.getFile()));

		// add global files to the dropdown (if any)
		if (modElementGlobalFiles.size() > 0) {
			if (modElementFiles.size() > 0)
				addSeparator();

			for (GeneratorTemplate modElementGlobalFile : modElementGlobalFiles)
				add(newModElementFileMenuItem(modElementGlobalFile.getFile()));
		}

		// add list files to the dropdown (if any)
		if (modElementListFiles.size() > 0) {
			if (modElementFiles.size() + modElementGlobalFiles.size() > 0)
				addSeparator();

			for (GeneratorTemplatesList fileList : modElementListFiles) {
				if (fileList.templates().size() > 0) {
					JMenu listMenu = new JMenu(fileList.groupName());
					listMenu.setIcon(UIRES.get("16px.list.gif"));
					listMenu.setBackground(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker());
					listMenu.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
					listMenu.setIconTextGap(8);
					listMenu.setBorder(BorderFactory.createEmptyBorder(3, 0, 5, 3));

					int listFilesFound = 0;
					for (int i = 0; i < fileList.listData().size(); i++) {
						if (i > 0) // separate files generated for different list items
							listMenu.addSeparator();

						for (GeneratorTemplate modElementListFile : fileList.templates().keySet()) {
							if (fileList.templates().get(modElementListFile).get(i)) {
								listFilesFound++;
								File indexedFile = fileList.processTokens(modElementListFile, i);
								listMenu.add(newModElementFileMenuItem(indexedFile));
							}
						}
					}

					if (listFilesFound > 0)
						add(listMenu);
				}
			}
		}
	}

	private JMenuItem newModElementFileMenuItem(File template) {
		JMenuItem item = new JMenuItem(
				"<html>" + template.getName() + "<br><small color=#666666>" + mcreator.getWorkspace()
						.getWorkspaceFolder().toPath().relativize(template.toPath()));
		item.setIcon(FileIcons.getIconForFile(template));
		item.setBackground(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker());
		item.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		item.setIconTextGap(8);
		item.setBorder(BorderFactory.createEmptyBorder(3, 0, 5, 3));
		item.addActionListener(e -> ProjectFileOpener.openCodeFile(mcreator, template));
		return item;
	}
}
