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

	private Set<String> generatorProcedures;
	private Set<String> generatorTriggers;
	private Set<String> jsonTriggers;
	private Set<String> generatorAITasks;
	private Set<String> generatorCmdArgs;
	private Set<String> featureProcedures;

	GeneratorStats(GeneratorConfiguration generatorConfiguration) {
		this.status = Status.valueOf(
				generatorConfiguration.getRaw().get("status").toString().toUpperCase(Locale.ENGLISH));

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
		coverageInfo.put("features", 100d);

		// lazy load actual values
		new Thread(() -> {
			generatorProcedures = PluginLoader.INSTANCE.getResources(
							generatorConfiguration.getGeneratorName() + ".procedures", ftlFile).stream()
					.map(FilenameUtilsPatched::getBaseName).map(FilenameUtilsPatched::getBaseName)
					.filter(e -> !e.startsWith("_")).collect(Collectors.toSet());
			coverageInfo.put("procedures", Math.min(
					(((double) generatorProcedures.size()) / (BlocklyLoader.INSTANCE.getProcedureBlockLoader()
							.getDefinedBlocks().size())) * 100, 100));

			generatorTriggers = PluginLoader.INSTANCE.getResources(
							generatorConfiguration.getGeneratorName() + ".triggers", ftlFile).stream()
					.map(FilenameUtilsPatched::getBaseName).map(FilenameUtilsPatched::getBaseName)
					.filter(e -> !e.startsWith("_")).collect(Collectors.toSet());
			coverageInfo.put("triggers", Math.min(
					(((double) generatorTriggers.size()) / BlocklyLoader.INSTANCE.getExternalTriggerLoader()
							.getExternalTrigers().size()) * 100, 100));

			jsonTriggers = PluginLoader.INSTANCE.getResources(
							generatorConfiguration.getGeneratorName() + ".jsontriggers", ftlFile).stream()
					.map(FilenameUtilsPatched::getBaseName).map(FilenameUtilsPatched::getBaseName)
					.filter(e -> !e.startsWith("_")).collect(Collectors.toSet());
			coverageInfo.put("jsontriggers", Math.min(
					(((double) jsonTriggers.size()) / (BlocklyLoader.INSTANCE.getJSONTriggerLoader().getDefinedBlocks()
							.size())) * 100, 100));

			generatorAITasks = PluginLoader.INSTANCE.getResources(
							generatorConfiguration.getGeneratorName() + ".aitasks", ftlFile).stream()
					.map(FilenameUtilsPatched::getBaseName).map(FilenameUtilsPatched::getBaseName)
					.collect(Collectors.toSet());
			coverageInfo.put("aitasks", Math.min(
					(((double) generatorAITasks.size()) / BlocklyLoader.INSTANCE.getAITaskBlockLoader()
							.getDefinedBlocks().size()) * 100, 100));

			generatorCmdArgs = PluginLoader.INSTANCE.getResources(
							generatorConfiguration.getGeneratorName() + ".cmdargs", ftlFile).stream()
					.map(FilenameUtilsPatched::getBaseName).map(FilenameUtilsPatched::getBaseName)
					.collect(Collectors.toSet());
			coverageInfo.put("cmdargs", Math.min(
					(((double) generatorCmdArgs.size()) / BlocklyLoader.INSTANCE.getCmdArgsBlockLoader()
							.getDefinedBlocks().size()) * 100, 100));

			featureProcedures = PluginLoader.INSTANCE.getResources(
							generatorConfiguration.getGeneratorName() + ".features", ftlFile).stream()
					.map(FilenameUtilsPatched::getBaseName).map(FilenameUtilsPatched::getBaseName)
					.collect(Collectors.toSet());
			coverageInfo.put("features", Math.min(
					(((double) featureProcedures.size()) / BlocklyLoader.INSTANCE.getFeatureBlockLoader()
							.getDefinedBlocks().size()) * 100, 100));
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

	public Set<String> getGeneratorProcedures() {
		return generatorProcedures;
	}

	public Set<String> getGeneratorTriggers() {
		return generatorTriggers;
	}

	public Set<String> getJsonTriggers() {
		return jsonTriggers;
	}

	public Set<String> getFeatureProcedures() {
		return featureProcedures;
	}

	public Set<String> getGeneratorAITasks() {
		return generatorAITasks;
	}

	public Set<String> getGeneratorCmdArgs() {
		return generatorCmdArgs;
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
