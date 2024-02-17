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
import net.mcreator.generator.GeneratorTemplatesList;
import net.mcreator.generator.ListTemplate;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.FileIcons;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.modgui.ModElementChangedListener;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.util.image.ImageUtils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ModElementCodeViewer<T extends GeneratableElement> extends JTabbedPane {

	private final ModElementGUI<T> modElementGUI;

	private final Map<GeneratorFile, FileCodeViewer<T>> cache = new HashMap<>();
	private final Map<String, JTabbedPane> listPager = new HashMap<>();

	private boolean updateRunning = false;
	private final ModElementChangedListener codeChangeListener;

	public ModElementCodeViewer(ModElementGUI<T> modElementGUI) {
		super(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);

		this.modElementGUI = modElementGUI;
		this.codeChangeListener = this::reload;

		setBackground(Theme.current().getAltBackgroundColor());
		setOpaque(true);

		addComponentListener(new ComponentAdapter() {
			@Override public void componentShown(ComponentEvent e) {
				super.componentShown(e);
				reload();
			}
		});

		// we group list templates inside separate tabs to improve UX
		ImageIcon enabledListIcon = UIRES.get("16px.list");
		ImageIcon disabledListIcon = ImageUtils.changeSaturation(enabledListIcon, 0);
		modElementGUI.getModElement().getGenerator().getModElementListTemplates(modElementGUI.getElementFromGUI())
				.stream().map(GeneratorTemplatesList::groupName).forEach(listName -> {
					JTabbedPane listPane = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
					listPane.setUI(new MetalTabbedPaneUI() {
						private final Insets borderInsets = new Insets(0, 0, 0, 0);

						@Override protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
						}

						@Override protected Insets getContentBorderInsets(int tabPlacement) {
							return borderInsets;
						}
					});

					listPane.setBackground(Theme.current().getAltBackgroundColor());
					listPane.setOpaque(true);

					listPane.addComponentListener(new ComponentAdapter() {
						@Override public void componentShown(ComponentEvent e) {
							super.componentShown(e);
							reload();
						}
					});

					addTab(listName, enabledListIcon, listPane);
					setDisabledIconAt(indexOfTab(listName), disabledListIcon);
					setEnabledAt(indexOfTab(listName), false);
					listPager.put(listName, listPane);
				});
	}

	public void registerUI(JComponent container) {
		codeChangeListener.registerUI(container);
	}

	private synchronized void reload() {
		if (isVisible() && !updateRunning) {
			updateRunning = true;
			new Thread(() -> {
				try {
					List<GeneratorFile> files = modElementGUI.getModElement().getGenerator()
							.generateElement(modElementGUI.getElementFromGUI(), false, false);
					files.sort(Comparator.<GeneratorFile, String>comparing(
									e -> FilenameUtils.getExtension(e.getFile().getName()))
							.thenComparing(e -> e.getFile().getName()));

					for (GeneratorFile file : files) {
						if (cache.containsKey(file)) { // existing file
							ThreadUtil.runOnSwingThreadAndWait(() -> {
								try {
									if (cache.get(file).update(file)) {
										if (file.source() instanceof ListTemplate lt) { // file from list
											JTabbedPane ownerList = listPager.get(lt.getTemplatesList().groupName());
											int tabid = indexOfComponent(ownerList);
											if (tabid != -1) {
												int subtabid = ownerList.indexOfComponent(cache.get(file));
												if (subtabid != -1) {
													setSelectedIndex(tabid);
													ownerList.setSelectedIndex(subtabid);
												}
											}
										} else { // simple file
											int tabid = indexOfComponent(cache.get(file));
											if (tabid != -1)
												setSelectedIndex(tabid);
										}
									}
								} catch (Exception ignored) {
								}
							});
						} else { // new file
							ThreadUtil.runOnSwingThreadAndWait(() -> {
								try {
									FileCodeViewer<T> fileCodeViewer = new FileCodeViewer<>(this, file);
									if (file.source() instanceof ListTemplate lt) { // file from list
										JTabbedPane ownerList = listPager.get(lt.getTemplatesList().groupName());
										ownerList.addTab(file.getFile().getName(),
												FileIcons.getIconForFile(file.getFile()), fileCodeViewer);
										if (ownerList.getTabCount() > 0)
											setEnabledAt(indexOfComponent(ownerList), true);
									} else { // simple file
										addTab(file.getFile().getName(), FileIcons.getIconForFile(file.getFile()),
												fileCodeViewer);
									}
									cache.put(file, fileCodeViewer);
								} catch (Exception ignored) {
								}
							});
						}
					}

					cache.keySet().stream().toList().forEach(file -> {
						if (!files.contains(file)) { // deleted file
							if (file.source() instanceof ListTemplate lt) { // file from list
								JTabbedPane ownerList = listPager.get(lt.getTemplatesList().groupName());
								ownerList.remove(cache.get(file));
								if (ownerList.getTabCount() == 0)
									setEnabledAt(indexOfComponent(ownerList), false);
							} else { // simple file
								remove(cache.get(file));
							}
							cache.remove(file);
						}
					});

					// this likely selects first file from cache if currently selected tab is disabled
					int selectedTab = getSelectedIndex();
					if (selectedTab >= 0 && !isEnabledAt(selectedTab) && !cache.isEmpty())
						setSelectedIndex(IntStream.range(0, getTabCount()).filter(this::isEnabledAt).min().orElse(0));

					setBackground(Theme.current().getAltBackgroundColor());
				} catch (Exception ignored) {
					setBackground(new Color(0x8D5C5C));
				}

				if (getTabCount() == 0)
					addTab(L10N.t("mod_element_code_viewer.no_files"), PanelUtils.totalCenterInPanel(
							ComponentUtils.setForeground(L10N.label("mod_element_code_viewer.no_files.desc"),
									Theme.current().getAltForegroundColor())));

				updateRunning = false;
			}, "CodePreviewReloader").start();
		}
	}

	public ModElementGUI<T> getModElementGUI() {
		return modElementGUI;
	}

}
