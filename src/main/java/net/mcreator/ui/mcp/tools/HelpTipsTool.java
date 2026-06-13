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
import net.mcreator.ui.help.HelpLoader;
import net.mcreator.ui.mcp.MCreatorMcpTool;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class HelpTipsTool extends MCreatorMcpTool<HelpTipsTool.Args> {

	public static class Args {
		public QueryType type;
		@Nullable public String query;

		public enum QueryType {
			LIST_CATEGORIES, LIST_ENTRIES_IN_CATEGORY, READ_ENTRY, SEARCH_ENTRIES_CONTAINING
		}
	}

	public HelpTipsTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "help_tips";
	}

	@Override public String getDescription() {
		return """
				Lists help tips or reads help tip by full path query or searches help tips containing query.\
				Help tips are written for human use and don't contain technical details.""";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		return switch (input.type) {
			case Args.QueryType.LIST_CATEGORIES ->
					CompletableFuture.completedFuture(ToolResult.collection(HelpLoader.getCategories()));
			case LIST_ENTRIES_IN_CATEGORY ->
					CompletableFuture.completedFuture(ToolResult.collection(HelpLoader.getEntriesForCategory(input.query)));
			case READ_ENTRY ->
					CompletableFuture.completedFuture(ToolResult.text(HelpLoader.getFromEnglishCache(input.query)));
			case SEARCH_ENTRIES_CONTAINING ->
					CompletableFuture.completedFuture(ToolResult.collection(HelpLoader.getEntriesMatching(input.query)));
		};
	}

}
