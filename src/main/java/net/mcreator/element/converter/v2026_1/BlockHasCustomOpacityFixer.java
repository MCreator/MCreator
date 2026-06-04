/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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
import net.mcreator.element.types.Block;
import net.mcreator.workspace.Workspace;

public class BlockHasCustomOpacityFixer implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput)
			throws Exception {
		Block block = (Block) input;
		String blockBase = block.blockBase;

		// No block base
		if (blockBase == null || blockBase.isEmpty()) {
			// Full solid cube with no transparency -> Block is considered "Solid render"
			if (!block.hasTransparency && !block.isNotColidable && block.isFullCube()) {
				block.hasCustomOpacity = (block.lightOpacity != 15); // In this case, light opacity is 15
			}
			// If block is not solid render, game then checks for skylight propagation (no fluid, bounding box isn't full cube)
			// If block propagates skylight, opacity is 0, otherwise 1
			else if (!block.isWaterloggable) {
				if (block.isFullCube()) { // Bounding box is a full cube -> No skylight propagation
					block.hasCustomOpacity = (block.lightOpacity != 1); // In this case, light opacity is 1
				} else {
					block.hasCustomOpacity = (block.lightOpacity != 0); // In this case, light opacity is 0
				}
			}
		}
		// Block bases
		else {
			if ("Leaves".equals(blockBase)) { // Leaves have an opacity of 1 by default
				block.hasCustomOpacity = (block.lightOpacity != 1);
			} else {
				/* End rods, flower pots, doors and trapdoors by default have an opacity of 0
				 * For other block bases, forcing light opacity of 0 is often an oversight and not intended:
				 * - Waterloggable bases were fully transparent even when waterlogged
				 * - Double slabs were fully transparent
				 * Additionally, we didn't override any method if light opacity was 15
				 */
				block.hasCustomOpacity = (block.lightOpacity != 0 && block.lightOpacity != 15);
			}
		}

		return block;
	}

	@Override public int getVersionConvertingTo() {
		return 84;
	}
}
