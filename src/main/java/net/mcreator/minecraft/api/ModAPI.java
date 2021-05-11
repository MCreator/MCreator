/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.minecraft.api;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class ModAPI {

	public final String id;
	public final String name;

	public Map<String, Implementation> implementations;

	public ModAPI(String id, String name) {
		this.id = id;
		this.name = name;
	}

	void setImplementations(Map<String, Implementation> implementations) {
		this.implementations = implementations;
	}

	public static class Implementation {

		public final String gradle;
		@Nullable public final List<String> update_files;
		public final ModAPI parent;
		public final boolean requiredWhenEnabled;

		public Implementation(ModAPI parent, String gradle, @Nullable List<String> update_files,
				boolean requiredWhenEnabled) {
			this.gradle = gradle;
			this.update_files = update_files;
			this.parent = parent;
			this.requiredWhenEnabled = requiredWhenEnabled;
		}

	}

}
