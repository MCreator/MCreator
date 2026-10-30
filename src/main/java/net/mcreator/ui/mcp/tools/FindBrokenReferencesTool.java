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
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ReferencesFinder;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class FindBrokenReferencesTool extends MCreatorMcpTool<Void> {

	public FindBrokenReferencesTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Void.class);
	}

	@Override public String getName() {
		return "find_broken_references";
	}

	@Override public String getDescription() {
		return """
				Scans the whole workspace for mod elements that reference mod elements that no longer exist. \
				Returns a list of affected mod elements with the names of missing mod elements they reference. \
				Empty list means no broken references were found.""";
	}

	@Override protected Boolean getReadOnlyHint() {
		return true;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Void input) {
		Map<ModElement, Set<String>> broken = ReferencesFinder.searchBrokenReferences(mcreator.getWorkspace());
		List<Map<String, Object>> result = broken.entrySet().stream()
				.sorted(Comparator.comparing(entry -> entry.getKey().getName()))
				.map(entry -> Map.of("modElement", entry.getKey().getName(), "type",
						entry.getKey().getType().getRegistryName(), "brokenReferences",
						entry.getValue().stream().sorted().toList()))
				.toList();
		return completed(ToolResult.collection(result));
	}

}
