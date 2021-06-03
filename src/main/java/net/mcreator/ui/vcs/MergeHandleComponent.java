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

import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.vcs.FileSyncHandle;
import net.mcreator.vcs.diff.MergeHandle;
import net.mcreator.vcs.diff.ResultSide;
import net.mcreator.workspace.settings.WorkspaceSettings;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;

class MergeHandleComponent extends JPanel {

	protected final JRadioButton local;
	protected final JRadioButton remote;

	MergeHandleComponent(List<MergeHandleComponent> mergeHandleComponents, MergeHandle<?> mergeHandle) {
		super(new BorderLayout(40, 2));
		setMinimumSize(new Dimension(400, 10));
		local = L10N.radiobutton("dialog.vcs.merge_handle_accept_mine",
				mergeHandle.getLocalChange().name().toLowerCase(Locale.ENGLISH));
		remote = L10N.radiobutton("dialog.vcs.merge_handle_accept_theirs",
				mergeHandle.getLocalChange().name().toLowerCase(Locale.ENGLISH));

		if (mergeHandle.getLocal() instanceof FileSyncHandle) {
			add("Center",
					PanelUtils.centerInPanel(new JLabel(((FileSyncHandle) mergeHandle.getLocal()).getLocalPath())));
		} else if (mergeHandle.getLocal() instanceof WorkspaceSettings) {
			add("Center", PanelUtils.centerInPanel(L10N.label("dialog.vcs.merge_handle_workspace_settings")));
		} else {
			add("Center", PanelUtils.centerInPanel(new JLabel(mergeHandle.getLocal().toString())));
		}

		add("West", local);
		add("East", remote);

		local.addActionListener(e -> mergeHandle.selectResultSide(ResultSide.LOCAL));
		remote.addActionListener(e -> mergeHandle.selectResultSide(ResultSide.REMOTE));

		local.setSelected(true);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(local);
		buttonGroup.add(remote);

		local.setSelected(true);

		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));

		mergeHandleComponents.add(this);
	}

}
