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
import net.mcreator.util.StringUtils;
import net.mcreator.util.Tuple;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LocalizationUtils {

	public static void generateLanguageFiles(Generator generator, Workspace workspace, Map<?, ?> config) {
		if (!config.isEmpty() && workspace.getFolderManager().isFileInWorkspace(generator.getLangFilesRoot())) {
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

	public static void generateLocalizationKeys(Generator generator, GeneratableElement element,
			@Nullable List<?> localizationkeys) {
		processDefinitionToLocalizationKeys(generator, element, localizationkeys).forEach(
				(k, v) -> addLocalizationEntry(generator, k, v.x(), v.y()));
	}

	public static void deleteLocalizationKeys(Generator generator, GeneratableElement element,
			@Nullable List<?> localizationkeys) {
		processDefinitionToLocalizationKeys(generator, element, localizationkeys).keySet()
				.forEach(generator.getWorkspace()::removeLocalizationEntryByKey);
	}

	static Map<String, Tuple<Map<?, ?>, Object>> processDefinitionToLocalizationKeys(Generator generator,
			GeneratableElement element, @Nullable List<?> localizationkeys) {
		// values are pairs of key's YAML definitions and objects to parse tokens with
		HashMap<String, Tuple<Map<?, ?>, Object>> keysToEntries = new HashMap<>();

		if (localizationkeys != null) {
			for (Object template : localizationkeys) {
				Map<?, ?> map = (Map<?, ?>) template;
				String keytpl = (String) map.get("key");
				Object fromlist = map.get("fromlist") != null ?
						TemplateExpressionParser.processFTLExpression(generator, (String) map.get("fromlist"),
								element) :
						null;

				if (fromlist instanceof Collection<?> listEntries) {
					for (Object entry : listEntries) {
						String key = GeneratorTokens.replaceVariableTokens(entry,
								GeneratorTokens.replaceTokens(generator.getWorkspace(), keytpl
												//@formatter:off
												.replace("@NAME", element.getModElement().getName())
												.replace("@modid", generator.getWorkspace().getWorkspaceSettings().getModID())
												.replace("@registryname", element.getModElement().getRegistryName())
												.replace("@lc1_name", StringUtils.lowercaseFirstLetter(element.getModElement().getName()))
												//@formatter:on
								));
						keysToEntries.put(key, new Tuple<>(map, entry));
					}
				} else {
					String key = GeneratorTokens.replaceTokens(generator.getWorkspace(), keytpl
									//@formatter:off
									.replace("@NAME", element.getModElement().getName())
									.replace("@modid", generator.getWorkspace().getWorkspaceSettings().getModID())
									.replace("@registryname", element.getModElement().getRegistryName())
									.replace("@lc1_name", StringUtils.lowercaseFirstLetter(element.getModElement().getName()))
									//@formatter:on
					);
					keysToEntries.put(key, new Tuple<>(map, element));
				}
			}
		}

		return keysToEntries;
	}

	private static void addLocalizationEntry(Generator generator, String key, Map<?, ?> template, Object entry) {
		try {
			String mapto = (String) template.get("mapto");
			String value = (String) (mapto.contains("()") ?
					entry.getClass().getMethod(mapto.replace("()", "").trim()).invoke(entry) :
					entry.getClass().getField(mapto.trim()).get(entry));

			String suffix = (String) template.get("suffix");
			if (suffix != null)
				value += suffix;

			String prefix = (String) template.get("prefix");
			if (prefix != null)
				value = prefix + value;

			if (TemplateExpressionParser.shouldSkipTemplateBasedOnCondition(generator, template, entry)) {
				// If localization key is skipped, we make sure to remove the localization entry
				generator.getWorkspace().removeLocalizationEntryByKey(key);
			} else {
				generator.getWorkspace().setLocalization(key, value);
			}
		} catch (ReflectiveOperationException e) {
			generator.getLogger().error("Failed to parse values", e);
		}
	}

}
