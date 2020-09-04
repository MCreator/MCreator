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

package net.mcreator.java;

import net.mcreator.io.Transliteration;
import net.mcreator.util.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class JavaConventions {

	public static final Set<String> JAVA_RESERVED_WORDS = new HashSet<>(
			Arrays.asList("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
					"continue", "default", "do", "double", "else", "extends", "false", "final", "finally", "float",
					"for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
					"new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
					"super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void",
					"volatile", "while", "text"));

	public static String convertToValidClassName(String source) {
		if (source == null)
			return null;

		String transliterated = Transliteration.transliterateString(source);

		StringBuilder builder = new StringBuilder();
		char[] nameAsArray = transliterated.toCharArray();
		boolean first = true;
		for (char element : nameAsArray) {
			if (first) {
				if (isLetter(element)) {
					first = false;//we have a letter as a first character, no more first
					builder.append(element);
				}
			} else {
				if (isLetterOrDigit(element))
					builder.append(element);
			}
		}

		return StringUtils.uppercaseFirstLetter(builder.toString()); //uppercase the first character if not already
	}

	public static boolean isValidJavaIdentifier(String s) {
		if (s == null || s.length() == 0) {
			return false;
		}

		char[] c = s.toCharArray();
		if (!Character.isJavaIdentifierStart(c[0])) {
			return false;
		}

		for (int i = 1; i < c.length; i++) {
			if (!Character.isJavaIdentifierPart(c[i])) {
				return false;
			}
		}

		return true;
	}

	public static boolean containsInvalidJavaNameCharacters(String text) {
		char[] chars = text.toCharArray();
		for (char c : chars)
			if (!isJavaLetter(c))
				return true;
		return false;
	}

	public static String escapeStringForJava(String source) {
		if (source == null)
			return null;

		return StringEscapeUtils.escapeJava(source);
	}

	public static boolean isStringReservedJavaWord(String name) {
		return JAVA_RESERVED_WORDS.contains(name.toLowerCase(Locale.ENGLISH));
	}

	private static boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	public static boolean isLetterOrDigit(char c) {
		return isLetter(c) || (c >= '0' && c <= '9');
	}

	private static boolean isJavaLetter(char c) {
		return isLetterOrDigit(c) || c == '_' || c == '$';
	}

}
