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

package net.mcreator.element;

import net.mcreator.workspace.elements.ModElement;

import java.util.Locale;

@SuppressWarnings("unused") public abstract class NamespacedGeneratableElement
		extends GeneratableElement {

	public String name;
	public String namespace;

	public NamespacedGeneratableElement(ModElement element) {
		super(element);
	}

	public String getName() {
		return name.toLowerCase(Locale.ENGLISH);
	}

	public String getNamespace() {
		if (namespace == null || namespace.equals("mod"))
			return getModElement().getWorkspace().getWorkspaceSettings().getModID();

		return namespace.toLowerCase(Locale.ENGLISH);
	}

	public String getResourceLocation() {
		return this.getNamespace() + ":" + this.getName();
	}

}
