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

import org.apache.commons.lang3.StringUtils;

public class ProcedureCodeOptimizer {
	enum ParseState {
		INSIDE_INLINE_COMMENT, INSIDE_COMMENT_BLOCK, INSIDE_STRING, INSIDE_STRING_ESCAPE_SEQUENCE, OUTSIDE
	}

	/**
	 * This method attempts to remove the parentheses surrounding the given code, if they are paired.
	 * Eventual marker comments at the beginning of the input are ignored.
	 *
	 * @param code The code to optimize
	 * @return If possible, the code without surrounding parentheses
	 */
	public static String removeParentheses(String code) {
		return removeParentheses(code, null);
	}

	/**
	 * This method attempts to remove the parentheses surrounding the given code, if they are paired.
	 * The optimization will fail if any of the blacklisted characters appears at the top nesting level.
	 * Eventual marker comments at the beginning of the input are ignored.
	 *
	 * @param code      The code to optimize
	 * @param blacklist The characters that can't be contained at the top nesting level
	 * @return If possible, the code without surrounding parentheses
	 */
	public static String removeParentheses(String code, String blacklist) {
		String toClean = code.strip();
		String prefix = "";
		if (toClean.startsWith("/*@BlockState*/")) {
			prefix = "/*@BlockState*/";
			toClean = toClean.substring(15);
		} else if (code.startsWith("/*@ItemStack*/")) {
			prefix = "/*@ItemStack*/";
			toClean = toClean.substring(14);
		} else if (code.startsWith("/*@int*/")) {
			prefix = "/*@int*/";
			toClean = toClean.substring(8);
		} else if (code.startsWith("/*@float*/")) {
			prefix = "/*@float*/";
			toClean = toClean.substring(10);
		}
		return canRemoveParentheses(toClean, blacklist) ? prefix + toClean.substring(1, toClean.length() - 1) : code;
	}

	/**
	 * This method checks if the given code has surrounding parentheses that can be removed (starts and ends with
	 * parentheses, they are paired, and there's no blacklisted character at the top nesting level)
	 *
	 * @param toCheck   The code to perform the check on
	 * @param blacklist The characters that can't be contained at the top nesting level
	 * @return true if the parentheses can be removed
	 */
	private static boolean canRemoveParentheses(String toCheck, String blacklist) {
		if (toCheck.startsWith("(") && toCheck.endsWith(")")) {
			var state = ParseState.OUTSIDE;
			int parentheses = 1;
			char prevChar = '(';
			int backSlashCounter = 0;
			var topLevelChars = new StringBuilder();
			for (int i = 1; i < toCheck.length() - 1; i++) {
				char c = toCheck.charAt(i);
				switch (state) {
				case OUTSIDE:
					if (c == '/' && prevChar == '/') {
						state = ParseState.INSIDE_INLINE_COMMENT;
						if (blacklist != null && parentheses == 1)
							topLevelChars.deleteCharAt(
									topLevelChars.length() - 1); // The previous character was part of the comment
					} else if (c == '*' && prevChar == '/') {
						state = ParseState.INSIDE_COMMENT_BLOCK;
						if (blacklist != null && parentheses == 1)
							topLevelChars.deleteCharAt(topLevelChars.length() - 1);
					} else if (c == '"')
						state = ParseState.INSIDE_STRING;
					else if (c == '(')
						parentheses++;
					else if (c == ')'
							&& --parentheses == 0) // The first ( isn't paired with the last ), we can't remove them
						return false;
					else if (blacklist != null && parentheses == 1) {
						topLevelChars.append(c);
					}
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
			return StringUtils.containsNone(topLevelChars, blacklist);
		}
		return false;
	}

	/**
	 * This method performs parentheses optimization and adds an (int) cast to the given code if needed.
	 *
	 * @param code The code representing the number to cast
	 * @return The code without parentheses, if it's already an int, or with a cast to (int) behind otherwise
	 */
	@SuppressWarnings("unused") public static String toInt(String code) {
		if (code.startsWith("/*@int*/"))
			return removeParentheses(code);
		return "(int)" + (code.contains("instanceof") ? code : removeParentheses(code, "*/%+-!=><&^|?"));
	}

	/**
	 * This method performs parentheses optimization and adds a (float) cast to the given code if needed.
	 *
	 * @param code The code representing the number to cast
	 * @return The code without parentheses, if it's already an int or float, or with a cast to (float) behind otherwise
	 */
	@SuppressWarnings("unused") public static String toFloat(String code) {
		if (code.startsWith("/*@int*/") || code.startsWith("/*@float*/"))
			return removeParentheses(code);
		return "(float)" + (code.contains("instanceof") ? code : removeParentheses(code, "*/%+-!=><&^|?"));
	}

	/**
	 * This method removes blockstate/itemstack/int markers from the given code
	 *
	 * @param code The code to optimize
	 * @return The code without blockstate/itemstack markers
	 */
	public static String removeMarkers(String code) {
		return code.replaceAll("(/\\*@BlockState\\*/|/\\*@ItemStack\\*/|/\\*@int\\*/|/\\*@float\\*/)", "");
	}
}