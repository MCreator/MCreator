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

import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.io.mcp.protocol.SchemaDescription;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.workspace.WorkspaceBuildStartedEvent;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class BuildTool extends MCreatorMcpTool<BuildTool.Args> {

	private static final Logger LOG = LogManager.getLogger(BuildTool.class);

	private static final int BUILD_AND_WAIT_TIMEOUT_SECONDS = 45;

	public static class Args {
		@SchemaDescription("""
				BUILD_AND_WAIT: generate base code, run Gradle build, and wait for completion with console output.\
				START_BUILD: start the same build flow without waiting (poll with is_gradle_running and read_console).\
				IS_GRADLE_RUNNING: check whether Gradle or another workspace task is currently running.""")
		public Action actionType;

		public enum Action {
			BUILD_AND_WAIT, START_BUILD, IS_GRADLE_RUNNING
		}
	}

	public BuildTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "build";
	}

	@Override public String getDescription() {
		return """
				Build workspace or inspect Gradle task state. Use build_and_wait to block until the build finishes,\
				start_build to kick off a build asynchronously, and is_gradle_running to poll task status.""";
	}

	@Override protected Boolean getReadOnlyHint() {
		return false;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input)
			throws TemplateGeneratorException {
		if (input.actionType == null) {
			return CompletableFuture.completedFuture(ToolResult.error("actionType must be provided"));
		}

		return switch (input.actionType) {
			case BUILD_AND_WAIT -> buildAndWait(mcreator);
			case START_BUILD -> startBuild(mcreator);
			case IS_GRADLE_RUNNING -> CompletableFuture.completedFuture(isGradleRunning(mcreator));
		};
	}

	private CompletableFuture<ToolResult> buildAndWait(MCreator mcreator) throws TemplateGeneratorException {
		if (isGradleBusy(mcreator)) {
			return CompletableFuture.completedFuture(
					ToolResult.error("Gradle is already running some task. Try later."));
		}

		CompletableFuture<ToolResult> future = new CompletableFuture<>();
		GradleConsole gradleConsole = mcreator.getGradleConsole();
		gradleConsole.markRunning();
		try {
			mcreator.getGenerator().generateBase(true);
			MCREvent.event(new WorkspaceBuildStartedEvent(mcreator));

			// exec mutates Swing components, so it must run on the EDT; the returned future still
			// blocks the caller until the taskComplete listener fires when the build finishes
			ThreadUtil.runOnSwingThreadAndWait(() -> gradleConsole.exec("build", result -> {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("finished", true);
				resultMap.put("result", result);
				resultMap.put("gradleOutput", gradleConsole.getConsoleText());
				future.complete(ToolResult.object(resultMap));
			}));
		} catch (Exception e) {
			gradleConsole.markReady();
			throw e;
		}

		Map<String, Object> timeoutMap = new HashMap<>();
		timeoutMap.put("finished", false);
		timeoutMap.put("message", "Build is still running after " + BUILD_AND_WAIT_TIMEOUT_SECONDS
				+ " seconds and continues in the background. Poll with IS_GRADLE_RUNNING until it reports not"
				+ " running, then use read_console to get the build output.");
		return future.completeOnTimeout(ToolResult.object(timeoutMap), BUILD_AND_WAIT_TIMEOUT_SECONDS,
				TimeUnit.SECONDS);
	}

	private CompletableFuture<ToolResult> startBuild(MCreator mcreator) {
		if (isGradleBusy(mcreator)) {
			return CompletableFuture.completedFuture(
					ToolResult.error("Gradle is already running some task. Try later."));
		}

		new Thread(() -> {
			GradleConsole gradleConsole = mcreator.getGradleConsole();
			gradleConsole.markRunning();
			try {
				mcreator.getGenerator().generateBase(true);
				MCREvent.event(new WorkspaceBuildStartedEvent(mcreator));
				SwingUtilities.invokeLater(() -> gradleConsole.exec("build"));
			} catch (Exception e) {
				LOG.error("Failed to start build", e);
				gradleConsole.markReady();
			}
		}, "MCP-Build").start();

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("started", true);
		return CompletableFuture.completedFuture(ToolResult.object(resultMap));
	}

	private ToolResult isGradleRunning(MCreator mcreator) {
		GradleConsole gradleConsole = mcreator.getGradleConsole();
		boolean running = isGradleBusy(mcreator);

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("running", running);
		resultMap.put("gradleSetupTaskRunning", gradleConsole.isGradleSetupTaskRunning());
		return ToolResult.object(resultMap);
	}

	private static boolean isGradleBusy(MCreator mcreator) {
		GradleConsole gradleConsole = mcreator.getGradleConsole();
		return gradleConsole.getStatus() == GradleConsole.RUNNING || gradleConsole.isGradleSetupTaskRunning();
	}

}
