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

import net.mcreator.ui.component.JSelectableList;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class WorkspacePanelStructures extends JPanel implements IReloadableFilterable {

	private final WorkspacePanel workspacePanel;

	private final FilterModel listmodel = new FilterModel();

	WorkspacePanelStructures(WorkspacePanel workspacePanel) {
		super(new BorderLayout());
		setOpaque(false);

		this.workspacePanel = workspacePanel;

		JSelectableList<String> structureElementList = new JSelectableList<>(listmodel);
		structureElementList.setOpaque(false);
		structureElementList.setCellRenderer(new Render());
		structureElementList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		JScrollPane sp = new JScrollPane(structureElementList);
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

		JButton importnbt = L10N.button("action.workspace.resources.import_structure");
		importnbt.setIcon(UIRES.get("16px.open.gif"));
		importnbt.setContentAreaFilled(false);
		importnbt.setOpaque(false);
		ComponentUtils.deriveFont(importnbt, 12);
		importnbt.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(importnbt);

		JButton importmc = L10N.button("action.workspace.resources.import_structure_from_minecraft");
		importmc.setIcon(UIRES.get("16px.open.gif"));
		importmc.setContentAreaFilled(false);
		importmc.setOpaque(false);
		ComponentUtils.deriveFont(importmc, 12);
		importmc.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(importmc);

		JButton del = L10N.button("workspace.sounds.delete_selected");
		del.setIcon(UIRES.get("16px.delete.gif"));
		del.setOpaque(false);
		del.setContentAreaFilled(false);
		del.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(del);

		del.addActionListener(actionEvent -> {
			List<String> files = structureElementList.getSelectedValuesList();
			if (files.size() > 0) {
				int n = JOptionPane.showConfirmDialog(workspacePanel.getMcreator(),
						L10N.t("workspace.structure.confirm_deletion_message"),
						L10N.t("workspace.structure.confirm_deletion_title"), JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (n == 0) {
					files.forEach(workspacePanel.getMcreator().getFolderManager()::removeStructure);
					reloadElements();
				}
			}
		});

		importnbt.addActionListener(e -> workspacePanel.getMcreator().actionRegistry.importStructure.doAction());
		importmc.addActionListener(
				e -> workspacePanel.getMcreator().actionRegistry.importStructureFromMinecraft.doAction());

		add("North", bar);

	}

	@Override public void reloadElements() {
		listmodel.removeAllElements();
		workspacePanel.getMcreator().getFolderManager().getStructureList().forEach(listmodel::addElement);
		refilterElements();
	}

	@Override public void refilterElements() {
		listmodel.refilter();
	}

	private class FilterModel extends DefaultListModel<String> {
		List<String> items;
		List<String> filterItems;

		FilterModel() {
			super();
			items = new ArrayList<>();
			filterItems = new ArrayList<>();
		}

		@Override public int indexOf(Object elem) {
			if (elem instanceof String)
				return filterItems.indexOf(elem);
			else
				return -1;
		}

		@Override public String getElementAt(int index) {
			if (index < filterItems.size())
				return filterItems.get(index);
			else
				return null;
		}

		@Override public int getSize() {
			return filterItems.size();
		}

		@Override public void addElement(String o) {
			items.add(o);
			refilter();
		}

		@Override public void removeAllElements() {
			super.removeAllElements();
			items.clear();
			filterItems.clear();
		}

		@Override public boolean removeElement(Object a) {
			if (a instanceof String) {
				items.remove(a);
				filterItems.remove(a);
			}
			return super.removeElement(a);
		}

		void refilter() {
			filterItems.clear();
			String term = workspacePanel.search.getText();
			filterItems.addAll(items.stream().filter(Objects::nonNull)
					.filter(item -> (item.toLowerCase(Locale.ENGLISH).contains(term.toLowerCase(Locale.ENGLISH))))
					.collect(Collectors.toList()));

			if (workspacePanel.sortName.isSelected()) {
				filterItems.sort(Comparator.comparing(String::toString));
			}

			if (workspacePanel.desc.isSelected())
				Collections.reverse(filterItems);

			fireContentsChanged(this, 0, getSize());
		}
	}

	static class Render extends JLabel implements ListCellRenderer<String> {

		Render() {
			setLayout(new GridLayout());
			setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
		}

		@Override
		public JLabel getListCellRendererComponent(JList<? extends String> list, String ma, int index,
				boolean isSelected, boolean cellHasFocus) {
			setOpaque(isSelected);
			setBackground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.MAIN_TINT") :
					(Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			setText(" " + ma);
			ComponentUtils.deriveFont(this, 17);
			setIcon(UIRES.get("16px.ext.gif"));
			setBorder(BorderFactory.createEmptyBorder(5, 13, 5, 0));
			return this;
		}

	}

}
