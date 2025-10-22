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

package net.mcreator.element.converter.v2023_3;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Plant;
import net.mcreator.workspace.Workspace;

public class PlantGenerationTypeConverter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Plant plant = (Plant) input;
		JsonObject definition = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject();
		if ("normal".equals(plant.plantType) && definition.get("staticPlantGenerationType") != null) {
			plant.generationType = definition.get("staticPlantGenerationType").getAsString();
		} else if ("double".equals(plant.plantType) && definition.get("doublePlantGenerationType") != null) {
			plant.generationType = definition.get("doublePlantGenerationType").getAsString();
		} else {
			plant.generationType = "Flower";
		}
		return plant;
	}

	@Override public int getVersionConvertingTo() {
		return 47;
	}

}