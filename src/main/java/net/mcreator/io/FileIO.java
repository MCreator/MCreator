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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public final class FileIO {

	private static final Logger LOG = LogManager.getLogger("File System");

	public static String readFileToString(File f) {
		try {
			return Files.readString(f.toPath(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			LOG.error("Error reading: {}", e.getMessage(), e);
			return "";
		}
	}

	public static String readResourceToString(String resource) {
		return readResourceToString(ClassLoader.getSystemClassLoader(), resource);
	}

	public static String readResourceToString(ClassLoader classLoader, String resource) {
		if (resource == null)
			return null;

		if (resource.startsWith("/"))
			resource = resource.substring(1);

		try (InputStream dis = classLoader.getResourceAsStream(resource)) {
			return dis != null ? IOUtils.toString(dis, StandardCharsets.UTF_8) : "";
		} catch (Exception e) {
			LOG.error("Error resource reading: {}", e.getMessage(), e);
			return "";
		}
	}

	public static String readResourceToString(URL resource) {
		if (resource == null)
			return null;

		try (InputStream dis = resource.openStream()) {
			return dis != null ? IOUtils.toString(dis, StandardCharsets.UTF_8) : "";
		} catch (Exception e) {
			LOG.error("Error resource reading: {}", e.getMessage(), e);
			return "";
		}
	}

	public static void touchFile(File f) {
		File parentDir = f.getAbsoluteFile().getParentFile();
		if (parentDir != null && !parentDir.isDirectory())
			parentDir.mkdirs();

		try {
			f.createNewFile();
		} catch (Exception e) {
			LOG.error("Error touching {}", e.getMessage(), e);
		}
	}

	public static void writeStringToFile(String c, File f) {
		File parentDir = f.getAbsoluteFile().getParentFile();
		if (parentDir != null && !parentDir.isDirectory())
			parentDir.mkdirs();

		try (BufferedWriter out = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
			out.write(c);
		} catch (Exception e) {
			LOG.error("Error writing {}", e.getMessage(), e);
		}
	}

	public static void writeBytesToFile(byte[] c, File f) {
		File parentDir = f.getAbsoluteFile().getParentFile();
		if (parentDir != null && !parentDir.isDirectory())
			parentDir.mkdirs();

		try (FileOutputStream out = new FileOutputStream(f)) {
			out.write(c);
		} catch (Exception e) {
			LOG.error("Error writing {}", e.getMessage(), e);
		}
	}

	public static void writeImageToPNGFile(RenderedImage image, File f) {
		File parentDir = f.getAbsoluteFile().getParentFile();
		if (parentDir != null && !parentDir.isDirectory())
			parentDir.mkdirs();

		try {
			ImageIO.write(image, "png", f);
		} catch (IOException e) {
			LOG.error("Error writing image {}", e.getMessage(), e);
		}
	}

	public static void copyFile(File from, File to) {
		if (from.isDirectory())
			LOG.fatal("Trying to copy folder as a file: {}", from);

		try {
			File parentDir = to.getAbsoluteFile().getParentFile();
			if (parentDir != null && !parentDir.isDirectory())
				parentDir.mkdirs();

			Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LOG.error("Error copying file: {}", e.getMessage(), e);
		}
	}

	public static void copyDirectory(File sourceLocation, File targetLocation) {
		if (!sourceLocation.exists())
			return;

		Path source = sourceLocation.toPath();
		Path target = targetLocation.toPath();

		if (target.toAbsolutePath().normalize().startsWith(source.toAbsolutePath().normalize()))
			return;

		try {
			Files.walkFileTree(source, new SimpleFileVisitor<>() {
				@Nonnull @Override
				public FileVisitResult preVisitDirectory(@Nonnull Path dir, @Nonnull BasicFileAttributes attrs)
						throws IOException {
					Path targetDir = target.resolve(source.relativize(dir));
					if (!Files.isDirectory(targetDir)) {
						Files.copy(dir, targetDir);
					}
					return FileVisitResult.CONTINUE;
				}

				@Nonnull @Override
				public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs)
						throws IOException {
					Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			LOG.warn("Error copying directory: {}", e.getMessage(), e);
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String element : children != null ? children : new String[0]) {
				boolean success = deleteDir(new File(dir, element));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	public static boolean moveToTrash(File dir) {
		if (dir.isDirectory() && Desktop.isDesktopSupported() && Desktop.getDesktop()
				.isSupported(Desktop.Action.MOVE_TO_TRASH)) {
			return Desktop.getDesktop().moveToTrash(dir);
		} else {
			return deleteDir(dir);
		}
	}

	public static void emptyDirectory(File directory, String... excludes) {
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			fileIteratorLoop:
			for (File file : files != null ? files : new File[0]) {
				for (String exclude : excludes)
					if (file.getAbsolutePath().endsWith(exclude))
						continue fileIteratorLoop;
				if (file.isFile())
					file.delete();
				if (file.isDirectory())
					deleteDir(file);
			}
		}
	}

	public static boolean isFileOnFileList(List<File> fileList, File file) {
		if (!file.exists())
			return false;

		String targetName = file.getName();
		Path targetPath = file.toPath();
		for (File fileFromList : fileList) {
			if (file.equals(fileFromList))
				return true;

			if (!targetName.equals(fileFromList.getName()))
				continue;

			try {
				if (Files.isSameFile(targetPath, fileFromList.toPath())) {
					return true;
				}
			} catch (IOException ignored) {
			}
		}

		return false;
	}

	public static boolean isFileSomewhereInDirectory(File file, File directory) {
		Path filePath = file.toPath().toAbsolutePath().normalize();
		Path dirPath = directory.toPath().toAbsolutePath().normalize();
		return filePath.startsWith(dirPath);
	}

	public static boolean isSameFile(File file1, File file2) {
		if (file1 == file2 || file1.equals(file2))
			return true;

		if (!file1.getName().equals(file2.getName()))
			return false;

		try {
			return Files.isSameFile(file1.toPath(), file2.toPath());
		} catch (IOException e) {
			try {
				return file1.getCanonicalPath().equals(file2.getCanonicalPath());
			} catch (IOException e2) {
				return file1.getAbsolutePath().equals(file2.getAbsolutePath());
			}
		}
	}

	public static boolean removeEmptyDirs(File root) {
		if (!root.isDirectory())
			return false;

		boolean isEmpty = true;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(root.toPath())) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					if (removeEmptyDirs(entry.toFile())) {
						Files.deleteIfExists(entry);
					} else {
						isEmpty = false;
					}
				} else {
					isEmpty = false;
				}
			}
		} catch (IOException e) {
			LOG.warn("Error while removing empty directories: {}", e.getMessage(), e);
			return false;
		}
		return isEmpty;
	}

	public static File[] listFilesRecursively(File root) {
		if (root.isDirectory()) {
			return FileUtils.listFiles(root, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).toArray(new File[0]);
		} else {
			return new File[] {};
		}
	}

}
