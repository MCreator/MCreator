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
import org.eclipse.jgit.api.Status;

import javax.swing.*;
import java.awt.*;

public class VCSCommitDialog {

	public static String getVCSCommitDialog(Window parent, Status status) {
		JPanel main = new JPanel(new BorderLayout(0, 5));

		main.add("North", new LocalChangesPanel(status));

		JLabel label = L10N.label("dialog.vcs.commit_short_message");
		JTextArea commitMessage = new JTextArea();
		commitMessage.setLineWrap(true);
		commitMessage.setWrapStyleWord(true);

		JScrollPane spane = new JScrollPane(commitMessage);
		spane.setBorder(BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));
		spane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		spane.setPreferredSize(new Dimension(550, 60));

		main.add("South", PanelUtils.northAndCenterElement(label, spane));

		int option = JOptionPane.showOptionDialog(parent, main, L10N.t("dialog.vcs.commit_enter_message"), JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, new String[] { "Create commit and sync", "Cancel" },
				"Create commit and sync");

		if (option == 0)
			return commitMessage.getText();

		return null;
	}

}
