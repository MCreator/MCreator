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

import net.mcreator.java.JavaCodeScanner;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class ProcedureCodeOptimizer {

	private static final String[] MARKERS = { "/*@BlockState*/", "/*@ItemStack*/", "/*@int*/", "/*@float*/" };

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
	public static String removeParentheses(String code, @Nullable String blacklist) {
		String toClean = code.strip();
		String prefix = "";
		for (String marker : MARKERS) {
			if (toClean.startsWith(marker)) {
				prefix = marker;
				toClean = toClean.substring(marker.length());
				break;
			}
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
	private static boolean canRemoveParentheses(String toCheck, @Nullable String blacklist) {
		if (!toCheck.startsWith("(") || !toCheck.endsWith(")"))
			return false;

		var topLevelChars = new StringBuilder();
		int[] parentheses = { 1 };
		boolean balanced = JavaCodeScanner.scan(toCheck.substring(1, toCheck.length() - 1), (_, c, region) -> {
			if (region != JavaCodeScanner.Region.CODE)
				return true; // ignore contents of strings, char literals, and comments
			if (c == '(') {
				parentheses[0]++;
			} else if (c == ')' && --parentheses[0] == 0) {
				return false; // The first "(" isn't paired with the last ")", we can't remove them
			} else if (blacklist != null && parentheses[0] == 1) {
				topLevelChars.append(c);
			}
			return true;
		});
		return balanced && StringUtils.containsNone(topLevelChars, blacklist);
	}

	/**
	 * This method performs parentheses optimization and adds an (int) cast to the given code if needed.
	 *
	 * @param code The code representing the number to cast
	 * @return The code without parentheses, if it's already an int, or with a cast to (int) behind otherwise
	 */
	@SuppressWarnings("unused") public static String toInt(String code) {
		return toInt(code, null);
	}

	/**
	 * This method performs parentheses optimization and adds an (int) cast to the given code if needed.
	 *
	 * @param code      The code representing the number to cast
	 * @param blacklist Characters that prevent removing the parenthesis if the code is already an int
	 * @return The code without parentheses, if it's already an int, or with a cast to (int) behind otherwise
	 */
	@SuppressWarnings("unused") public static String toInt(String code, @Nullable String blacklist) {
		if (code.startsWith("/*@int*/"))
			return removeParentheses(code, blacklist);
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
	 * This method performs parentheses optimization and adds a (double) cast to the given code.
	 *
	 * @param code The code representing the number to cast
	 * @return The code without parentheses and with a cast to (double) behind
	 */
	@SuppressWarnings("unused") public static String toDouble(String code) {
		return "(double)" + (code.contains("instanceof") ? code : removeParentheses(code, "*/%+-!=><&^|?"));
	}

	/**
	 * This method removes blockstate/itemstack/int markers from the given code
	 *
	 * @param code The code to optimize
	 * @return The code without blockstate/itemstack markers
	 */
	public static String removeMarkers(String code) {
		for (String marker : MARKERS)
			code = code.replace(marker, "");
		return code;
	}
}