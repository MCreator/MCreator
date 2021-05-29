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
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.elements.VariableElementType;
import net.mcreator.workspace.elements.VariableElementTypeLoader;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.stream.Collectors;

public class CustomDependencyBlock implements IBlockGenerator {
	private final String[] names;

	public CustomDependencyBlock() {
		names = VariableElementTypeLoader.getVariables().stream().map(VariableElementType::getName)
				.collect(Collectors.toList()).stream().map(s -> s = "custom_dependency_" + s).toArray(String[]::new);
	}

	@Override public void generateBlock(BlocklyToCode master, Element block) {
		Element element = XMLUtil.getFirstChildrenWithName(block, "field");
		if (element != null && element.getTextContent() != null && !element.getTextContent().equals("")) {
			String depname = element.getTextContent();

			String deptype = StringUtils.removeStart(block.getAttribute("type"), "custom_dependency_");
			master.addDependency(new Dependency(depname, deptype));

			if (deptype.equalsIgnoreCase("itemstack"))
				master.append("/*@ItemStack*/");

			master.append("(").append(element.getTextContent()).append(")");
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					"Custom dependency block is not well defined!"));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return names;
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}
}
