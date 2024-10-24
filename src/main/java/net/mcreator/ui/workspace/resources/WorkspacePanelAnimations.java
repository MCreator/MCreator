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

import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.resources.Animation;

import javax.swing.*;
import java.util.List;
import java.util.Locale;

public class WorkspacePanelAnimations extends AbstractResourcePanel<Animation> {

	WorkspacePanelAnimations(WorkspacePanel workspacePanel) {
		super(workspacePanel, new ResourceFilterModel<>(workspacePanel,
						(item, query) -> item.getName().toLowerCase(Locale.ENGLISH).contains(query), Animation::getName),
				new Render(), JList.HORIZONTAL_WRAP);

		if (workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("model_animations_java")
				!= GeneratorStats.CoverageStatus.NONE)
			addToolBarButton("action.workspace.resources.import_java_animation",
					UIRES.get("16px.importjavamodelanimation"),
					e -> workspacePanel.getMCreator().actionRegistry.importJavaModelAnimation.doAction());

		//TODO: Usages system
		/*addToolBarButton("common.search_usages", UIRES.get("16px.search"), e -> {
			if (!elementList.isSelectionEmpty()) {
				workspacePanel.getMCreator().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				Set<ModElement> refs = new HashSet<>();
				for (Animation model : elementList.getSelectedValuesList())
					refs.addAll(ReferencesFinder.searchModelUsages(workspacePanel.getMCreator().getWorkspace(), model));

				workspacePanel.getMCreator().setCursor(Cursor.getDefaultCursor());
				SearchUsagesDialog.showUsagesDialog(workspacePanel.getMCreator(),
						L10N.t("dialog.search_usages.type.resource.model"), refs);
			}
		});*/
		addToolBarButton("common.delete_selected", UIRES.get("16px.delete"), e -> deleteCurrentlySelected());
	}

	@Override void deleteCurrentlySelected() {
		List<Animation> elements = elementList.getSelectedValuesList();
		if (!elements.isEmpty()) {
			//TODO: Usages system
			/*workspacePanel.getMCreator().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			Set<ModElement> references = new HashSet<>();
			for (Animation m : elementList.getSelectedValuesList())
				references.addAll(ReferencesFinder.searchModelUsages(workspacePanel.getMCreator().getWorkspace(), m));

			workspacePanel.getMCreator().setCursor(Cursor.getDefaultCursor());

			if (SearchUsagesDialog.showDeleteDialog(workspacePanel.getMCreator(),
					L10N.t("dialog.search_usages.type.resource.model"), references)) {*/
			elements.forEach(animation -> animation.getFile().delete());
			reloadElements();
			/*}*///TODO: Usages system
		}
	}

	@Override public void reloadElements() {
		filterModel.removeAllElements();
		filterModel.addAll(Animation.getAnimations(workspacePanel.getMCreator().getWorkspace()));
	}

	static class Render extends JLabel implements ListCellRenderer<Animation> {

		@Override
		public JLabel getListCellRendererComponent(JList<? extends Animation> list, Animation ma, int index,
				boolean isSelected, boolean cellHasFocus) {
			setOpaque(isSelected);
			setBackground(isSelected ? Theme.current().getAltBackgroundColor() : Theme.current().getBackgroundColor());
			setText("(" + ma.getSubanimations().size() + ") " + StringUtils.abbreviateString(ma.getName(), 13));
			setToolTipText(ma.getName());
			ComponentUtils.deriveFont(this, 11);
			setVerticalTextPosition(BOTTOM);
			setHorizontalTextPosition(CENTER);
			setHorizontalAlignment(CENTER);
			setIcon(UIRES.get("model.javaanimation"));
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			return this;
		}

	}

}
