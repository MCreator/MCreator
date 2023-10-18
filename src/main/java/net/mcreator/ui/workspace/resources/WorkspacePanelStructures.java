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
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.workspace.WorkspacePanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WorkspacePanelStructures extends AbstractResourcePanel<String> {

	WorkspacePanelStructures(WorkspacePanel workspacePanel) {
		super(workspacePanel, new ResourceFilterModel<>(workspacePanel, String::toString), new Render());

		addToolBarButton("action.workspace.resources.import_structure", UIRES.get("16px.open.gif"),
				e -> workspacePanel.getMCreator().actionRegistry.importStructure.doAction());
		addToolBarButton("action.workspace.resources.import_structure_from_minecraft", UIRES.get("16px.open.gif"),
				e -> workspacePanel.getMCreator().actionRegistry.importStructureFromMinecraft.doAction());
		addToolBarButton("workspace.sounds.delete_selected", UIRES.get("16px.delete.gif"),
				e -> deleteCurrentlySelected());
	}

	@Override void deleteCurrentlySelected() {
		List<String> elements = elementList.getSelectedValuesList();
		if (!elements.isEmpty()) {
			int n = JOptionPane.showConfirmDialog(workspacePanel.getMCreator(),
					L10N.t("workspace.structure.confirm_deletion_message"), L10N.t("common.confirmation"),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (n == 0) {
				elements.forEach(workspacePanel.getMCreator().getFolderManager()::removeStructure);
				reloadElements();
			}
		}
	}

	@Override public void reloadElements() {
		filterModel.removeAllElements();
		workspacePanel.getMCreator().getFolderManager().getStructureList().forEach(filterModel::addElement);
		refilterElements();
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
