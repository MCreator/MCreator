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

package net.mcreator.ui.mcp.tools.utils;

import net.mcreator.io.mcp.tool.ToolResult;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class CollectionFilter {

	private CollectionFilter() {}

	public static ToolResult applyStrings(Collection<String> collection, @Nullable String filterRegex) {
		return apply(collection, filterRegex, Function.identity());
	}

	public static <T> ToolResult apply(Collection<T> collection, @Nullable String filterRegex,
			Function<T, String> filterTextSupplier) {
		if (filterRegex == null || filterRegex.isBlank()) {
			return ToolResult.collection(collection);
		}

		try {
			List<T> source = collection instanceof List<T> list ? list : List.copyOf(collection);
			Pattern pattern = Pattern.compile(filterRegex, Pattern.CASE_INSENSITIVE);
			List<T> matched = new ArrayList<>();
			int filteredOut = 0;
			for (T item : source) {
				String text = filterTextSupplier.apply(item);
				if (text != null && pattern.matcher(text).find()) {
					matched.add(item);
				} else {
					filteredOut++;
				}
			}

			Map<String, Object> response = new HashMap<>();
			response.put("items", matched);
			response.put("filteredOutCount", filteredOut);
			return ToolResult.object(response);
		} catch (PatternSyntaxException e) {
			return ToolResult.error("Invalid filter regex: " + e.getDescription());
		}
	}

}
