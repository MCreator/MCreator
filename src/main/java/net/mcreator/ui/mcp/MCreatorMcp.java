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
import net.mcreator.io.mcp.McpInstallHelper;
import net.mcreator.io.mcp.McpServer;
import net.mcreator.io.mcp.transport.HttpMcpTransport;
import net.mcreator.io.mcp.transport.McpTransport;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.tools.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Supplier;

public record MCreatorMcp(McpServer server, Supplier<MCreator> currentMCreator) implements Closeable {

	public MCreatorMcp(McpTransport transport, Supplier<MCreator> currentMCreator) {
		this(new McpServer("MCreator", Launcher.version.full, transport), currentMCreator);

		registerTools();
	}

	public void start() throws IOException {
		this.server.start();
	}

	private void registerTools() {
		server.registerTool(new QueryWorkspaceTool(currentMCreator));
		server.registerTool(new BuildTool(currentMCreator));
		server.registerTool(new ReadConsoleTool(currentMCreator));
		server.registerTool(new ModElementTool(currentMCreator));
		server.registerTool(new HelpTipsTool(currentMCreator));
		server.registerTool(new ModElementSchemaTool(currentMCreator));
		server.registerTool(new CreateTextureTool(currentMCreator));
		server.registerTool(new CreateArmorTextureTool(currentMCreator));
		server.registerTool(new DataListTool(currentMCreator));
		server.registerTool(new TagTool(currentMCreator));
		server.registerTool(new ReadClassSourceTool(currentMCreator));
		server.registerTool(new ProjectFilesTool(currentMCreator));
		server.registerTool(new BlocklyBlocksTool(currentMCreator));
		server.registerTool(new BlocklyTriggersTool(currentMCreator));
		server.registerTool(new BlocklyTemplatesTool(currentMCreator));
		server.registerTool(new CurrentTabTool(currentMCreator));
		server.registerTool(new VariableTool(currentMCreator));
		server.registerTool(new FindReferencesTool(currentMCreator));
		server.registerTool(new PackMakerTool(currentMCreator));
	}

	@Override public void close() throws IOException {
		server.stop();
	}

}
