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

package net.mcreator.ui.component;

import net.mcreator.ui.FileOpener;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.FileIcons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JFileBreadCrumb extends JPanel {

	private static final Logger LOG = LogManager.getLogger("File Breadcrumb");

	private final File root;
	private final MCreator mcreator;

	public JFileBreadCrumb(MCreator mcreator, File file, File root) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));

		this.root = root;
		this.mcreator = mcreator;

		reloadPath(file);

		setBorder(BorderFactory.createEmptyBorder(3, 7, 5, 0));
	}

	public void reloadPath(File file) {
		File origfile = file;

		removeAll();

		List<File> path = new ArrayList<>();
		int depth = 0;
		while (true) {
			path.add(file);
			file = file.getParentFile();
			if (file == null)
				break;
			try {
				if (file.exists() && Files.isSameFile(file.toPath(), root.toPath()))
					break;
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
			depth++;
			if (depth > 9)
				break;
		}

		Collections.reverse(path);

		int idx = 0;

		MouseAdapter adapter = null;

		for (File filePathPart : path) {
			JLabel entry = new JLabel(
					((idx == path.size() - 1 && !filePathPart.isDirectory()) ? "<html><b>" : "") + filePathPart
							.getName());

			if (filePathPart.isFile())
				entry.setIcon(FileIcons.getIconForFile(filePathPart));
			else
				entry.setIcon(UIRES.get("laf.directory.gif"));

			add(entry);
			if (idx < path.size() - 1 || filePathPart.isDirectory()) {
				adapter = new MouseAdapter() {
					@Override public void mouseClicked(MouseEvent mouseEvent) {
						JScrollablePopupMenu popupMenu = new JScrollablePopupMenu();
						File[] files = filePathPart.listFiles();
						for (File file : files != null ? files : new File[0]) {
							JMenuItem menuItem = new JMenuItem(file.getName());
							if (file.isFile())
								menuItem.setIcon(FileIcons.getIconForFile(file));
							else
								menuItem.setIcon(UIRES.get("laf.directory.gif"));
							menuItem.addActionListener(e -> {
								if (file.isFile()) {
									FileOpener.openFile(mcreator, file);
								} else if (file.isDirectory()) {
									reloadPath(file);
								}
							});
							popupMenu.add(menuItem);
						}
						try {
							popupMenu.show(entry, 0, 18);
						} catch (Exception e) {
							popupMenu.show(JFileBreadCrumb.this, 0, 18);
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
			if (origfile.isDirectory()) {
				if (finalAdapter != null) {
					finalAdapter.mouseReleased(new MouseEvent(this, 0, 0, 0, 0, 0, 0, false, 0));
				}
			}
		});
	}

}