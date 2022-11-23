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
import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.template.TemplateExpressionParser;
import net.mcreator.io.FileIO;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalizationUtils {

	public static void generateLanguageFiles(Generator generator, Workspace workspace, Map<?, ?> config) {
		if (workspace.getFolderManager().isFileInWorkspace(generator.getLangFilesRoot()) && config.size() > 0) {
			switch ((String) config.get("format")) {
			case "keyvalue" -> generateKeyValueLanguageFiles(generator, workspace, config);
			case "json" -> generateJSONLanguageFiles(generator, workspace, config);
			default -> {
			}
			}
		}
	}

	private static void generateKeyValueLanguageFiles(Generator generator, Workspace workspace, Map<?, ?> config) {
		String rawName = (String) config.get("langfile_name");

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
			FileIO.writeStringToFile(langFileContent.toString(), new File(generator.getLangFilesRoot(), fileName));
		}
	}

	private static void generateJSONLanguageFiles(Generator generator, Workspace workspace, Map<?, ?> config) {
		String rawName = (String) config.get("langfile_name");

		generator.getLangFilesRoot().mkdirs();
		FileIO.emptyDirectory(generator.getLangFilesRoot()); // remove old localizations

		for (Map.Entry<String, ConcurrentHashMap<String, String>> entry : workspace.getLanguageMap().entrySet()) {
			ConcurrentHashMap<String, String> entries = entry.getValue();
			String fileName = GeneratorTokens.replaceTokens(workspace, rawName.replace("@langname", entry.getKey()));
			FileIO.writeStringToFile(new GsonBuilder().setPrettyPrinting().create().toJson(entries),
					new File(generator.getLangFilesRoot(), fileName));
		}
	}

	public static void extractLocalizationKeys(Generator generator, GeneratableElement element,
			@Nullable List<?> localizationkeys) {
		if (localizationkeys != null) {
			for (Object template : localizationkeys) {
				String keytpl = (String) ((Map<?, ?>) template).get("key");
				Object fromlist = TemplateExpressionParser.processFTLExpression(generator,
						(String) ((Map<?, ?>) template).get("fromlist"), element);

				if (fromlist instanceof Collection<?> listEntries) {
					for (Object entry : listEntries) {
						String key = GeneratorTokens.replaceVariableTokens(entry,
								GeneratorTokens.replaceTokens(generator.getWorkspace(),
										keytpl.replace("@NAME", element.getModElement().getName()).replace("@modid",
														generator.getWorkspace().getWorkspaceSettings().getModID())
												.replace("@registryname", element.getModElement().getRegistryName())));
						addLocalizationEntry(generator, template, entry, key);
					}
				} else {
					String key = GeneratorTokens.replaceTokens(generator.getWorkspace(),
							keytpl.replace("@NAME", element.getModElement().getName())
									.replace("@modid", generator.getWorkspace().getWorkspaceSettings().getModID())
									.replace("@registryname", element.getModElement().getRegistryName()));
					addLocalizationEntry(generator, template, element, key);
				}
			}
		}
	}

	private static void addLocalizationEntry(Generator generator, Object template, Object entry, String key) {
		try {
			String mapto = (String) ((Map<?, ?>) template).get("mapto");
			String value = (String) (mapto.contains("()") ?
					entry.getClass().getMethod(mapto.replace("()", "").trim()).invoke(entry) :
					entry.getClass().getField(mapto.trim()).get(entry));

			String suffix = (String) ((Map<?, ?>) template).get("suffix");
			if (suffix != null)
				value += suffix;

			String prefix = (String) ((Map<?, ?>) template).get("prefix");
			if (prefix != null)
				value = prefix + value;

			generator.getWorkspace().setLocalization(key, value);
		} catch (ReflectiveOperationException e) {
			generator.LOG.error("Failed to parse values", e);
		}
	}

	public static void deleteLocalizationKeys(Generator generator, ModElement element,
			@Nullable List<?> localizationkeys) {
		if (localizationkeys != null) {
			for (Object template : localizationkeys) {
				String keytpl = (String) ((Map<?, ?>) template).get("key");
				Object fromlist = TemplateExpressionParser.processFTLExpression(generator,
						(String) ((Map<?, ?>) template).get("fromlist"), element);

				if (fromlist instanceof Collection<?> listEntries) {
					for (Object entry : listEntries) {
						String key = GeneratorTokens.replaceVariableTokens(entry,
								GeneratorTokens.replaceTokens(generator.getWorkspace(),
										keytpl.replace("@NAME", element.getName()).replace("@modid",
														generator.getWorkspace().getWorkspaceSettings().getModID())
												.replace("@registryname", element.getRegistryName())));
						generator.getWorkspace().removeLocalizationEntryByKey(key);
					}
				} else {
					String key = GeneratorTokens.replaceTokens(generator.getWorkspace(),
							keytpl.replace("@NAME", element.getName())
									.replace("@modid", generator.getWorkspace().getWorkspaceSettings().getModID())
									.replace("@registryname", element.getRegistryName()));
					generator.getWorkspace().removeLocalizationEntryByKey(key);
				}
			}
		}
	}
}
