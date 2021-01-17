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

import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.L10N;
import org.eclipse.jgit.api.Status;

import javax.swing.*;
import java.awt.*;

public class LocalChangesPanel extends JPanel {

	public LocalChangesPanel(Status status) {
		super(new BorderLayout());

		JLabel changes = L10N.label("dialog.remote_workspace.local_uncommited_changes");
		JPanel changelist = new JPanel();
		changelist.setLayout(new BoxLayout(changelist, BoxLayout.PAGE_AXIS));

		for (String file : status.getAdded())
			changelist.add(ComponentUtils.deriveFont(ComponentUtils
					.setForeground(L10N.label("dialog.remote_workspace.added", file), new Color(155, 255, 185)), 11));

		for (String file : status.getChanged())
			changelist.add(ComponentUtils.deriveFont(ComponentUtils
					.setForeground(L10N.label("dialog.remote_workspace.changed", file), new Color(225, 255, 164)), 11));

		for (String file : status.getRemoved())
			changelist.add(ComponentUtils.deriveFont(ComponentUtils
					.setForeground(L10N.label("dialog.remote_workspace.removed", file), new Color(251, 255, 154)), 11));

		for (String file : status.getMissing())
			changelist.add(ComponentUtils.deriveFont(ComponentUtils
					.setForeground(L10N.label("dialog.remote_workspace.missing", file), new Color(255, 165, 139)), 11));

		for (String file : status.getModified())
			changelist.add(ComponentUtils.deriveFont(ComponentUtils
							.setForeground(L10N.label("dialog.remote_workspace.modified", file), new Color(225, 255, 164)),
					11));

		for (String file : status.getConflicting())
			changelist.add(ComponentUtils.deriveFont(ComponentUtils
							.setForeground(L10N.label("dialog.remote_workspace.conflicting", file), new Color(255, 165, 139)),
					11));

		JScrollPane cscroll = new JScrollPane(changelist);
		cscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		cscroll.setPreferredSize(new Dimension(500, 70));

		add("North", changes);
		add("Center", cscroll);
	}
}
