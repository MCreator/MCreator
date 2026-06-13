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
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DataListTool extends MCreatorMcpTool<DataListTool.Args> {

	public static class Args {
		public QueryType type;
		@Nullable public String listName;

		public enum QueryType {
			LIST_ALL, GET_ENTRIES
		}
	}

	public DataListTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "data_list";
	}

	@Override public String getDescription() {
		return "Lists available vanilla data lists or lists entry names from a selected data list.";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		return switch (input.type) {
			case Args.QueryType.LIST_ALL ->
					CompletableFuture.completedFuture(ToolResult.collection(getAvailableDataListNames()));
			case GET_ENTRIES -> {
				if (input.listName == null || input.listName.isBlank()) {
					yield CompletableFuture.completedFuture(ToolResult.error("listName is required"));
				}
				if (!getAvailableDataListNames().contains(input.listName)) {
					yield CompletableFuture.completedFuture(ToolResult.error("Unknown data list: " + input.listName));
				}
				yield CompletableFuture.completedFuture(ToolResult.collection(
						DataListLoader.loadDataList(input.listName).stream().map(DataListEntry::getName)
								.sorted(Comparator.naturalOrder()).toList()));
			}
		};
	}

	private static List<String> getAvailableDataListNames() {
		return DataListLoader.getCache().keySet().stream().sorted(Comparator.naturalOrder()).toList();
	}

}
