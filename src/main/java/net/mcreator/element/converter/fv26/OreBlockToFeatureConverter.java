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

package net.mcreator.element.converter.fv26;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.Block;
import net.mcreator.element.types.Feature;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OreBlockToFeatureConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(OreBlockToFeatureConverter.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Block block = (Block) input;

		if (block.spawnWorldTypes != null && block.spawnWorldTypes.size() > 0) {
			Feature feature = new Feature(
					new ModElement(workspace, input.getModElement().getName() + "Feature", ModElementType.FEATURE));

			feature.generationType = "Ore";
			feature.spawnWorldTypes = block.spawnWorldTypes;
			feature.restrictionBiomes = block.restrictionBiomes;
			feature.blocksToReplace = block.blocksToReplace;
			feature.generateCondition = block.generateCondition;
			feature.blockToGenerate = new MItemBlock(workspace, "CUSTOM:" + input.getModElement().getName());
			// To do when #2470 is merged feature.generationShape = block.generationShape
			feature.frequencyPerChunks = block.frequencyPerChunks;
			feature.frequencyOnChunk = block.frequencyOnChunk;
			feature.minGenerateHeight = block.minGenerateHeight;
			feature.maxGenerateHeight = block.maxGenerateHeight;

			LOG.debug("Converted " + input.getModElement().getName() + " generation parameters to a new Feature mod element...");

			feature.getModElement().setParentFolder(FolderElement.dummyFromPath(input.getModElement().getFolderPath()));
			workspace.getModElementManager().storeModElementPicture(feature);
			workspace.addModElement(feature.getModElement());
			workspace.getGenerator().generateElement(feature);
			workspace.getModElementManager().storeModElement(feature);
		}

		return block;
	}

	@Override public int getVersionConvertingTo() {
		return 26;
	}
}
