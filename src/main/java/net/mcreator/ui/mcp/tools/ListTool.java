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
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ListTool extends MCreatorMcpTool<ListTool.ListArgs> {

	public static class ListArgs {
		public ListType type;

		public enum ListType {
			MOD_ELEMENTS
		}
	}

	public ListTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, ListTool.ListArgs.class);
	}

	@Override public String getName() {
		return "list";
	}

	@Override public String getDescription() {
		return "Provides list of specified workspace elements or data list entries";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, ListTool.ListArgs input) {
		switch (input.type) {
		case ListArgs.ListType.MOD_ELEMENTS:
			return CompletableFuture.completedFuture(ToolResult.collection(mcreator.getWorkspace().getModElements()));
		default:
			return CompletableFuture.completedFuture(ToolResult.error("Unsupported list type: " + input));
		}
	}

}

