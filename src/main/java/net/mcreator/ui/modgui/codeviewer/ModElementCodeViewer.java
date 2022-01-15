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

import javafx.embed.swing.JFXPanel;
import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.GeneratorFile;
import net.mcreator.generator.GeneratorTemplatesList;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.FileIcons;
import net.mcreator.ui.minecraft.JEntriesList;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.util.image.ImageUtils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ModElementCodeViewer<T extends GeneratableElement> extends JTabbedPane
		implements MouseListener, KeyListener, ActionListener, ChangeListener, DocumentListener {

	private final ModElementGUI<T> modElementGUI;

	private final Map<File, FileCodeViewer<T>> cache = new HashMap<>();
	private final Map<GeneratorTemplatesList, JTabbedPane> listPager;

	private volatile boolean updateRunning = false;

	public ModElementCodeViewer(ModElementGUI<T> modElementGUI) {
		super(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);

		this.modElementGUI = modElementGUI;

		setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		setOpaque(true);

		addComponentListener(new ComponentAdapter() {
			@Override public void componentShown(ComponentEvent e) {
				super.componentShown(e);
				reload();
			}
		});

		// we group list templates inside separate tabs to improve UX
		ImageIcon enabledListIcon = UIRES.get("16px.list.gif");
		ImageIcon disabledListIcon = ImageUtils.changeSaturation(enabledListIcon, 0);
		this.listPager = modElementGUI.getModElement().getGenerator()
				.getModElementListTemplates(modElementGUI.getModElement(), modElementGUI.getElementFromGUI())
				.stream().collect(Collectors.toUnmodifiableMap(key -> key, list -> {
					JTabbedPane listTab = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
					listTab.addComponentListener(new ComponentAdapter() {
						@Override public void componentShown(ComponentEvent e) {
							super.componentShown(e);
							reload();
						}
					});

					addTab(list.groupName(), enabledListIcon, listTab);
					setDisabledIconAt(indexOfTab(list.groupName()), disabledListIcon);
					setEnabledAt(indexOfTab(list.groupName()), false);

					return listTab;
				}));
	}

	public void registerUI(JComponent container) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			if (component instanceof MCItemHolder mcItemHolder) {
				mcItemHolder.addBlockSelectedListener(this);
			} else if (component instanceof JItemListField<?> listField) {
				listField.addChangeListener(this);
			} else if (component instanceof JEntriesList entriesList) {
				registerUI(entriesList);
				entriesList.setEntryCreationListener(c -> {
					registerUI(c);
					reload();
				});

				component.addMouseListener(this);
			} else if (component instanceof AbstractButton button) {
				button.addActionListener(this);
			} else if (component instanceof JSpinner button) {
				button.addChangeListener(this);
			} else if (component instanceof JComboBox<?> comboBox) {
				comboBox.addActionListener(this);
			} else if (component instanceof JTextComponent textComponent) {
				textComponent.getDocument().addDocumentListener(this);
			} else if (component instanceof JFXPanel) {
				component.addMouseListener(this);
				component.addKeyListener(this);
			} else if (component instanceof JComponent jcomponent) {
				registerUI(jcomponent);

				if (!(component instanceof JLabel) && !(component instanceof JPanel)) {
					component.addMouseListener(this);
					component.addKeyListener(this);
				}
			}
		}
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
						Optional<GeneratorTemplatesList> ownerListOptional = listPager.keySet().stream()
								.filter(e -> e.getCorrespondingListTemplate(file.file(), true) != null).findFirst();

						if (cache.containsKey(file.file())) { // existing file
							SwingUtilities.invokeAndWait(() -> {
								try {
									if (cache.get(file.file()).update(file)) {
										if (ownerListOptional.isPresent()) { // file from list
											JTabbedPane ownerList = listPager.get(ownerListOptional.get());
											int tabid = indexOfComponent(ownerList);
											if (tabid != -1) {
												setSelectedIndex(tabid);
												int subtabid = ownerList.indexOfComponent(cache.get(file.file()));
												if (subtabid != -1)
													ownerList.setSelectedIndex(subtabid);
											}
										} else { // simple file
											int tabid = indexOfComponent(cache.get(file.file()));
											if (tabid != -1)
												setSelectedIndex(tabid);
										}
									}
								} catch (Exception ignored) {
								}
							});
						} else { // new file
							SwingUtilities.invokeAndWait(() -> {
								try {
									FileCodeViewer<T> fileCodeViewer = new FileCodeViewer<>(this, file);
									if (ownerListOptional.isPresent()) { // file from list
										JTabbedPane ownerList = listPager.get(ownerListOptional.get());
										ownerList.addTab(file.file().getName(), FileIcons.getIconForFile(file.file()),
												fileCodeViewer);
										if (ownerList.getTabCount() == 1)
											setEnabledAt(indexOfComponent(ownerList), true);
									} else { // simple file
										addTab(file.file().getName(), FileIcons.getIconForFile(file.file()),
												fileCodeViewer);
									}
									cache.put(file.file(), fileCodeViewer);
								} catch (Exception ignored) {
								}
							});
						}
					}

					List<File> cacheFiles = files.stream().map(GeneratorFile::file).toList();
					cache.keySet().stream().toList().forEach(file -> {
						if (!cacheFiles.contains(file)) { // deleted file
							Optional<GeneratorTemplatesList> ownerListOptional = listPager.keySet().stream()
									.filter(e -> e.getCorrespondingListTemplate(file, true) != null).findFirst();
							if (ownerListOptional.isPresent()) { // file from list
								JTabbedPane ownerList = listPager.get(ownerListOptional.get());
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
					if (!isEnabledAt(getSelectedIndex()) && !cache.isEmpty())
						setSelectedIndex(IntStream.range(0, getTabCount()).filter(this::isEnabledAt).min().orElse(0));

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

	@Override public void mouseReleased(MouseEvent e) {
		reload();
	}

	@Override public void keyReleased(KeyEvent e) {
		reload();
	}

	@Override public void actionPerformed(ActionEvent e) {
		reload();
	}

	@Override public void stateChanged(ChangeEvent e) {
		reload();
	}

	@Override public void changedUpdate(DocumentEvent e) {
		reload();
	}

	@Override public void insertUpdate(DocumentEvent e) {
		reload();
	}

	@Override public void removeUpdate(DocumentEvent e) {
		reload();
	}

	@Override public void keyTyped(KeyEvent e) {
	}

	@Override public void keyPressed(KeyEvent e) {
	}

	@Override public void mouseClicked(MouseEvent e) {
	}

	@Override public void mousePressed(MouseEvent e) {
	}

	@Override public void mouseEntered(MouseEvent e) {
	}

	@Override public void mouseExited(MouseEvent e) {
	}

}
