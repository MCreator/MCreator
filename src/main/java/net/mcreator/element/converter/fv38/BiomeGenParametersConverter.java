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

package net.mcreator.element.converter.fv38;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Biome;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class BiomeGenParametersConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(BiomeGenParametersConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Biome biome = (Biome) input;

		try {
			double normalizedWeight =
					jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("biomeWeight")
							.getAsDouble() / 50;

			double temperatureBase = ((biome.temperature + 1) / 3) * 2 - 1;
			double humidityBase = (biome.rainingPossibility * 2) - 1;
			double continentalnessBase =
					(jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("baseHeight")
							.getAsDouble() + 5) / 10.0;
			double erosionBase =
					2 - jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("heightVariation")
							.getAsDouble() - 1;
			double weirdnessBase = (random(input.getModElement().getRegistryName()) * 2) - 1;

			biome.genTemperature = new Biome.ClimatePoint(Math.max(-2, temperatureBase - normalizedWeight),
					Math.min(2, temperatureBase + normalizedWeight));
			biome.genHumidity = new Biome.ClimatePoint(Math.max(-2, humidityBase - normalizedWeight),
					Math.min(2, humidityBase + normalizedWeight));
			biome.genContinentalness = new Biome.ClimatePoint(Math.max(-2, continentalnessBase - normalizedWeight),
					Math.min(2, continentalnessBase + normalizedWeight));
			biome.genErosion = new Biome.ClimatePoint(Math.max(-2, erosionBase - normalizedWeight),
					Math.min(2, erosionBase + normalizedWeight));
			biome.genWeirdness = new Biome.ClimatePoint(Math.max(-2, weirdnessBase - normalizedWeight),
					Math.min(2, weirdnessBase + normalizedWeight));
		} catch (Exception e) {
			LOG.warn("Could not update biome gen parameters of: " + biome.getModElement().getName(), e);
		}

		return biome;
	}

	private double random(String seed) {
		long hash = 0;
		for (char c : seed.toCharArray()) {
			hash = 31L * hash + c;
		}
		return new Random(hash).nextDouble();
	}

	@Override public int getVersionConvertingTo() {
		return 38;
	}

}
