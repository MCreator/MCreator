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

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * A generator templates list is used for generating several similar templates for each item on a list provided by given
 * mod element.
 *
 * @param groupName The name of this group of templates shown in workspace panel.
 * @param listData  The collection used by workspace to generate given templates.
 * @param element   The {@link GeneratableElement} used to process tokens in template names.
 * @param templates The map of templates to be generated for each entry of {@code listData};
 *                  keys are templates themselves, values represent generation conditions of their key template
 *                  for all items on the mentioned collection.
 */
public record GeneratorTemplatesList(String groupName, List<?> listData, GeneratableElement element,
									 Map<GeneratorTemplate, List<Boolean>> templates) {

	/**
	 * Iterates over all regular templates that can be produced by this templates list instance.
	 *
	 * @param action             Action to be performed for each generated template.
	 * @param beforeNextListItem Optional action to be performed before next item from the list data is processed.
	 */
	public void forEachTemplate(Consumer<ListTemplate> action, @Nullable IntConsumer beforeNextListItem) {
		for (int index = 0; index < listData.size(); index++) {
			if (beforeNextListItem != null)
				beforeNextListItem.accept(index);
			for (GeneratorTemplate template : templates.keySet()) {
				if (templates.get(template).get(index)) {
					File targetFile = new File(GeneratorTokens.replaceVariableTokens(element, listData.get(index),
							GeneratorTokens.replaceTokens(element.getModElement().getWorkspace(),
									template.getFile().getPath().replace("@NAME", element.getModElement().getName())
											.replace("@registryname", element.getModElement().getRegistryName())
											.replace("@itemindex", Integer.toString(index)))));
					action.accept(new ListTemplate(targetFile, template.getTemplateIdentificator(), this, index,
							template.getTemplateData()));
				}
			}
		}
	}
}
