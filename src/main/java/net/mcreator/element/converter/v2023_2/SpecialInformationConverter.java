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

package net.mcreator.element.converter.v2023_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.procedure.StringProcedure;
import net.mcreator.workspace.Workspace;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpecialInformationConverter<T extends GeneratableElement> implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		T object = (T) input;
		JsonObject oldObject = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

		List<String> specialInfo = new ArrayList<>();
		List<String> helmetSpecialInfo = new ArrayList<>();
		List<String> bodySpecialInfo = new ArrayList<>();
		List<String> leggingsSpecialInfo = new ArrayList<>();
		List<String> bootsSpecialInfo = new ArrayList<>();
		if (oldObject.get("specialInfo") != null)
			oldObject.getAsJsonArray("specialInfo").iterator()
					.forEachRemaining(jsonElement -> specialInfo.add(jsonElement.getAsString()));
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

		if (!specialInfo.isEmpty()) {
			try {
				Field specialInformationField = object.getClass().getDeclaredField("specialInformation");
				specialInformationField.setAccessible(true);

				specialInformationField.set(object, new StringProcedure(null,
						specialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(","))));
			} catch (IllegalAccessException | NoSuchFieldException ignored) {

			}
		}
		if (!helmetSpecialInfo.isEmpty()) {
			try {
				Field helmetSecialInformationField = object.getClass().getDeclaredField("helmetSpecialInformation");
				helmetSecialInformationField.setAccessible(true);

				helmetSecialInformationField.set(object, new StringProcedure(null,
						helmetSpecialInfo.stream().map(info -> info.replace(",", "\\,"))
								.collect(Collectors.joining(","))));
			} catch (IllegalAccessException | NoSuchFieldException ignored) {

			}
		}
		if (!bodySpecialInfo.isEmpty()) {
			try {
				Field bodySecialInformationField = object.getClass().getDeclaredField("bodySpecialInformation");
				bodySecialInformationField.setAccessible(true);

				bodySecialInformationField.set(object, new StringProcedure(null,
						bodySpecialInfo.stream().map(info -> info.replace(",", "\\,"))
								.collect(Collectors.joining(","))));
			} catch (IllegalAccessException | NoSuchFieldException ignored) {

			}
		}
		if (!leggingsSpecialInfo.isEmpty()) {
			try {
				Field leggingsSecialInformationField = object.getClass().getDeclaredField("leggingsSpecialInformation");
				leggingsSecialInformationField.setAccessible(true);

				leggingsSecialInformationField.set(object, new StringProcedure(null,
						leggingsSpecialInfo.stream().map(info -> info.replace(",", "\\,"))
								.collect(Collectors.joining(","))));
			} catch (IllegalAccessException | NoSuchFieldException ignored) {

			}
		}
		if (!bootsSpecialInfo.isEmpty()) {
			try {
				Field bootsSecialInformationField = object.getClass().getDeclaredField("bootsSpecialInformation");
				bootsSecialInformationField.setAccessible(true);

				bootsSecialInformationField.set(object, new StringProcedure(null,
						bootsSpecialInfo.stream().map(info -> info.replace(",", "\\,"))
								.collect(Collectors.joining(","))));
			} catch (IllegalAccessException | NoSuchFieldException ignored) {

			}
		}

		return object;
	}

	@Override public int getVersionConvertingTo() {
		return 44;
	}
}
