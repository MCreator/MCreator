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

import com.google.gson.Gson;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.elements.VariableTypeLoader;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GeneratorStats {

	private static final Pattern ftlFile = Pattern.compile(".*\\.ftl");

	private final Map<ModElementType<?>, CoverageStatus> modElementTypeCoverageInfo = new TreeMap<>(
			Comparator.comparing(ModElementType::getRegistryName));
	private final Map<String, Double> coverageInfo = new HashMap<>();
	private final Map<String, CoverageStatus> baseCoverageInfo = new HashMap<>();

	private final Status status;

	private final Map<String, Set<String>> generatorBlocklyBlocks;

	GeneratorStats(GeneratorConfiguration generatorConfiguration) {
		this.status = Status.valueOf(
				generatorConfiguration.getRaw().get("status").toString().toUpperCase(Locale.ENGLISH));
		this.generatorBlocklyBlocks = new LinkedHashMap<>();

		// determine supported mod element types
		for (ModElementType<?> type : ModElementTypeLoader.REGISTRY) {
			Map<?, ?> definition = generatorConfiguration.getDefinitionsProvider().getModElementDefinition(type);
			if (definition != null) {
				if (definition.containsKey("field_inclusions") || definition.containsKey("field_exclusions")) {
					modElementTypeCoverageInfo.put(type, CoverageStatus.PARTIAL);
				} else {
					modElementTypeCoverageInfo.put(type, CoverageStatus.FULL);
				}
			} else {
				modElementTypeCoverageInfo.put(type, CoverageStatus.NONE);
			}
		}

		Map<String, LinkedHashMap<String, DataListEntry>> datalistchache = DataListLoader.getCache();

		// calculate percentage of mappings/element lists supported
		for (Map.Entry<String, LinkedHashMap<String, DataListEntry>> list : datalistchache.entrySet()) {
			int elementsCount = list.getValue().size();
			int supportedElementsCount = 0;
			Map<?, ?> mapping = generatorConfiguration.getMappingLoader().getMapping(list.getKey());
			if (mapping != null) {
				for (String element : list.getValue().keySet()) {
					if (mapping.containsKey(element))
						supportedElementsCount++;
				}
			}
			coverageInfo.put(list.getKey(), (((double) supportedElementsCount) / elementsCount) * 100);
		}

		// load dummy values
		coverageInfo.put("procedures", 100d);
		coverageInfo.put("triggers", 100d);
		coverageInfo.put("jsontriggers", 100d);
		coverageInfo.put("aitasks", 100d);
		coverageInfo.put("cmdargs", 100d);

		// lazy load actual values
		new Thread(() -> {
			BlocklyLoader.INSTANCE.getBlockLoaders().forEach((name, value) -> addBlocklyFolder(generatorConfiguration, name));
			addBlocklyFolder(generatorConfiguration, "triggers");
		}).start();

		if (generatorConfiguration.getVariableTypes().getSupportedVariableTypes().isEmpty()) {
			baseCoverageInfo.put("variables", CoverageStatus.NONE);
		} else {
			baseCoverageInfo.put("variables",
					generatorConfiguration.getVariableTypes().getSupportedVariableTypes().size()
							== VariableTypeLoader.INSTANCE.getAllVariableTypes().stream()
							.filter(e -> !e.isIgnoredByCoverage()).count() ?
							CoverageStatus.FULL :
							CoverageStatus.PARTIAL);
		}

		if (generatorConfiguration.getJavaModelsKey().equals("legacy")) {
			baseCoverageInfo.put("model_java",
					forElement(((List<?>) generatorConfiguration.getRaw().get("basefeatures")), "model_java"));
		} else {
			baseCoverageInfo.put("model_java", CoverageStatus.FULL);
		}

		String resourceTasksJSON = new Gson().toJson(generatorConfiguration.getResourceSetupTasks());
		baseCoverageInfo.put("model_json",
				resourceTasksJSON.contains("\"type\":\"JSON") ? CoverageStatus.FULL : CoverageStatus.NONE);
		baseCoverageInfo.put("model_obj",
				resourceTasksJSON.contains("\"type\":\"OBJ") ? CoverageStatus.FULL : CoverageStatus.NONE);

		baseCoverageInfo.put("textures", generatorConfiguration.getSpecificRoot("other_textures_dir") == null ?
				CoverageStatus.NONE :
				CoverageStatus.FULL);

		baseCoverageInfo.put("i18n", generatorConfiguration.getLanguageFileSpecification().isEmpty() ?
				CoverageStatus.NONE :
				CoverageStatus.FULL);

		baseCoverageInfo.put("sounds", generatorConfiguration.getSpecificRoot("sounds_dir") == null ?
				CoverageStatus.NONE :
				CoverageStatus.FULL);

		baseCoverageInfo.put("structures", generatorConfiguration.getSpecificRoot("structures_dir") == null ?
				CoverageStatus.NONE :
				CoverageStatus.FULL);
	}

	/**
	 * Load all Blockly files of a {@link Generator} inside the provided folder.
	 *
	 * @param genConfig The current generator's config to use
	 * @param name The name of the folder to load
	 */
	public void addBlocklyFolder(GeneratorConfiguration genConfig, String name) {
		Set<String> blocks = PluginLoader.INSTANCE.getResources(genConfig.getGeneratorName() + "." + name, ftlFile)
				.stream().map(FilenameUtilsPatched::getBaseName).map(FilenameUtilsPatched::getBaseName)
				.collect(Collectors.toSet());
		coverageInfo.put(name, Math.min(
				(((double) blocks.size()) / BlocklyLoader.INSTANCE.getSpecificBlockLoader("cmdargs").getDefinedBlocks().size())
						* 100, 100));
		generatorBlocklyBlocks.put(name, blocks);
	}

	public Map<String, Set<String>> getGeneratorBlocklyBlocks() {
		return generatorBlocklyBlocks;
	}

	public Set<String> getBlocklyBlocks(String name) {
		return generatorBlocklyBlocks.get(name);
	}

	private CoverageStatus forElement(List<?> features, String feature) {
		if (features == null)
			return CoverageStatus.NONE;

		return features.contains("~" + feature) ?
				CoverageStatus.PARTIAL :
				(features.contains(feature) ? CoverageStatus.FULL : CoverageStatus.NONE);
	}

	public Map<ModElementType<?>, CoverageStatus> getModElementTypeCoverageInfo() {
		return modElementTypeCoverageInfo;
	}

	public Map<String, Double> getCoverageInfo() {
		return coverageInfo;
	}

	public Map<String, CoverageStatus> getBaseCoverageInfo() {
		return baseCoverageInfo;
	}

	public Status getStatus() {
		return status;
	}

	public enum CoverageStatus {
		FULL, PARTIAL, NONE
	}

	// @formatter:off
	public enum Status {

		DEPRECATED("dialog.generator_selector.generator_status.deprecated"),
		DEV("dialog.generator_selector.generator_status.dev"),
		EXPERIMENTAL("dialog.generator_selector.generator_status.experimental"),
		LEGACY("dialog.generator_selector.generator_status.legacy"),
		LTS("dialog.generator_selector.generator_status.lts"),
		STABLE("dialog.generator_selector.generator_status.stable");

		private final String key;

		Status(String key) {
			this.key = key;
		}

		public String getName() {
			return L10N.t(key);
		}

	}
	// @formatter:on
}
