/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.java;

/**
 * A lightweight scanner for Java-like source code that classifies each character as either actual code
 * or part of a string literal, text block, character literal, or comment. Unlike a full parser, it is
 * tolerant to incomplete or invalid code: unterminated literals and comments simply extend to the end
 * of the input. This makes it suitable for processing code fragments and code being edited.
 */
public final class JavaCodeScanner {

	/**
	 * Classification of a scanned character. Region delimiters (quotes, comment markers) belong
	 * to the region they delimit, not to {@link #CODE}.
	 */
	public enum Region {
		CODE, STRING, CHAR_LITERAL, COMMENT
	}

	@FunctionalInterface public interface Visitor {

		/**
		 * Called for every character of the scanned code, in order.
		 *
		 * @param index  The index of the character in the scanned code
		 * @param c      The character at the given index
		 * @param region The region the character belongs to
		 * @return true to continue scanning, false to abort the scan
		 */
		boolean visit(int index, char c, Region region);

	}

	/**
	 * Scans the given code and reports every character along with its {@link Region} to the visitor.
	 * Text blocks ({@code """ ... """}) are reported as {@link Region#STRING}. The line terminator
	 * ending a line comment is not part of the comment and is reported as {@link Region#CODE}.
	 *
	 * @param code    The code to scan
	 * @param visitor The visitor to report each character to
	 * @return true if the entire code was scanned, false if the visitor aborted the scan
	 */
	public static boolean scan(String code, Visitor visitor) {
		int length = code.length();
		int i = 0;
		while (i < length) {
			char c = code.charAt(i);
			if (c == '/' && i + 1 < length && code.charAt(i + 1) == '/') {
				int end = i;
				while (end < length && code.charAt(end) != '\n' && code.charAt(end) != '\r')
					end++;
				if (!visitRange(code, i, end, Region.COMMENT, visitor))
					return false;
				i = end;
			} else if (c == '/' && i + 1 < length && code.charAt(i + 1) == '*') {
				int close = code.indexOf("*/", i + 2);
				int end = close == -1 ? length : close + 2;
				if (!visitRange(code, i, end, Region.COMMENT, visitor))
					return false;
				i = end;
			} else if (code.startsWith("\"\"\"", i)) {
				int end = i + 3;
				while (end < length) {
					if (code.charAt(end) == '\\') {
						end += 2; // skip the escaped character
					} else if (code.startsWith("\"\"\"", end)) {
						end += 3;
						break;
					} else {
						end++;
					}
				}
				end = Math.min(end, length);
				if (!visitRange(code, i, end, Region.STRING, visitor))
					return false;
				i = end;
			} else if (c == '"' || c == '\'') {
				int end = i + 1;
				while (end < length) {
					if (code.charAt(end) == '\\') {
						end += 2; // skip the escaped character
					} else if (code.charAt(end) == c) {
						end++;
						break;
					} else {
						end++;
					}
				}
				end = Math.min(end, length);
				if (!visitRange(code, i, end, c == '"' ? Region.STRING : Region.CHAR_LITERAL, visitor))
					return false;
				i = end;
			} else {
				if (!visitor.visit(i, c, Region.CODE))
					return false;
				i++;
			}
		}
		return true;
	}

	private static boolean visitRange(String code, int from, int to, Region region, Visitor visitor) {
		for (int i = from; i < to; i++) {
			if (!visitor.visit(i, code.charAt(i), region))
				return false;
		}
		return true;
	}

	/**
	 * Returns a copy of the given code in which every character of string literals, text blocks,
	 * character literals, and comments (including their delimiters) is replaced with a space.
	 * The returned string has the same length as the input, so every index maps 1:1 to the
	 * original code, which allows performing index-based operations on the original code based
	 * on findings in the masked copy.
	 *
	 * @param code The code to mask
	 * @return The code of the same length with strings, char literals, and comments masked out
	 */
	public static String maskStringsAndComments(String code) {
		StringBuilder masked = new StringBuilder(code.length());
		scan(code, (_, c, region) -> {
			masked.append(region == Region.CODE ? c : ' ');
			return true;
		});
		return masked.toString();
	}

}
