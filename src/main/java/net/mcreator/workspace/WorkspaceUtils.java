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

package net.mcreator.workspace;

import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.element.converter.ConverterRegistry;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.plugin.modapis.ModAPIManager;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableType;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WorkspaceUtils {

	public static File getWorkspaceFileForWorkspaceFolder(File workspaceDir) {
		File[] files = workspaceDir.listFiles();
		for (File wfile : files != null ? files : new File[0])
			if (wfile.isFile() && wfile.getName().endsWith(".mcreator"))
				return wfile;
		return null;
	}

	public static void verifyPluginRequirements(Workspace workspace, GeneratorConfiguration generatorConfiguration)
			throws MissingGeneratorFeaturesException {
		Map<String, Collection<String>> missingDefinitions = new LinkedHashMap<>();

		// Check if all required APIs are present
		Set<String> workspaceRequiredAPIs = workspace.getWorkspaceSettings().getMCreatorDependenciesRaw();
		Set<String> generatorSupportedAPIs = ModAPIManager.getModAPIsForGenerator(
						generatorConfiguration.getGeneratorName()).stream().map(e -> e.parent().id())
				.collect(Collectors.toSet());
		Set<String> missingAPIs = workspaceRequiredAPIs.stream().filter(e -> !generatorSupportedAPIs.contains(e))
				.collect(Collectors.toSet());
		if (!missingAPIs.isEmpty())
			missingDefinitions.put("APIs", missingAPIs);

		// Check if all required METs are present
		Set<String> workspaceRequiredMETs = workspace.getModElements().stream().map(ModElement::getTypeString)
				.collect(Collectors.toSet());
		Set<String> generatorSupportedMETs = ModElementTypeLoader.getModElementTypes().stream()
				.filter(e -> generatorConfiguration.getGeneratorStats().getModElementTypeCoverageInfo().get(e)
						!= GeneratorStats.CoverageStatus.NONE).map(ModElementType::getRegistryName)
				.collect(Collectors.toSet());
		generatorSupportedMETs.addAll(ConverterRegistry.getConvertibleModElementTypes());
		Set<String> missingMETs = workspaceRequiredMETs.stream().filter(e -> !generatorSupportedMETs.contains(e))
				.collect(Collectors.toSet());
		if (!missingMETs.isEmpty())
			missingDefinitions.put("Mod element types", missingMETs);

		// Check if all required VETs are present
		Set<String> workspaceRequiredVETs = workspace.getVariableElements().stream().map(VariableElement::getTypeString)
				.collect(Collectors.toSet());
		Set<String> generatorSupportedVETs = generatorConfiguration.getVariableTypes().getSupportedVariableTypes()
				.stream().map(VariableType::getName).collect(Collectors.toSet());
		Set<String> missingVETs = workspaceRequiredVETs.stream().filter(e -> !generatorSupportedVETs.contains(e))
				.collect(Collectors.toSet());
		if (!missingVETs.isEmpty())
			missingDefinitions.put("Variable types", missingVETs);

		if (!missingDefinitions.isEmpty())
			throw new MissingGeneratorFeaturesException(missingDefinitions);
	}

}
