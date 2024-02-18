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

package net.mcreator.io.zip;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipIO {

	private static final Logger LOG = LogManager.getLogger("ZipIO");

	public static ZipFile openZipFile(File zipFile) throws IOException {
		try {
			return new ZipFile(zipFile);
		} catch (ZipException e) {
			return new ZipFile(zipFile, Charset.forName("cp437"));
		}
	}

	public static void unzip(String strZipFile, String dst) {
		Path extractFolder = new File(dst).toPath();
		try (ZipFile zipFile = openZipFile(new File(strZipFile))) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				Path toPath = extractFolder.resolve(entry.getName());
				if (entry.isDirectory()) {
					toPath.toFile().mkdirs();
				} else {
					toPath.toFile().getParentFile().mkdirs();
					Files.copy(zipFile.getInputStream(entry), toPath, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		} catch (IOException e) {
			reportError("Unzip file", strZipFile, e);
		}
	}

	public static void iterateZip(File zipFilePointer, Consumer<ZipEntry> action, boolean sortByName) {
		try (ZipFile zipFile = openZipFile(zipFilePointer)) {
			List<? extends ZipEntry> entries = Collections.list(zipFile.entries());
			if (sortByName)
				entries.sort(Comparator.comparing(ZipEntry::getName));
			entries.forEach(action);
		} catch (IOException e) {
			reportError("Iterate zip", zipFilePointer.getAbsolutePath(), e);
		}
	}

	public static <T> T readFileInZip(File zipFilePointer, String path, BiFunction<ZipFile, ZipEntry, T> transformer) {
		if (path.startsWith("/"))
			path = path.substring(1);

		try (ZipFile zipFile = openZipFile(zipFilePointer)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				if (zipEntry.toString().startsWith(path)) {
					return transformer.apply(zipFile, zipEntry);
				}
			}
		} catch (IOException e) {
			reportError("Read file in zip", zipFilePointer.getAbsolutePath(), e);
		}
		return null;
	}

	public static String entryToString(ZipFile file, ZipEntry entry) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(entry)))) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (IOException e) {
			reportError("Read entry to string", entry.getName(), e);
		}
		return sb.toString();
	}

	public static String readCodeInZip(File zipFilePointer, String path) {
		return readFileInZip(zipFilePointer, path, ZipIO::entryToString);
	}

	public static void zipDir(String dirName, String nameZipFile, String... excludes) throws IOException {
		if (!new File(nameZipFile).getParentFile().isDirectory())
			new File(nameZipFile).getParentFile().mkdirs();

		FileOutputStream fw = new FileOutputStream(nameZipFile);
		ZipOutputStream zip = new ZipOutputStream(fw);
		File dir = new File(dirName);
		File[] all = dir.listFiles();
		if (all != null) {
			for (File el : all) {
				if (el.isDirectory() && Arrays.asList(excludes).contains(el.getName() + "/"))
					continue;

				if (el.isFile() && Arrays.asList(excludes).contains("#" + el.getName()))
					continue;

				if (el.isDirectory())
					addFolderToZip("", el.getAbsolutePath(), zip, excludes);
				else
					addFileToZip("", el.getAbsolutePath(), zip, false, excludes);
			}
		}
		zip.close();
		fw.close();
	}

	private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip, String... excludes)
			throws IOException {

		if (Arrays.asList(excludes).contains(path + "/"))
			return;

		File folder = new File(srcFolder);
		String[] filesin = folder.list();
		if (filesin != null && filesin.length == 0) {
			addFileToZip(path, srcFolder, zip, true, excludes);
		} else {
			for (String fileName : filesin != null ? filesin : new String[0]) {
				if (Arrays.asList(excludes).contains("#" + fileName))
					continue;

				if (path.isEmpty()) {
					addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false, excludes);
				} else {
					addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, false, excludes);
				}
			}
		}
	}

	private static void addFileToZip(String path, String srcFile, ZipOutputStream zip, boolean isDirFile,
			String... excludes) throws IOException {
		File file = new File(srcFile);
		if (isDirFile) {
			zip.putNextEntry(new ZipEntry(path + "/" + file.getName() + "/"));
		} else {
			if (file.isDirectory()) {
				addFolderToZip(path, srcFile, zip, excludes);
			} else {
				byte[] buf = new byte[8192];
				int len;
				FileInputStream in = new FileInputStream(srcFile);
				if (!path.isEmpty())
					zip.putNextEntry(new ZipEntry(path + "/" + file.getName()));
				else
					zip.putNextEntry(new ZipEntry(file.getName()));
				while ((len = in.read(buf)) > 0) {
					zip.write(buf, 0, len);
				}
				in.close();
			}
		}
	}

	public static boolean checkIfZip(File zipfile) {
		try (RandomAccessFile raf = new RandomAccessFile(zipfile, "r")) {
			return raf.readInt() == 0x504B0304;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean checkIfJMod(File zipfile) {
		try (RandomAccessFile raf = new RandomAccessFile(zipfile, "r")) {
			return raf.readInt() == 0x4A4D0100;
		} catch (IOException e) {
			return false;
		}
	}

	private static void reportError(String action, String path, Throwable exception) {
		LOG.error(action + ": " + exception.getMessage() + " - for file: " + path, exception);
	}

}
