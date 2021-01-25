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

package net.mcreator.generator;

import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneratorTokens {

	private static final Logger LOG = LogManager.getLogger("Generator Tokens");

	public static String replaceTokens(Workspace workspace, String rawname) {
		return replaceTokens(workspace, workspace.getWorkspaceSettings(), rawname);
	}

	public static String replaceTokens(Workspace workspace, WorkspaceSettings workspaceSettings, String rawname) {
		if (rawname.contains("@SRCROOT"))
			rawname = rawname.replace("@SRCROOT", workspace.getGenerator().getSourceRoot().getAbsolutePath());

		if (rawname.contains("@RESROOT"))
			rawname = rawname.replace("@RESROOT", workspace.getGenerator().getResourceRoot().getAbsolutePath());

		if (rawname.contains("@MODASSETSROOT"))
			rawname = rawname.replace("@MODASSETSROOT", workspace.getGenerator().getModAssetsRoot().getAbsolutePath());

		if (rawname.contains("@MODDATAROOT"))
			rawname = rawname.replace("@MODDATAROOT", workspace.getGenerator().getModDataRoot().getAbsolutePath());

		return rawname.replace("@WORKSPACEROOT", workspace.getWorkspaceFolder().getAbsolutePath())
				.replace("@modid", workspaceSettings.getModID())
				.replace("@JavaModName", workspaceSettings.getJavaModName()).replace("@modpicture",
						workspaceSettings.getModPicture() != null ? workspaceSettings.getModPicture() : "")
				.replace("@BASEPACKAGEPATH", workspaceSettings.getModElementsPackage().replace(".", "/"))
				.replace("@BASEPACKAGE", workspaceSettings.getModElementsPackage());
	}

	private static final Pattern brackets = Pattern.compile("@\\[(.*?)]");

	static String replaceVariableTokens(GeneratableElement element, String rawname) {
		if (containsVariableTokens(rawname)) {
			Matcher m = brackets.matcher(rawname);
			while (m.find()) {
				String match = m.group(1);
				Object value = null;
				if (match.contains("()")) {
					try {
						value = element.getClass().getMethod(match.replace("()", "").trim()).invoke(element);
					} catch (Exception e) {
						LOG.warn("Failed to load token value " + match, e);
					}
				} else {
					try {
						value = element.getClass().getField(match.replace("()", "").trim()).get(element);
					} catch (Exception e) {
						LOG.warn("Failed to load token value " + match, e);
					}
				}
				rawname = rawname.replace("@[" + match + "]", value != null ? value.toString() : "null");
			}
		}
		return rawname;
	}

	static boolean containsVariableTokens(String rawname) {
		return rawname.contains("@[");
	}

}
