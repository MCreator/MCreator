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
import net.mcreator.io.mcp.server.McpServer;
import net.mcreator.io.mcp.transport.HttpMcpTransport;
import net.mcreator.io.mcp.transport.McpTransport;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Supplier;

public class ApplicationMCP implements Closeable {

	private static final Logger LOG = LogManager.getLogger(ApplicationMCP.class);

	private final McpServer server;

	public ApplicationMCP(MCreatorApplication application, Supplier<MCreator> mcreatorReference) {
		McpTransport transport = new HttpMcpTransport(8080);
		this.server = new McpServer("MCreator", Launcher.version.full, transport);
		try {
			this.server.start();
			LOG.debug("MCP server started");
		} catch (IOException e) {
			LOG.warn("Failed to start MCP server", e);
		}
	}

	@Override public void close() throws IOException {
		LOG.debug("Stopping MCP server...");
		server.stop();
	}

}
