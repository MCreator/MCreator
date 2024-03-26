/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.generator;

import java.util.List;
import java.util.Map;

public class GeneratorImport {

	private final String path;
	private final List<String> excludes;

	@SuppressWarnings("unchecked") public GeneratorImport(Object yaml) {
		if (yaml instanceof String yamlAsPath) {
			this.path = yamlAsPath;
			this.excludes = List.of();
		} else if (yaml instanceof Map<?, ?> yamlAsMap) {
			this.path = yamlAsMap.keySet().toArray()[0].toString();
			this.excludes = yamlAsMap.containsKey("exclude") ? (List<String>) yamlAsMap.get("exclude") : List.of();
		} else {
			throw new IllegalArgumentException("Invalid import definition: " + yaml);
		}
	}

	public String getPath() {
		return path;
	}

	public boolean isExcluded(String subpath) {
		for (String excludePath : excludes) {
			if (subpath.startsWith(excludePath)) {
				return true;
			}
		}
		return false;
	}

}
