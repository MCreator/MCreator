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

package net.mcreator.element.converter.v2025_2;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Block;
import net.mcreator.workspace.Workspace;

public class BlockLegacyMaterialRemover implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput)
			throws Exception {
		Block block = (Block) input;
		String material = jsonElementInput.getAsJsonObject().getAsJsonObject("definition")
				.getAsJsonObject("material").get("value").getAsString();

		// Handle ignited by lava and note block instrument fields
		switch (material) {
			case "CARPET", "TALL_PLANTS", "BAMBOO_SAPLING", "BAMBOO", "CLOTH", "TNT", "LEAVES" ->
					block.ignitedByLava = true;
			case "OCEAN_PLANT", "ROCK" -> block.noteBlockInstrument = "basedrum";
			case "SAND" -> block.noteBlockInstrument = "snare";
			case "WOOD" -> {
				block.noteBlockInstrument = "bass";
				block.ignitedByLava = true;
			}
			case "GLASS" -> block.noteBlockInstrument = "hat";
		}

		// Handle block set type for specific block bases
		String blockBase = block.blockBase;
		if (blockBase != null) {
			switch (blockBase) {
				case "PressurePlate" -> block.blockSetType = material.equals("WOOD") ? "OAK" : "IRON";
				case "Button", "Fence" -> block.blockSetType = material.equals("WOOD") ? "OAK" : "STONE";
				case "Door", "TrapDoor" -> block.blockSetType = switch(material) {
						case "WOOD" -> "OAK";
						case "IRON" -> "IRON";
						default -> "STONE";
					};
			}
		}

		return block;
	}

	@Override public int getVersionConvertingTo() {
		return 78;
	}
}
