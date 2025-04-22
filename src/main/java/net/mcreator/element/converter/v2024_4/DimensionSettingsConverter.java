/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2024_4;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Dimension;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.workspace.Workspace;

public class DimensionSettingsConverter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		JsonObject definition = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject();
		Dimension dimension = (Dimension) input;

		// Update mob settings
		dimension.piglinSafe = !dimension.imitateOverworldBehaviour;
		dimension.hasRaids = dimension.imitateOverworldBehaviour;
		if ("Nether like gen".equals(dimension.worldGenType)) {
			dimension.minMonsterSpawnLightLimit = 11;
			dimension.maxMonsterSpawnLightLimit = 11;
			dimension.monsterSpawnBlockLightLimit = 15;
		} else {
			dimension.minMonsterSpawnLightLimit = 0;
			dimension.maxMonsterSpawnLightLimit = 7;
			dimension.monsterSpawnBlockLightLimit = 0;
		}

		// Update dimension effect settings (data packs and Java mods are handled separately)
		if (workspace.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
				== GeneratorFlavor.BaseLanguage.JAVA) {
			dimension.useCustomEffects = true;
			dimension.hasClouds = dimension.imitateOverworldBehaviour;
			dimension.cloudHeight = 192;
			dimension.skyType = dimension.imitateOverworldBehaviour ? "NORMAL" : "NONE";
			dimension.sunHeightAffectsFog = dimension.airColor == null && dimension.imitateOverworldBehaviour;
		} else {
			dimension.defaultEffects = dimension.hasFog ? "the_nether" : "overworld";
		}

		// Update ambient light and bed works settings (we fetch the old values from the element definition)
		dimension.bedWorks = definition.get("sleepResult").getAsString().equals("ALLOW");
		dimension.ambientLight = definition.get("isDark").getAsBoolean() ? 0 : 0.5;

		// Update worldgen settings
		if ("Normal world gen".equals(dimension.worldGenType)) {
			dimension.seaLevel = 63;
			dimension.generateOreVeins = true;
			dimension.generateAquifers = true;
			dimension.horizontalNoiseSize = 1;
			dimension.verticalNoiseSize = 2;
		} else if ("Nether like gen".equals(dimension.worldGenType)) {
			dimension.generateOreVeins = false;
			dimension.generateAquifers = false;
			dimension.seaLevel = 32;
			dimension.horizontalNoiseSize = 1;
			dimension.verticalNoiseSize = 2;
		} else {
			dimension.generateOreVeins = false;
			dimension.generateAquifers = false;
			dimension.seaLevel = 0;
			dimension.horizontalNoiseSize = 2;
			dimension.verticalNoiseSize = 1;

		}

		return dimension;
	}

	@Override public int getVersionConvertingTo() {
		return 73;
	}
}
