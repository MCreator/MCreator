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
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

class ModElementCodeDropdown extends JPopupMenu {
	private final MCreator mcreator;

	ModElementCodeDropdown(MCreator mcreator, List<GeneratorTemplate> modElementFiles,
			List<GeneratorTemplate> modElementGlobalFiles, List<GeneratorTemplatesList> modElementListFiles) {
		this.mcreator = mcreator;
		setBorder(BorderFactory.createEmptyBorder());
		setBackground((Theme.current().getAltBackgroundColor()).darker());

		int entryCounter = 0;

		// add regular files to the dropdown (if any)
		for (GeneratorTemplate modElementFile : modElementFiles) {
			if (!modElementFile.isHidden()) {
				add(modElementFileMenuItem(modElementFile));
				entryCounter++;
			}
		}

		// add global files to the dropdown (if any)
		boolean separatorPlaceFlag = entryCounter > 0;
		for (GeneratorTemplate modElementGlobalFile : modElementGlobalFiles) {
			if (!modElementGlobalFile.isHidden()) {
				// if there were entries before, add separator on the top, then prevent this from happening again by setting hasEntriesAbove to false
				if (separatorPlaceFlag) {
					addSeparator();
					separatorPlaceFlag = false;
				}

				add(modElementFileMenuItem(modElementGlobalFile));
				entryCounter++;
			}
		}

		// add list files to the dropdown (if any)
		separatorPlaceFlag = entryCounter > 0;
		for (GeneratorTemplatesList list : modElementListFiles) {
			if (!list.templates().isEmpty()) {
				JMenu listMenu = new JMenu(list.groupName());
				listMenu.setIcon(UIRES.get("16px.list"));
				listMenu.setBackground((Theme.current().getAltBackgroundColor()).darker());
				listMenu.setForeground(Theme.current().getForegroundColor());
				listMenu.setIconTextGap(8);
				listMenu.setBorder(BorderFactory.createEmptyBorder(10, 8, 11, 0));

				for (int i = 0; i < list.listData().size(); i++) {
					if (i > 0 && listMenu.getMenuComponents().length > 0 && !list.templates().get(i).isEmpty())
						listMenu.addSeparator(); // separate files generated for different list items

					list.templates().get(i).stream().filter(e -> !e.isHidden()).map(this::modElementFileMenuItem)
							.forEach(listMenu::add);
				}

				if (Arrays.stream(listMenu.getMenuComponents()).anyMatch(e -> e instanceof JMenuItem)) {
					// if there were entries before, add separator on the top, then prevent this from happening again by setting hasEntriesAbove to false
					if (separatorPlaceFlag) {
						addSeparator();
						separatorPlaceFlag = false;
					}

					add(listMenu);
				}
			}
		}
	}

	private JMenuItem modElementFileMenuItem(GeneratorTemplate template) {
		JMenuItem item = new JMenuItem(
				"<html>" + template.getFile().getName() + "<br><small color=#666666>" + mcreator.getWorkspace()
						.getFolderManager().getPathInWorkspace(template.getFile()));
		item.setIcon(FileIcons.getIconForFile(template.getFile()));
		item.setBackground((Theme.current().getAltBackgroundColor()).darker());
		item.setForeground(Theme.current().getForegroundColor());
		item.setIconTextGap(8);
		item.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 0));
		item.addActionListener(e -> ProjectFileOpener.openCodeFile(mcreator, template.getFile()));
		return item;
	}
}
