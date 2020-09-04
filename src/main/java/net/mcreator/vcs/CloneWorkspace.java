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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.awt.*;
import java.io.File;

public class CloneWorkspace {

	public static void cloneWorkspace(Window parent, VCSInfo vcsInfo, File folderInto) throws GitAPIException {
		Git.cloneRepository().setURI(vcsInfo.getRemote()).setDirectory(folderInto).setCredentialsProvider(
				new UsernamePasswordCredentialsProvider(vcsInfo.getUsername(), vcsInfo.getPassword(folderInto, parent)))
				.call();
		VCSInfo.saveToFile(vcsInfo, new File(folderInto, "/.mcreator/vcsInfo"));
	}

}
