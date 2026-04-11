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
import net.mcreator.blockly.java.ProcedureCodeOptimizer;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

public class PrintTextBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		org.w3c.dom.Element element = XMLUtil.getFirstChildrenWithName(block, "value");
		if (element != null) {
			String elementcode = BlocklyToCode.directProcessOutputBlock(master, element);
			if (master.getTemplateGenerator() != null) {
				if (master.getTemplateGenerator().hasTemplate("_print.java.ftl")) {
					Map<String, Object> dataModel = new HashMap<>();
					dataModel.put("value", elementcode);
					master.append(master.getTemplateGenerator().generateFromTemplate("_print.java.ftl", dataModel));
				} else {
					master.append("System.out.println(");
					master.append(ProcedureCodeOptimizer.removeParentheses(elementcode));
					master.append(");");
				}
			}
		} else {
			master.getCompileNotes().add(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
					L10N.t("blockly.warnings.empty_print_block")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "text_print" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}
}
