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

package net.mcreator.ui.vcs;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.vcs.diff.MergeHandle;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.elements.VariableElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class VCSWorkspaceMergeDialog {

	static void show(MCreator mcreator, WorkspaceMergeHandles input) {
		JPanel dialog = new JPanel(new BorderLayout());

		JPanel merges = new JPanel();
		merges.setLayout(new BoxLayout(merges, BoxLayout.Y_AXIS));
		merges.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		merges.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		List<MergeHandleComponent> mergeHandleComponents = new ArrayList<>();

		JButton allLocal = L10N.button("dialog.vcs.element_merge_all_local");
		JButton allRemote = L10N.button("dialog.vcs.element_merge_all_remote");
		allLocal.addActionListener(e -> mergeHandleComponents.forEach(mhc -> mhc.local.setSelected(true)));
		allRemote.addActionListener(e -> mergeHandleComponents.forEach(mhc -> mhc.remote.setSelected(true)));
		merges.add(PanelUtils.westAndEastElement(allLocal, allRemote));

		if (input.workspaceSettingsMergeHandle() != null) {
			merges.add(PanelUtils.join(FlowLayout.LEFT,
					ComponentUtils.deriveFont(L10N.label("dialog.vcs.element_merge_elements.workspace_settings"), 19)));
			merges.add(new MergeHandleComponent(mergeHandleComponents, input.workspaceSettingsMergeHandle()));
		}

		if (input.workspaceFoldersMergeHandle() != null) {
			merges.add(PanelUtils.join(FlowLayout.LEFT,
					ComponentUtils.deriveFont(L10N.label("dialog.vcs.element_merge_elements.folder_structure"), 19)));
			merges.add(new MergeHandleComponent(mergeHandleComponents, input.workspaceFoldersMergeHandle()));
		}

		if (input.conflictingModElements().size() > 0) {
			merges.add(PanelUtils.join(FlowLayout.LEFT,
					ComponentUtils.deriveFont(L10N.label("dialog.vcs.element_merge_elements.mod_elements"), 19)));
			for (MergeHandle<ModElement> modElementMergeHandle : input.conflictingModElements())
				merges.add(new MergeHandleComponent(mergeHandleComponents, modElementMergeHandle));
		}

		if (input.conflictingVariableElements().size() > 0) {
			merges.add(PanelUtils.join(FlowLayout.LEFT,
					ComponentUtils.deriveFont(L10N.label("dialog.vcs.element_merge_elements.variable_elements"), 19)));
			for (MergeHandle<VariableElement> variableElementMergeHandle : input.conflictingVariableElements())
				merges.add(new MergeHandleComponent(mergeHandleComponents, variableElementMergeHandle));
		}

		if (input.conflictingSoundElements().size() > 0) {
			merges.add(PanelUtils.join(FlowLayout.LEFT,
					ComponentUtils.deriveFont(L10N.label("dialog.vcs.element_merge_elements.sound_elements"), 19)));
			for (MergeHandle<SoundElement> soundElementMergeHandle : input.conflictingSoundElements())
				merges.add(new MergeHandleComponent(mergeHandleComponents, soundElementMergeHandle));
		}

		if (input.conflictingLangMaps().size() > 0) {
			merges.add(PanelUtils.join(FlowLayout.LEFT,
					ComponentUtils.deriveFont(L10N.label("dialog.vcs.element_merge_elements.language_maps"), 19)));
			for (MergeHandle<String> languageMapMergeHandle : input.conflictingLangMaps())
				merges.add(new MergeHandleComponent(mergeHandleComponents, languageMapMergeHandle));
		}

		JScrollPane scrollPane = new JScrollPane(PanelUtils.totalCenterInPanel(merges));
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setMaximumSize(new Dimension(410, 400));

		dialog.add("Center", scrollPane);
		dialog.add("North", L10N.label("dialog.vcs.element_merge_manual_message"));

		JOptionPane.showOptionDialog(mcreator, dialog, L10N.t("dialog.vcs.element_merge_manual_required"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				new String[] { L10N.t("dialog.vcs.element_merge_finish") }, null);
	}

}
