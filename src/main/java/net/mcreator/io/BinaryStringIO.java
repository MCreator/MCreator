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

import net.mcreator.plugin.PluginLoader;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BinaryStringIO {

	private static final Logger LOG = LogManager.getLogger("BinaryStringIO");

	public static String readFileToString(File file) {
		try {
			return bytesToString(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			LOG.error("Error reading " + e.getMessage());
			return "";
		}
	}

	public static String readResourceToString(String resource) {
		try {
			if (resource.startsWith("/"))
				resource = resource.substring(1);

			InputStream inputStream = PluginLoader.INSTANCE.getResourceAsStream(resource);
			if (inputStream != null)
				return bytesToString(IOUtils.toByteArray(inputStream));
			else
				throw new IOException("Failed to load resoruce from any plugin. Resource: " + resource);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			LOG.error("Error reading " + e.getMessage());
			return "";
		}
	}

	private static String bytesToString(byte[] datain) {
		try {
			// unzip
			ByteArrayInputStream bis = new ByteArrayInputStream(datain);
			GZIPInputStream gis = new GZIPInputStream(bis);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[8192];
			while ((nRead = gis.read(data, 0, data.length)) != -1)
				buffer.write(data, 0, nRead);
			byte[] base64 = buffer.toByteArray();

			// decrypt
			return new String(Base64.getDecoder().decode(base64));

		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			LOG.error("Error reading " + e.getMessage());
			return "";
		}
	}

	public static void writeStringToFile(String c, File f) {
		if (!f.getParentFile().isDirectory())
			f.getParentFile().mkdirs();

		try (FileOutputStream out = new FileOutputStream(f)) {

			// encrypt
			byte[] base64 = Base64.getEncoder().withoutPadding().encode(c.getBytes());

			// zip
			ByteArrayOutputStream bos = new ByteArrayOutputStream(base64.length);
			GZIPOutputStream gzip = new GZIPOutputStream(bos);
			gzip.write(base64);
			gzip.close();
			byte[] compressed = bos.toByteArray();
			bos.close();

			// save
			out.write(compressed, 0, compressed.length);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			LOG.error("Error writing " + e.getMessage());
		}
	}

}
