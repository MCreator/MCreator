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
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.elements.VariableElementType;
import net.mcreator.workspace.elements.VariableElementTypeLoader;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.*;

public class ReturnBlock implements IBlockGenerator {
	private final String[] names;

	public ReturnBlock() {
		Set<String> temp = new HashSet<>();
		for (VariableElementType var : VariableElementTypeLoader.getVariables()) {
			temp.add("return_" + var.getName());
		}
		names = temp.toArray(new String[0]);
	}

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		String type = StringUtils.removeStart(block.getAttribute("type"), "return_");
		VariableElementType returnType = VariableElementTypeLoader.getVariableFromType(type);

		Element value = XMLUtil.getFirstChildrenWithName(block, "value");
		if (master instanceof BlocklyToProcedure && value != null) {
			if (((BlocklyToProcedure) master).getReturnType() != null) {
				if (((BlocklyToProcedure) master).getReturnType() != returnType) {
					master.getCompileNotes().add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							"Only one return type can be used in a single procedure."));
				}
			} else {
				((BlocklyToProcedure) master).setReturnType(returnType);
			}

			String valuecode = BlocklyToCode.directProcessOutputBlock(master, value);

			if (master.getTemplateGenerator() != null) {
				Map<String, Object> dataModel = new HashMap<>();
				dataModel.put("type", type);
				dataModel.put("value", valuecode);
				String code = master.getTemplateGenerator().generateFromTemplate("_return.java.ftl", dataModel);
				master.append(code);
			}
		} else {
			master.getCompileNotes()
					.add(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING, "Skipped empty return block."));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return names;
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}
}
