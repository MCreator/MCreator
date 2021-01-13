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
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.vcs.FileSyncHandle;
import net.mcreator.vcs.diff.MergeHandle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class VCSFileMergeDialog {

	static void show(MCreator mcreator, List<MergeHandle<FileSyncHandle>> unmergedPaths) {
		if (unmergedPaths.size() == 0)
			return;

		JPanel dialog = new JPanel(new BorderLayout());

		JPanel merges = new JPanel();
		merges.setLayout(new BoxLayout(merges, BoxLayout.Y_AXIS));

		List<MergeHandleComponent> mergeHandleComponents = new ArrayList<>();

		JButton allLocal = L10N.button("dialog.vcs.file_merge_all_local");
		JButton allRemote = L10N.button("dialog.vcs.file_merge_all_remote");
		allLocal.addActionListener(e -> mergeHandleComponents.forEach(mhc -> mhc.local.setSelected(true)));
		allRemote.addActionListener(e -> mergeHandleComponents.forEach(mhc -> mhc.remote.setSelected(true)));
		merges.add(PanelUtils.westAndEastElement(allLocal, allRemote));

		for (MergeHandle<FileSyncHandle> modElementMergeHandle : unmergedPaths)
			merges.add(new MergeHandleComponent(mergeHandleComponents, modElementMergeHandle));

		JScrollPane scrollPane = new JScrollPane(PanelUtils.totalCenterInPanel(merges));
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(650, 350));

		dialog.add("Center", scrollPane);
		dialog.add("North", L10N.label("dialog.vcs.file_merge_manual_message"));

		JOptionPane.showOptionDialog(mcreator, dialog, L10N.t("dialog.vcs.file_merge_manual_required"), JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, new String[] { "Finish merge" }, null);
	}

}
