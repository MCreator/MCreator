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

package net.mcreator.io.mcp.tool;

import java.util.concurrent.CompletableFuture;

/**
 * Result of invoking an MCP tool. Most tools return an immediate result only.
 * Tools that perform background work can also provide a deferred result that is
 * delivered to the client as an MCP notification once the work completes.
 */
public final class ToolInvocation {

	private final ToolResult immediate;
	private final CompletableFuture<ToolResult> deferred;

	private ToolInvocation(ToolResult immediate, CompletableFuture<ToolResult> deferred) {
		this.immediate = immediate;
		this.deferred = deferred;
	}

	public static ToolInvocation immediate(ToolResult result) {
		return new ToolInvocation(result, null);
	}

	public static ToolInvocation deferred(ToolResult immediate, CompletableFuture<ToolResult> deferred) {
		return new ToolInvocation(immediate, deferred);
	}

	public ToolResult immediate() {
		return immediate;
	}

	public CompletableFuture<ToolResult> deferred() {
		return deferred;
	}

	public boolean hasDeferredResult() {
		return deferred != null;
	}

}
