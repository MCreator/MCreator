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

package net.mcreator.ui.modgui.codeviewer;

import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.GeneratorFile;
import net.mcreator.ui.laf.FileIcons;
import net.mcreator.ui.modgui.ModElementChangedListener;
import net.mcreator.ui.modgui.ModElementGUI;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModElementCodeViewer<T extends GeneratableElement> extends JTabbedPane {

	private final ModElementGUI<T> modElementGUI;

	private final Map<File, FileCodeViewer<T>> cache = new HashMap<>();

	private boolean updateRunning = false;
	private final ModElementChangedListener codeChangeListener;

	public ModElementCodeViewer(ModElementGUI<T> modElementGUI) {
		super(JTabbedPane.BOTTOM);

		this.modElementGUI = modElementGUI;
		this.codeChangeListener = this::reload;

		setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		setOpaque(true);

		addComponentListener(new ComponentAdapter() {
			@Override public void componentShown(ComponentEvent e) {
				super.componentShown(e);
				reload();
			}
		});
	}

	public void registerUI(JComponent container) {
		codeChangeListener.registerUI(container);
	}

	private void reload() {
		if (isVisible() && !updateRunning) {
			updateRunning = true;
			new Thread(() -> {
				try {
					List<GeneratorFile> files = modElementGUI.getModElement().getGenerator()
							.generateElement(modElementGUI.getElementFromGUI(), false, false);

					files.sort(
							Comparator.comparing(e -> FilenameUtils.getExtension(((GeneratorFile) e).file().getName()))
									.thenComparing(e -> ((GeneratorFile) e).file().getName()));

					for (GeneratorFile file : files) {
						if (cache.containsKey(file.file())) { // existing file
							SwingUtilities.invokeAndWait(() -> {
								try {
									if (cache.get(file.file()).update(file)) {
										int tabid = indexOfComponent(cache.get(file.file()));
										if (tabid != -1)
											setSelectedIndex(tabid);
									}
								} catch (Exception ignored) {
								}
							});
						} else { // new file
							SwingUtilities.invokeAndWait(() -> {
								try {
									FileCodeViewer<T> fileCodeViewer = new FileCodeViewer<>(this, file);
									addTab(file.file().getName(), FileIcons.getIconForFile(file.file()),
											fileCodeViewer);
									cache.put(file.file(), fileCodeViewer);
								} catch (Exception ignored) {
								}
							});
						}
					}

					cache.keySet().stream().toList().forEach(file -> {
						if (!files.stream().map(GeneratorFile::file).toList().contains(file)) { // deleted file
							remove(cache.get(file));
							cache.remove(file);
						}
					});
					setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
				} catch (Exception ignored) {
					setBackground(new Color(0x8D5C5C));
				}
				updateRunning = false;
			}).start();
		}
	}

	public ModElementGUI<T> getModElementGUI() {
		return modElementGUI;
	}

}
