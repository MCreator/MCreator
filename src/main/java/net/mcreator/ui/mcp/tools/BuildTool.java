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

package net.mcreator.ui.mcp.tools;

import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.workspace.WorkspaceBuildStartedEvent;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BuildTool extends MCreatorMcpTool<Void> {

	public BuildTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Void.class);
	}

	@Override public String getName() {
		return "build";
	}

	@Override public String getDescription() {
		return "Provides list of specified workspace elements or data list entries";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Void input) {
		CompletableFuture<ToolResult> future = new CompletableFuture<>();
		mcreator.getGenerator().generateBase();
		MCREvent.event(new WorkspaceBuildStartedEvent(mcreator));

		mcreator.getGradleConsole().exec("build", result -> {
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("result", result);
			resultMap.put("gradleOutput", mcreator.getGradleConsole().getConsoleText());
			future.complete(ToolResult.object(resultMap));
		});
		return future;
	}

}

