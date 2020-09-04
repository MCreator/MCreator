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

import java.io.StringWriter;
import java.util.HashMap;

public class HtmlUtils {

	private static HashMap<String, CharSequence> lookupMap = null;

	public static String unescapeHtml(final String input) {

		if (lookupMap == null) {
			lookupMap = new HashMap<>();
			for (final CharSequence[] seq : ESCAPES)
				lookupMap.put(seq[1].toString(), seq[0]);
		}

		StringWriter writer = null;
		int len = input.length();
		int i = 1;
		int st = 0;
		while (true) {

			while (i < len && input.charAt(i - 1) != '&')
				i++;
			if (i >= len)
				break;

			int j = i;
			while (j < len && j < i + MAX_ESCAPE + 1 && input.charAt(j) != ';')
				j++;
			if (j == len || j < i + MIN_ESCAPE || j == i + MAX_ESCAPE + 1) {
				i++;
				continue;
			}

			if (input.charAt(i) == '#') {

				int k = i + 1;
				int radix = 10;

				final char firstChar = input.charAt(k);
				if (firstChar == 'x' || firstChar == 'X') {
					k++;
					radix = 16;
				}

				try {
					int entityValue = Integer.parseInt(input.substring(k, j), radix);

					if (writer == null)
						writer = new StringWriter(input.length());
					writer.append(input.substring(st, i - 1));

					if (entityValue > 0xFFFF) {
						final char[] chrs = Character.toChars(entityValue);
						writer.write(chrs[0]);
						writer.write(chrs[1]);
					} else {
						writer.write(entityValue);
					}

				} catch (NumberFormatException ex) {
					i++;
					continue;
				}
			} else {

				CharSequence value = lookupMap.get(input.substring(i, j));
				if (value == null) {
					i++;
					continue;
				}

				if (writer == null)
					writer = new StringWriter(input.length());
				writer.append(input.substring(st, i - 1));

				writer.append(value);
			}

			st = j + 1;
			i = st;
		}

		if (writer != null) {
			writer.append(input.substring(st, len));
			return writer.toString();
		}
		return input;
	}

	private static final String[][] ESCAPES = { { "\"", "quot" }, { "&", "amp" }, { "<", "lt" }, { ">", "gt" },
			{ "\u00A0", "nbsp" }, { "\u00A1", "iexcl" }, { "\u00A2", "cent" }, { "\u00A3", "pound" },
			{ "\u00A4", "curren" }, { "\u00A5", "yen" }, { "\u00A6", "brvbar" }, { "\u00A7", "sect" },
			{ "\u00A8", "uml" }, { "\u00A9", "copy" }, { "\u00AA", "ordf" }, { "\u00AB", "laquo" }, { "\u00AC", "not" },
			{ "\u00AD", "shy" }, { "\u00AE", "reg" }, { "\u00AF", "macr" }, { "\u00B0", "deg" }, { "\u00B1", "plusmn" },
			{ "\u00B2", "sup2" }, { "\u00B3", "sup3" }, { "\u00B4", "acute" }, { "\u00B5", "micro" },
			{ "\u00B6", "para" }, { "\u00B7", "middot" }, { "\u00B8", "cedil" }, { "\u00B9", "sup1" },
			{ "\u00BA", "ordm" }, { "\u00BB", "raquo" }, { "\u00BC", "frac14" }, { "\u00BD", "frac12" },
			{ "\u00BE", "frac34" }, { "\u00BF", "iquest" }, { "\u00C0", "Agrave" }, { "\u00C1", "Aacute" },
			{ "\u00C2", "Acirc" }, { "\u00C3", "Atilde" }, { "\u00C4", "Auml" }, { "\u00C5", "Aring" },
			{ "\u00C6", "AElig" }, { "\u00C7", "Ccedil" }, { "\u00C8", "Egrave" }, { "\u00C9", "Eacute" },
			{ "\u00CA", "Ecirc" }, { "\u00CB", "Euml" }, { "\u00CC", "Igrave" }, { "\u00CD", "Iacute" },
			{ "\u00CE", "Icirc" }, { "\u00CF", "Iuml" }, { "\u00D0", "ETH" }, { "\u00D1", "Ntilde" },
			{ "\u00D2", "Ograve" }, { "\u00D3", "Oacute" }, { "\u00D4", "Ocirc" }, { "\u00D5", "Otilde" },
			{ "\u00D6", "Ouml" }, { "\u00D7", "times" }, { "\u00D8", "Oslash" }, { "\u00D9", "Ugrave" },
			{ "\u00DA", "Uacute" }, { "\u00DB", "Ucirc" }, { "\u00DC", "Uuml" }, { "\u00DD", "Yacute" },
			{ "\u00DE", "THORN" }, { "\u00DF", "szlig" }, { "\u00E0", "agrave" }, { "\u00E1", "aacute" },
			{ "\u00E2", "acirc" }, { "\u00E3", "atilde" }, { "\u00E4", "auml" }, { "\u00E5", "aring" },
			{ "\u00E6", "aelig" }, { "\u00E7", "ccedil" }, { "\u00E8", "egrave" }, { "\u00E9", "eacute" },
			{ "\u00EA", "ecirc" }, { "\u00EB", "euml" }, { "\u00EC", "igrave" }, { "\u00ED", "iacute" },
			{ "\u00EE", "icirc" }, { "\u00EF", "iuml" }, { "\u00F0", "eth" }, { "\u00F1", "ntilde" },
			{ "\u00F2", "ograve" }, { "\u00F3", "oacute" }, { "\u00F4", "ocirc" }, { "\u00F5", "otilde" },
			{ "\u00F6", "ouml" }, { "\u00F7", "divide" }, { "\u00F8", "oslash" }, { "\u00F9", "ugrave" },
			{ "\u00FA", "uacute" }, { "\u00FB", "ucirc" }, { "\u00FC", "uuml" }, { "\u00FD", "yacute" },
			{ "\u00FE", "thorn" }, { "\u00FF", "yuml" }, };

	private static final int MIN_ESCAPE = 2;
	private static final int MAX_ESCAPE = 6;

}
