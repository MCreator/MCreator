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
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

public class ReturnBlock implements IBlockGenerator {
	private final String[] names;

	public ReturnBlock() {
		names = VariableTypeLoader.INSTANCE.getAllVariableTypes().stream().map(VariableType::getName)
				.map(s -> s = "return_" + s).toArray(String[]::new);
	}

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		String type = StringUtils.removeStart(block.getAttribute("type"), "return_");
		VariableType returnType = VariableTypeLoader.INSTANCE.fromName(type);

		if (!master.getStatementInputsMatching(si -> true).isEmpty()) {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.retval.inside_statement", type)));
			return;
		}

		Element value = XMLUtil.getFirstChildrenWithName(block, "value");
		if (master instanceof BlocklyToProcedure && value != null) {
			if (((BlocklyToProcedure) master).getReturnType() != null) {
				if (((BlocklyToProcedure) master).getReturnType() != returnType) {
					master.getCompileNotes().add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.retval.one_retval_block")));
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
			master.getCompileNotes().add(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
					L10N.t("blockly.warnings.retval.empty")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return names;
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}
}
