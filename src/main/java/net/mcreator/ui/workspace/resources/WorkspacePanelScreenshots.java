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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class WorkspacePanelScreenshots extends AbstractResourcePanel<File> {

	private static final int THUMBNAIL_WIDTH = 145;
	private static final int THUMBNAIL_HEIGHT = 82;

	private final Render render;

	private final ExecutorService thumbnailLoader = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable, "Screenshot-Thumbnail-Loader");
		thread.setDaemon(true);
		return thread;
	});
	private Future<?> thumbnailLoadTask;

	WorkspacePanelScreenshots(WorkspacePanel workspacePanel) {
		super(workspacePanel, new ResourceFilterModel<>(workspacePanel, File::getName), this.render = new Render(),
				JList.HORIZONTAL_WRAP);

		elementList.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					exportSelectedScreenshots();
			}
		});

		addToolBarButton("workspace.screenshots.export_selected", UIRES.get("16px.ext"),
				_ -> exportSelectedScreenshots());
		addToolBarButton("workspace.screenshots.use_as_background", UIRES.get("16px.textures"),
				_ -> useSelectedAsBackgrounds());
		addToolBarButton("common.delete_selected", UIRES.get("16px.delete"), _ -> {
			deleteCurrentlySelected();
			reloadElements();
		});
	}

	@Override protected void deleteCurrentlySelected() {
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

		reloadThumbnails(screenshots == null ? new File[0] : screenshots);
	}

	private void reloadThumbnails(File[] screenshots) {
		if (thumbnailLoadTask != null)
			thumbnailLoadTask.cancel(true);

		render.thumbnailCache.keySet().retainAll(Set.of(screenshots));

		// Single thread executor makes sure at most one thumbnail load task runs at any given time
		thumbnailLoadTask = thumbnailLoader.submit(() -> {
			for (File screenshot : screenshots) {
				if (Thread.currentThread().isInterrupted())
					return;

				if (render.thumbnailCache.containsKey(screenshot))
					continue;

				try {
					Image image = ImageIO.read(screenshot);
					if (image != null) {
						render.thumbnailCache.put(screenshot,
								new ImageIcon(ImageUtils.resize(image, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)));
						elementList.repaint();
					}
				} catch (IOException _) {
				}
			}
		});
	}

	private void useSelectedAsBackgrounds() {
		if (!elementList.getSelectedValuesList().isEmpty()) {
			elementList.getSelectedValuesList().forEach(f -> FileIO.copyFile(f,
					new File(UserFolderManager.getFileFromUserFolder("backgrounds"), f.getName())));
			JOptionPane.showMessageDialog(workspacePanel.getMCreator(),
					L10N.t("workspace.screenshots.use_background_message"),
					L10N.t("workspace.screenshots.action_complete"), JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void exportSelectedScreenshots() {
		elementList.getSelectedValuesList().forEach(f -> {
			File to = FileDialogs.getSaveDialog(workspacePanel.getMCreator(), new String[] { ".png" });
			if (to != null)
				FileIO.copyFile(f, to);
		});
	}

	static class Render extends JLabel implements ListCellRenderer<File> {

		private final Map<File, ImageIcon> thumbnailCache = new ConcurrentHashMap<>();

		private final ImageIcon placeholder = new ImageIcon(
				ImageUtils.emptyImageWithSize(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, null));

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

			setIcon(thumbnailCache.getOrDefault(ma, placeholder));

			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			return this;
		}

	}

}
