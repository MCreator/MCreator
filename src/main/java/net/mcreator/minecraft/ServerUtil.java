/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.minecraft;

import net.mcreator.io.FileIO;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerUtil {

	private static final Logger LOG = LogManager.getLogger(ServerUtil.class);

	public static File getEULAFile(Workspace workspace) {
		return new File(workspace.getFolderManager().getServerRunDir(), "eula.txt");
	}

	public static boolean isEULAAccepted(Workspace workspace) {
		File eulaFile = getEULAFile(workspace);
		if (!eulaFile.isFile())
			return false;
		String eula = FileIO.readFileToString(eulaFile);
		return eula.contains("eula=true");
	}

	public static void acceptEULA(Workspace workspace) {
		try {
			File eulaFile = getEULAFile(workspace);
			Properties por = new Properties();
			if (eulaFile.isFile())
				por.load(new FileInputStream(eulaFile));
			por.setProperty("eula", "true");
			por.store(new FileOutputStream(eulaFile),
					"#Edited by MCreator - user agreed to EULA inside MCreator");
		} catch (IOException e) {
			LOG.warn("Failed to write EULA file", e);
		}
	}

}
