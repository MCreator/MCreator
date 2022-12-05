/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

public class ListTemplate extends GeneratorTemplate {
	private GeneratorTemplatesList templatesList;
	private final int listItemIndex;

	ListTemplate(File file, String templateIdentificator, Object templateData) {
		this(file, templateIdentificator, null, -1, templateData);
	}

	ListTemplate(File file, String templateIdentificator, GeneratorTemplatesList templatesList, int listItemIndex,
			Object templateData) {
		super(file, templateIdentificator, templateData);
		this.templatesList = templatesList;
		this.listItemIndex = listItemIndex;
	}

	public GeneratorTemplatesList getTemplatesList() {
		return templatesList;
	}

	void setTemplatesList(GeneratorTemplatesList templatesList) {
		this.templatesList = templatesList;
	}

	public int getListItemIndex() {
		return listItemIndex;
	}
}
