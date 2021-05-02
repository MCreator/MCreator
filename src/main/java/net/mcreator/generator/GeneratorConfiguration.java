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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	private final TemplateGeneratorConfiguration templateGeneratorConfiguration;
	private final TemplateGeneratorConfiguration procedureGeneratorConfiguration;
	private final TemplateGeneratorConfiguration triggerGeneratorConfiguration;
	private final TemplateGeneratorConfiguration aitaskGeneratorConfiguration;
	private final TemplateGeneratorConfiguration jsonTriggerGeneratorConfiguration;

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

		// load template configurations
		this.templateGeneratorConfiguration = new TemplateGeneratorConfiguration(generatorName, "templates");
		this.procedureGeneratorConfiguration = new TemplateGeneratorConfiguration(generatorName, "procedures");
		this.triggerGeneratorConfiguration = new TemplateGeneratorConfiguration(generatorName, "triggers");
		this.aitaskGeneratorConfiguration = new TemplateGeneratorConfiguration(generatorName, "aitasks");
		this.jsonTriggerGeneratorConfiguration = new TemplateGeneratorConfiguration(generatorName, "jsontriggers");

		this.generatorStats = new GeneratorStats(this);
	}

	@NotNull public String getSourceRoot() {
		return (String) generatorConfig.get("source_root");
	}

	@NotNull public String getResourceRoot() {
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

	@NotNull public String getGeneratorMinecraftVersion() {
		return this.generatorName.split("-")[1];
	}

	@NotNull public String getGeneratorBuildFileVersion() {
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

	public String getGeneratorName() {
		return generatorName;
	}

	@Override public boolean equals(Object o) {
		return o instanceof GeneratorConfiguration && ((GeneratorConfiguration) o).generatorName
				.equals(this.generatorName);
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

	public TemplateGeneratorConfiguration getTemplateGeneratorConfiguration() {
		return templateGeneratorConfiguration;
	}

	public TemplateGeneratorConfiguration getProcedureGeneratorConfiguration() {
		return procedureGeneratorConfiguration;
	}

	public TemplateGeneratorConfiguration getTriggerGeneratorConfiguration() {
		return triggerGeneratorConfiguration;
	}

	public TemplateGeneratorConfiguration getAITaskGeneratorConfiguration() {
		return aitaskGeneratorConfiguration;
	}

	public TemplateGeneratorConfiguration getJSONTriggerGeneratorConfiguration() {
		return jsonTriggerGeneratorConfiguration;
	}

	@Nullable public List<String> getSupportedDefinitionFields(ModElementType type) {
		Map<?, ?> map = definitionsProvider.getModElementDefinition(type);

		if (map == null) {
			LOG.info("Failed to load element definition for mod element type " + type.name());
			return null;
		}

		List<?> inclusions = (List<?>) map.get("field_inclusions");
		if (inclusions != null)
			return inclusions.stream().map(Object::toString).map(String::trim).collect(Collectors.toList());

		return null;
	}

	@Override public int compareTo(@NotNull GeneratorConfiguration o) {
		if (o.getGeneratorStats().getStatus() == generatorStats.getStatus()) { // same status, sort by version
			return o.getGeneratorMinecraftVersion().compareTo(getGeneratorMinecraftVersion());
		} else { // different status, sort by status
			return o.getGeneratorStats().getStatus().ordinal() - generatorStats.getStatus().ordinal();
		}
	}
}
