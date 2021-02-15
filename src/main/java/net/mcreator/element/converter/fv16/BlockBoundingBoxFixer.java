/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.element.converter.fv16;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.IBoundingBox;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Block;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockBoundingBoxFixer implements IConverter {
	private static final Logger LOG = LogManager.getLogger(BlockBoundingBoxFixer.class);

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Block block = (Block) input;
		try {
			JsonObject blockDefinition = jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject();
			if (checkOldBoundingBox(blockDefinition)) {
				IBoundingBox.BoxEntry newBB = new IBoundingBox.BoxEntry();
				newBB.mx = blockDefinition.get("mx").getAsDouble() * 16;
				newBB.my = blockDefinition.get("my").getAsDouble() * 16;
				newBB.mz = blockDefinition.get("mz").getAsDouble() * 16;
				newBB.Mx = blockDefinition.get("Mx").getAsDouble() * 16;
				newBB.My = blockDefinition.get("My").getAsDouble() * 16;
				newBB.Mz = blockDefinition.get("Mz").getAsDouble() * 16;
				block.boundingBoxes.add(newBB);
			}
		} catch (Exception e) {
			LOG.warn("Could not update bounding box of: " + block.getModElement().getName());
		}
		return block;
	}

	@Override public int getVersionConvertingTo() {
		return 16;
	}

	private boolean checkOldBoundingBox(JsonObject blockDef) {
		return blockDef.get("mx") != null && blockDef.get("my") != null && blockDef.get("mz") != null
				&& blockDef.get("Mx") != null && blockDef.get("My") != null && blockDef.get("Mz") != null;
	}

}
