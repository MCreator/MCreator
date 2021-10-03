/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GeneratorVariableTypes {

	private static final Logger LOG = LogManager.getLogger(GeneratorVariableTypes.class);

	private final Map<VariableType, Map<?, ?>> variableTypesCache = new ConcurrentHashMap<>();

	GeneratorVariableTypes(GeneratorConfiguration generatorConfiguration) {
		Set<String> fileNames = PluginLoader.INSTANCE.getResources(
				generatorConfiguration.getGeneratorName() + ".variables", Pattern.compile(".*\\.yaml"));

		for (String res : fileNames) {
			String variableTypeName = res.split("variables/")[1].replace(".yaml", "");
			if (VariableTypeLoader.INSTANCE.doesVariableTypeExist(variableTypeName)) {
				String config = FileIO.readResourceToString(PluginLoader.INSTANCE, res);
				YamlReader reader = new YamlReader(config);

				// load generator configuration
				try {
					Map<?, ?> variableTypesData = (Map<?, ?>) reader.read();
					variableTypesData = new ConcurrentHashMap<>(
							variableTypesData); // make this map concurrent, cache can be reused by multiple instances
					variableTypesCache.put(VariableTypeLoader.INSTANCE.fromName(variableTypeName), variableTypesData);
				} catch (YamlException e) {
					LOG.fatal("[" + generatorConfiguration.getGeneratorName()
							+ "] Failed to load variable type definition: " + e.getMessage());
				}
			} else {
				LOG.warn("Generator defines variable definition for unknown type: " + variableTypeName);
			}
		}
	}

	public boolean canBeGlobal(VariableType type) {
		return getSupportedScopesWithoutLocal(type).length > 0;
	}

	public boolean canBeLocal(VariableType type) {
		if (!variableTypesCache.containsKey(type) || !variableTypesCache.get(type).containsKey("scopes"))
			return false;

		Map<?, ?> scopes = (Map<?, ?>) variableTypesCache.get(type).get("scopes");
		return scopes.keySet().stream().map(Object::toString).anyMatch(s -> s.equals("local"));
	}

	public String getDefaultValue(VariableType type) {
		if (!variableTypesCache.containsKey(type) || !variableTypesCache.get(type).containsKey("defaultvalue"))
			return "null";

		return variableTypesCache.get(type).get("defaultvalue").toString();
	}

	public VariableType.Scope[] getSupportedScopesWithoutLocal(VariableType type) {
		if (!variableTypesCache.containsKey(type) || !variableTypesCache.get(type).containsKey("scopes"))
			return new VariableType.Scope[0];

		Map<?, ?> scopes = (Map<?, ?>) variableTypesCache.get(type).get("scopes");
		return scopes.keySet().stream().map(Object::toString).filter(s -> !s.equals("local")).sorted()
				.map(e -> VariableType.Scope.valueOf(e.toUpperCase(Locale.ENGLISH))).toArray(VariableType.Scope[]::new);
	}

	public Map<?, ?> getScopeDefinition(VariableType type, String scope) {
		if (!variableTypesCache.containsKey(type) || !variableTypesCache.get(type).containsKey("scopes"))
			return Collections.emptyMap();
		Map<?, ?> scopes = (Map<?, ?>) variableTypesCache.get(type).get("scopes");
		if (scopes.containsKey(scope.toLowerCase(Locale.ENGLISH)))
			return (Map<?, ?>) scopes.get(scope.toLowerCase(Locale.ENGLISH));
		else
			return Collections.emptyMap();
	}

	public Collection<VariableType> getSupportedVariableTypes() {
		return variableTypesCache.keySet().stream().filter(this::canBeGlobal).filter(this::canBeLocal)
				.collect(Collectors.toList());
	}

}
