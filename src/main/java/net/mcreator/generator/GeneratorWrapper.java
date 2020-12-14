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

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeRegistry;
import net.mcreator.element.NamespacedGeneratableElement;
import net.mcreator.element.parts.Procedure;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElement;
import org.apache.commons.io.FilenameUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused") public class GeneratorWrapper {

	private final Generator generator;

	public GeneratorWrapper(Generator generator) {
		this.generator = generator;
	}

	public String getGeneratorBuildFileVersion() {
		return generator.getGeneratorBuildFileVersion();
	}

	public int getStartIDFor(String baseType) {
		return generator.getStartIDFor(baseType);
	}

	public String map(String rawName, String mappingMap) {
		return new NameMapper(generator.getWorkspace(), mappingMap).getMapping(rawName);
	}

	public String map(String rawName, String mappingMap, int mappingTable) {
		return new NameMapper(generator.getWorkspace(), mappingMap).getMapping(rawName, mappingTable);
	}

	public VariableElement getVariableElementByName(String elementName) {
		return generator.getWorkspace().getVariableElementByName(elementName);
	}

	public List<Procedure> procedureNamesToObjects(String proceduresString) {
		return Arrays.stream(proceduresString.split(",")).map(e -> {
			if (e.equals("null"))
				return null;
			return new Procedure(e);
		}).collect(Collectors.toList());
	}

	public ModElementType.RecipeElementType getRecipeElementType(String elementName) {
		try {
			return generator.getWorkspace().getModElementByName(getElementPlainName(elementName)).getType()
					.getRecipeElementType();
		} catch (Exception e) {
			generator.getLogger().warn("Failed to determine recipe type for: " + elementName, e);
			return ModElementType.RecipeElementType.NONE;
		}
	}

	public String getElementPlainName(String elementName) {
		return elementName.replace("CUSTOM:", "").replace(".block", "").replace(".helmet", "").replace(".body", "")
				.replace(".legs", "").replace(".boots", "");
	}

	public String getElementExtension(String elementName) {
		if (elementName.contains(".")) {
			return FilenameUtils.getExtension(elementName);
		}
		return elementName;
	}

	public Workspace getWorkspace() {
		return generator.getWorkspace();
	}

	public String getRegistryNameForModElement(String modElement) {
		ModElement element = generator.getWorkspace().getModElementByName(modElement);
		if (element != null)
			return element.getRegistryName();

		generator.LOG.warn("Failed to determine registry name for: " + modElement);
		return "unknown_element";
	}

	public String getResourceLocationForModElement(String modElement) {
		ModElement element = generator.getWorkspace().getModElementByName(modElement);
		if (element != null) {
			// check if we are dealing with namespaced element
			if (NamespacedGeneratableElement.class.isAssignableFrom(
					ModElementTypeRegistry.REGISTRY.get(element.getType()).getModElementStorageClass())) {
				GeneratableElement namespacedgeneratableemenet = element.getGeneratableElement();
				if (namespacedgeneratableemenet instanceof NamespacedGeneratableElement) {
					return ((NamespacedGeneratableElement) namespacedgeneratableemenet).getResourceLocation();
				}
			}

			// otherwise use normal registry name
			return generator.getWorkspace().getWorkspaceSettings().getModID() + ":" + element.getRegistryName();
		}

		generator.LOG.warn("Failed to determine resource location for: " + modElement);
		return "unknown_element";
	}

}
