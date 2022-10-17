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
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

public final class FileIO {

	private static final Logger LOG = LogManager.getLogger("File System");

	public static void touchFile(File f) {
		if (!f.getAbsoluteFile().getParentFile().isDirectory())
			f.getAbsoluteFile().getParentFile().mkdirs();

		try {
			f.createNewFile();
		} catch (Exception e) {
			LOG.error("Error touching " + e.getMessage(), e);
		}
	}

	public static String readFileToString(File f) {
		try {
			FileChannel channel = new FileInputStream(f).getChannel();
			ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
			channel.read(buffer);
			channel.close();
			return new String(buffer.array(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			LOG.error("Error reading: " + e.getMessage(), e);
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
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int length;
			if (dis != null) {
				while ((length = dis.read(buffer)) != -1) {
					result.write(buffer, 0, length);
				}
			}
			return result.toString(StandardCharsets.UTF_8);
		} catch (Exception e) {
			LOG.error("Error resource reading: " + e.getMessage(), e);
			return "";
		}
	}

	public static String readResourceToString(URL resource) {
		if (resource == null)
			return null;

		try (InputStream dis = resource.openConnection().getInputStream()) {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int length;
			if (dis != null) {
				while ((length = dis.read(buffer)) != -1) {
					result.write(buffer, 0, length);
				}
			}
			return result.toString();
		} catch (Exception e) {
			LOG.error("Error resource reading: " + e.getMessage(), e);
			return "";
		}
	}

	public static void writeStringToFile(String c, File f) {
		if (!f.getAbsoluteFile().getParentFile().isDirectory())
			f.getAbsoluteFile().getParentFile().mkdirs();

		try (BufferedWriter out = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
			out.write(c);
		} catch (Exception e) {
			LOG.error("Error writing " + e.getMessage(), e);
		}
	}

	public static void writeBytesToFile(byte[] c, File f) {
		if (!f.getAbsoluteFile().getParentFile().isDirectory())
			f.getAbsoluteFile().getParentFile().mkdirs();

		try (FileOutputStream out = new FileOutputStream(f)) {
			out.write(c);
		} catch (Exception e) {
			LOG.error("Error writing " + e.getMessage(), e);
		}
	}

	public static void writeImageToPNGFile(RenderedImage image, File f) {
		if (!f.getAbsoluteFile().getParentFile().isDirectory())
			f.getAbsoluteFile().getParentFile().mkdirs();

		try {
			ImageIO.write(image, "png", f);
		} catch (IOException e) {
			LOG.error("Error writing image " + e.getMessage(), e);
		}
	}

	public static void copyDirectory(File sourceLocation, File targetLocation) {
		if (!sourceLocation.exists())
			return;

		// prevent recursive copy in case if directory is copied in a subdirectory
		try {
			if (targetLocation.getParentFile().getCanonicalPath().equals(sourceLocation.getCanonicalPath()))
				return;
		} catch (IOException e) {
			return;
		}

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}
			String[] children = sourceLocation.list();
			for (String element : children != null ? children : new String[0]) {
				copyDirectory(new File(sourceLocation, element), new File(targetLocation, element));
			}
		} else {
			copyFile(sourceLocation, targetLocation);
		}
	}

	public static void copyFile(File from, File to) {
		if (from.isDirectory())
			LOG.fatal("Trying to copy folder as a file: " + from);

		try {
			if (!to.getParentFile().isDirectory())
				to.getParentFile().mkdirs();

			Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LOG.error("Error copying file: " + e.getMessage(), e);
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

		for (File fileFromList : fileList) {
			if (fileFromList.getName().equals(file.getName())) {
				try {
					if (Files.isSameFile(file.toPath(), fileFromList.toPath()))
						return true;
				} catch (IOException ignored) {
				}
			}
		}

		return false;
	}

	public static boolean isFileSomewhereInDirectory(File file, File directory) {
		try {
			return file.getCanonicalPath().startsWith(directory.getCanonicalPath());
		} catch (Exception ignored) {
			return file.getAbsolutePath().startsWith(directory.getAbsolutePath());
		}
	}

	public static boolean removeEmptyDirs(File root) {
		File[] files = root.listFiles();
		for (File file : files != null ? files : new File[0]) {
			if (file.isDirectory()) {
				boolean isEmpty = removeEmptyDirs(file);
				if (isEmpty)
					file.delete();
			}
		}
		return Objects.requireNonNull(root.listFiles()).length == 0;
	}

	public static File[] listFilesRecursively(File root) {
		if (root.isDirectory()) {
			return FileUtils.listFiles(root, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).toArray(new File[0]);
		} else {
			return new File[] {};
		}
	}

}
