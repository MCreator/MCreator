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
import org.eclipse.jgit.api.Status;

import javax.swing.*;
import java.awt.*;

public class LocalChangesPanel extends JPanel {

	public LocalChangesPanel(Status status) {
		super(new BorderLayout());

		JLabel changes = new JLabel("<html>Local uncommited/unsynced files:<br><br>");
		JPanel changelist = new JPanel();
		changelist.setLayout(new BoxLayout(changelist, BoxLayout.PAGE_AXIS));

		for (String file : status.getAdded())
			changelist.add(ComponentUtils.deriveFont(
					ComponentUtils.setForeground(new JLabel("\u2022 " + file + " (added)"), new Color(155, 255, 185)),
					11));

		for (String file : status.getChanged())
			changelist.add(ComponentUtils.deriveFont(
					ComponentUtils.setForeground(new JLabel("\u2022 " + file + " (changed)"), new Color(225, 255, 164)),
					11));

		for (String file : status.getRemoved())
			changelist.add(ComponentUtils.deriveFont(
					ComponentUtils.setForeground(new JLabel("\u2022 " + file + " (removed)"), new Color(251, 255, 154)),
					11));

		for (String file : status.getMissing())
			changelist.add(ComponentUtils.deriveFont(
					ComponentUtils.setForeground(new JLabel("\u2022 " + file + " (missing)"), new Color(255, 165, 139)),
					11));

		for (String file : status.getModified())
			changelist.add(ComponentUtils.deriveFont(ComponentUtils
					.setForeground(new JLabel("\u2022 " + file + " (modified)"), new Color(225, 255, 164)), 11));

		for (String file : status.getConflicting())
			changelist.add(ComponentUtils.deriveFont(ComponentUtils
					.setForeground(new JLabel("\u2022 " + file + " (conflicting)"), new Color(255, 165, 139)), 11));

		JScrollPane cscroll = new JScrollPane(changelist);
		cscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		cscroll.setPreferredSize(new Dimension(500, 70));

		add("North", changes);
		add("Center", cscroll);
	}
}
