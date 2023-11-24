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
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.Structure;
import net.mcreator.workspace.Workspace;

import java.util.ArrayList;

public class StructureIgnoredBlocksConverter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Structure structure = (Structure) input;
		structure.ignoredBlocks = new ArrayList<>();

		JsonObject definition = jsonElementInput.getAsJsonObject().getAsJsonObject("definition");
		String ignoreBlocks = definition.has("ignoreBlocks") ?
				definition.get("ignoreBlocks").getAsString() :
				"STRUCTURE_BLOCK";
		switch (ignoreBlocks) {
		case "STRUCTURE_BLOCK" -> structure.ignoredBlocks.add(new MItemBlock(workspace, "Blocks.STRUCTURE_BLOCK"));
		case "AIR_AND_STRUCTURE_BLOCK" -> {
			structure.ignoredBlocks.add(new MItemBlock(workspace, "Blocks.AIR"));
			structure.ignoredBlocks.add(new MItemBlock(workspace, "Blocks.STRUCTURE_BLOCK"));
		}
		case "AIR" -> structure.ignoredBlocks.add(new MItemBlock(workspace, "Blocks.AIR"));
		}

		return structure;
	}

	@Override public int getVersionConvertingTo() {
		return 56;
	}
}
