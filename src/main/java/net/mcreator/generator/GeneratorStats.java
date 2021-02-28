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

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.element.ModElementType;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.plugin.PluginLoader;
import org.apache.commons.io.FilenameUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GeneratorStats {

	private static final Pattern ftlFile = Pattern.compile(".*\\.ftl");

	private final Map<ModElementType, CoverageStatus> modElementTypeCoverageInfo = new TreeMap<>(
			Comparator.comparing(Enum::name));
	private final Map<String, Double> coverageInfo = new HashMap<>();
	private final Map<String, CoverageStatus> baseCoverageInfo = new HashMap<>();

	private final Status status;

	private Set<String> generatorProcedures;
	private Set<String> generatorTriggers;
	private Set<String> jsonTriggers;
	private Set<String> generatorAITasks;
	private Set<String> generatorCmdArgs;

	GeneratorStats(GeneratorConfiguration generatorConfiguration) {
		this.status = Status
				.valueOf(generatorConfiguration.getRaw().get("status").toString().toUpperCase(Locale.ENGLISH));

		// determine supported mod element types
		List<?> partials = ((List<?>) generatorConfiguration.getRaw().get("partial_support"));
		if (partials == null)
			partials = new ArrayList<>();
		for (ModElementType type : ModElementType.values()) {
			if (generatorConfiguration.getDefinitionsProvider().getModElementDefinition(type) != null) {
				if (partials.contains(type.name().toLowerCase(Locale.ENGLISH))) {
					modElementTypeCoverageInfo.put(type, CoverageStatus.PARTIAL);
				} else {
					modElementTypeCoverageInfo.put(type, CoverageStatus.FULL);
				}
			} else {
				modElementTypeCoverageInfo.put(type, CoverageStatus.NONE);
			}
		}

		Map<String, LinkedHashMap<String, DataListEntry>> datalistchache = DataListLoader.getCache();

		// caclulate percentage of mappings/element lists supported
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
			generatorProcedures = PluginLoader.INSTANCE
					.getResources(generatorConfiguration.getGeneratorName() + ".procedures", ftlFile).stream()
					.map(FilenameUtils::getBaseName).map(FilenameUtils::getBaseName).collect(Collectors.toSet());
			coverageInfo.put("procedures", Math.min((((double) generatorProcedures.size()) / (
					BlocklyLoader.INSTANCE.getProcedureBlockLoader().getDefinedBlocks().size() + 6)) * 100, 100));

			generatorTriggers = PluginLoader.INSTANCE
					.getResources(generatorConfiguration.getGeneratorName() + ".triggers", ftlFile).stream()
					.map(FilenameUtils::getBaseName).map(FilenameUtils::getBaseName).collect(Collectors.toSet());
			coverageInfo.put("triggers", Math.min(
					(((double) generatorTriggers.size()) / BlocklyLoader.INSTANCE.getExternalTriggerLoader()
							.getExternalTrigers().size()) * 100, 100));

			jsonTriggers = PluginLoader.INSTANCE
					.getResources(generatorConfiguration.getGeneratorName() + ".jsontriggers", ftlFile).stream()
					.map(FilenameUtils::getBaseName).map(FilenameUtils::getBaseName).collect(Collectors.toSet());
			coverageInfo.put("jsontriggers", Math.min((((double) jsonTriggers.size()) / (
					BlocklyLoader.INSTANCE.getJSONTriggerLoader().getDefinedBlocks().size() + 1)) * 100, 100));

			generatorAITasks = PluginLoader.INSTANCE
					.getResources(generatorConfiguration.getGeneratorName() + ".aitasks", ftlFile).stream()
					.map(FilenameUtils::getBaseName).map(FilenameUtils::getBaseName).collect(Collectors.toSet());
			coverageInfo.put("aitasks", Math.min(
					(((double) generatorAITasks.size()) / BlocklyLoader.INSTANCE.getAITaskBlockLoader()
							.getDefinedBlocks().size()) * 100, 100));

			generatorCmdArgs = PluginLoader.INSTANCE
					.getResources(generatorConfiguration.getGeneratorName() + ".cmdargs", ftlFile).stream()
					.map(FilenameUtils::getBaseName).map(FilenameUtils::getBaseName).collect(Collectors.toSet());
			coverageInfo.put("cmdargs", Math.min(
					(((double) generatorCmdArgs.size()) / BlocklyLoader.INSTANCE.getCmdArgsBlockLoader()
							.getDefinedBlocks().size()) * 100, 100));
		}).start();

		baseCoverageInfo.put("variables",
				forElement(((List<?>) generatorConfiguration.getRaw().get("basefeatures")), "variables"));

		baseCoverageInfo.put("model_json",
				forElement(((List<?>) generatorConfiguration.getRaw().get("basefeatures")), "model_json"));
		baseCoverageInfo.put("model_java",
				forElement(((List<?>) generatorConfiguration.getRaw().get("basefeatures")), "model_java"));
		baseCoverageInfo.put("model_obj",
				forElement(((List<?>) generatorConfiguration.getRaw().get("basefeatures")), "model_obj"));

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

	public Map<ModElementType, CoverageStatus> getModElementTypeCoverageInfo() {
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

	public Set<String> getGeneratorAITasks() {
		return generatorAITasks;
	}

	public Set<String> getGeneratorCmdArgs() {
		return generatorCmdArgs;
	}

	public enum CoverageStatus {
		FULL, PARTIAL, NONE
	}

	public enum Status {

		DEPRECATED("Deprecated"), DEV("In development"), EXPERIMENTAL("Experimental"), LEGACY("Legacy"), LTS(
				"Long term support"), STABLE("Stable");

		String name;

		Status(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}
}
