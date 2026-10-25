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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

public class FragmentsUtils {

	private static final Logger LOG = LogManager.getLogger(FragmentsUtils.class);

	/**
	 * Collects fragments defined in "fragments" lists of used mod element type definitions and generator configuration.
	 * Each fragment defines "type" and either "template" to render or literal "content" (string or list of strings),
	 * and can optionally define a condition. Fragments of mod element types get elements of their type in the data model.
	 *
	 * @param generator             Generator to collect fragments for
	 * @param modElementTypeListMap Generatable elements grouped by mod element type
	 * @return Fragments grouped by fragment type, types with a fragment definition file are always present
	 */
	public static Map<String, List<String>> collectFragments(Generator generator,
			Map<ModElementType<?>, List<GeneratableElement>> modElementTypeListMap) {
		DefinitionsProvider definitionsProvider = generator.getGeneratorConfiguration().getDefinitionsProvider();

		Map<String, List<String>> fragments = new HashMap<>();
		definitionsProvider.getFragmentDefinitions().keySet().forEach(type -> fragments.put(type, new ArrayList<>()));

		// Fragments from definitions of used mod element types, in mod element type registry order
		for (ModElementType<?> type : ModElementTypeLoader.getAllModElementTypes()) {
			List<GeneratableElement> elements = modElementTypeListMap.get(type);
			Map<?, ?> definition = definitionsProvider.getModElementDefinition(type);
			if (elements != null && definition != null)
				collectFragments(generator, definition.get("fragments"), Map.of(type.getPluralName(), elements),
						fragments);
		}

		// Fragments from generator configuration
		collectFragments(generator, generator.getGeneratorConfiguration().getGlobalFragments(), Map.of(), fragments);

		return fragments;
	}

	private static void collectFragments(Generator generator, @Nullable Object fragmentDefinitions,
			Map<String, Object> dataModel, Map<String, List<String>> fragments) {
		if (!(fragmentDefinitions instanceof List<?> fragmentDefinitionsList))
			return;

		TemplateGenerator templateGenerator = generator.getTemplateGeneratorFromName("templates");
		for (Object fragmentDefinition : fragmentDefinitionsList) {
			if (!(fragmentDefinition instanceof Map<?, ?> fragment) || fragment.get("type") == null)
				continue;

			if (TemplateExpressionParser.shouldSkipTemplateBasedOnCondition(generator, fragment,
					generator.getWorkspace().getWorkspaceInfo()))
				continue;

			List<String> typeFragments = fragments.computeIfAbsent(fragment.get("type").toString(),
					_ -> new ArrayList<>());
			if (fragment.get("template") != null) {
				try {
					String code = templateGenerator.generateBaseFromTemplate((String) fragment.get("template"),
							new HashMap<>(dataModel), (String) fragment.get("variables"));
					if (!code.isBlank())
						typeFragments.add(code);
				} catch (TemplateGeneratorException e) {
					LOG.error("Failed to generate fragment from template {}", fragment.get("template"), e);
				}
			} else if (fragment.get("content") instanceof List<?> content) {
				content.forEach(e -> typeFragments.add(e.toString()));
			} else if (fragment.get("content") != null) {
				typeFragments.add(fragment.get("content").toString());
			}
		}
	}

}
