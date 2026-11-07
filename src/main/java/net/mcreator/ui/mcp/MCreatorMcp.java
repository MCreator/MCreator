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
import net.mcreator.io.mcp.transport.McpTransport;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.MCreatorMcpLoadedEvent;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.tools.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Supplier;

public record MCreatorMcp(McpServer server, Supplier<MCreator> currentMCreator) implements Closeable {

	public static final String INSTRUCTIONS = """
			MCreator MCP server. Tools operate on the workspace currently selected for MCP in MCreator.
			Mods are built from mod elements (blocks, items, entities, procedures, ...). Add or edit them with \
			mod_element_definition and always fetch get_mod_element_schema for the element type first, the JSON \
			definition must match the schema. Use query_workspace and datalist_entries as the only source of \
			valid mod element, texture, variable, and registry names, never guess them. Create missing textures with \
			create_texture or create_armor_texture before referencing them.
			Logic belongs in procedure mod elements built from Blockly blocks. Use blockly_triggers, blockly_blocks, \
			and blockly_templates to discover triggers, blocks, and example assemblies, and compose the procedure from \
			blocks. Custom code snippet blocks (java_code, java_code_get) are only for a short expression or statement \
			no block provides. Never put classes, event handlers, registrations, renderers, or whole features into \
			them; split the work into separate mod elements and procedures instead. If raw Java is truly unavoidable, \
			lock the code of a single mod element with LOCK_CODE and edit its files with project_file_edit. Never edit \
			generated files of unlocked mod elements, the generator overwrites them.
			Use help_tips to understand what a parameter does. After changes, run build, use read_console on failure, \
			and fix the cause before continuing. Use run_dir_files to read logs and crash reports. \
			Mod element names are CamelCase and must be unique.""";

	public MCreatorMcp(McpTransport transport, Supplier<MCreator> currentMCreator) {
		this(new McpServer("MCreator", Launcher.version.full, transport, INSTRUCTIONS), currentMCreator);
	}

	public MCreatorMcp(McpServer server, Supplier<MCreator> currentMCreator) {
		this.server = server;
		this.currentMCreator = currentMCreator;

		registerTools();

		MCREvent.event(new MCreatorMcpLoadedEvent(this));
	}

	public void start() throws IOException {
		this.server.start();
	}

	private void registerTools() {
		server.registerTool(new QueryWorkspaceTool(currentMCreator));
		server.registerTool(new BuildTool(currentMCreator));
		server.registerTool(new RunClientTool(currentMCreator));
		server.registerTool(new ReadConsoleTool(currentMCreator));
		server.registerTool(new ModElementTool(currentMCreator));
		server.registerTool(new HelpTipsTool(currentMCreator));
		server.registerTool(new ModElementSchemaTool(currentMCreator));
		server.registerTool(new CreateTextureTool(currentMCreator));
		server.registerTool(new CreateArmorTextureTool(currentMCreator));
		server.registerTool(new ImportModelTool(currentMCreator));
		server.registerTool(new DataListTool(currentMCreator));
		server.registerTool(new TagTool(currentMCreator));
		server.registerTool(new ReadClassSourceTool(currentMCreator));
		server.registerTool(new ProjectFilesTool(currentMCreator));
		server.registerTool(new ProjectFileEditTool(currentMCreator));
		server.registerTool(new RunDirFilesTool(currentMCreator));
		server.registerTool(new BlocklyBlocksTool(currentMCreator));
		server.registerTool(new BlocklyTriggersTool(currentMCreator));
		server.registerTool(new BlocklyTemplatesTool(currentMCreator));
		server.registerTool(new CurrentTabTool(currentMCreator));
		server.registerTool(new OpenTabTool(currentMCreator));
		server.registerTool(new VariableTool(currentMCreator));
		server.registerTool(new FindReferencesTool(currentMCreator));
		server.registerTool(new FindBrokenReferencesTool(currentMCreator));
		server.registerTool(new PackMakerTool(currentMCreator));
	}

	@Override public void close() throws IOException {
		server.stop();
	}

}
