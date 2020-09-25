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
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import java.util.List;

public class IfBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		List<Element> elements = XMLUtil.getDirectChildren(block);
		boolean hasMainIf = false;
		for (Element element : elements) {
			if (element.getNodeName().equals("statement")) {
				String ifname = element.getAttribute("name");
				if (ifname.startsWith("DO")) {
					int ifindex = Integer.parseInt(ifname.replace("DO", ""));
					if (ifindex == 0) { // first if statement
						master.append("if (");
						hasMainIf = true;
					} else {
						master.append("else if (");
					}
					// find the corresponding condition for this statement
					Element condition = null;
					List<Element> conditions = XMLUtil.getChildrenWithName(block, "value");
					for (Element cond_candidate : conditions) {
						if (cond_candidate.getAttribute("name").equals("IF" + ifindex)) {
							condition = cond_candidate;
						}
					}
					if (condition != null) {
						master.processOutputBlock(condition);
					} else {
						master.append("true");
						master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
								"Found if block without condition. Condition will always be true."));
					}
					master.append(") ");
				} else if (ifname.equals("ELSE")) {
					master.append("else");
				}
				master.append("{");
				List<Element> base_blocks = BlocklyBlockUtil.getBlockProcedureStartingWithBlock(element);
				master.processBlockProcedure(base_blocks);
				master.append("}");
			}
		}

		if (!hasMainIf) {
			master.addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR, "Found if block without main body!"));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "controls_if" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}
}
