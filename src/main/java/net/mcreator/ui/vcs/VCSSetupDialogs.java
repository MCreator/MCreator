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
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.vcs.VCSInfo;
import org.eclipse.jgit.api.errors.TransportException;

import javax.swing.*;
import java.awt.*;

public class VCSSetupDialogs {

	public static VCSInfo getVCSInfoDialog(Window parent, String text) {
		return getVCSInfoDialog(parent, text, null, null, null, true);
	}

	public static VCSInfo getVCSInfoDialog(Window parent, String text, String r, String u, Boolean p,
			boolean enableRemote) {
		JPanel main = new JPanel(new BorderLayout(0, 20));

		JTextField remote = new JTextField(34);
		JTextField username = new JTextField(25);
		JPasswordField password = new JPasswordField(25);
		JCheckBox savePassword = new JCheckBox(
				"Save password to encrypted password vault (if not selected, you will be prompted each time when required)");

		savePassword.setSelected(true);

		remote.setEnabled(enableRemote);

		remote.setText(r);
		username.setText(u);
		if (p != null)
			savePassword.setSelected(!p);

		remote.setPreferredSize(new Dimension(300, 15));

		JPanel form = new JPanel(new GridLayout(4, 1, 0, 5));
		form.add(PanelUtils.westAndEastElement(new JLabel("Remote Git repository HTTPS URL: "), remote));
		form.add(PanelUtils.westAndEastElement(new JLabel("Your Git account username: "), username));
		form.add(PanelUtils.westAndEastElement(new JLabel("Your Git account password:"), password));
		form.add(savePassword);

		main.add("Center", form);

		main.add("South", ComponentUtils.setForeground(new JLabel("<html><small>"
				+ "If selected, MCreator will store this password for the current workspace securely in an encrypted local password store. It will only be<br>"
				+ "used for authentication with the remote repository. Your login data will not be shared with any other server than the one entered in<br>"
				+ "the Remote GIT repository URL field."), (Color) UIManager.get("MCreatorLAF.GRAY_COLOR")));

		main.add("North", new JLabel(text));

		int option = JOptionPane.showOptionDialog(parent, main, "Remote workspace details", JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, new String[] { "Setup remote workspace", "Cancel" },
				"Setup remote workspace");

		if (option == 0) {
			VCSInfo info = new VCSInfo(remote.getText(), username.getText(), new String(password.getPassword()),
					!savePassword.isSelected());
			parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			boolean valid;
			try {
				valid = info.isValid();
			} catch (Exception e) {
				if (e instanceof TransportException && e.getMessage().contains("not authorized")) {
					JOptionPane.showMessageDialog(parent, "<html><b>Username or password is incorrect!</b><br><br>"
									+ "Make sure the username and password you entered are correct<br>"
									+ "and that your user has proper permissions on the remote repository.",
							"Invalid repository parameters", JOptionPane.WARNING_MESSAGE);
				} else if (e instanceof TransportException && e.getMessage().contains("not found")) {
					JOptionPane.showMessageDialog(parent,
							"<html><b>Repository URL is not valid or you can't access it!</b><br><br>"
									+ "Make sure that remote repository URL is valid and that the repository exists.<br>"
									+ "If this is true, make sure that your user account has proper permissions on the remote repository.",
							"Invalid repository parameters", JOptionPane.WARNING_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(parent,
							"<html><b>One of the parameters of your repository is not valid!</b><br><br>"
									+ "Make sure that remote repository URL is valid and that the repository exists.<br>"
									+ "If this is true, make sure the username and password you entered are correct<br>"
									+ "and that your user has proper permissions on the remote repository.",
							"Invalid repository parameters", JOptionPane.WARNING_MESSAGE);
				}
				parent.setCursor(Cursor.getDefaultCursor());
				return getVCSInfoDialog(parent, text, remote.getText(), username.getText(), !savePassword.isSelected(),
						enableRemote);
			}
			parent.setCursor(Cursor.getDefaultCursor());
			if (valid)
				return info;
		}
		return null;
	}

}
