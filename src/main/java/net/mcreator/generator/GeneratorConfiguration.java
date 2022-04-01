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

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import net.mcreator.element.ModElementType;
import net.mcreator.generator.mapping.MappingLoader;
import net.mcreator.generator.template.TemplateGeneratorConfiguration;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.workspace.types.WorkspaceType;
import net.mcreator.workspace.types.WorkspaceTypeLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	private Map<?, ?> generatorConfig;
	private final String generatorName;

	private final MappingLoader mappingLoader;
	private final DefinitionsProvider definitionsProvider;
	private final GeneratorStats generatorStats;

	private final GeneratorFlavor generatorFlavor;

	private final GeneratorVariableTypes generatorVariableTypes;

	private final Map<String, TemplateGeneratorConfiguration> templateGeneratorConfigs = new HashMap<>();

	public GeneratorConfiguration(String generatorName) {
		this.generatorName = generatorName;

		String config = FileIO.readResourceToString(PluginLoader.INSTANCE, "/" + generatorName + "/generator.yaml");
		YamlReader reader = new YamlReader(config);

		// load generator configuration
		try {
			generatorConfig = (Map<?, ?>) reader.read();
			generatorConfig = new ConcurrentHashMap<>(
					generatorConfig); // make this map concurent, cache can be reused by multiple instances
		} catch (YamlException e) {
			LOG.fatal("[" + generatorName + "] Error: " + e.getMessage());
		}

		this.generatorFlavor = GeneratorFlavor.valueOf(this.generatorName.split("-")[0].toUpperCase(Locale.ENGLISH));

		// load mappings
		this.mappingLoader = new MappingLoader(this);
		this.definitionsProvider = new DefinitionsProvider(generatorName);

		// load global variable definitions
		this.generatorVariableTypes = new GeneratorVariableTypes(this);

		this.generatorStats = new GeneratorStats(this);
	}

	@Nonnull public WorkspaceType getWorkspaceType() {
		return WorkspaceTypeLoader.INSTANCE.fromID((String) generatorConfig.get("workspacetype"));
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

	public Map<?, ?> getStardIDMap() {
		return generatorConfig.get("start_id_map") != null ?
				(Map<?, ?>) generatorConfig.get("start_id_map") :
				new HashMap<>();
	}

	public Map<?, ?> getLanguageFileSpecification() {
		return generatorConfig.get("language_file") != null ?
				(Map<?, ?>) generatorConfig.get("language_file") :
				new HashMap<>();
	}

	public List<?> getBaseTemplates() {
		return (generatorConfig.get("base_templates") != null) ?
				(List<?>) generatorConfig.get("base_templates") :
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
		List<String> retval = new ArrayList<>();
		retval.add(getJavaModelsKey());

		if (generatorConfig.get("java_models") != null) {
			if (((Map<?, ?>) generatorConfig.get("java_models")).get("compatible") != null) {
				retval.addAll(((List<?>) ((Map<?, ?>) generatorConfig.get("java_models")).get("compatible")).stream()
						.map(Object::toString).toList());
			}
		}

		return retval;
	}

	public List<String> getJavaModelRequirementKeyWords() {
		List<String> retval = new ArrayList<>();

		if (generatorConfig.get("java_models") != null) {
			if (((Map<?, ?>) generatorConfig.get("java_models")).get("requested_key_words") != null) {
				retval.addAll(
						((List<?>) ((Map<?, ?>) generatorConfig.get("java_models")).get("requested_key_words")).stream()
								.map(Object::toString).toList());
			}
		}

		return retval;
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
		if (templateGeneratorConfigs.containsKey(name))
			return templateGeneratorConfigs.get(name);
		else {
			TemplateGeneratorConfiguration tpl = new TemplateGeneratorConfiguration(generatorName, name);
			templateGeneratorConfigs.put(name, tpl);
			return tpl;
		}
	}

	public GeneratorVariableTypes getVariableTypes() {
		return generatorVariableTypes;
	}

	@Nullable public List<String> getSupportedDefinitionFields(ModElementType<?> type) {
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
