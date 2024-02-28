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
import net.mcreator.ui.dialogs.SoundElementDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.references.ReferencesFinder;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkspacePanelSounds extends AbstractResourcePanel<SoundElement> {

	WorkspacePanelSounds(WorkspacePanel workspacePanel) {
		super(workspacePanel, new ResourceFilterModel<>(workspacePanel, SoundElement::getName), new Render());

		addToolBarButton("action.workspace.resources.import_sound", UIRES.get("16px.open"),
				e -> workspacePanel.getMCreator().actionRegistry.importSound.doAction());
		addToolBarButton("workspace.sounds.edit_selected", UIRES.get("16px.edit"),
				e -> editSelectedSound(elementList.getSelectedValue()));
		addToolBarButton("common.search_usages", UIRES.get("16px.search"), e -> {
			if (!elementList.isSelectionEmpty()) {
				workspacePanel.getMCreator().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				Set<ModElement> refs = new HashSet<>();
				for (SoundElement sound : elementList.getSelectedValuesList())
					refs.addAll(ReferencesFinder.searchSoundUsages(workspacePanel.getMCreator().getWorkspace(), sound));

				workspacePanel.getMCreator().setCursor(Cursor.getDefaultCursor());
				SearchUsagesDialog.showUsagesDialog(workspacePanel.getMCreator(),
						L10N.t("dialog.search_usages.type.resource.sound"), refs);
			}
		});
		addToolBarButton("common.delete_selected", UIRES.get("16px.delete"), e -> deleteCurrentlySelected());
	}

	private void editSelectedSound(SoundElement selectedValue) {
		if (selectedValue != null) {
			SoundElementDialog.soundDialog(workspacePanel.getMCreator(), selectedValue, null);
			workspacePanel.getMCreator().getWorkspace().markDirty();
			reloadElements();
		}
	}

	@Override void deleteCurrentlySelected() {
		List<SoundElement> elements = elementList.getSelectedValuesList();
		if (!elements.isEmpty()) {
			workspacePanel.getMCreator().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			Set<ModElement> references = new HashSet<>();
			for (SoundElement s : elements)
				references.addAll(ReferencesFinder.searchSoundUsages(workspacePanel.getMCreator().getWorkspace(), s));

			workspacePanel.getMCreator().setCursor(Cursor.getDefaultCursor());

			if (SearchUsagesDialog.showDeleteDialog(workspacePanel.getMCreator(),
					L10N.t("dialog.search_usages.type.resource.sound"), references)) {
				elements.forEach(workspacePanel.getMCreator().getWorkspace()::removeSoundElement);
				reloadElements();
			}
		}
	}

	@Override public void reloadElements() {
		filterModel.removeAllElements();
		filterModel.addAll(workspacePanel.getMCreator().getWorkspace().getSoundElements());
	}

	static class Render extends JPanel implements ListCellRenderer<SoundElement> {

		Render() {
			setLayout(new GridLayout());
			setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
		}

		@Override
		public JPanel getListCellRendererComponent(JList<? extends SoundElement> list, SoundElement ma, int index,
				boolean isSelected, boolean cellHasFocus) {

			removeAll();

			JPanel cont = new JPanel(new BorderLayout());
			cont.setBackground(isSelected ?
					(Theme.current().getAltBackgroundColor()).brighter() :
					Theme.current().getAltBackgroundColor());
			cont.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

			JPanel namepan = new JPanel(new BorderLayout());
			namepan.setBorder(BorderFactory.createEmptyBorder(0, 8, 5, 0));
			namepan.setOpaque(false);

			JLabel name = new JLabel(ma.getName());
			name.setFont(Theme.current().getSecondaryFont().deriveFont(20.0f));
			namepan.add("North", name);

			JLabel name2 = L10N.label("workspace.sounds.files", String.join(", ", ma.getFiles()));
			ComponentUtils.deriveFont(name2, 11);
			namepan.add("South", name2);

			JPanel iconpn = new JPanel(new BorderLayout());
			iconpn.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
			iconpn.setOpaque(false);
			iconpn.add("West", new JLabel(UIRES.get("note")));
			iconpn.add("Center", namepan);

			String rightText;

			if (ma.getSubtitle() != null && !ma.getSubtitle().isEmpty()) {
				rightText = L10N.t("workspace.sounds.subtitle_and_category", ma.getSubtitle(), ma.getCategory());
			} else {
				rightText = L10N.t("workspace.sounds.category", ma.getCategory());
			}

			JLabel rightTextLabel = new JLabel(rightText);
			ComponentUtils.deriveFont(rightTextLabel, 17);
			cont.add("East", rightTextLabel);

			cont.add("West", iconpn);

			add(cont);
			setOpaque(false);

			return this;
		}

	}

}
