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
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import java.util.List;

public class NumberFromTextBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		List<Element> elements = XMLUtil.getDirectChildren(block);
		Element num = null;
		for (Element element : elements) {
			if (element.getNodeName().equals("value") && element.getAttribute("name").equals("NUMTEXT"))
				num = element;
		}
		if (num != null) {
			master.append(
					"new Object() {int convert(String s) { try { return Integer.parseInt(s.trim()); } catch (Exception e) { }return 0; }}.convert(");
			master.processOutputBlock(num);
			master.append(")");
		} else {
			master.addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR, "Text to number converter block is empty"));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "math_from_text" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}

}
