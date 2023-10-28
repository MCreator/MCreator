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
import net.mcreator.ui.component.JSelectableList;
import net.mcreator.ui.component.ListGroup;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.ListUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkspacePanelTextures extends JPanel implements IReloadableFilterable {

	private final Map<String, JComponentWithList<File>> mapLists = new HashMap<>();

	private final ListGroup<File> listGroup = new ListGroup<>();

	private final WorkspacePanel workspacePanel;

	private final MouseAdapter mouseAdapter;

	private final WorkspacePanelTextures.Render textureRender = new Render();

	WorkspacePanelTextures(WorkspacePanel workspacePanel) {
		super(new BorderLayout());
		setOpaque(false);

		this.workspacePanel = workspacePanel;

		mouseAdapter = new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					editSelectedFile();
			}
		};

		JPanel respan = new JPanel(new GridBagLayout());
		respan.setLayout(new BoxLayout(respan, BoxLayout.Y_AXIS));

		Arrays.stream(TextureType.values()).forEach(section -> {
			JComponentWithList<File> compList = createListElement(
					L10N.t("workspace.textures.category." + section.getID()));
			respan.add(compList.component());
			mapLists.put(section.getID(), compList);
		});

		respan.setOpaque(false);

		respan.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));

		JScrollPane sp = new JScrollPane(respan);
		sp.setOpaque(false);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.getViewport().setOpaque(false);
		sp.getVerticalScrollBar().setUnitIncrement(20);
		sp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), sp.getVerticalScrollBar()));
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		sp.setBorder(null);

		add("Center", sp);

		TransparentToolBar bar = new TransparentToolBar();
		bar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));

		JButton create = L10N.button("workspace.textures.new");
		create.setIcon(UIRES.get("16px.add.gif"));
		create.setContentAreaFilled(false);
		create.setOpaque(false);
		ComponentUtils.deriveFont(create, 12);
		create.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(create);

		JPopupMenu createMenu = new JPopupMenu();
		createMenu.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		createMenu.setBorder(
				BorderFactory.createMatteBorder(0, 3, 0, 0, (Color) UIManager.get("MCreatorLAF.MAIN_TINT")));

		createMenu.add(workspacePanel.getMCreator().actionRegistry.createMCItemTexture);
		createMenu.add(workspacePanel.getMCreator().actionRegistry.createArmorTexture);
		createMenu.add(workspacePanel.getMCreator().actionRegistry.createAnimatedTexture);

		create.addActionListener(e -> createMenu.show(create, 5, create.getHeight() + 5));

		JButton importt = L10N.button("workspace.textures.import");
		importt.setIcon(UIRES.get("16px.open.gif"));
		importt.setContentAreaFilled(false);
		importt.setOpaque(false);
		ComponentUtils.deriveFont(importt, 12);
		importt.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(importt);

		JPopupMenu importMenu = new JPopupMenu();
		importMenu.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		importMenu.setBorder(
				BorderFactory.createMatteBorder(0, 3, 0, 0, (Color) UIManager.get("MCreatorLAF.MAIN_TINT")));

		importMenu.add(workspacePanel.getMCreator().actionRegistry.importBlockTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importItemTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importEntityTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importEffectTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importParticleTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importScreenTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importArmorTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importOtherTexture);

		importt.addActionListener(e -> importMenu.show(importt, 5, importt.getHeight() + 5));

		JButton edit = L10N.button("workspace.textures.edit_selected");
		edit.setIcon(UIRES.get("16px.edit.gif"));
		edit.setContentAreaFilled(false);
		edit.setOpaque(false);
		ComponentUtils.deriveFont(edit, 12);
		edit.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(edit);

		JButton duplicate = L10N.button("workspace.textures.duplicate_selected");
		duplicate.setIcon(UIRES.get("16px.duplicate.gif"));
		duplicate.setContentAreaFilled(false);
		duplicate.setOpaque(false);
		ComponentUtils.deriveFont(duplicate, 12);
		duplicate.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(duplicate);

		JButton del = L10N.button("workspace.textures.delete_selected");
		del.setIcon(UIRES.get("16px.delete.gif"));
		del.setOpaque(false);
		del.setContentAreaFilled(false);
		del.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(del);

		JButton export = L10N.button("workspace.textures.export_selected");
		export.setIcon(UIRES.get("16px.ext.gif"));
		export.setOpaque(false);
		export.setContentAreaFilled(false);
		export.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(export);
		export.addActionListener(e -> exportSelectedImages());

		del.addActionListener(a -> deleteCurrentlySelected());

		edit.addActionListener(e -> editSelectedFile());
		duplicate.addActionListener(e -> duplicateSelectedFile());

		add("North", bar);
	}

	private void deleteCurrentlySelected() {
		List<File> files = listGroup.getSelectedItemsList();
		if (!files.isEmpty()) {
			int n = JOptionPane.showConfirmDialog(workspacePanel.getMCreator(),
					L10N.t("workspace.textures.confirm_deletion_message"), L10N.t("common.confirmation"),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);

			if (n == 0) {
				files.forEach(file -> {
					if (file != null) {
						file.delete();

						// try to delete mcmeta file if it exists too
						File mcmeta = new File(file.getAbsolutePath() + ".mcmeta");
						if (mcmeta.isFile())
							mcmeta.delete();
					}
				});
				reloadElements();
			}
		}
	}

	private void exportSelectedImages() {
		List<File> files = listGroup.getSelectedItemsList();
		if (!files.isEmpty()) {
			files.forEach(f -> {
				File to = FileDialogs.getSaveDialog(workspacePanel.getMCreator(), new String[] { ".png" });
				if (to != null)
					FileIO.copyFile(f, to);
			});
		}
	}

	private void duplicateSelectedFile() {
		File file = listGroup.getSelectedItem();
		if (file != null) {
			TextureImportDialogs.importSingleTexture(workspacePanel.getMCreator(), file,
					L10N.t("workspace.textures.select_dupplicate_type"));
		}
	}

	private void editSelectedFile() {
		File file = listGroup.getSelectedItem();
		if (file != null) {
			ImageMakerView imageMakerView = new ImageMakerView(workspacePanel.getMCreator());
			imageMakerView.openInEditMode(file);
			imageMakerView.showView();
		}
	}

	private JComponentWithList<File> createListElement(String title) {
		JSelectableList<File> listElement = new JSelectableList<>(
				new ResourceFilterModel<>(workspacePanel, File::getName));
		listElement.setCellRenderer(textureRender);
		listElement.setOpaque(false);
		listElement.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		listElement.setVisibleRowCount(-1);
		listElement.setFixedCellHeight(64);
		listElement.setFixedCellWidth(57);
		listElement.addMouseListener(mouseAdapter);
		listGroup.addList(listElement);
		listElement.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_DELETE -> deleteCurrentlySelected();
				case KeyEvent.VK_ENTER -> editSelectedFile();
				}
			}
		});
		listElement.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0), title, 0, 0,
						listElement.getFont().deriveFont(24.0f), Color.white));
		return new JComponentWithList<>(PanelUtils.gridElements(1, 1, listElement), listElement);
	}

	@Override public void reloadElements() {
		new Thread(() -> {
			Arrays.stream(TextureType.values()).forEach(section -> {
				List<File> selected = mapLists.get(section.getID()).list().getSelectedValuesList();

				// instead of clearing and adding all elements, we just replace the model (less flickering)
				ResourceFilterModel<File> newfm = new ResourceFilterModel<>(workspacePanel, File::getName);
				workspacePanel.getMCreator().getFolderManager().getTexturesList(section).forEach(newfm::addElement);

				SwingUtilities.invokeLater(() -> {
					JList<File> list = mapLists.get(section.getID()).list();
					list.setModel(newfm);
					ListUtil.setSelectedValues(list, selected);

					refilterElements();
				});
			});

			SwingUtilities.invokeLater(() -> {
				textureRender.invalidateIconCache();
				refilterElements();
			});
		}, "WorkspaceTexturesReload").start();
	}

	@Override public void refilterElements() {
		Arrays.stream(TextureType.values()).map(section -> mapLists.get(section.getID())).forEach(compList -> {
			ResourceFilterModel<?> filterModel = (ResourceFilterModel<?>) compList.list().getModel();
			filterModel.refilter();
			if (filterModel.getSize() > 0) {
				compList.component().setPreferredSize(null);
				compList.component().setVisible(true);
			} else {
				compList.component().setPreferredSize(new Dimension(0, 0));
				compList.component().setVisible(false);
			}
		});
	}

	static class Render extends JLabel implements ListCellRenderer<File> {

		private final Map<File, ImageIcon> TEXTURE_CACHE = new ConcurrentHashMap<>();

		Render() {
			setHorizontalTextPosition(JLabel.CENTER);
			setVerticalTextPosition(JLabel.BOTTOM);
			setHorizontalAlignment(JLabel.CENTER);
			setVerticalAlignment(JLabel.CENTER);
			setBorder(null);
			ComponentUtils.deriveFont(this, 10.0f);
		}

		private void invalidateIconCache() {
			TEXTURE_CACHE.clear();
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends File> list, File ma, int index,
				boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				setOpaque(true);
			} else {
				setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				setOpaque(false);
			}

			if (ma != null) {
				String name = StringUtils.abbreviateString(FilenameUtilsPatched.removeExtension(ma.getName()), 10);
				if (name.isBlank())
					name = "(untitled)";

				setText(name);
				setToolTipText(ma.getName());

				if (ma.getName().endsWith(".png")) {
					if (TEXTURE_CACHE.get(ma) != null && TEXTURE_CACHE.get(ma).getImage() != null)
						setIcon(TEXTURE_CACHE.get(ma));
					else {
						ImageIcon icon = new ImageIcon(
								ImageUtils.resize(new ImageIcon(ma.getAbsolutePath()).getImage(), 42));
						TEXTURE_CACHE.put(ma, icon);
						setIcon(icon);
					}

					if (!ma.getName().matches("[a-z0-9/._-]+")) {
						if (getIcon() instanceof ImageIcon icon) {
							icon = ImageUtils.changeSaturation(icon, 0.5f);
							setIcon(ImageUtils.drawOver(icon, UIRES.get("18px.warning"), 0, 0, 18, 18));
							setForeground(new Color(255, 245, 15));
						}
					}
				}
			}

			return this;
		}

	}

	private record JComponentWithList<T>(JComponent component, JList<T> list) {}

}
