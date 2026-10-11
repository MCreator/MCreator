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

import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.io.OS;
import net.mcreator.io.WindowsProcessUtil;
import net.mcreator.io.mcp.protocol.SchemaDescription;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.minecraft.BedrockUtils;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.mcp.MCreatorMcpTool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class RunClientTool extends MCreatorMcpTool<RunClientTool.Args> {

	public static class Args {
		@SchemaDescription("""
				START: generate base code and launch the Minecraft client (runs until the client is closed or stopped).\
				IS_RUNNING: check whether the client or any other Gradle task is currently running.\
				STOP: stop the running client or any other running Gradle task.""")
		public Action actionType;

		public enum Action {
			START, IS_RUNNING, STOP
		}
	}

	public RunClientTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "run_client";
	}

	@Override public String getDescription() {
		return """
				Launches the Minecraft client of the workspace, checks if it is running, or stops it.\
				 Only one Gradle task can run at a time, so the client can not be started while a build or another task is running.\
				 Use read_console for live game output and run_dir_files for logs and crash reports.""";
	}

	@Override protected Boolean getReadOnlyHint() {
		return false;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) throws Exception {
		if (input.actionType == null) {
			return completedError("actionType must be provided");
		}

		return switch (input.actionType) {
			case START -> start(mcreator);
			case IS_RUNNING -> isRunning(mcreator);
			case STOP -> stop(mcreator);
		};
	}

	private CompletableFuture<ToolResult> start(MCreator mcreator) {
		BasicAction runClient = mcreator.getActionRegistry().runClient;
		if (!runClient.isEnabled()) {
			return completedError("Client can not be started: Gradle is running or generator does not support it");
		}

		runClient.actionPerformed(null);

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("started", true);
		return completedObject(resultMap);
	}

	private CompletableFuture<ToolResult> isRunning(MCreator mcreator) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("running", BuildTool.isGradleBusy(mcreator) || isBedrockClientRunning(mcreator));
		return completedObject(resultMap);
	}

	private CompletableFuture<ToolResult> stop(MCreator mcreator) throws Exception {
		if (BuildTool.isGradleBusy(mcreator)) {
			mcreator.getGradleConsole().cancelTask();
		} else if (isBedrockClientRunning(mcreator)) {
			WindowsProcessUtil.killProcess(BedrockUtils.MC_PROCESS);
		} else {
			return completedError("Client or Gradle task is not running");
		}

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("stopped", true);
		return completedObject(resultMap);
	}

	private static boolean isBedrockClientRunning(MCreator mcreator) throws Exception {
		return mcreator.getGeneratorConfiguration().getGeneratorFlavor() == GeneratorFlavor.ADDON
				&& OS.getOS() == OS.WINDOWS
				&& WindowsProcessUtil.getProcessNameIfExists(BedrockUtils.MC_PROCESS) != null;
	}

}
