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

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class LoopBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		Element value = XMLUtil.getFirstChildrenWithName(block, "value");
		Element statement = XMLUtil.getFirstChildrenWithName(block, "statement");

		if (value != null && statement != null) {
			int index = getNestingLevel(block);

			master.append("for (int _i").append(index).append(" = 0; _i").append(index).append("<");
			master.processOutputBlockToInt(value);
			master.append("; _i").append(index).append("++) {");
			master.processBlockProcedure(BlocklyBlockUtil.getBlockProcedureStartingWithBlock(statement));
			master.append("}");
		} else {
			master.addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING, L10N.t("blockly.warnings.empty_loop")));
		}
	}

	private static int getNestingLevel(Element block) {
		int level = 1;
		Node node = block;
		while (node.getParentNode() != null) {
			Node parent = node.getParentNode();
			if ("statement".equals(node.getNodeName()) && parent instanceof Element parentElement
					&& "controls_repeat_ext".equals(parentElement.getAttribute("type")))
				level++;
			node = parent;
		}
		return level;
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "controls_repeat_ext" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}
}
