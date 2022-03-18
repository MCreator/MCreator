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

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A generator templates list is used for generating several similar templates (called <i>list templates</i>)
 * for each item from a list provided by a mod element.
 *
 * @param groupName The name of this group of templates shown in workspace panel
 * @param listData  The collection used by workspace to generate given templates
 * @param templates The map of list templates to be generated for each entry of {@code listData};
 *                  keys are templates themselves, values represent generation conditions of their key template
 *                  for all items on the mentioned collection
 */
public record GeneratorTemplatesList(String groupName, Collection<?> listData,
									 Map<GeneratorTemplate, List<Boolean>> templates) {

	/**
	 * Attempts to locate the source generator template used to create given file.
	 *
	 * @param generatorFile    The input file claimed to be generated from a template from this list
	 * @param ignoreConditions Specifies whether generation conditions of templates should not be respected
	 * @return Corresponding list template in case of success, or {@code null} otherwise
	 */
	public GeneratorTemplate getCorrespondingListTemplate(File generatorFile, boolean ignoreConditions) {
		String filePath = generatorFile.getPath();
		for (GeneratorTemplate generatorTemplate : templates.keySet()) {
			String[] templatePath = generatorTemplate.getFile().getPath().split("@elementindex");
			if (filePath.startsWith(templatePath[0]) && filePath.endsWith(templatePath[1])) {
				try { // we check if given file name has list template's index in place of @elementindex
					int i = Integer.parseInt(filePath.replace(templatePath[0], "").replace(templatePath[1], ""));
					if (ignoreConditions || templates.get(generatorTemplate).get(i))
						return generatorTemplate;
				} catch (IndexOutOfBoundsException | NumberFormatException ignored) {
				}
			}
		}

		return null;
	}

	/**
	 * Extracts one of list templates for specified index and returns it.
	 *
	 * @param template One of templates in this list instance
	 * @param index    Index of list data element for which to acquire the template
	 * @return List template for given index, or {@code null} if it is greater or equal then list data size
	 */
	public GeneratorTemplate forIndex(GeneratorTemplate template, int index) {
		try {
			return new GeneratorTemplate(
					new File(template.getFile().getPath().replace("@elementindex", Integer.toString(index))),
					template.getTemplateIdentificator(), template.getTemplateData());
		} catch (IndexOutOfBoundsException ignored) {
			return null;
		}
	}
}
