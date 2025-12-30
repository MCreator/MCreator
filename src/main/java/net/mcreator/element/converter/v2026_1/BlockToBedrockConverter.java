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
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Block;
import net.mcreator.element.types.bedrock.BEBlock;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

public class BlockToBedrockConverter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput)
			throws Exception {
		Block block = (Block) input;

		if (workspace.getGenerator().getGeneratorConfiguration().getGeneratorFlavor() == GeneratorFlavor.ADDON) {
			BEBlock beblock = new BEBlock(new ModElement(workspace, block.getModElement().getName(), ModElementType.BEBLOCK));
			beblock.name = block.name;
			beblock.texture = block.texture;
			beblock.textureBack = block.textureBack;
			beblock.textureFront = block.textureFront;
			beblock.textureLeft = block.textureLeft;
			beblock.textureRight = block.textureRight;
			beblock.textureTop = block.textureTop;
			beblock.hardness = block.hardness;
			beblock.resistance = block.resistance;
			beblock.customDrop = block.customDrop;
			beblock.dropAmount = block.dropAmount;
			if (beblock.dropAmount > 64)
				beblock.dropAmount = 64; // Current Bedrock limit
			beblock.colorOnMap = block.colorOnMap;
			beblock.soundOnStep = block.soundOnStep;
			beblock.flammability = block.flammability;
			beblock.flammableDestroyChance = block.fireSpreadSpeed;
			beblock.friction = block.slipperiness;
			if (beblock.friction > 0.9)
					beblock.friction = 0.9; // Current Bedrock limit
			beblock.lightEmission = block.luminance;
			beblock.generateFeature = block.generateFeature;
			beblock.frequencyPerChunks = block.frequencyPerChunks;
			beblock.oreCount = block.frequencyOnChunk;
			beblock.minGenerateHeight = block.minGenerateHeight;
			beblock.maxGenerateHeight = block.maxGenerateHeight;
			beblock.blocksToReplace  = block.blocksToReplace;

			return beblock;
		}

		return block;
	}

	@Override public int getVersionConvertingTo() {
		return 82;
	}

}
