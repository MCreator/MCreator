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

import net.mcreator.io.mcp.protocol.McpSchema;
import net.mcreator.io.mcp.tool.McpTool;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class MCreatorMcpTool<T> extends McpTool<T> {

	private final Supplier<MCreator> currentMCreator;

	private static final Set<Integer> REPORTED_SESSIONS_MAP = new HashSet<>();

	protected MCreatorMcpTool(Supplier<MCreator> currentMCreator, Class<T> inputType) {
		super(inputType);
		this.currentMCreator = currentMCreator;
	}

	@Override public McpSchema.ToolAnnotations getAnnotations() {
		Boolean readOnlyHint = getReadOnlyHint();
		Boolean destructiveHint = getDestructiveHint();
		if (readOnlyHint == null && destructiveHint == null) {
			return null;
		}
		return new McpSchema.ToolAnnotations(null, readOnlyHint, destructiveHint);
	}

	@Override protected CompletableFuture<ToolResult> call(T input) {
		MCreator mcreator = currentMCreator.get();
		if (mcreator == null) {
			return CompletableFuture.completedFuture(
					ToolResult.error("No workspace open or marked to be used by MCP server by user."));
		}

		if (mcreator.getGradleConsole().isGradleSetupTaskRunning()) {
			return CompletableFuture.completedFuture(
					ToolResult.error("Workspace Gradle setup is running. Try again later."));
		}

		if (!REPORTED_SESSIONS_MAP.contains(mcreator.getWorkspace().hashCode())) {
			REPORTED_SESSIONS_MAP.add(mcreator.getWorkspace().hashCode());
			mcreator.getHistoryManager().importantCheckpoint("mcp_new_session");
			mcreator.getApplication().getAnalytics().trackEvent("mcp", "mcp_new_session");
		}

		try {
			return call(mcreator, input);
		} catch (Exception e) {
			return CompletableFuture.completedFuture(
					ToolResult.error("An error occurred while executing the tool: " + e.getMessage(), e));
		}
	}

	protected abstract CompletableFuture<ToolResult> call(MCreator mcreator, T input) throws Exception;

	protected Boolean getReadOnlyHint() {
		return null;
	}

	protected Boolean getDestructiveHint() {
		return null;
	}

}
