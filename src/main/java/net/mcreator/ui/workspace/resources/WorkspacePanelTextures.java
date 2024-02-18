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
import net.mcreator.ui.dialogs.SearchUsagesDialog;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.workspace.AbstractWorkspacePanel;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ReferencesFinder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.*;
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
		sp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI(Theme.current().getBackgroundColor(),
				Theme.current().getAltBackgroundColor(), sp.getVerticalScrollBar()));
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		sp.setBorder(null);

		add("Center", sp);

		TransparentToolBar bar = new TransparentToolBar();
		bar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));

		JPopupMenu createMenu = new JPopupMenu();
		createMenu.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, Theme.current().getInterfaceAccentColor()));
		createMenu.setBackground(Theme.current().getAltBackgroundColor());
		createMenu.add(workspacePanel.getMCreator().actionRegistry.createMCItemTexture);
		createMenu.add(workspacePanel.getMCreator().actionRegistry.createArmorTexture);
		createMenu.add(workspacePanel.getMCreator().actionRegistry.createAnimatedTexture);

		JButton create = AbstractWorkspacePanel.createToolBarButton("workspace.textures.new",
				UIRES.get("16px.add"));
		create.addActionListener(e -> createMenu.show(create, 5, create.getHeight() + 5));
		bar.add(create);

		JPopupMenu importMenu = new JPopupMenu();
		importMenu.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, Theme.current().getInterfaceAccentColor()));
		importMenu.setBackground(Theme.current().getAltBackgroundColor());
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importBlockTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importItemTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importEntityTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importEffectTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importParticleTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importScreenTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importArmorTexture);
		importMenu.add(workspacePanel.getMCreator().actionRegistry.importOtherTexture);

		JButton importt = AbstractWorkspacePanel.createToolBarButton("workspace.textures.import",
				UIRES.get("16px.open"));
		importt.addActionListener(e -> importMenu.show(importt, 5, importt.getHeight() + 5));
		bar.add(importt);

		bar.add(AbstractWorkspacePanel.createToolBarButton("workspace.textures.edit_selected",
				UIRES.get("16px.edit"), e -> editSelectedFile()));

		bar.add(AbstractWorkspacePanel.createToolBarButton("workspace.textures.duplicate_selected",
				UIRES.get("16px.duplicate"), e -> duplicateSelectedFile()));

		bar.add(AbstractWorkspacePanel.createToolBarButton("workspace.textures.replace_selected",
				UIRES.get("16px.editorder"), e -> replaceSelectedFile()));

		bar.add(AbstractWorkspacePanel.createToolBarButton("common.search_usages", UIRES.get("16px.search"), e -> {
			workspacePanel.getMCreator().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			Set<ModElement> refs = new HashSet<>();
			for (TextureType section : TextureType.getSupportedTypes(workspacePanel.getMCreator().getWorkspace(),
					true)) {
				JList<File> list = mapLists.get(section.getID()).list();
				for (File texture : list.getSelectedValuesList()) {
					refs.addAll(
							ReferencesFinder.searchTextureUsages(workspacePanel.getMCreator().getWorkspace(), texture,
									section));
				}
			}

			workspacePanel.getMCreator().setCursor(Cursor.getDefaultCursor());
			SearchUsagesDialog.showUsagesDialog(workspacePanel.getMCreator(),
					L10N.t("dialog.search_usages.type.resource.texture"), refs);
		}));

		bar.add(AbstractWorkspacePanel.createToolBarButton("common.delete_selected", UIRES.get("16px.delete"),
				e -> deleteCurrentlySelected()));

		bar.add(AbstractWorkspacePanel.createToolBarButton("workspace.textures.export_selected",
				UIRES.get("16px.ext"), e -> exportSelectedImages()));

		add("North", bar);
	}

	private void deleteCurrentlySelected() {
		List<File> files = listGroup.getSelectedItemsList();
		if (!files.isEmpty()) {
			workspacePanel.getMCreator().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			Set<ModElement> references = new HashSet<>();
			for (TextureType section : TextureType.getSupportedTypes(workspacePanel.getMCreator().getWorkspace(),
					true)) {
				JList<File> list = mapLists.get(section.getID()).list();
				for (File texture : list.getSelectedValuesList()) {
					references.addAll(
							ReferencesFinder.searchTextureUsages(workspacePanel.getMCreator().getWorkspace(), texture,
									section));
				}
			}

			workspacePanel.getMCreator().setCursor(Cursor.getDefaultCursor());

			if (SearchUsagesDialog.showDeleteDialog(workspacePanel.getMCreator(),
					L10N.t("dialog.search_usages.type.resource.texture"), references)) {
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

	private void replaceSelectedFile() {
		File file = listGroup.getSelectedItem();
		if (file != null) {
			File newTexture = FileDialogs.getOpenDialog(workspacePanel.getMCreator(), new String[] { ".png" });
			if (newTexture != null) {
				FileIO.copyFile(newTexture, file);
				new ImageIcon(file.getAbsolutePath()).getImage().flush();
				reloadElements();
			}
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
						listElement.getFont().deriveFont(24.0f), Theme.current().getForegroundColor()));
		return new JComponentWithList<>(PanelUtils.gridElements(1, 1, listElement), listElement);
	}

	@Override public void reloadElements() {
		Arrays.stream(TextureType.values()).forEach(section -> {
			List<File> selected = mapLists.get(section.getID()).list().getSelectedValuesList();

			JList<File> list = mapLists.get(section.getID()).list();

			((ResourceFilterModel<File>) list.getModel()).removeAllElements();
			((ResourceFilterModel<File>) list.getModel()).addAll(
					workspacePanel.getMCreator().getFolderManager().getTexturesList(section));

			ListUtil.setSelectedValues(list, selected);
		});

		textureRender.invalidateIconCache();
		refilterElements();
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
				setForeground(Theme.current().getBackgroundColor());
				setBackground(Theme.current().getForegroundColor());
				setOpaque(true);
			} else {
				setForeground(Theme.current().getForegroundColor());
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
