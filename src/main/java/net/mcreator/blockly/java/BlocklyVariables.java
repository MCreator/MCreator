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

package net.mcreator.blockly.java;

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.elements.VariableElementType;
import net.mcreator.workspace.elements.VariableElementTypeLoader;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class BlocklyVariables {

	private final BlocklyToJava generator;

	BlocklyVariables(BlocklyToJava generator) {
		this.generator = generator;
	}

	List<String> processLocalVariables(Element variables_block) {
		List<String> varlist = new ArrayList<>();

		if (variables_block != null) {
			List<Element> variables = XMLUtil.getChildrenWithName(variables_block, "variable");
			for (Element variable : variables) {
				String type = variable.getAttribute("type");
				String name = variable.getAttribute("id");
				VariableElementType variableType = VariableElementTypeLoader.INSTANCE.getVariableTypeFromString(type);
				if (variableType != null && variableType.getBlocklyVariableType() != null && name != null) {
					generator.append(variableType.getJavaType(generator.getWorkspace())).append(" ").append(name).append(" = ")
							.append(variableType.getDefaultValue()).append(";\n");

					// add variable to the array of variables
					varlist.add(name);
				} else {
					generator.addCompileNote(
							new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING, "Skipping unknown variable type!"));
				}
			}
		}

		return varlist;
	}

}
