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

package net.mcreator.element.types;

import net.mcreator.element.NamespacedGeneratableElement;
import net.mcreator.element.parts.EntityEntry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.workspace.elements.ModElement;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused") public class Tag extends NamespacedGeneratableElement {

	public String type;

	public List<MItemBlock> items;
	public List<MItemBlock> blocks;
	public List<EntityEntry> entities;
	public List<String> functions;

	public Tag(ModElement element) {
		super(element);
	}

	public String tagType() {
		return type.toLowerCase(Locale.ENGLISH);
	}

}
