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

@SuppressWarnings("unused") public class ProcedureCodeOptimizer {
	enum ParseState {
		INSIDE_INLINE_COMMENT, INSIDE_COMMENT_BLOCK, INSIDE_STRING, INSIDE_STRING_ESCAPE_SEQUENCE, OUTSIDE
	}

	/**
	 * This method attempts to remove the parentheses surrounding the given code, if they are paired.
	 * @param code The code to optimize
	 * @return If possible, the code without surrounding parentheses
	 */
	public static String removeParentheses(String code) {
		String toClean = code.strip();
		if (toClean.startsWith("(") && toClean.endsWith(")")) {
			var state = ParseState.OUTSIDE;
			int parentheses = 1;
			char prevChar = '(';
			int backSlashCounter = 0;
			for (int i = 1; i < toClean.length() - 1; i++) {
				char c = toClean.charAt(i);
				switch (state) {
				case OUTSIDE:
					if (c == '/' && prevChar == '/')
						state = ParseState.INSIDE_INLINE_COMMENT;
					else if (c == '*' && prevChar == '/')
						state = ParseState.INSIDE_COMMENT_BLOCK;
					else if (c == '"')
						state = ParseState.INSIDE_STRING;
					else if (c == '(')
						parentheses++;
					else if (c == ')' && --parentheses == 0) // The first ( isn't paired with the last ), we can't remove them
						return code;
					break;
				case INSIDE_INLINE_COMMENT:
					if (c == '\n' || c == '\r')
						state = ParseState.OUTSIDE;
					break;
				case INSIDE_STRING_ESCAPE_SEQUENCE:
					if (c == '\\') {
						backSlashCounter++;
						break;
					}
					state = ParseState.INSIDE_STRING;
				case INSIDE_STRING:
					if (c == '\\') {
						state = ParseState.INSIDE_STRING_ESCAPE_SEQUENCE;
						backSlashCounter = 0;
					} else if (c == '"' && (prevChar != '\\' || backSlashCounter % 2 != 0)) {
						state = ParseState.OUTSIDE;
					}
					break;
				case INSIDE_COMMENT_BLOCK:
					if (c == '/' && prevChar == '*')
						state = ParseState.OUTSIDE;
					break;
				}
				prevChar = c;
			}
			if (state == ParseState.OUTSIDE && --parentheses == 0) // The last character is a valid ")"
				return toClean.substring(1, toClean.length() - 1);
		}
		return code;
	}

	/**
	 * This method removes the parentheses surrounding the input code, while ignoring the blockstate/itemstack markers.<br>
	 * This should be used only in blocks that accept blockstate/itemstack inputs but don't process them,
	 * such as the print block.
	 * @param code The code to optimize
	 * @return The code without the initial comment and, if possible, without surrounding parentheses
	 */
	public static String removeParenthesesIgnoreComment(String code) {
		String prefix;
		String withoutComment;
		if (code.startsWith("/*@BlockState*/")) {
			prefix = "/*@BlockState*/";
			withoutComment = code.replaceFirst("/\\*@BlockState\\*/", "");
		}
		else if (code.startsWith("/*@ItemStack*/")) {
			prefix = "/*@ItemStack*/";
			withoutComment = code.replaceFirst("/\\*@ItemStack\\*/", "");
		} else {
			prefix = "";
			withoutComment = code;
		}
		return prefix + removeParentheses(withoutComment);
	}
}