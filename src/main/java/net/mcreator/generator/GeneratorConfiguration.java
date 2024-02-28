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

import net.mcreator.element.ModElementType;
import net.mcreator.generator.mapping.MappingLoader;
import net.mcreator.generator.template.TemplateGeneratorConfiguration;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.util.YamlUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GeneratorConfiguration implements Comparable<GeneratorConfiguration> {

	private static final Logger LOG = LogManager.getLogger("Generator Configuration");

	@Nullable
	public static GeneratorConfiguration getRecommendedGeneratorForFlavor(
			Collection<GeneratorConfiguration> generatorConfigurations, GeneratorFlavor generatorFlavor) {
		return generatorConfigurations.stream().filter(gc -> gc.getGeneratorFlavor() == generatorFlavor).sorted()
				.findFirst().orElse(null);
	}

	@Nullable
	public static GeneratorConfiguration getRecommendedGeneratorForBaseLanguage(
			Collection<GeneratorConfiguration> generatorConfigurations, GeneratorFlavor.BaseLanguage baseLanguage) {
		return generatorConfigurations.stream().filter(gc -> gc.getGeneratorFlavor().getBaseLanguage() == baseLanguage)
				.sorted().findFirst().orElse(null);
	}

	private Map<?, ?> generatorConfig;
	private final String generatorName;

	private final MappingLoader mappingLoader;
	private final DefinitionsProvider definitionsProvider;
	private final GeneratorStats generatorStats;

	private final GeneratorFlavor generatorFlavor;

	private final GeneratorVariableTypes generatorVariableTypes;

	private final Map<String, TemplateGeneratorConfiguration> templateGeneratorConfigs = new ConcurrentHashMap<>();

	// Cached values
	private final List<String> compatibleJavaModelKeys = new ArrayList<>();
	private final List<String> compatibleJavaModelRequirementKeyWords = new ArrayList<>();
	private final List<String> importFormatterDuplicatesWhitelist = new ArrayList<>();
	private final Map<String, String> importFormatterPriorityImports = new HashMap<>();

	public GeneratorConfiguration(String generatorName) {
		this.generatorName = generatorName;

		String config = FileIO.readResourceToString(PluginLoader.INSTANCE, "/" + generatorName + "/generator.yaml");

		// load generator configuration
		try {
			generatorConfig = (Map<?, ?>) new Load(YamlUtil.getSimpleLoadSettings()).loadFromString(config);
			generatorConfig = new ConcurrentHashMap<>(
					generatorConfig); // make this map concurrent, cache can be reused by multiple instances
		} catch (YamlEngineException e) {
			LOG.fatal("[" + generatorName + "] Error: " + e.getMessage());
		}

		this.generatorFlavor = GeneratorFlavor.valueOf(this.generatorName.split("-")[0].toUpperCase(Locale.ENGLISH));

		// load mappings
		this.mappingLoader = new MappingLoader(this);
		this.definitionsProvider = new DefinitionsProvider(generatorName);

		// load global variable definitions
		this.generatorVariableTypes = new GeneratorVariableTypes(this);

		// Preprocess compatible java model keys
		compatibleJavaModelKeys.add(getJavaModelsKey());
		if (generatorConfig.get("java_models") != null) {
			if (((Map<?, ?>) generatorConfig.get("java_models")).get("compatible") != null) {
				compatibleJavaModelKeys.addAll(
						((List<?>) ((Map<?, ?>) generatorConfig.get("java_models")).get("compatible")).stream()
								.map(Object::toString).toList());
			}
		}

		// Preprocess compatible java model requirement keywords
		if (generatorConfig.get("java_models") != null) {
			if (((Map<?, ?>) generatorConfig.get("java_models")).get("requested_key_words") != null) {
				compatibleJavaModelRequirementKeyWords.addAll(
						((List<?>) ((Map<?, ?>) generatorConfig.get("java_models")).get("requested_key_words")).stream()
								.map(Object::toString).toList());
			}
		}

		// Preprocess import formatter duplicates whitelist
		if (generatorConfig.get("import_formatter") != null) {
			if (((Map<?, ?>) generatorConfig.get("import_formatter")).get("duplicates_whitelist") != null) {
				importFormatterDuplicatesWhitelist.addAll(
						((List<?>) ((Map<?, ?>) generatorConfig.get("import_formatter")).get(
								"duplicates_whitelist")).stream().map(Object::toString).toList());
			}
		}

		// Preprocess import formatter priority imports
		if (generatorConfig.get("import_formatter") != null) {
			if (((Map<?, ?>) generatorConfig.get("import_formatter")).get("priority_imports") != null) {
				((Map<?, ?>) ((Map<?, ?>) generatorConfig.get("import_formatter")).get("priority_imports")).forEach(
						(k, v) -> importFormatterPriorityImports.put("." + k, v + "." + k));
			}
		}

		// compute generator stats
		this.generatorStats = new GeneratorStats(this);
	}

	@Nonnull public String getSourceRoot() {
		return (String) generatorConfig.get("source_root");
	}

	@Nonnull public String getResourceRoot() {
		return (String) generatorConfig.get("res_root");
	}

	public String getModAssetsRoot() {
		return (String) generatorConfig.get("mod_assets_root");
	}

	public String getModDataRoot() {
		return (String) generatorConfig.get("mod_data_root");
	}

	public String getSpecificRoot(String root) {
		return (String) generatorConfig.get(root);
	}

	@Nonnull public String getGeneratorMinecraftVersion() {
		return this.generatorName.split("-")[1];
	}

	@Nonnull public String getGeneratorBuildFileVersion() {
		return generatorConfig.get("buildfileversion") != null ? (String) generatorConfig.get("buildfileversion") : "";
	}

	@Nullable public String getGeneratorSubVersion() {
		return (String) generatorConfig.get("subversion");
	}

	public Map<?, ?> getLanguageFileSpecification() {
		return generatorConfig.get("language_file") != null ?
				(Map<?, ?>) generatorConfig.get("language_file") :
				new HashMap<>();
	}

	public Map<?, ?> getTagsSpecification() {
		return generatorConfig.get("tags") != null ? (Map<?, ?>) generatorConfig.get("tags") : new HashMap<>();
	}

	public List<?> getBaseTemplates() {
		return (generatorConfig.get("base_templates") != null) ?
				(List<?>) generatorConfig.get("base_templates") :
				new ArrayList<>();
	}

	public List<String> getImports() {
		return (generatorConfig.get("import") != null) ?
				((List<?>) generatorConfig.get("import")).stream().map(Object::toString).toList() :
				new ArrayList<>();
	}

	public GeneratorFlavor getGeneratorFlavor() {
		return generatorFlavor;
	}

	public String getGradleTaskFor(String purpose) {
		Map<?, ?> map = (Map<?, ?>) generatorConfig.get("gradle");
		if (map != null)
			return (String) map.get(purpose);
		else
			return null;
	}

	@Nullable public List<?> getResourceSetupTasks() {
		return (List<?>) generatorConfig.get("resources_setup_tasks");
	}

	@Nullable public List<?> getSourceSetupTasks() {
		return (List<?>) generatorConfig.get("sources_setup_tasks");
	}

	public String getJavaModelsKey() {
		return generatorConfig.get("java_models") != null ?
				((Map<?, ?>) generatorConfig.get("java_models")).get("key").toString() :
				"legacy";
	}

	public List<String> getCompatibleJavaModelKeys() {
		return compatibleJavaModelKeys;
	}

	public List<String> getJavaModelRequirementKeyWords() {
		return compatibleJavaModelRequirementKeyWords;
	}

	public List<String> getImportFormatterDuplicatesWhitelist() {
		return importFormatterDuplicatesWhitelist;
	}

	public Map<String, String> getImportFormatterPriorityImports() {
		return importFormatterPriorityImports;
	}

	public String getGeneratorName() {
		return generatorName;
	}

	@Override public boolean equals(Object o) {
		return o instanceof GeneratorConfiguration && ((GeneratorConfiguration) o).generatorName.equals(
				this.generatorName);
	}

	@Override public int hashCode() {
		return generatorName.hashCode();
	}

	@Override public String toString() {
		return ((String) generatorConfig.get("name")).replace("@minecraft", getGeneratorMinecraftVersion())
				.replace("@buildfileversion", getGeneratorBuildFileVersion());
	}

	public Map<?, ?> getRaw() {
		return generatorConfig;
	}

	public MappingLoader getMappingLoader() {
		return mappingLoader;
	}

	public DefinitionsProvider getDefinitionsProvider() {
		return definitionsProvider;
	}

	public GeneratorStats getGeneratorStats() {
		return generatorStats;
	}

	public TemplateGeneratorConfiguration getTemplateGenConfigFromName(String name) {
		return templateGeneratorConfigs.computeIfAbsent(name, key -> new TemplateGeneratorConfiguration(this, key));
	}

	public GeneratorVariableTypes getVariableTypes() {
		return generatorVariableTypes;
	}

	@Nullable public List<String> getSupportedDefinitionFields(ModElementType<?> type) {
		if (type == ModElementType.UNKNOWN)
			return null; // silently return null for unknown mod element type

		Map<?, ?> map = definitionsProvider.getModElementDefinition(type);

		if (map == null) {
			LOG.info("Failed to load element definition for mod element type " + type.getRegistryName());
			return null;
		}

		List<?> inclusions = (List<?>) map.get("field_inclusions");
		if (inclusions != null)
			return inclusions.stream().map(Object::toString).map(String::trim).collect(Collectors.toList());

		return null;
	}

	@Nullable public List<String> getUnsupportedDefinitionFields(ModElementType<?> type) {
		if (type == ModElementType.UNKNOWN)
			return null; // silently return null for unknown mod element type

		Map<?, ?> map = definitionsProvider.getModElementDefinition(type);

		if (map == null) {
			LOG.info("Failed to load element definition for mod element type " + type.getRegistryName());
			return null;
		}

		List<?> exclusions = (List<?>) map.get("field_exclusions");
		if (exclusions != null)
			return exclusions.stream().map(Object::toString).map(String::trim).collect(Collectors.toList());

		return null;
	}

	@Override public int compareTo(@Nonnull GeneratorConfiguration o) {
		if (o.getGeneratorStats().getStatus() == generatorStats.getStatus()) { // same status, sort by version
			return o.getGeneratorMinecraftVersion().compareTo(getGeneratorMinecraftVersion());
		} else { // different status, sort by status
			return o.getGeneratorStats().getStatus().ordinal() - generatorStats.getStatus().ordinal();
		}
	}

}
