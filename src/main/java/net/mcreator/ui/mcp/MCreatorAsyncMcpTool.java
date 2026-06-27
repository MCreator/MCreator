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

import net.mcreator.io.mcp.tool.ToolInvocation;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Base class for MCP tools that return immediately and deliver a follow-up result asynchronously.
 * Implement {@link #invokeAsync} to provide an immediate {@link ToolInvocation} with an optional deferred result.
 */
public abstract class MCreatorAsyncMcpTool<T> extends MCreatorMcpTool<T> {

	protected MCreatorAsyncMcpTool(Supplier<MCreator> currentMCreator, Class<T> inputType) {
		super(currentMCreator, inputType);
	}

	@Override protected CompletableFuture<ToolInvocation> invoke(MCreator mcreator, T input) {
		return invokeAsync(mcreator, input);
	}

	@Override protected final CompletableFuture<ToolResult> call(MCreator mcreator, T input) {
		return invokeAsync(mcreator, input).thenApply(ToolInvocation::immediate);
	}

	protected abstract CompletableFuture<ToolInvocation> invokeAsync(MCreator mcreator, T input);

}
