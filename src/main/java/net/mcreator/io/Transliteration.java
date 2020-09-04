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

package net.mcreator.io;

import gcardone.junidecode.Junidecode;
import org.jetbrains.annotations.NotNull;

public class Transliteration {

	/**
	 * This method transliterates string if required. If not, source String is returned. It uses Junidecode library, but strips failed transliterated characters away.
	 *
	 * @param source Soruce string
	 * @return Transliterated string or original if transliteration is not required
	 */
	@NotNull public static String transliterateString(@NotNull String source) {
		boolean pureASCII = true;
		char[] chars = source.toCharArray();

		for (char c : chars)
			if (!isASCII(c)) {
				pureASCII = false;
				break;
			}

		if (pureASCII)
			return source;
		else {

			String transliterated = Junidecode.unidecode(source);
			char[] transliterated_chars = transliterated.toCharArray();
			StringBuilder transliterated_stripped = new StringBuilder();

			for (char element : transliterated_chars)
				if (isASCII(element))
					transliterated_stripped.append(element);

			transliterated = transliterated_stripped.toString();

			return transliterated;
		}

	}

	private static boolean isASCII(char c) {
		return c < 128;
	}

}
