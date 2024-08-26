/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

import net.mcreator.generator.IGeneratorProvider;

import javax.annotation.Nullable;
import java.io.File;

public class TrackingFileIO {

	public static void writeFile(@Nullable IGeneratorProvider generatorProvider, String code, File path) {
		if (generatorProvider != null)
			generatorProvider.getGenerator().trackFile(path);
		FileIO.writeStringToFile(code, path);
	}

	public static void deleteFile(@Nullable IGeneratorProvider generatorProvider, File path) {
		if (generatorProvider != null)
			generatorProvider.getGenerator().trackFile(path);
		path.delete();
	}

	public static void emptyDirectory(@Nullable IGeneratorProvider generatorProvider, File path) {
		if (generatorProvider != null)
			generatorProvider.getGenerator().trackFile(path);
		FileIO.emptyDirectory(path);
	}

	public static void copyFile(@Nullable IGeneratorProvider generatorProvider, File from, File to) {
		if (generatorProvider != null) {
			generatorProvider.getGenerator().trackFile(from);
			generatorProvider.getGenerator().trackFile(to);
		}
		FileIO.copyFile(from, to);
	}

}
