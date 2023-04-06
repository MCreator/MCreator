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

package net.mcreator.element.converter.v2023_2;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.Block;
import net.mcreator.workspace.Workspace;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BlockOreReplacementBlocksFixer implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Block block = (Block) input;

		if (block.blocksToReplace != null) {
			Set<MItemBlock> noDuplicatesList = new LinkedHashSet<>(block.blocksToReplace);

			MItemBlock STONE = new MItemBlock(workspace, "Blocks.STONE");

			if (noDuplicatesList.contains(STONE)) {
				noDuplicatesList.remove(STONE);
				noDuplicatesList.addAll(List.of(
						//@formatter:off
						new MItemBlock(workspace, "Blocks.STONE#0"),
						new MItemBlock(workspace, "Blocks.STONE#1"),
						new MItemBlock(workspace, "Blocks.STONE#3"),
						new MItemBlock(workspace, "Blocks.STONE#5")
						//@formatter:on
				));
			}

			block.blocksToReplace = new ArrayList<>(noDuplicatesList);
		}

		return block;
	}

	@Override public int getVersionConvertingTo() {
		return 40;
	}

}
