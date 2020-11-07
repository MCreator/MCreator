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
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WorkspacePanelTextures extends JPanel implements IReloadableFilterable {

	private FilterModel dmlb = new FilterModel();
	private FilterModel dmli = new FilterModel();
	private FilterModel dmla = new FilterModel();
	private FilterModel dmle = new FilterModel();
	private FilterModel dmlp = new FilterModel();
	private FilterModel dmlo = new FilterModel();

	private final ListGroup<File> listGroup = new ListGroup<>();

	private final WorkspacePanel workspacePanel;

	private final JComponentWithList<File> listb;
	private final JComponentWithList<File> listi;
	private final JComponentWithList<File> lista;
	private final JComponentWithList<File> liste;
	private final JComponentWithList<File> listp;
	private final JComponentWithList<File> listo;

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

		listb = createListElement(dmlb, L10N.t("workspace.textures.category_blocks"));
		listi = createListElement(dmli, L10N.t("workspace.textures.category_items"));
		lista = createListElement(dmla, L10N.t("workspace.textures.category_armor"));
		listo = createListElement(dmlo, L10N.t("workspace.textures.category_other"));
		liste = createListElement(dmle, L10N.t("workspace.textures.category_entities"));
		listp = createListElement(dmlp, L10N.t("workspace.textures.category_paintings"));

		respan.add(listb.getComponent());
		respan.add(listi.getComponent());
		respan.add(lista.getComponent());
		respan.add(liste.getComponent());
		respan.add(listp.getComponent());
		respan.add(listo.getComponent());

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
		createMenu
				.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, (Color) UIManager.get("MCreatorLAF.MAIN_TINT")));

		createMenu.add(workspacePanel.mcreator.actionRegistry.createMCItemTexture);
		createMenu.add(workspacePanel.mcreator.actionRegistry.createArmorTexture);
		createMenu.add(workspacePanel.mcreator.actionRegistry.createAnimatedTexture);

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
		importMenu
				.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, (Color) UIManager.get("MCreatorLAF.MAIN_TINT")));

		importMenu.add(workspacePanel.mcreator.actionRegistry.importBlockTexture);
		importMenu.add(workspacePanel.mcreator.actionRegistry.importEntityTexture);
		importMenu.add(workspacePanel.mcreator.actionRegistry.importItemTexture);
		importMenu.add(workspacePanel.mcreator.actionRegistry.importPaintingTexture);
		importMenu.add(workspacePanel.mcreator.actionRegistry.importArmorTexture);
		importMenu.add(workspacePanel.mcreator.actionRegistry.importOtherTexture);

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

		del.addActionListener(actionEvent -> {
			List<File> files = listGroup.getSelectedItemsList();
			if (files.size() > 0) {
				Object[] options = { "Yes", "No" };
				int n = JOptionPane.showOptionDialog(workspacePanel.mcreator,
						"<html>Are you sure that you want to delete this file?"
								+ "<br>NOTE: If you use this file anywhere, you might have broken textures!",
						"Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
						options[1]);

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
		});

		edit.addActionListener(e -> editSelectedFile());
		duplicate.addActionListener(e -> duplicateSelectedFile());

		add("North", bar);
	}

	private void exportSelectedImages() {
		List<File> elements = listGroup.getSelectedItemsList();
		List<File> files = new LinkedList<>();
		for(File element : elements){
			files.add(element);
		}
		if (files.size() > 0) {
			files.forEach(f -> {
				File to = FileDialogs.getSaveDialog(workspacePanel.mcreator, new String[] { ".png" });
				if (to != null)
					FileIO.copyFile(f, to);
			});
		}
	}

	private void duplicateSelectedFile() {
		File element = listGroup.getSelectedItem();
		if (element != null) {
			TextureImportDialogs.importTextureGeneral(workspacePanel.mcreator, element,
					"Select the type of texture you would like to duplicate into:");
		}
	}

	private void editSelectedFile() {
		File element = listGroup.getSelectedItem();
		if (element != null) {
			ImageMakerView imageMakerView = new ImageMakerView(workspacePanel.mcreator);
			imageMakerView.openInEditMode(element);
			imageMakerView.showView();
		}
	}

	private JComponentWithList<File> createListElement(FilterModel dmlb, String title) {
		JSelectableList<File> listElement = new JSelectableList<>(dmlb);
		listElement.setCellRenderer(textureRender);
		listElement.setOpaque(false);
		listElement.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		listElement.setVisibleRowCount(-1);
		listElement.setFixedCellHeight(64);
		listElement.setFixedCellWidth(57);
		listElement.addMouseListener(mouseAdapter);
		listGroup.addList(listElement);
		listElement.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), title, 0, 0,
				listElement.getFont().deriveFont(24.0f), Color.white));
		return new JComponentWithList<>(PanelUtils.gridElements(1, 1, listElement), listElement);
	}

	public void reloadElements() {
		new Thread(() -> {
			List<File> selectedb = listb.getList().getSelectedValuesList();
			List<File> selectedi = listi.getList().getSelectedValuesList();
			List<File> selectede = liste.getList().getSelectedValuesList();
			List<File> selectedp = listp.getList().getSelectedValuesList();
			List<File> selecteda = lista.getList().getSelectedValuesList();
			List<File> selectedo = listo.getList().getSelectedValuesList();

			FilterModel newdmlb = new FilterModel();
			FilterModel newdmli = new FilterModel();
			FilterModel newdmle = new FilterModel();
			FilterModel newdmlp = new FilterModel();
			FilterModel newdmla = new FilterModel();
			FilterModel newdmlo = new FilterModel();

			workspacePanel.mcreator.getWorkspace().getFolderManager().getBlockTexturesList()
					.forEach(newdmlb::addElement);
			workspacePanel.mcreator.getWorkspace().getFolderManager().getItemTexturesList()
					.forEach(newdmli::addElement);
			workspacePanel.mcreator.getWorkspace().getFolderManager().getEntityTexturesList()
					.forEach(newdmle::addElement);
			workspacePanel.mcreator.getWorkspace().getFolderManager().getPaintingTexturesList()
					.forEach(newdmlp::addElement);
			workspacePanel.mcreator.getWorkspace().getFolderManager().getArmorTexturesList()
					.forEach(newdmla::addElement);
			workspacePanel.mcreator.getWorkspace().getFolderManager().getOtherTexturesList()
					.forEach(newdmlo::addElement);

			SwingUtilities.invokeLater(() -> {
				listb.getList().setModel(dmlb = newdmlb);
				listi.getList().setModel(dmli = newdmli);
				liste.getList().setModel(dmle = newdmle);
				listp.getList().setModel(dmlp = newdmlp);
				lista.getList().setModel(dmla = newdmla);
				listo.getList().setModel(dmlo = newdmlo);

				textureRender.invalidateIconCache();

				ListUtil.setSelectedValues(listb.getList(), selectedb);
				ListUtil.setSelectedValues(listi.getList(), selectedi);
				ListUtil.setSelectedValues(liste.getList(), selectede);
				ListUtil.setSelectedValues(listp.getList(), selectedp);
				ListUtil.setSelectedValues(lista.getList(), selecteda);
				ListUtil.setSelectedValues(listo.getList(), selectedo);

				refilterElements();
			});
		}).start();
	}

	public void refilterElements() {
		dmli.refilter();
		dmlb.refilter();
		dmle.refilter();
		dmlp.refilter();
		dmla.refilter();
		dmlo.refilter();

		if (dmli.getSize() > 0) {
			listi.getComponent().setPreferredSize(null);
			listi.getComponent().setVisible(true);
		} else {
			listi.getComponent().setPreferredSize(new Dimension(0, 0));
			listi.getComponent().setVisible(false);
		}

		if (dmle.getSize() > 0) {
			liste.getComponent().setPreferredSize(null);
			liste.getComponent().setVisible(true);
		} else {
			liste.getComponent().setPreferredSize(new Dimension(0, 0));
			liste.getComponent().setVisible(false);
		}

		if (dmlp.getSize() > 0) {
			listp.getComponent().setPreferredSize(null);
			listp.getComponent().setVisible(true);
		} else {
			listp.getComponent().setPreferredSize(new Dimension(0, 0));
			listp.getComponent().setVisible(false);
		}

		if (dmla.getSize() > 0) {
			lista.getComponent().setPreferredSize(null);
			lista.getComponent().setVisible(true);
		} else {
			lista.getComponent().setPreferredSize(new Dimension(0, 0));
			lista.getComponent().setVisible(false);
		}

		if (dmlb.getSize() > 0) {
			listb.getComponent().setPreferredSize(null);
			listb.getComponent().setVisible(true);
		} else {
			listb.getComponent().setPreferredSize(new Dimension(0, 0));
			listb.getComponent().setVisible(false);
		}

		if (dmlo.getSize() > 0) {
			listo.getComponent().setPreferredSize(null);
			listo.getComponent().setVisible(true);
		} else {
			listo.getComponent().setPreferredSize(new Dimension(0, 0));
			listo.getComponent().setVisible(false);
		}

	}

	private class FilterModel extends DefaultListModel<File> {
		List<File> items;
		List<File> filterItems;

		FilterModel() {
			super();
			items = new ArrayList<>();
			filterItems = new ArrayList<>();
		}

		@Override public File getElementAt(int index) {
			if (index < filterItems.size())
				return filterItems.get(index);
			else
				return null;
		}

		@Override public int indexOf(Object elem) {
			if (elem instanceof File)
				return filterItems.indexOf(elem);
			else
				return -1;
		}

		@Override public int getSize() {
			return filterItems.size();
		}

		@Override public void addElement(File o) {
			items.add(o);
			refilter();
		}

		@Override public void removeAllElements() {
			super.removeAllElements();
			items.clear();
			filterItems.clear();
		}

		@Override public boolean removeElement(Object a) {
			if (a instanceof File) {
				items.remove(a);
				filterItems.remove(a);
			}
			return super.removeElement(a);
		}

		void refilter() {
			filterItems.clear();
			String term = workspacePanel.search.getText();
			filterItems.addAll(items.stream().filter(Objects::nonNull)
					.filter(item -> (item.getName().toLowerCase(Locale.ENGLISH)
							.contains(term.toLowerCase(Locale.ENGLISH)))).collect(Collectors.toList()));

			if (workspacePanel.sortName.isSelected()) {
				filterItems.sort(Comparator.comparing(File::getName));
			}

			if (workspacePanel.desc.isSelected())
				Collections.reverse(filterItems);

			fireContentsChanged(this, 0, getSize());
		}
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
			File file = new File(ma.getPath());
			if (isSelected) {
				setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				setBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				setOpaque(true);
			} else {
				setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				setOpaque(false);
			}

			if (ma != null) {
				String name = StringUtils.abbreviateString(FilenameUtils.removeExtension(file.getName()), 10);
				if (name.trim().equals(""))
					name = "(untitled)";

				setText(name);
				setToolTipText(file.getName());

				if (file.getName().endsWith(".png")) {
					if (TEXTURE_CACHE.get(ma) != null && TEXTURE_CACHE.get(ma).getImage() != null)
						setIcon(TEXTURE_CACHE.get(ma));
					else {
						ImageIcon icon = new ImageIcon(
								ImageUtils.resize(new ImageIcon(file.getAbsolutePath()).getImage(), 42));
						TEXTURE_CACHE.put(file, icon);
						setIcon(icon);
					}

					if (!file.getName().matches("[a-z0-9/._-]+")) {
						if (getIcon() instanceof ImageIcon) {
							ImageIcon icon = (ImageIcon) getIcon();
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

	private static class JComponentWithList<T> {
		private final JComponent component;
		private final JList<T> list;

		public JComponentWithList(JComponent component, JList<T> list) {
			this.component = component;
			this.list = list;
		}

		public JComponent getComponent() {
			return component;
		}

		public JList<T> getList() {
			return list;
		}
	}

}
