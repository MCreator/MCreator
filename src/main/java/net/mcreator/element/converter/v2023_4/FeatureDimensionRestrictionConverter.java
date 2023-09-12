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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.types.Dimension;
import net.mcreator.element.types.Feature;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FeatureDimensionRestrictionConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger("FeatureDimensionRestrictionConverter");

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		try {
			Feature feature = (Feature) input;
			if (jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("restrictionDimensions") != null) {
				JsonArray restrictionDimensions = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject()
						.get("restrictionDimensions").getAsJsonArray();

				List<BiomeEntry> restrictionBiomes = new ArrayList<>();

				// We are only able to directly convert this if there is only one dimension restriction
				// otherwise we lift restriction, so it generates in all dimensions
				if (restrictionDimensions.size() == 1) {
					String restrictionDimension = restrictionDimensions.get(0).getAsString();
					if (restrictionDimension.equals("Surface")) {
						restrictionBiomes.add(new BiomeEntry(workspace, "#is_overworld"));
					} else if (restrictionDimension.equals("Nether")) {
						restrictionBiomes.add(new BiomeEntry(workspace, "#is_nether"));
					} else if (restrictionDimension.equals("End")) {
						restrictionBiomes.add(new BiomeEntry(workspace, "#is_end"));
					} else if (restrictionDimension.startsWith("CUSTOM:")) {
						ModElement modElement = workspace.getModElementByName(
								restrictionDimension.replaceFirst("CUSTOM:", ""));
						GeneratableElement generatableElement = modElement.getGeneratableElement();
						if (generatableElement instanceof Dimension dimension) {
							restrictionBiomes.addAll(dimension.biomesInDimension);
						}
					}
				}

				// we only define our restriction if there are not already biome restrictions in place
				if (feature.restrictionBiomes.isEmpty())
					feature.restrictionBiomes = restrictionBiomes;
			} else {
				throw new NullPointerException("restrictionDimensions not defined");
			}
		} catch (Exception e) {
			LOG.warn("Failed to convert dimension restriction to biome list", e);
		}

		return input;
	}

	@Override public int getVersionConvertingTo() {
		return 49;
	}

}
