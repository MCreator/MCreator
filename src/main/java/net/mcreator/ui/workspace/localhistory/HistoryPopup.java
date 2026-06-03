/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.workspace.localhistory;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JScrollablePopupMenu;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.ColorUtils;
import net.mcreator.workspace.localhistory.HistoryCheckpoint;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HistoryPopup {

	// TODO: localize this popup
	public static void showHistoryPopup(MCreator mcreator, JComponent parent, int x, int y) {
		JScrollablePopupMenu popupMenu = new JScrollablePopupMenu();
		List<HistoryCheckpoint> checkpoints = mcreator.getWorkspace().getHistoryManager().getCheckpoints();
		if (checkpoints.isEmpty()) {
			JMenuItem noHistoryItem = new JMenuItem("No history available");
			noHistoryItem.setEnabled(false);
			popupMenu.add(noHistoryItem);
		} else {
			for (HistoryCheckpoint checkpoint : checkpoints) {
				JMenuItem item = new JMenuItem("<html>" + checkpoint.name() + "<br><small color='" + ColorUtils.formatColor(
						Theme.current().getAltForegroundColor()) + "'>" + checkpoint.getTimestampString());
				item.addActionListener(_ -> {
					// TODO: confirm message
					// TODO: implement restore procedure, close mcreator, run restore, reopen mcreator
				});
				popupMenu.add(item);
			}
		}

		popupMenu.pack();
		Dimension size = popupMenu.getPreferredSize();
		popupMenu.show(parent, x - size.width, y);
	}

}
