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

import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.SearchUsagesDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ReferencesFinder;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkspacePanelStructures extends AbstractResourcePanel<String> {

	WorkspacePanelStructures(WorkspacePanel workspacePanel) {
		super(workspacePanel, new ResourceFilterModel<>(workspacePanel, String::toString), new Render());

		addToolBarButton("action.workspace.resources.import_structure", UIRES.get("16px.open"),
				e -> workspacePanel.getMCreator().actionRegistry.importStructure.doAction());
		addToolBarButton("action.workspace.resources.import_structure_from_minecraft", UIRES.get("16px.open"),
				e -> workspacePanel.getMCreator().actionRegistry.importStructureFromMinecraft.doAction());
		addToolBarButton("common.search_usages", UIRES.get("16px.search"), e -> {
			if (!elementList.isSelectionEmpty()) {
				workspacePanel.getMCreator().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				Set<ModElement> refs = new HashSet<>();
				for (String structure : elementList.getSelectedValuesList()) {
					refs.addAll(ReferencesFinder.searchStructureUsages(workspacePanel.getMCreator().getWorkspace(),
							structure));
				}

				workspacePanel.getMCreator().setCursor(Cursor.getDefaultCursor());
				SearchUsagesDialog.showUsagesDialog(workspacePanel.getMCreator(),
						L10N.t("dialog.search_usages.type.resource.structure"), refs);
			}
		});
		addToolBarButton("common.delete_selected", UIRES.get("16px.delete"), e -> deleteCurrentlySelected());
	}

	@Override void deleteCurrentlySelected() {
		List<String> elements = elementList.getSelectedValuesList();
		if (!elements.isEmpty()) {
			workspacePanel.getMCreator().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			Set<ModElement> references = new HashSet<>();
			for (String structure : elements) {
				references.addAll(
						ReferencesFinder.searchStructureUsages(workspacePanel.getMCreator().getWorkspace(), structure));
			}

			workspacePanel.getMCreator().setCursor(Cursor.getDefaultCursor());

			if (SearchUsagesDialog.showDeleteDialog(workspacePanel.getMCreator(),
					L10N.t("dialog.search_usages.type.resource.structure"), references)) {
				elements.forEach(workspacePanel.getMCreator().getFolderManager()::removeStructure);
				reloadElements();
			}
		}
	}

	@Override public void reloadElements() {
		filterModel.removeAllElements();
		filterModel.addAll(workspacePanel.getMCreator().getFolderManager().getStructureList());
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
			setBackground(
					isSelected ? Theme.current().getInterfaceAccentColor() : Theme.current().getBackgroundColor());
			setText(" " + ma);
			ComponentUtils.deriveFont(this, 17);
			setIcon(UIRES.get("16px.ext"));
			setBorder(BorderFactory.createEmptyBorder(5, 13, 5, 0));
			return this;
		}

	}

}
