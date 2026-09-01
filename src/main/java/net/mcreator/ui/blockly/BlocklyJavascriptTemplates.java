/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.blockly;

import net.mcreator.workspace.elements.VariableType;

public class BlocklyJavascriptTemplates {

	public static String variableListExtension(VariableType variableType) {
		// If the referenced workspace variable does not exist (anymore), keep the block shape as-is
		// so a potential entity input and blocks attached to it are preserved (e.g. when the block
		// is restored on undo after the variable it references was deleted)
		// Reason for the early return if statement in JS code
		return """
				Blockly.Extensions.register('%s_variables', function () {
					this.getInput("var").appendField(new Blockly.FieldDropdown(getVariablesOfType("%s")), 'VAR');
					this.getField('VAR').setValidator(function (variable) {
						if (variable.startsWith('global:') && !javabridge.globalVariableExists(variable))
							return;
						var isPlayerVar = javabridge.isPlayerVariable(variable);
						this.getSourceBlock().updateShape_(isPlayerVar, true);
					});
				});""".formatted(variableType.getName(), variableType.getBlocklyVariableType());
	}

}
