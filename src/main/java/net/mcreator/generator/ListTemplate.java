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

import java.io.File;
import java.util.Map;

/**
 * This is a subclass of {@code GeneratorTemplate} that is connected to a {@code GeneratorTemplatesList}
 * it's created by and specific {@code listData} item index. These two are used by generator to generate the result file
 * with the appropriate contents that in this case don't only depend on given mod element, but also on a certain
 * {@code listData} item.
 */
public class ListTemplate extends GeneratorTemplate {
	private final GeneratorTemplatesList templatesList;
	private final int listItemIndex;

	ListTemplate(File file, String templateIdentifier, GeneratorTemplatesList templatesList, int listItemIndex,
			Map<?, ?> templateData) {
		super(file, templateIdentifier, templateData);
		this.templatesList = templatesList;
		this.listItemIndex = listItemIndex;
	}

	public GeneratorTemplatesList getTemplatesList() {
		return templatesList;
	}

	public int getListItemIndex() {
		return listItemIndex;
	}
}
