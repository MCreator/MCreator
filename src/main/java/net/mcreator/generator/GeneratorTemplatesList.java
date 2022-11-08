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

import net.mcreator.element.GeneratableElement;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * A generator templates list is used for generating several similar templates (called <i>list templates</i>)
 * for each item from a list provided by a mod element.
 *
 * @param groupName The name of this group of templates shown in workspace panel.
 * @param listData  The collection used by workspace to generate given templates.
 * @param element   The {@link GeneratableElement} used to process tokens in template names.
 * @param templates The map of list templates to be generated for each entry of {@code listData};
 *                  keys are templates themselves, values represent generation conditions of their key template
 *                  for all items on the mentioned collection.
 */
public record GeneratorTemplatesList(String groupName, List<?> listData, GeneratableElement element,
									 Map<GeneratorTemplate, List<Boolean>> templates) {

	/**
	 * The sole constructor.
	 */
	public GeneratorTemplatesList {
		for (GeneratorTemplate listTemplate : templates.keySet())
			listTemplate.setTemplatesList(this);
	}

	/**
	 * Attempts to locate the source generator template used to create given file.
	 *
	 * @param generatorFile    The input file claimed to be generated from a template from this list.
	 * @param ignoreConditions Specifies whether generation conditions of templates should not be respected.
	 * @return Corresponding list template in case of success, or {@code null} otherwise.
	 */
	public GeneratorTemplate getCorrespondingListTemplate(File generatorFile, boolean ignoreConditions) {
		for (GeneratorTemplate listTemplate : templates.keySet()) {
			for (int i = 0; i < listData.size(); i++) {
				if (processTokens(listTemplate, i).getAbsolutePath().equals(generatorFile.getAbsolutePath())) {
					if (ignoreConditions || templates.get(listTemplate).get(i))
						return listTemplate;
				}
			}
		}

		return null;
	}

	/**
	 * Replaces tokens on name of provided list template with appropriate values from
	 * {@link GeneratorTemplatesList#element} and returns the target file.
	 *
	 * @param generatorTemplate One of templates in this list instance.
	 * @param index             Index of listData element for which to acquire the output file path.
	 * @return The file generated from given list template with given token values.
	 */
	public File processTokens(GeneratorTemplate generatorTemplate, int index) {
		if (generatorTemplate.getTemplatesList() == this) {
			return new File(GeneratorTokens.replaceVariableTokens(element, listData.get(index),
					GeneratorTokens.replaceTokens(element.getModElement().getWorkspace(),
							generatorTemplate.getFile().getAbsolutePath()
									.replace("@NAME", element.getModElement().getName())
									.replace("@registryname", element.getModElement().getRegistryName())
									.replace("@elementindex", Integer.toString(index)))));
		}
		return generatorTemplate.getFile();
	}
}
