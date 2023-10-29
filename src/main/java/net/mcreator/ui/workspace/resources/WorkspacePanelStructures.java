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
import net.mcreator.ui.dialogs.SearchUsagesDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.workspace.references.ReferencesFinder;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class WorkspacePanelStructures extends JPanel implements IReloadableFilterable {

	private final WorkspacePanel workspacePanel;

	private final ResourceFilterModel<String> filterModel;

	WorkspacePanelStructures(WorkspacePanel workspacePanel) {
		super(new BorderLayout());
		setOpaque(false);

		this.workspacePanel = workspacePanel;
		this.filterModel = new ResourceFilterModel<>(workspacePanel, String::toString);

		JSelectableList<String> structureElementList = new JSelectableList<>(filterModel);
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

		JButton search = L10N.button("common.search_usages");
		search.setIcon(UIRES.get("16px.search"));
		search.setContentAreaFilled(false);
		search.setOpaque(false);
		ComponentUtils.deriveFont(search, 12);
		search.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(search);

		JButton del = L10N.button("workspace.sounds.delete_selected");
		del.setIcon(UIRES.get("16px.delete.gif"));
		del.setOpaque(false);
		del.setContentAreaFilled(false);
		del.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(del);

		search.addActionListener(a -> {
			if (!structureElementList.isSelectionEmpty()) {
				workspacePanel.getMCreator().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				Set<ModElement> refs = new HashSet<>();
				for (String structure : structureElementList.getSelectedValuesList()) {
					refs.addAll(ReferencesFinder.searchStructureUsages(workspacePanel.getMCreator().getWorkspace(),
							structure));
				}

				workspacePanel.getMCreator().setCursor(Cursor.getDefaultCursor());
				SearchUsagesDialog.showUsages(workspacePanel.getMCreator(),
						L10N.t("dialog.search_usages.type.resource.structure"), new ArrayList<>(refs));
			}
		});
		del.addActionListener(a -> deleteCurrentlySelected(structureElementList));

		structureElementList.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteCurrentlySelected(structureElementList);
				}
			}
		});

		importnbt.addActionListener(e -> workspacePanel.getMCreator().actionRegistry.importStructure.doAction());
		importmc.addActionListener(
				e -> workspacePanel.getMCreator().actionRegistry.importStructureFromMinecraft.doAction());

		add("North", bar);

	}

	private void deleteCurrentlySelected(JSelectableList<String> structureElementList) {
		List<String> files = structureElementList.getSelectedValuesList();
		if (!files.isEmpty()) {
			workspacePanel.getMCreator().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			Set<ModElement> references = new HashSet<>();
			for (String structure : files) {
				references.addAll(
						ReferencesFinder.searchStructureUsages(workspacePanel.getMCreator().getWorkspace(), structure));
			}

			workspacePanel.getMCreator().setCursor(Cursor.getDefaultCursor());

			if (SearchUsagesDialog.show(workspacePanel.getMCreator(),
					L10N.t("dialog.search_usages.type.resource.structure"), new ArrayList<>(references), true)) {
				files.forEach(workspacePanel.getMCreator().getFolderManager()::removeStructure);
				reloadElements();
			}
		}
	}

	@Override public void reloadElements() {
		filterModel.removeAllElements();
		workspacePanel.getMCreator().getFolderManager().getStructureList().forEach(filterModel::addElement);
		refilterElements();
	}

	@Override public void refilterElements() {
		filterModel.refilter();
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
