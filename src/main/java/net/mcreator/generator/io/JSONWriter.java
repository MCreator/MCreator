/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.generator.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.Strictness;
import net.mcreator.util.TestUtil;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;

public class JSONWriter {

	private static final Logger LOG = LogManager.getLogger("JSON Writer");

	private static final Gson lenientParser = new GsonBuilder().setStrictness(Strictness.LENIENT).create();
	private static final Gson strictParser = new GsonBuilder().setStrictness(Strictness.STRICT).create();

	public static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

	public static void writeJSONToFile(@Nullable Workspace workspace, String srcjson, File file) {
		GradleTrackingFileIO.writeFile(workspace, formatJSON(srcjson), file);
	}

	public static String formatJSON(String srcjson) {
		String jsonout;
		try {
			JsonElement json = (TestUtil.isTestingEnvironment() ? strictParser : lenientParser).fromJson(srcjson,
					JsonElement.class);
			jsonout = gson.toJson(json);
		} catch (Exception e) {
			LOG.debug("JSON content: {}", srcjson);
			LOG.error("JSON Prettify failed, error: {}", e.getMessage(), e);
			jsonout = srcjson;
		}
		return jsonout;
	}

}
