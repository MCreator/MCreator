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
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

public class CustomDependencyBlock implements IBlockGenerator {
	private final String[] names;

	public CustomDependencyBlock() {
		names = VariableTypeLoader.INSTANCE.getAllVariableTypes().stream().map(VariableType::getName)
				.map(s -> "custom_dependency_" + s).toArray(String[]::new);
	}

	@Override public void generateBlock(BlocklyToCode master, Element block) {
		Element element = XMLUtil.getFirstChildrenWithName(block, "field");
		if (element != null && element.getTextContent() != null && !element.getTextContent().equals("")) {
			String depname = element.getTextContent();

			String deptype = StringUtils.removeStart(block.getAttribute("type"), "custom_dependency_");
			master.addDependency(new Dependency(depname, deptype));

			if (deptype.equalsIgnoreCase("itemstack"))
				master.append("/*@ItemStack*/");
			else if (deptype.equalsIgnoreCase("blockstate"))
				master.append("/*@BlockState*/");

			master.append("(").append(element.getTextContent()).append(")");
		} else {
			master.addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR, L10N.t("blockly.errors.custom_dependency")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return names;
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}
}
