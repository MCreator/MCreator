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

package net.mcreator.ui.workspace.breadcrumb;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JScrollablePopupMenu;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.IElement;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.ModElementManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WorkspaceFolderBreadcrumb extends JPanel {

	private final MCreator mcreator;

	public WorkspaceFolderBreadcrumb(MCreator mcreator) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));

		this.mcreator = mcreator;
		setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));
		setOpaque(false);
	}

	@Override public void paintComponent(Graphics g) {
		g.setColor(new Color(0.3f, 0.3f, 0.3f, 0.4f));
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

	@SuppressWarnings("EqualsBetweenInconvertibleTypes")
	public void reloadPath(FolderElement file, Class<? extends IElement> childElement) {
		removeAll();

		List<FolderElement> path = new ArrayList<>();

		int depth = 0;
		while (true) {
			path.add(file);
			if (file == null || file.equals(mcreator.getWorkspace().getFoldersRoot()))
				break;
			file = file.getParent();

			depth++;
			if (depth > 9)
				break;
		}
		Collections.reverse(path);

		int idx = 0;
		MouseAdapter adapter = null;
		for (FolderElement filePathPart : path) {
			JLabel entry = new FolderElementCrumb(filePathPart);

			if (filePathPart.equals(mcreator.getWorkspace().getFoldersRoot())) {
				entry.setText(mcreator.getWorkspaceSettings().getModName());
			}

			add(entry);

			if (idx < path.size() - 1) {
				adapter = new MouseAdapter() {
					@Override public void mouseClicked(MouseEvent mouseEvent) {
						if (mouseEvent.getClickCount() == 2) {
							mcreator.mv.switchFolder(filePathPart);
							return;
						}

						JScrollablePopupMenu popupMenu = new JScrollablePopupMenu();
						List<IElement> files = filePathPart.getDirectFolderChildren().stream().map(e -> (IElement) e)
								.collect(Collectors.toList());

						if (childElement == ModElement.class) {
							for (ModElement modElement : mcreator.getWorkspace().getModElements()) {
								if (filePathPart.equals(modElement.getFolderPath())) {
									files.add(modElement);
								}
							}
						}

						for (IElement file : files) {
							JMenuItem menuItem = new JMenuItem("<html>&nbsp;" + file.getName());
							if (file instanceof ModElement)
								menuItem.setIcon(new ImageIcon(ImageUtils
										.resizeAA(ModElementManager.getModElementIcon((ModElement) file).getImage(),
												16)));
							else if (file instanceof FolderElement)
								menuItem.setIcon(UIRES.get("laf.directory.gif"));

							menuItem.addActionListener(e -> {
								if (file instanceof ModElement) {
									mcreator.mv.editCurrentlySelectedModElement((ModElement) file, entry,
											mouseEvent.getX(), mouseEvent.getY());
								} else if (file instanceof FolderElement) {
									mcreator.mv.switchFolder((FolderElement) file);
								}
							});
							popupMenu.add(menuItem);
						}
						try {
							popupMenu.show(entry, 0, 18);
						} catch (Exception e) {
							popupMenu.show(WorkspaceFolderBreadcrumb.this, 0, 18);
						}
					}
				};
				entry.addMouseListener(adapter);
				add(new JLabel(UIRES.get("16px.subpath")));
			}

			idx++;
		}

		revalidate();
		repaint();

		MouseAdapter finalAdapter = adapter;
		SwingUtilities.invokeLater(() -> {
			if (finalAdapter != null)
				finalAdapter.mouseReleased(new MouseEvent(this, 0, 0, 0, 0, 0, 0, false, 0));
		});
	}

}