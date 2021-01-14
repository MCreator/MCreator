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
import net.mcreator.ui.component.JSelectableList;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.ListUtil;
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

class WorkspacePanelScreenshots extends JPanel implements IReloadableFilterable {

	private final WorkspacePanel workspacePanel;

	private final FilterModel listmodel = new FilterModel();
	private final JSelectableList<File> modelList = new JSelectableList<>(listmodel);

	WorkspacePanelScreenshots(WorkspacePanel workspacePanel) {
		super(new BorderLayout());
		setOpaque(false);

		this.workspacePanel = workspacePanel;

		modelList.setOpaque(false);
		modelList.setCellRenderer(new Render());
		modelList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		modelList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		modelList.setVisibleRowCount(-1);

		JScrollPane sp = new JScrollPane(modelList);
		sp.setOpaque(false);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.getViewport().setOpaque(false);
		sp.getVerticalScrollBar().setUnitIncrement(11);
		sp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), sp.getVerticalScrollBar()));
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

		add("Center", sp);

		TransparentToolBar bar = new TransparentToolBar();
		bar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));

		JButton edit = L10N.button("workspace.screenshots.export_selected");
		edit.setIcon(UIRES.get("16px.ext.gif"));
		edit.setOpaque(false);
		edit.setContentAreaFilled(false);
		edit.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(edit);
		edit.addActionListener(e -> exportSelectedScreenshots());

		JButton useasbg = L10N.button("workspace.screenshots.use_as_background");
		useasbg.setIcon(UIRES.get("16px.textures"));
		useasbg.setOpaque(false);
		useasbg.setContentAreaFilled(false);
		useasbg.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(useasbg);
		useasbg.addActionListener(e -> useSelectedAsBackgrounds());

		JButton del = L10N.button("workspace.screenshots.delete_selected");
		del.setIcon(UIRES.get("16px.delete.gif"));
		del.setOpaque(false);
		del.setContentAreaFilled(false);
		del.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(del);
		del.addActionListener(e -> {
			modelList.getSelectedValuesList().forEach(File::delete);
			reloadElements();
		});

		modelList.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					exportSelectedScreenshots();
			}
		});

		add("North", bar);
	}

	private void useSelectedAsBackgrounds() {
		modelList.getSelectedValuesList().forEach(
				f -> FileIO.copyFile(f, new File(UserFolderManager.getFileFromUserFolder("backgrounds"), f.getName())));
		JOptionPane.showMessageDialog(workspacePanel.getMcreator(),
				L10N.t("workspace.screenshots.use_background_message"),
				L10N.t("workspace.screenshots.action_complete"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void exportSelectedScreenshots() {
		modelList.getSelectedValuesList().forEach(f -> {
			File to = FileDialogs.getSaveDialog(workspacePanel.getMcreator(), new String[] { ".png" });
			if (to != null)
				FileIO.copyFile(f, to);
		});
	}

	@Override public void reloadElements() {
		List<File> selected = modelList.getSelectedValuesList();

		listmodel.removeAllElements();
		File[] screenshots = new File(workspacePanel.getMcreator().getWorkspaceFolder(), "run/screenshots/")
				.listFiles();
		if (screenshots != null)
			Arrays.stream(screenshots).forEach(listmodel::addElement);

		ListUtil.setSelectedValues(modelList, selected);

		refilterElements();
	}

	@Override public void refilterElements() {
		listmodel.refilter();
	}

	private class FilterModel extends DefaultListModel<File> {
		List<File> items;
		List<File> filterItems;

		FilterModel() {
			super();
			items = new ArrayList<>();
			filterItems = new ArrayList<>();
		}

		@Override public int indexOf(Object elem) {
			if (elem instanceof File)
				return filterItems.indexOf(elem);
			else
				return -1;
		}

		@Override public File getElementAt(int index) {
			if (index < filterItems.size())
				return filterItems.get(index);
			else
				return null;
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
					.filter(item -> item.getName().toLowerCase(Locale.ENGLISH)
							.contains(term.toLowerCase(Locale.ENGLISH))).collect(Collectors.toList()));

			if (workspacePanel.sortName.isSelected()) {
				filterItems.sort(Comparator.comparing(File::getName));
			}

			if (workspacePanel.desc.isSelected())
				Collections.reverse(filterItems);

			fireContentsChanged(this, 0, getSize());
		}
	}

	static class Render extends JLabel implements ListCellRenderer<File> {

		@Override
		public JLabel getListCellRendererComponent(JList<? extends File> list, File ma, int index, boolean isSelected,
				boolean cellHasFocus) {
			setOpaque(isSelected);
			setBackground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT") :
					(Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			setText(ma.getName());
			ComponentUtils.deriveFont(this, 11);
			setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			setVerticalTextPosition(BOTTOM);
			setHorizontalTextPosition(CENTER);
			setHorizontalAlignment(CENTER);

			setIcon(new ImageIcon(ImageUtils.resize(new ImageIcon(ma.getAbsolutePath()).getImage(), 145, 82)));

			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			return this;
		}

	}

}
