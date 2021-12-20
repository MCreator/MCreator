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

public record GeneratorTemplatesList(String groupName, Collection<?> listData,
									 Map<GeneratorTemplate, List<Boolean>> templates) {

	public GeneratorTemplate getCorrespondingListTemplate(File generatorFile) {
		for (GeneratorTemplate generatorTemplate : templates.keySet()) {
			String filePath = generatorFile.getPath();
			String[] templatePathParts = generatorTemplate.getFile().getPath().split("@elementindex");
			boolean validToCheck = true;

			try { // we check if given file name has list template's index in place of @elementindex
				Integer.parseInt(filePath.replace(templatePathParts[0], "").replace(templatePathParts[1], ""));
			} catch (NumberFormatException ignored) {
				validToCheck = false;
			}

			if (validToCheck && filePath.startsWith(templatePathParts[0]) && filePath.endsWith(templatePathParts[1]))
				return generatorTemplate;
		}
		return null;
	}
}
