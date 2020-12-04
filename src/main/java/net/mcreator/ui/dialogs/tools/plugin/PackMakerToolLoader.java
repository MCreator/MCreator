/*
 * MCToolkit (https://mctoolkit.net/)
 * Copyright (C) 2020 MCToolkit and contributors
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

package net.mcreator.ui.dialogs.tools.plugin;

import com.google.gson.*;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;

public class PackMakerToolLoader {
	protected static final Logger LOG = LogManager.getLogger("Pack Maker Tool loader");

	public static PackMakerToolLoader INSTANCE;

	private static final Pattern format = Pattern.compile("^[^$].*\\.json");
	private static final List<PackMakerTool> packMakersList = new ArrayList<>();

	public static void init(){
		INSTANCE = new PackMakerToolLoader();
	}

	private PackMakerToolLoader() {
		final Gson gson = new GsonBuilder().setLenient().create();

		Set<String> fileNames = PluginLoader.INSTANCE.getResources("tools.packmakers", format);
		for (String packMakerTool : fileNames) {
			try {
				JsonObject jsonresult = JsonParser
						.parseString(FileIO.readResourceToString(PluginLoader.INSTANCE, packMakerTool))
						.getAsJsonObject();

				PackMakerTool packMaker = gson.fromJson(jsonresult, PackMakerTool.class);
				if (packMaker != null) {
					packMaker.packID = FilenameUtils.getBaseName(packMakerTool);
					packMakersList.add(packMaker);
					LOG.debug("Loaded " + packMaker.packID + " pack maker tool");
					}
			} catch (Exception e) {
				LOG.error("Failed to load pack maker tool: " + packMakerTool, e);
			}
		}
	}

	public static PackMakerTool getPackMakerTool(String packId){
		for(PackMakerTool pmt : packMakersList){
			if(pmt.packID.equals(packId)) {
				return pmt;
			}
			else {
				LOG.error("The pack maker tool with ID: " + packId + " doesn't exit or is not loaded.");
				return null;
			}
		}
		return null;
	}

	public static List<PackMakerTool> getPackMakersList() {
		return packMakersList;
	}
}
