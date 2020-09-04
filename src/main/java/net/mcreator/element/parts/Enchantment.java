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

package net.mcreator.element.parts;

import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.workspace.Workspace;
import org.jetbrains.annotations.NotNull;

public class Enchantment extends MappableElement {

	private Enchantment() {
		super(new NameMapper(null, "enchantments"));
	}

	public Enchantment(@NotNull Workspace owner, String name) {
		this();
		mapper.setWorkspace(owner);
		setValue(name);
	}

	public Enchantment(@NotNull Workspace owner, DataListEntry name) {
		this(owner, name.getName());
	}

}
