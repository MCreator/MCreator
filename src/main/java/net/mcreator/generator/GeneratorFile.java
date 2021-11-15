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

import java.io.File;

public class GeneratorFile {

	private final String contents;
	private final File file;
	private final String writer;

	public GeneratorFile(String contents, File file, String writer) {
		this.contents = contents;
		this.file = file;
		this.writer = writer;
	}

	public String getContents() {
		return contents;
	}

	public File getFile() {
		return file;
	}

	public String getWriter() {
		return writer;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		return file.equals(((GeneratorFile) o).file);
	}

	@Override public int hashCode() {
		return file.hashCode();
	}
}
