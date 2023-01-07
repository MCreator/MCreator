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

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * A generator templates list is used for generating several similar templates for each item on a list provided by given
 * mod element.
 *
 * @param groupName The name of this group of templates shown in workspace panel.
 * @param listData  The collection used by workspace to generate given templates.
 * @param templates The list of templates to be generated for each {@code listData} item.
 */
public record GeneratorTemplatesList(String groupName, List<?> listData, List<List<ListTemplate>> templates) {

	/**
	 * The sole constructor.
	 */
	public GeneratorTemplatesList {
		templates.forEach(l -> l.forEach(e -> e.setTemplatesList(this)));
	}

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
			templates.get(index).forEach(action);
		}
	}
}
