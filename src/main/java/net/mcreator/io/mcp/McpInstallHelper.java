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

package net.mcreator.io.mcp;

import com.google.gson.*;
import net.mcreator.io.FileIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class McpInstallHelper {

	private static final Logger LOG = LogManager.getLogger(McpInstallHelper.class);

	/**
	 * Orchestrator method that attempts to install the MCP server to all supported clients.
	 *
	 * @param serverName The unique ID/key for your MCP server in the config.
	 * @param targetUrl  The full URL endpoint
	 */
	public static void installToAll(String serverName, String targetUrl) {
		installForCursor(serverName, targetUrl);
	}

	/**
	 * Handles the installation specifically for Cursor.
	 */
	private static void installForCursor(String serverName, String targetUrl) {
		String userHome = System.getProperty("user.home");
		File cursorDir = new File(userHome, ".cursor");

		// Skip installation if Cursor is not installed
		if (!cursorDir.isDirectory()) {
			return;
		}

		File cursorFile = new File(cursorDir, "mcp.json");

		JsonObject newServerConfig = new JsonObject();
		newServerConfig.addProperty("type", "http");
		newServerConfig.addProperty("url", targetUrl);

		try {
			JsonObject root = new JsonObject();
			if (cursorFile.exists()) {
				String content = FileIO.readFileToString(cursorFile).trim();
				if (!content.isEmpty()) {
					try {
						root = JsonParser.parseString(content).getAsJsonObject();
					} catch (JsonSyntaxException | IllegalStateException e) {
						LOG.error("Existing JSON in Cursor is malformed. No changes will be made.");
						return;
					}
				}
			}

			if (!root.has("mcpServers") || !root.get("mcpServers").isJsonObject()) {
				root.add("mcpServers", new JsonObject());
			}

			JsonObject mcpServers = root.getAsJsonObject("mcpServers");

			// Validate if an update is even necessary to prevent unnecessary I/O
			// A non-object entry (e.g. hand-edited config) is treated as outdated and overwritten
			if (mcpServers.has(serverName) && mcpServers.get(serverName).isJsonObject()) {
				JsonObject existingServer = mcpServers.getAsJsonObject(serverName);
				boolean urlMatches = existingServer.get("url") instanceof JsonPrimitive urlPrimitive && targetUrl.equals(
						urlPrimitive.getAsString());

				if (urlMatches) {
					LOG.info("Server '{}' is already up-to-date in Cursor.", serverName);
					return;
				} else {
					LOG.info("Server '{}' found, but configuration changed. Updating...", serverName);
				}
			}

			// Apply payload and save
			mcpServers.add(serverName, newServerConfig);

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonOutput = gson.toJson(root);

			if (cursorFile.exists()) {
				File backupFile = new File(cursorFile.getAbsolutePath() + ".mcr_backup");
				FileIO.copyFile(cursorFile, backupFile);
			}

			FileIO.writeStringToFile(jsonOutput, cursorFile);
			LOG.info("Successfully configured '{}' for Cursor.", serverName);
		} catch (Exception e) {
			LOG.warn("Failed to install MCP server config for Cursor", e);
		}
	}

}