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

package net.mcreator.io.writer;

import net.mcreator.io.FileIO;
import net.mcreator.java.CodeCleanup;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

public class ClassWriter {

	private static CodeCleanup codeCleanup;

	public static void writeClassToFileWithoutQueue(@Nullable Workspace workspace, String code, File file,
			boolean formatAndOrganiseImports) {
		if (codeCleanup == null)
			codeCleanup = new CodeCleanup();

		if (formatAndOrganiseImports) {
			FileIO.writeStringToFile(codeCleanup.reformatTheCodeAndOrganiseImports(workspace, code), file);
		} else {
			FileIO.writeStringToFile(code, file);
		}
	}

	public static void formatAndOrganiseImportsForFiles(@Nullable Workspace workspace, @Nonnull Collection<File> files,
			@Nullable IntConsumer intConsumer) {
		Map<File, String> codes = files.stream()
				.filter(file -> FilenameUtils.isExtension(file.getName().toLowerCase(Locale.ENGLISH), "java"))
				.collect(Collectors.toMap(file -> file, FileIO::readFileToString));

		// reload mod classes by "dummy" formatting the first element
		codes.values().stream().findFirst()
				.ifPresent(code -> codeCleanup.reformatTheCodeAndOrganiseImports(workspace, code, false));

		AtomicInteger counter = new AtomicInteger();
		Map<File, String> formattedCodes = files.parallelStream().peek(file -> {
			if (intConsumer != null)
				intConsumer.accept(counter.incrementAndGet());
		}).filter(codes::containsKey).collect(Collectors.toMap(file -> file,
				file -> codeCleanup.reformatTheCodeAndOrganiseImports(workspace, codes.get(file), true)));

		formattedCodes.forEach((file, code) -> FileIO.writeStringToFile(code, file));
	}

}
