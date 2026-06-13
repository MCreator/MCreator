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

import net.mcreator.element.ModElementType;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ListTool extends MCreatorMcpTool<ListTool.Args> {

	public static class Args {
		public ListType type;

		public enum ListType {
			MOD_ELEMENTS, MOD_VARIABLES, MOD_TAGS, SUPPORTED_MOD_ELEMENT_TYPES, WORKSPACE_SETTINGS
		}
	}

	public ListTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "list";
	}

	@Override public String getDescription() {
		return "Provides list of specified workspace elements or types or data list entries or info.";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		return switch (input.type) {
			case Args.ListType.MOD_ELEMENTS ->
					CompletableFuture.completedFuture(ToolResult.collection(mcreator.getWorkspace().getModElements()));
			case Args.ListType.MOD_VARIABLES -> CompletableFuture.completedFuture(
					ToolResult.collection(mcreator.getWorkspace().getVariableElements()));
			case Args.ListType.MOD_TAGS -> CompletableFuture.completedFuture(
					ToolResult.collection(mcreator.getWorkspace().getTagElements().entrySet()));
			case Args.ListType.SUPPORTED_MOD_ELEMENT_TYPES -> CompletableFuture.completedFuture(ToolResult.collection(
					mcreator.getGeneratorStats().getSupportedModElementTypes().stream()
							.map(ModElementType::getRegistryName).toList()));
			case Args.ListType.WORKSPACE_SETTINGS -> CompletableFuture.completedFuture(
					ToolResult.object(mcreator.getWorkspace().getWorkspaceSettings()));
		};
	}

}
