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
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlocklyVariables {

	private final BlocklyToJava generator;

	BlocklyVariables(BlocklyToJava generator) {
		this.generator = generator;
	}

	List<VariableElement> processLocalVariables(Element variables_block) {
		List<VariableElement> varlist = new ArrayList<>();

		if (variables_block != null) {
			List<Element> variables = XMLUtil.getChildrenWithName(variables_block, "variable");
			for (Element variable : variables) {
				String type = variable.getAttribute("type");
				String name = variable.getAttribute("id");
				VariableType variableType = VariableTypeLoader.INSTANCE.fromName(type);
				if (variableType != null && variableType.getBlocklyVariableType() != null && name != null) {
					VariableElement element = new VariableElement();
					element.setName(name);
					element.setType(variableType);
					element.setScope(VariableType.Scope.LOCAL);
					varlist.add(element); // add variable to the array of variables
				} else {
					generator.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
							L10N.t("blockly.warnings.skip_unknown_var_type")));
				}
			}
		}

		return varlist;
	}

	public static boolean isPlayerVariableForWorkspace(Workspace workspace, String field) {
		if (field == null)
			return false;
		String[] name = field.split(":");
		if (name.length == 2 && name[0].equals("global")) {
			VariableType.Scope scope = workspace.getVariableElementByName(name[1]).getScope();
			return scope == VariableType.Scope.PLAYER_LIFETIME || scope == VariableType.Scope.PLAYER_PERSISTENT;
		}
		return false;
	}

	public static Set<VariableElement> tryToExtractVariables(String xml) {
		Set<VariableElement> retval = new HashSet<>();
		for (VariableType elementType : VariableTypeLoader.INSTANCE.getAllVariableTypes()) {
			Matcher m = Pattern.compile("<block type=\"(?:variables_set_" + elementType.getName() + "|variables_get_"
							+ elementType.getName() + ")\">(?:<mutation.*?\"/>)?<field name=\"VAR\">local:(.*?)</field>")
					.matcher(xml);

			try {
				while (m.find()) {
					VariableElement element = new VariableElement();
					element.setName(m.group(1));
					element.setType(elementType);
					retval.add(element);
				}
			} catch (Exception ignored) {
			}
		}
		return retval;
	}

}
