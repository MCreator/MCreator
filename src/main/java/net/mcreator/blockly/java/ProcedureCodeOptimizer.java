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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.blockly.java;

public class ProcedureCodeOptimizer {
	/**
	 * This method attempts to remove the parentheses surrounding the given code, if they are paired.
	 * @param code The code to optimize
	 * @return If possible, the code without surrounding parentheses
	 */
	public static String removeParentheses(String code) {
		if (code.startsWith("(") && code.endsWith(")")) {
			boolean isInString = false;
			boolean isInComment = false;
			int parentheses = 1;
			for (int i = 1; i < code.length() - 1; i++) {
				if (code.charAt(i) == '"' && code.charAt(i-1) != '\\' && !isInComment)
					isInString = !isInString;
				else if (code.charAt(i) == '*' && code.charAt(i-1) == '/' && !isInString)
					isInComment = true;
				else if (code.charAt(i) == '/' && code.charAt(i-1) == '*' && !isInString)
					isInComment = false;
				if (!isInString && !isInComment) { // Ignore parentheses in strings and comments
					if (code.charAt(i) == '(')
						parentheses++;
					else if (code.charAt(i) == ')') {
						if (--parentheses == 0) // The first and last parentheses aren't paired, we can't remove them
							return code;
					}
				}
			}
			if (--parentheses == 0) // The last character is a ")"
				return code.substring(1, code.length() - 1);
		}
		return code;
	}
}