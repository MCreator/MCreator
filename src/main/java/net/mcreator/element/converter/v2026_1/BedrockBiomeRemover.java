/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2026_1;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.workspace.Workspace;

public class BedrockBiomeRemover implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput)
			throws Exception {
		if (workspace.getGenerator().getGeneratorConfiguration().getGeneratorFlavor() == GeneratorFlavor.ADDON) {
			// We simply remove biomes from add-on workspaces as they don't work
			// If the game adds support for biomes in Bedrock Edition back, we will
			// add biome back as a separate BEBiome mod element type
			return null;
		}

		// If not addon (Java Edition), don't do any processing here
		return input;
	}

	@Override public int getVersionConvertingTo() {
		return 82;
	}

}
