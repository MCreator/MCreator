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

package net.mcreator.util;

import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	private static final String NAME_PARTS_SPLITTER_REGEX = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|(_)|(?=\\d)";

	public static String abbreviateString(String input, int maxLength) {
		return abbreviateString(input, maxLength, true);
	}

	public static String abbreviateString(String input, int maxLength, boolean sumUp) {
		if (input.length() <= maxLength)
			return input;
		else if (sumUp)
			return input.substring(0, maxLength - 3) + "...";
		else
			return input.substring(0, maxLength);
	}

	public static String abbreviateStringInverse(String input, int maxLength) {
		if (input.length() <= maxLength)
			return input;

		return "..." + input.substring(input.length() - maxLength);
	}

	public static boolean isUppercaseLetter(char c) {
		return (c >= 'A' && c <= 'Z');
	}

	public static String uppercaseFirstLetter(String name) {
		if (name.length() <= 1)
			return name.toUpperCase(Locale.ENGLISH);
		return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
	}

	public static String camelToSnake(String original) {
		return String.join("_", original.split(NAME_PARTS_SPLITTER_REGEX)).replaceAll("(?<=\\d)_(?=\\d)", "");
	}

	public static String machineToReadableName(@NotNull String input) {
		String merged = String.join(" ", input.split(NAME_PARTS_SPLITTER_REGEX));
		return WordUtils.capitalize(org.apache.commons.lang3.StringUtils.normalizeSpace(merged));
	}

	public static List<String> splitCommaSeparatedStringListWithEscapes(String specialInfoString) {
		List<String> retval = new ArrayList<>();
		if (!specialInfoString.equals("")) {
			String[] info = specialInfoString.split("(?<!\\\\),");
			for (String infoelement : info) {
				String data = infoelement.trim().replace("\\,", ",");
				if (!data.trim().equals(""))
					retval.add(data);
			}
		}
		return retval;
	}

	public static int countRegexMatches(String where, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(where);

		int count = 0;
		while (matcher.find())
			count++;

		return count;
	}

}
