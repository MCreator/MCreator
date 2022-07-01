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
import net.mcreator.element.types.Armor;
import net.mcreator.workspace.Workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArmorSpecialInformationConverter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Armor armor = (Armor) input;
		JsonObject oldArmor = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

		List<String> helmetSpecialInfo = new ArrayList<>();
		List<String> bodySpecialInfo = new ArrayList<>();
		List<String> leggingsSpecialInfo = new ArrayList<>();
		List<String> bootsSpecialInfo = new ArrayList<>();
		if (oldArmor.get("helmetSpecialInfo") != null)
			oldArmor.getAsJsonArray("helmetSpecialInfo").iterator()
					.forEachRemaining(jsonElement -> helmetSpecialInfo.add(jsonElement.getAsString()));
		if (oldArmor.get("bodySpecialInfo") != null)
			oldArmor.getAsJsonArray("bodySpecialInfo").iterator()
					.forEachRemaining(jsonElement -> bodySpecialInfo.add(jsonElement.getAsString()));
		if (oldArmor.get("leggingsSpecialInfo") != null)
			oldArmor.getAsJsonArray("leggingsSpecialInfo").iterator()
					.forEachRemaining(jsonElement -> leggingsSpecialInfo.add(jsonElement.getAsString()));
		if (oldArmor.get("bootsSpecialInfo") != null)
			oldArmor.getAsJsonArray("bootsSpecialInfo").iterator()
					.forEachRemaining(jsonElement -> bootsSpecialInfo.add(jsonElement.getAsString()));

		armor.helmetSpecialInformation.setFixedText(
				helmetSpecialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));
		armor.bodySpecialInformation.setFixedText(
				bodySpecialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));
		armor.leggingsSpecialInformation.setFixedText(
				leggingsSpecialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));
		armor.bootsSpecialInformation.setFixedText(
				bootsSpecialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));

		return armor;
	}

	@Override public int getVersionConvertingTo() {
		return 31;
	}
}
