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

package net.mcreator.element.converter.fv30;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.MusicDisc;
import net.mcreator.workspace.Workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MusicDiscSpecialInformationConverter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		MusicDisc musicDisc = (MusicDisc) input;
		JsonObject oldMusicDisc = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");

		List<String> specialInfo = new ArrayList<>();
		if (oldMusicDisc.get("specialInfo") != null)
			oldMusicDisc.getAsJsonArray("specialInfo").iterator()
					.forEachRemaining(jsonElement -> specialInfo.add(jsonElement.getAsString()));

		musicDisc.specialInformation.setFixedText(
				specialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));
		return musicDisc;
	}

	@Override public int getVersionConvertingTo() {
		return 30;
	}
}
