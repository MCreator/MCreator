/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.component.util;

import net.mcreator.ui.init.L10N;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class CBEntry {

	private final transient String translationPrefix;
	@Nonnull private final String key;

	public CBEntry(String translationPrefix, @Nonnull String key) {
		this.key = key;
		this.translationPrefix = translationPrefix;
	}

	public CBEntry(@Nonnull String key) {
		this(null, key);
	}

	public @Nonnull String getKey() {
		return key;
	}

	@Override public String toString() {
		if (translationPrefix != null)
			return L10N.t(translationPrefix + key);
		else
			return key;
	}

	@Override public boolean equals(Object obj) {
		if (obj instanceof CBEntry entry)
			return this.key.equals(entry.key);
		return false;
	}

	public static CBEntry[] createArray(String translationPrefix, String... keys) {
		return Arrays.stream(keys).toList().stream().filter(e -> e != null && !e.isEmpty()).map(e -> new CBEntry(translationPrefix, e))
				.toList().toArray(new CBEntry[0]);
	}
}
