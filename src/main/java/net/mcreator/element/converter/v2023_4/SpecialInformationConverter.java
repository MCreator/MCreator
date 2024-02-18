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

package net.mcreator.element.converter.v2023_4;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.procedure.StringListProcedure;
import net.mcreator.element.types.Armor;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SpecialInformationConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger("SpecialInformationConverter");

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		try {
			JsonObject oldObject = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

			if (input instanceof Armor armor) { // Armor is special case, we can handle this one without reflection
				List<String> helmetSpecialInfo = new ArrayList<>();
				List<String> bodySpecialInfo = new ArrayList<>();
				List<String> leggingsSpecialInfo = new ArrayList<>();
				List<String> bootsSpecialInfo = new ArrayList<>();

				if (oldObject.get("helmetSpecialInfo") != null)
					oldObject.getAsJsonArray("helmetSpecialInfo").iterator()
							.forEachRemaining(jsonElement -> helmetSpecialInfo.add(jsonElement.getAsString()));
				if (oldObject.get("bodySpecialInfo") != null)
					oldObject.getAsJsonArray("bodySpecialInfo").iterator()
							.forEachRemaining(jsonElement -> bodySpecialInfo.add(jsonElement.getAsString()));
				if (oldObject.get("leggingsSpecialInfo") != null)
					oldObject.getAsJsonArray("leggingsSpecialInfo").iterator()
							.forEachRemaining(jsonElement -> leggingsSpecialInfo.add(jsonElement.getAsString()));
				if (oldObject.get("bootsSpecialInfo") != null)
					oldObject.getAsJsonArray("bootsSpecialInfo").iterator()
							.forEachRemaining(jsonElement -> bootsSpecialInfo.add(jsonElement.getAsString()));

				armor.helmetSpecialInformation = new StringListProcedure(null, helmetSpecialInfo);
				armor.bodySpecialInformation = new StringListProcedure(null, bodySpecialInfo);
				armor.leggingsSpecialInformation = new StringListProcedure(null, leggingsSpecialInfo);
				armor.bootsSpecialInformation = new StringListProcedure(null, bootsSpecialInfo);
			} else {
				List<String> specialInfo = new ArrayList<>();
				if (oldObject.get("specialInfo") != null)
					oldObject.getAsJsonArray("specialInfo").iterator()
							.forEachRemaining(jsonElement -> specialInfo.add(jsonElement.getAsString()));

				Field specialInformationField = input.getClass().getDeclaredField("specialInformation");
				specialInformationField.setAccessible(true);
				specialInformationField.set(input, new StringListProcedure(null, specialInfo));
			}
		} catch (Exception e) {
			LOG.warn("Failed to convert special information for " + input.getModElement().getName(), e);
		}

		return input;
	}

	@Override public int getVersionConvertingTo() {
		return 50;
	}

}
