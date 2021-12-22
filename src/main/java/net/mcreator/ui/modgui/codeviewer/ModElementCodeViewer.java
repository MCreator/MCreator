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
import java.util.concurrent.locks.ReentrantLock;

public class ModElementCodeViewer<T extends GeneratableElement> extends JTabbedPane
		implements MouseListener, KeyListener, ActionListener, ChangeListener, DocumentListener {

	private final ModElementGUI<T> modElementGUI;

	private final Map<File, FileCodeViewer<T>> cache = new HashMap<>();
	private final Map<GeneratorTemplatesList, JTabbedPane> listPager = new HashMap<>();

	private final ReentrantLock updateRunning = new ReentrantLock(true);

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
		Icon disabledListIcon = ImageUtils.changeSaturation(UIRES.get("16px.list.gif"), 0);
		modElementGUI.getModElement().getGenerator()
				.getModElementGeneratorListTemplates(modElementGUI.getModElement(), modElementGUI.getElementFromGUI())
				.forEach(e -> {
					if (indexOfTab(e.groupName()) == -1) {
						JTabbedPane subTab = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
						subTab.addComponentListener(new ComponentAdapter() {
							@Override public void componentShown(ComponentEvent e) {
								super.componentShown(e);
								reload();
							}
						});

						listPager.put(e, subTab);
						addTab(e.groupName(), UIRES.get("16px.list.gif"), subTab);

						setDisabledIconAt(indexOfTab(e.groupName()), disabledListIcon);
						setEnabledAt(indexOfTab(e.groupName()), false);
					}
				});
	}

	public void registerUI(JComponent container) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			if (component instanceof MCItemHolder mcItemHolder) {
				mcItemHolder.addBlockSelectedListener(this);
			} else if (component instanceof JItemListField<?> listField) {
				listField.addChangeListener(this);
			} else if (component instanceof JEntriesList entriesList) {
				entriesList.setEntryCreationListener(c -> {
					this.registerUI(c);
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

	private synchronized void reload() {
		if (isVisible()) {
			new Thread(() -> {
				updateRunning.lock();
				try {
					List<GeneratorFile> files = modElementGUI.getModElement().getGenerator()
							.generateElement(modElementGUI.getElementFromGUI(), false, false);

					files.sort(
							Comparator.comparing(e -> FilenameUtils.getExtension(((GeneratorFile) e).file().getName()))
									.thenComparing(e -> ((GeneratorFile) e).file().getName()));

					for (GeneratorFile file : files) {
						if (cache.containsKey(file.file())) { // existing file
							if (cache.get(file.file()).update(file)) {
								Optional<GeneratorTemplatesList> ownerListOptional = listPager.keySet().stream()
										.filter(e -> e.getCorrespondingListTemplate(file.file()) != null).findFirst();
								if (ownerListOptional.isPresent()
										&& listPager.get(ownerListOptional.get()) != null) { // file from list
									JTabbedPane ownerList = listPager.get(ownerListOptional.get());
									int tabid = indexOfComponent(ownerList);
									if (tabid != -1)
										setSelectedIndex(tabid);
									int subtabid = ownerList.indexOfComponent(cache.get(file.file()));
									if (subtabid != -1)
										ownerList.setSelectedIndex(subtabid);
								} else { // simple file
									int tabid = indexOfComponent(cache.get(file.file()));
									if (tabid != -1)
										setSelectedIndex(tabid);
								}
							}
						} else { // new file
							try {
								FileCodeViewer<T> fileCodeViewer = new FileCodeViewer<>(this, file);
								Optional<GeneratorTemplatesList> ownerListOptional = listPager.keySet().stream()
										.filter(e -> e.getCorrespondingListTemplate(file.file()) != null).findFirst();
								if (ownerListOptional.isPresent()) { // file from list
									JTabbedPane ownerList = listPager.get(ownerListOptional.get());
									ownerList.addTab(file.file().getName(), FileIcons.getIconForFile(file.file()),
											fileCodeViewer);
									setEnabledAt(indexOfComponent(ownerList), true);
								} else { // simple file
									addTab(file.file().getName(), FileIcons.getIconForFile(file.file()),
											fileCodeViewer);
								}
								cache.put(file.file(), fileCodeViewer);
							} catch (Exception ignored) {
							}
						}
					}

					// this likely selects first file from cache if currently selected tab is disabled
					if (!isEnabledAt(getSelectedIndex())) {
						int first = 0;
						for (int i = getTabCount() - 1; i >= 0; i--) {
							if (isEnabledAt(i) || cache.isEmpty())
								first = i;
						}
						setSelectedIndex(first);
					}

					cache.keySet().stream().toList().forEach(file -> {
						if (!files.stream().map(GeneratorFile::file).toList().contains(file)) { // deleted file
							Optional<GeneratorTemplatesList> ownerListOptional = listPager.keySet().stream()
									.filter(e -> e.getCorrespondingListTemplate(file) != null).findFirst();
							if (ownerListOptional.isPresent()
									&& listPager.get(ownerListOptional.get()) != null) { // file from list
								JTabbedPane ownerList = listPager.get(ownerListOptional.get());
								ownerList.removeTabAt(ownerList.indexOfTab(file.getName()));
								if (ownerList.getTabCount() == 0)
									setEnabledAt(indexOfComponent(ownerList), false);
							} else { // simple file
								remove(cache.get(file));
							}
							cache.remove(file);
						}
					});
					setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
				} catch (Exception ignored) {
					setBackground(new Color(0x8D5C5C));
				} finally {
					updateRunning.unlock();
				}
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
