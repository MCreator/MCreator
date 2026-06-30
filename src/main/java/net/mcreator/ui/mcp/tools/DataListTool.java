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

import net.mcreator.io.mcp.protocol.SchemaDescription;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyElementUtil;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.mcp.tools.utils.CollectionFilter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DataListTool extends MCreatorMcpTool<DataListTool.Args> {

	public static class Args {
		public QueryType type;
		@Nullable public String listName;
		@SchemaDescription("Optional Java regex filter to limit returned list entries size.") @Nullable
		public String filter;

		public enum QueryType {
			GET_LIST_TYPES, GET_LIST_ENTRIES
		}
	}

	public DataListTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "datalist_entries";
	}

	@Override public String getDescription() {
		return """
				Lists available datalists or lists entries of selected datalist (vanilla and/or custom).\
				The entries listed by this tool are the only entries you can use for given datalist.
				For blocks and items, use query_workspace tool.""";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		return switch (input.type) {
			case Args.QueryType.GET_LIST_TYPES ->
					CompletableFuture.completedFuture(ToolResult.collection(getAvailableDataListNames()));
			case GET_LIST_ENTRIES -> {
				if (input.listName == null || input.listName.isBlank()) {
					yield CompletableFuture.completedFuture(ToolResult.error("listName is required"));
				}

				if (!getAvailableDataListNames().contains(input.listName)) {
					yield CompletableFuture.completedFuture(ToolResult.error("Unknown data list: " + input.listName));
				}

				// If list is handled by BlocklyElementUtil.getStringArrayForEntrySelector, return it
				String[] retval = BlocklyElementUtil.getStringArrayForEntrySelector(mcreator.getWorkspace(),
						input.listName, null);
				if (retval != null && retval.length > 0) {
					yield completed(CollectionFilter.applyStrings(List.of(retval), input.filter));
				}

				Collection<DataListEntryInfo> entries = ElementUtil.getAllEntriesFor(mcreator.getWorkspace(),
								input.listName).stream().map(DataListEntryInfo::new)
						.sorted(Comparator.comparing(DataListEntryInfo::name)).toList();
				if (!entries.isEmpty()) {
					yield completed(CollectionFilter.apply(entries, input.filter, DataListEntryInfo::toString));
				}

				yield CompletableFuture.completedFuture(
						ToolResult.error("No entries found for data list: " + input.listName));
			}
		};
	}

	private static Set<String> getAvailableDataListNames() {
		Set<String> retval = new HashSet<>(DataListLoader.getCache().keySet());
		retval.addAll(ElementUtil.getVanillaEntryProviders().keySet());
		retval.addAll(ElementUtil.getCustomEntryProviders().keySet());
		retval.addAll(BlocklyElementUtil.getStringArrayEntryProviders().keySet());
		return retval;
	}

	private record DataListEntryInfo(String name, @Nullable String readableName, @Nullable String description) {
		DataListEntryInfo(DataListEntry entry) {
			this(entry.getName(), entry.getRawReadableName(), entry.getRawDescription());
		}

		@Nonnull @Override public String toString() {
			String retval = name;
			if (readableName != null)
				retval += " " + readableName;
			if (description != null)
				retval += " " + description;
			return retval;
		}
	}

}
