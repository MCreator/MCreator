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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.mcreator.io.FileIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class JSONWriter {

	private static final Logger LOG = LogManager.getLogger("JSON Writer");

	public static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

	public static void writeJSONToFileWithoutQueue(String srcjson, File file) {
		String jsonout;
		try {
			JsonElement json = JsonParser.parseString(srcjson);
			jsonout = gson.toJson(json);
		} catch (Exception e) {
			LOG.error("JSON Prettify failed, error: " + e.getMessage());
			jsonout = srcjson;
		}
		FileIO.writeStringToFile(jsonout, file);
	}

}
