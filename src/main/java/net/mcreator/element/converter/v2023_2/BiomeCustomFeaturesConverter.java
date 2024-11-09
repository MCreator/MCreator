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
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Biome;
import net.mcreator.workspace.Workspace;

public class BiomeCustomFeaturesConverter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Biome biome = (Biome) input;

		if (jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("grassPerChunk") != null) {
			int grassPerChunk = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject()
					.get("grassPerChunk").getAsInt();
			if (grassPerChunk > 0) {
				if (!biome.defaultFeatures.contains("ForestGrass"))
					biome.defaultFeatures.add("ForestGrass");
			}
		}

		if (jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("flowersPerChunk") != null) {
			int flowersPerChunk = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject()
					.get("flowersPerChunk").getAsInt();
			if (flowersPerChunk > 0) {
				if (!biome.defaultFeatures.contains("DefaultFlowers"))
					biome.defaultFeatures.add("DefaultFlowers");
			}
		}

		if (jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("reedsPerChunk") != null) {
			int reedsPerChunk = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject()
					.get("reedsPerChunk").getAsInt();
			if (reedsPerChunk > 0) {
				if (!biome.defaultFeatures.contains("DesertFeatures"))
					biome.defaultFeatures.add("DesertFeatures");
			}
		}

		if (jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("cactiPerChunk") != null) {
			int cactiPerChunk = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject()
					.get("cactiPerChunk").getAsInt();
			if (cactiPerChunk > 0) {
				if (!biome.defaultFeatures.contains("DesertFeatures"))
					biome.defaultFeatures.add("DesertFeatures");
			}
		}

		return biome;
	}

	@Override public int getVersionConvertingTo() {
		return 42;
	}

}
