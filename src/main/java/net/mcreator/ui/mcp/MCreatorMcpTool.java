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

import net.mcreator.io.mcp.tool.McpTool;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class MCreatorMcpTool<T> extends McpTool<T> {

	private final Supplier<MCreator> currentMCreator;

	protected MCreatorMcpTool(Supplier<MCreator> currentMCreator, Class<T> inputType) {
		super(inputType);
		this.currentMCreator = currentMCreator;
	}

	@Override protected CompletableFuture<ToolResult> call(T input) {
		MCreator mcreator = currentMCreator.get();
		if (mcreator == null) {
			return CompletableFuture.completedFuture(
					ToolResult.error("No active MCreator instance. Open a workspace first."));
		}

		try {
			return call(mcreator, input);
		} catch (Exception e) {
			return CompletableFuture.completedFuture(
					ToolResult.error("An error occurred while executing the tool: " + e.getMessage()));
		}
	}

	protected abstract CompletableFuture<ToolResult> call(MCreator mcreator, T input);

}
