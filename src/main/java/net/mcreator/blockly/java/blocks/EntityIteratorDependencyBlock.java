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

package net.mcreator.blockly.java.blocks;

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.data.Dependency;
import net.mcreator.ui.init.L10N;
import org.w3c.dom.Element;

public class EntityIteratorDependencyBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) {
		if (master.checkIfDepProviderInputsProvide(new Dependency("entityiterator", "entity"))) {
			master.append("entityiterator");
			master.addDependency(new Dependency("entityiterator", "entity"));
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.entity_iterator_outside_foreach")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "entity_iterator" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}

}
