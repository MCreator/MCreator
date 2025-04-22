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
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.SearchUsagesDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.DesktopUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ReferencesFinder;
import net.mcreator.workspace.resources.Animation;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class WorkspacePanelAnimations extends AbstractResourcePanel<Animation> {

	WorkspacePanelAnimations(WorkspacePanel workspacePanel) {
		super(workspacePanel, new ResourceFilterModel<>(workspacePanel,
						(item, query) -> item.getName().toLowerCase(Locale.ENGLISH).contains(query), Animation::getName),
				new Render(), JList.HORIZONTAL_WRAP);

		if (workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("model_animations_java")
				!= GeneratorStats.CoverageStatus.NONE)
			addToolBarButton("action.workspace.resources.import_java_animation",
					UIRES.get("16px.importjavamodelanimation"),
					e -> workspacePanel.getMCreator().getActionRegistry().importJavaModelAnimation.doAction());

		addToolBarButton("common.search_usages", UIRES.get("16px.search"), e -> {
			if (!elementList.isSelectionEmpty()) {
				workspacePanel.getMCreator().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				Set<ModElement> refs = new HashSet<>();
				for (Animation animation : elementList.getSelectedValuesList())
					refs.addAll(ReferencesFinder.searchAnimationUsages(workspacePanel.getMCreator().getWorkspace(),
							animation));

				workspacePanel.getMCreator().setCursor(Cursor.getDefaultCursor());
				SearchUsagesDialog.showUsagesDialog(workspacePanel.getMCreator(),
						L10N.t("dialog.search_usages.type.resource.animation"), refs);
			}
		});
		addToolBarButton("common.delete_selected", UIRES.get("16px.delete"), e -> deleteCurrentlySelected());

		addToolBarButton("action.workspace.resources.animations.help", UIRES.get("16px.info"),
				e -> DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/wiki/entity-model-animations"));
	}

	@Override void deleteCurrentlySelected() {
		List<Animation> elements = elementList.getSelectedValuesList();
		if (!elements.isEmpty()) {
			workspacePanel.getMCreator().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			Set<ModElement> references = new HashSet<>();
			for (Animation a : elementList.getSelectedValuesList())
				references.addAll(
						ReferencesFinder.searchAnimationUsages(workspacePanel.getMCreator().getWorkspace(), a));

			workspacePanel.getMCreator().setCursor(Cursor.getDefaultCursor());

			if (SearchUsagesDialog.showDeleteDialog(workspacePanel.getMCreator(),
					L10N.t("dialog.search_usages.type.resource.model"), references)) {
				elements.forEach(animation -> animation.getFile().delete());
				reloadElements();
			}
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
			StringBuilder sb = new StringBuilder(ma.getName()).append("\n");
			for (String s : ma.getSubanimations())
				sb.append("-").append(s).append("\n");
			setToolTipText(sb.toString().trim());
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
