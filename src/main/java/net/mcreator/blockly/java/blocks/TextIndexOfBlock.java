/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import java.util.List;

public class TextIndexOfBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		List<Element> elements = XMLUtil.getDirectChildren(block);
		Element check = null, text = null, from = null;
		for (Element element : elements)
			if (element.getNodeName().equals("value"))
				if (element.getAttribute("name").equals("check"))
					check = element;
				else if (element.getAttribute("name").equals("from"))
					from = element;
				else if (element.getAttribute("name").equals("text"))
					text = element;

		if (text != null && check != null && from != null) {
			master.append("(");
			master.processOutputBlock(text);
			master.append(".indexOf(");
			master.processOutputBlockWithoutParentheses(check);
			master.append(",");
			master.processOutputBlockWithoutParentheses(from);
			master.append("))");
		} else {
			master.addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR, L10N.t("blockly.errors.empty_replace")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "text_index_of" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}
}
