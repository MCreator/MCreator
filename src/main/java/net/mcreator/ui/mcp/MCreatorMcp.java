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

package net.mcreator.ui.mcp;

import net.mcreator.Launcher;
import net.mcreator.io.mcp.McpServer;
import net.mcreator.io.mcp.transport.HttpMcpTransport;
import net.mcreator.io.mcp.transport.McpTransport;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.tools.*;
import net.mcreator.ui.mcp.tools.schema.ModElementSchemaTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Supplier;

public final class MCreatorMcp implements Closeable {

	private static final Logger LOG = LogManager.getLogger(MCreatorMcp.class);

	private final McpServer server;

	private final Supplier<MCreator> currentMCreator;

	public MCreatorMcp(Supplier<MCreator> currentMCreator) {
		this.currentMCreator = currentMCreator;

		McpTransport transport = new HttpMcpTransport(PreferencesManager.PREFERENCES.integrations.mcpPort.get());
		this.server = new McpServer("MCreator", Launcher.version.full, transport);
		if (PreferencesManager.PREFERENCES.integrations.mcpEnable.get()) {
			try {
				registerTools();
				this.server.start();
				LOG.debug("MCP server started at port {}", PreferencesManager.PREFERENCES.integrations.mcpPort.get());
			} catch (IOException e) {
				LOG.warn("Failed to start MCP server", e);
			}
		}
	}

	private void registerTools() {
		server.registerTool(new ListTool(currentMCreator));
		server.registerTool(new BuildTool(currentMCreator));
		server.registerTool(new ReadConsoleTool(currentMCreator));
		server.registerTool(new ModElementTool(currentMCreator));
		server.registerTool(new HelpTipsTool(currentMCreator));
		server.registerTool(new ModElementSchemaTool(currentMCreator));
		server.registerTool(new CreateTextureTool(currentMCreator));
		server.registerTool(new CreateArmorTextureTool(currentMCreator));
		// TODO: list vanilla data lists and get data list entries tool
		// TODO: add tag or add tag entries tool
		// TODO: read MC source code tool
	}

	public McpServer getServer() {
		return server;
	}

	public Supplier<MCreator> getCurrentMCreator() {
		return currentMCreator;
	}

	@Override public void close() throws IOException {
		LOG.debug("Stopping MCP server...");
		server.stop();
	}

}
