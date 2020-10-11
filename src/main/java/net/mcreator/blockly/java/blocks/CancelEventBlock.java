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
import net.mcreator.blockly.java.BlocklyToProcedure;
import net.mcreator.generator.template.TemplateGeneratorException;
import org.w3c.dom.Element;

import java.util.HashMap;

public class CancelEventBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		if (master instanceof BlocklyToProcedure) {
			if (((BlocklyToProcedure) master).getExternalTrigger() != null) {
				if (master.getTemplateGenerator() != null) {
					master.append(master.getTemplateGenerator()
							.generateFromTemplate("_cancel_event.java.ftl", new HashMap<>()));
				}
			} else {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						"This procedure does not use global trigger so the cancel event block can not cancel any trigger"));
			}
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					"Cancel event procedure block is not supported in this editor!"));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "cancel_event" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}
}
