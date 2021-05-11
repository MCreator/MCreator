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

package net.mcreator.vcs;

import com.google.gson.Gson;
import net.mcreator.io.FileIO;
import net.mcreator.io.PasswordVault;
import net.mcreator.ui.init.L10N;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.UUID;

public class VCSInfo {

	private static final Logger LOG = LogManager.getLogger("VCS Info");

	private String remote, username;

	private UUID vault_id;

	private transient String password;

	private boolean promptForPassword;

	public VCSInfo(String remote, String username, String password, boolean promptForPassword) {
		this.remote = remote;
		this.username = username;
		this.password = password;
		this.promptForPassword = promptForPassword;
		this.vault_id = UUID.randomUUID();
	}

	public String getRemote() {
		return remote;
	}

	public String getUsername() {
		return username;
	}

	public String getPasswordDirect() {
		return password;
	}

	public String getPassword(File workspaceFolder, Window parent) {
		if (password == null) {
			JPasswordField passwordFiled = new JPasswordField(25);
			JCheckBox remember = L10N.checkbox("dialog.vcs.info_remember_password");
			JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
			panel.add(L10N.label("dialog.vcs.info_enter_password", username));
			panel.add(passwordFiled);
			panel.add(remember);
			JOptionPane.showMessageDialog(parent, panel, L10N.t("dialog.vcs.info_account_authentication"),
					JOptionPane.QUESTION_MESSAGE);
			if (remember.isSelected()) {
				this.password = new String(passwordFiled.getPassword());
				try {
					if (isValid()) {
						this.promptForPassword = false;
						VCSInfo.saveToFile(this, new File(workspaceFolder, "/.mcreator/vcsInfo"));
					} else {
						this.password = null;
					}
				} catch (Exception e) {
					this.password = null;
				}
			}
			return new String(passwordFiled.getPassword());
		}
		return password;
	}

	public boolean isPromptForPassword() {
		return promptForPassword;
	}

	public boolean isValid() throws Exception {
		LsRemoteCommand cloneCommand = Git.lsRemoteRepository();
		cloneCommand.setRemote(getRemote());
		cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(getUsername(), password));
		try {
			cloneCommand.call();
		} catch (Exception e) {
			LOG.warn("VCS info check failed.", e);
			throw e;
		}
		return true;
	}

	public static void saveToFile(@Nonnull VCSInfo vcsInfo, File file) {
		try {
			if (vcsInfo.promptForPassword)
				vcsInfo.password = null; // we don't save password in such case

			// store password in vault if not already
			if (vcsInfo.password != null) {
				PasswordVault.INSTANCE.addPassword(vcsInfo.vault_id, vcsInfo.password);
			}

			FileIO.writeStringToFile(new Gson().toJson(vcsInfo), file);
		} catch (Exception e) {
			LOG.error("Failed to save VCS info to file", e);
		}
	}

	public static VCSInfo loadFromFile(File file) {
		try {
			VCSInfo retval = new Gson().fromJson(FileIO.readFileToString(file), VCSInfo.class);

			// load password from vault when loaded by GSON
			retval.password = PasswordVault.INSTANCE.getPassword(retval.vault_id);

			return retval;
		} catch (Exception e) {
			return null;
		}
	}

}
