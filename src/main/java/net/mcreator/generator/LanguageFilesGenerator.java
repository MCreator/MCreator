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

import com.google.gson.GsonBuilder;
import net.mcreator.io.FileIO;
import net.mcreator.workspace.Workspace;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LanguageFilesGenerator {

	public static void generateLanguageFiles(Generator generator, Workspace workspace, Map<?, ?> config) {
		if (config.size() > 0)
			switch ((String) config.get("format")) {
			case "keyvalue":
				generateKeyValueLanguageFiles(generator, workspace, config);
				break;
			case "json":
				generateJSONLanguageFiles(generator, workspace, config);
				break;
			default:
				break;
			}
	}

	private static void generateKeyValueLanguageFiles(Generator generator, Workspace workspace, Map<?, ?> config) {
		String rawName = (String) config.get("langfile_name");

		if (!workspace.getFolderManager().isFileInWorkspace(generator.getLangFilesRoot()))
			return;

		generator.getLangFilesRoot().mkdirs();
		FileIO.emptyDirectory(generator.getLangFilesRoot()); // remove old localizations

		for (Map.Entry<String, ConcurrentHashMap<String, String>> entry : workspace.getLanguageMap().entrySet()) {
			StringBuilder langFileContent = new StringBuilder();
			ConcurrentHashMap<String, String> entries = entry.getValue();
			for (Map.Entry<String, String> lang_entry : entries.entrySet()) {
				langFileContent.append(lang_entry.getKey()).append("=").append(lang_entry.getValue()).append("\n");
			}

			String uppercaseLangName =
					entry.getKey().split("_")[0] + "_" + entry.getKey().split("_")[1].toUpperCase(Locale.ENGLISH);

			String fileName = GeneratorTokens.replaceTokens(workspace,
					rawName.replace("@langname", entry.getKey()).replace("@lang_NAME", uppercaseLangName));
			FileIO.writeUTF8toFile(langFileContent.toString(), new File(generator.getLangFilesRoot(), fileName));
		}
	}

	private static void generateJSONLanguageFiles(Generator generator, Workspace workspace, Map<?, ?> config) {
		String rawName = (String) config.get("langfile_name");

		if (!workspace.getFolderManager().isFileInWorkspace(generator.getLangFilesRoot()))
			return;

		generator.getLangFilesRoot().mkdirs();
		FileIO.emptyDirectory(generator.getLangFilesRoot()); // remove old localizations

		for (Map.Entry<String, ConcurrentHashMap<String, String>> entry : workspace.getLanguageMap().entrySet()) {
			ConcurrentHashMap<String, String> entries = entry.getValue();
			String fileName = GeneratorTokens.replaceTokens(workspace, rawName.replace("@langname", entry.getKey()));
			FileIO.writeUTF8toFile(new GsonBuilder().setPrettyPrinting().create().toJson(entries),
					new File(generator.getLangFilesRoot(), fileName));
		}
	}

}
