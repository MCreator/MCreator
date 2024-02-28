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

package net.mcreator.ui.workspace.resources;

import net.mcreator.io.FileIO;
import net.mcreator.io.UserFolderManager;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.ListUtil;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

class WorkspacePanelScreenshots extends AbstractResourcePanel<File> {

	WorkspacePanelScreenshots(WorkspacePanel workspacePanel) {
		super(workspacePanel, new ResourceFilterModel<>(workspacePanel, File::getName), new Render(),
				JList.HORIZONTAL_WRAP);

		elementList.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					exportSelectedScreenshots();
			}
		});

		addToolBarButton("workspace.screenshots.export_selected", UIRES.get("16px.ext"),
				e -> exportSelectedScreenshots());
		addToolBarButton("workspace.screenshots.use_as_background", UIRES.get("16px.textures"),
				e -> useSelectedAsBackgrounds());
		addToolBarButton("common.delete_selected", UIRES.get("16px.delete"), e -> {
			deleteCurrentlySelected();
			reloadElements();
		});
	}

	@Override void deleteCurrentlySelected() {
		List<File> elements = elementList.getSelectedValuesList();
		elements.forEach(File::delete);
	}

	@Override public void reloadElements() {
		List<File> selected = elementList.getSelectedValuesList();

		filterModel.removeAllElements();
		File[] screenshots = new File(workspacePanel.getMCreator().getFolderManager().getClientRunDir(),
				"screenshots/").listFiles();

		if (screenshots != null)
			filterModel.addAll(List.of(screenshots));

		ListUtil.setSelectedValues(elementList, selected);
	}

	private void useSelectedAsBackgrounds() {
		elementList.getSelectedValuesList().forEach(
				f -> FileIO.copyFile(f, new File(UserFolderManager.getFileFromUserFolder("backgrounds"), f.getName())));
		JOptionPane.showMessageDialog(workspacePanel.getMCreator(),
				L10N.t("workspace.screenshots.use_background_message"), L10N.t("workspace.screenshots.action_complete"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void exportSelectedScreenshots() {
		elementList.getSelectedValuesList().forEach(f -> {
			File to = FileDialogs.getSaveDialog(workspacePanel.getMCreator(), new String[] { ".png" });
			if (to != null)
				FileIO.copyFile(f, to);
		});
	}

	static class Render extends JLabel implements ListCellRenderer<File> {

		@Override
		public JLabel getListCellRendererComponent(JList<? extends File> list, File ma, int index, boolean isSelected,
				boolean cellHasFocus) {
			setOpaque(isSelected);
			setBackground(isSelected ? Theme.current().getAltBackgroundColor() : Theme.current().getBackgroundColor());
			setText(ma.getName());
			ComponentUtils.deriveFont(this, 11);
			setForeground(Theme.current().getForegroundColor());
			setVerticalTextPosition(BOTTOM);
			setHorizontalTextPosition(CENTER);
			setHorizontalAlignment(CENTER);

			setIcon(new ImageIcon(ImageUtils.resize(new ImageIcon(ma.getAbsolutePath()).getImage(), 145, 82)));

			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			return this;
		}

	}

}
