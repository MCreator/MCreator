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

package net.mcreator.element.converter.fv32;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.interfaces.ISpecialInformationHolder;
import net.mcreator.workspace.Workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpecialInformationConverter<T extends ISpecialInformationHolder> implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		T object = (T) input;
		JsonObject oldObject = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

		List<String> specialInfo = new ArrayList<>();
		if (oldObject.get("specialInfo") != null)
			oldObject.getAsJsonArray("specialInfo").iterator()
					.forEachRemaining(jsonElement -> specialInfo.add(jsonElement.getAsString()));

		object.getSpecialInformation().setFixedText(
				specialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));
		return (GeneratableElement) object;
	}

	@Override public int getVersionConvertingTo() {
		return 32;
	}
}
