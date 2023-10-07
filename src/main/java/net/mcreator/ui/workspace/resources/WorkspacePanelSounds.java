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
import net.mcreator.ui.dialogs.SoundElementDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.MCreatorTheme;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.ListUtils;
import net.mcreator.util.SoundUtils;
import net.mcreator.workspace.elements.SoundElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.*;

public class WorkspacePanelSounds extends AbstractResourcePanel<SoundElement> {

	WorkspacePanelSounds(WorkspacePanel workspacePanel) {
		super(workspacePanel, new ResourceFilterModel<>(workspacePanel, item -> (item.getName().toLowerCase(Locale.ENGLISH)
				.contains(workspacePanel.search.getText().toLowerCase(Locale.ENGLISH))), Comparator.comparing(SoundElement::getName)), new Render());

		addToolBarButton("action.workspace.resources.import_sound", UIRES.get("16px.open.gif"),
				e -> workspacePanel.getMCreator().actionRegistry.importSound.doAction());
		addToolBarButton("workspace.sounds.edit_selected", UIRES.get("16px.edit.gif"),
				e -> editSelectedSound(elementList.getSelectedValue()));
		addToolBarButton("workspace.sounds.delete_selected", UIRES.get("16px.delete.gif"),
				e -> deleteCurrentlySelected(Collections.singletonList(elementList.getSelectedValue())));
		addToolBarButton("workspace.sounds.play_selected", UIRES.get("16px.play"), new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				SoundElement soundElement = elementList.getSelectedValue();
				if (soundElement != null) {
					if (!soundElement.getFiles().isEmpty()) {
						SoundUtils.playSound(
								new File(workspacePanel.getMCreator().getWorkspace().getFolderManager().getSoundsDir(),
										ListUtils.getRandomItem(soundElement.getFiles()) + ".ogg"));
					}
				}
			}
			@Override public void mouseReleased(MouseEvent e) {
				SoundUtils.stopAllSounds();
			}

		});
	}

	private void editSelectedSound(SoundElement selectedValue) {
		if (selectedValue != null) {
			SoundElementDialog.soundDialog(workspacePanel.getMCreator(), selectedValue, null);
			workspacePanel.getMCreator().getWorkspace().markDirty();
			reloadElements();
		}
	}

	@Override void deleteCurrentlySelected(List<SoundElement> elements) {
		if (!elements.isEmpty()) {
			int n = JOptionPane.showConfirmDialog(workspacePanel.getMCreator(),
					L10N.t("workspace.sounds.confirm_deletion_message"), L10N.t("common.confirmation"),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (n == 0) {
				elements.forEach(workspacePanel.getMCreator().getWorkspace()::removeSoundElement);
				reloadElements();
			}
		}
	}

	@Override public void reloadElements() {
		filterModel.removeAllElements();
		workspacePanel.getMCreator().getWorkspace().getSoundElements().forEach(filterModel::addElement);
		refilterElements();
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
					((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).brighter() :
					(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			cont.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

			JPanel namepan = new JPanel(new BorderLayout());
			namepan.setBorder(BorderFactory.createEmptyBorder(0, 8, 5, 0));
			namepan.setOpaque(false);

			JLabel name = new JLabel(ma.getName());
			name.setFont(MCreatorTheme.secondary_font.deriveFont(20.0f));
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
