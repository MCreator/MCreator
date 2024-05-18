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
		return replaceTokens(workspace, workspace.getGeneratorConfiguration(), workspaceSettings, rawname);
	}

	public static String replaceTokens(Workspace workspace, GeneratorConfiguration generatorConfiguration,
			WorkspaceSettings workspaceSettings, String rawname) {
		if (rawname.contains("@SRCROOT")) // we need this check to prevent infinite recursion
			rawname = rawname.replace("@SRCROOT",
					GeneratorUtils.getSourceRoot(workspace, generatorConfiguration).getAbsolutePath());

		if (rawname.contains("@RESROOT")) // we need this check to prevent infinite recursion
			rawname = rawname.replace("@RESROOT",
					GeneratorUtils.getResourceRoot(workspace, generatorConfiguration).getAbsolutePath());

		if (rawname.contains("@MODASSETSROOT")) // we need this check to prevent infinite recursion
			rawname = rawname.replace("@MODASSETSROOT",
					GeneratorUtils.getModAssetsRoot(workspace, generatorConfiguration).getAbsolutePath());

		if (rawname.contains("@MODDATAROOT")) // we need this check to prevent infinite recursion
			rawname = rawname.replace("@MODDATAROOT",
					GeneratorUtils.getModDataRoot(workspace, generatorConfiguration).getAbsolutePath());

		//@formatter:off
		return rawname
				.replace("@WORKSPACEROOT", workspace.getWorkspaceFolder().getAbsolutePath())
				.replace("@modid", workspaceSettings.getModID())
				.replace("@JavaModName", workspaceSettings.getJavaModName())
				.replace("@modpicture", workspaceSettings.getModPicture() != null ? workspaceSettings.getModPicture() : "")
				.replace("@BASEPACKAGEPATH", workspaceSettings.getModElementsPackage().replace(".", "/"))
				.replace("@BASEPACKAGE", workspaceSettings.getModElementsPackage());
		//@formatter:on
	}

	private static final Pattern brackets = Pattern.compile("@\\[(.*?)]");

	static String replaceVariableTokens(Object element, String rawname) {
		return replaceVariableTokens(element, null, rawname);
	}

	static String replaceVariableTokens(Object element, Object listItem, String rawname) {
		Matcher m = brackets.matcher(rawname);
		while (m.find()) {
			String match = m.group(1);
			Object value = null;
			if (match.startsWith("item.")) { // a value from list item is requested
				if (listItem != null) { // get it if available
					try {
						String ref = match.substring("item.".length());
						value = match.contains("()") ?
								listItem.getClass().getMethod(ref.replace("()", "").trim()).invoke(listItem) :
								listItem.getClass().getField(ref.trim()).get(listItem);
					} catch (Exception e) {
						LOG.warn("Failed to load token value {}", match, e);
					}
				}
			} else if (element != null) { // get a value from the mod element
				try {
					value = match.contains("()") ?
							element.getClass().getMethod(match.replace("()", "").trim()).invoke(element) :
							element.getClass().getField(match.trim()).get(element);
				} catch (Exception e) {
					LOG.warn("Failed to load token value {}", match, e);
				}
			}
			rawname = rawname.replace("@[" + match + "]", String.valueOf(value));
		}
		return rawname;
	}

}
