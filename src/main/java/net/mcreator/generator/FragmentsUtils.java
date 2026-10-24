/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.generator.template.TemplateExpressionParser;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.util.*;

public class FragmentsUtils {

	/**
	 * Collects and renders fragments of the given type. Fragments are defined in "fragments" lists of mod element
	 * type definitions and generator configuration. Each fragment defines "type" and either "template" to render or
	 * literal "content" (a string or a list of strings, each list entry is a separate fragment), and can optionally
	 * define template condition and variables. Only fragments from
	 * definitions of types present in the workspace are considered and these fragments get the list of elements of
	 * their type in the data model, same as global templates do. Fragments that render to blank output are omitted.
	 *
	 * @param generator Generator to collect fragments for
	 * @param type      Type of fragments to collect
	 * @return Rendered fragments of the given type in deterministic order
	 */
	public static List<String> getFragments(Generator generator, String type) throws TemplateGeneratorException {
		GeneratorConfiguration generatorConfiguration = generator.getGeneratorConfiguration();
		DefinitionsProvider definitionsProvider = generatorConfiguration.getDefinitionsProvider();
		Workspace workspace = generator.getWorkspace();

		List<String> fragments = new ArrayList<>();

		// Fragments from definitions of mod element types, elements are only loaded for types defining fragments
		for (ModElementType<?> modElementType : ModElementTypeLoader.getAllModElementTypes()) {
			Map<?, ?> definition = definitionsProvider.getModElementDefinition(modElementType);
			List<Map<?, ?>> fragmentDefinitions = getFragmentDefinitionsOfType(
					definition != null ? definition.get("fragments") : null, type);
			if (fragmentDefinitions.isEmpty())
				continue;

			List<GeneratableElement> elements = workspace.getModElementsByType(modElementType).stream()
					.map(ModElement::getGeneratableElement).filter(Objects::nonNull).toList();
			if (!elements.isEmpty())
				collectFragments(generator, fragmentDefinitions, Map.of(modElementType.getPluralName(), elements),
						fragments);
		}

		// Fragments from generator configuration
		collectFragments(generator, getFragmentDefinitionsOfType(generatorConfiguration.getGlobalFragments(), type),
				Map.of(), fragments);

		return fragments;
	}

	private static List<Map<?, ?>> getFragmentDefinitionsOfType(@Nullable Object fragmentDefinitions, String type) {
		if (fragmentDefinitions instanceof List<?> fragmentDefinitionsList)
			return fragmentDefinitionsList.stream()
					.filter(e -> e instanceof Map<?, ?> fragment && type.equals(fragment.get("type")))
					.<Map<?, ?>>map(e -> (Map<?, ?>) e).toList();
		return List.of();
	}

	private static void collectFragments(Generator generator, List<Map<?, ?>> fragmentDefinitions,
			Map<String, Object> dataModel, List<String> fragments) throws TemplateGeneratorException {
		TemplateGenerator templateGenerator = generator.getTemplateGeneratorFromName("templates");
		for (Map<?, ?> fragment : fragmentDefinitions) {
			if (TemplateExpressionParser.shouldSkipTemplateBasedOnCondition(generator, fragment,
					generator.getWorkspace().getWorkspaceInfo()))
				continue;

			if (fragment.get("template") != null) {
				String code = templateGenerator.generateBaseFromTemplate((String) fragment.get("template"),
						new HashMap<>(dataModel), (String) fragment.get("variables"));
				if (!code.isBlank())
					fragments.add(code);
			} else if (fragment.get("content") instanceof List<?> content) {
				content.forEach(e -> fragments.add(e.toString()));
			} else if (fragment.get("content") != null) {
				fragments.add(fragment.get("content").toString());
			}
		}
	}

}
