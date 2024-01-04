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

package net.mcreator.generator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public record GeneratorFile(GeneratorTemplate source, @Nonnull Writer writer, String contents) {

	public File getFile() {
		return source.getFile();
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		return source.equals(((GeneratorFile) o).source);
	}

	@Override public int hashCode() {
		return source.hashCode();
	}

	@Override public String toString() {
		return source.getFile().toString();
	}

	public enum Writer {

		JAVA, JSON, FILE;

		public static Writer fromString(@Nullable String string) {
			// Default to JAVA if nothing is specified (null) - for backwards compatibility
			if (string == null)
				return JAVA;

			return switch (string) {
				case "java" -> JAVA;
				case "json" -> JSON;
				case "file" -> FILE;
				default -> throw new IllegalStateException("Unexpected value: " + string);
			};
		}

	}

}
