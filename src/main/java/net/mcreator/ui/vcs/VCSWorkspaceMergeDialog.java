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

		List<MergeHandleComponent> mergeHandleComponents = new ArrayList<>();

		JButton allLocal = new JButton("All local");
		JButton allRemote = new JButton("All remote");
		allLocal.addActionListener(e -> mergeHandleComponents.forEach(mhc -> mhc.local.setSelected(true)));
		allRemote.addActionListener(e -> mergeHandleComponents.forEach(mhc -> mhc.remote.setSelected(true)));
		merges.add(PanelUtils.westAndEastElement(allLocal, allRemote));

		if (input.getWorkspaceSettingsMergeHandle() != null) {
			merges.add(
					PanelUtils.join(FlowLayout.LEFT, ComponentUtils.deriveFont(new JLabel("Workspace settings"), 19)));
			merges.add(new MergeHandleComponent(mergeHandleComponents, input.getWorkspaceSettingsMergeHandle()));
		}

		if (input.getWorkspaceFoldersMergeHandle() != null) {
			merges.add(
					PanelUtils.join(FlowLayout.LEFT, ComponentUtils.deriveFont(new JLabel("Folder structure"), 19)));
			merges.add(new MergeHandleComponent(mergeHandleComponents, input.getWorkspaceFoldersMergeHandle()));
		}

		if (input.getConflictingModElements().size() > 0) {
			merges.add(PanelUtils.join(FlowLayout.LEFT, ComponentUtils.deriveFont(new JLabel("Mod elements"), 19)));
			for (MergeHandle<ModElement> modElementMergeHandle : input.getConflictingModElements())
				merges.add(new MergeHandleComponent(mergeHandleComponents, modElementMergeHandle));
		}

		if (input.getConflictingVariableElements().size() > 0) {
			merges.add(
					PanelUtils.join(FlowLayout.LEFT, ComponentUtils.deriveFont(new JLabel("Variable elements"), 19)));
			for (MergeHandle<VariableElement> variableElementMergeHandle : input.getConflictingVariableElements())
				merges.add(new MergeHandleComponent(mergeHandleComponents, variableElementMergeHandle));
		}

		if (input.getConflictingSoundElements().size() > 0) {
			merges.add(PanelUtils.join(FlowLayout.LEFT, ComponentUtils.deriveFont(new JLabel("Sound elements"), 19)));
			for (MergeHandle<SoundElement> soundElementMergeHandle : input.getConflictingSoundElements())
				merges.add(new MergeHandleComponent(mergeHandleComponents, soundElementMergeHandle));
		}

		if (input.getConflictingLangMaps().size() > 0) {
			merges.add(PanelUtils.join(FlowLayout.LEFT, ComponentUtils.deriveFont(new JLabel("Language maps"), 19)));
			for (MergeHandle<String> languageMapMergeHandle : input.getConflictingLangMaps())
				merges.add(new MergeHandleComponent(mergeHandleComponents, languageMapMergeHandle));
		}

		JScrollPane scrollPane = new JScrollPane(PanelUtils.totalCenterInPanel(merges));
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(410, 400));

		dialog.add("Center", scrollPane);
		dialog.add("North", new JLabel(
				"<html><b>Some of the workspace elements were changed both on remote and your local side.</b>"
						+ "<br><small>For such elements, you need to choose if you want to keep your local changes or the changes from the<br>"
						+ "remote workspace. To do this, select local or remote side to be preserved for each element listed below<br>"
						+ "and click \"Finish merge\" after you are done."));

		JOptionPane.showOptionDialog(mcreator, dialog, "Manual workspace merge required", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, new String[] { "Finish merge" }, null);
	}

}
