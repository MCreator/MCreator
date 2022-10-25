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

import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;

public class CustomElement extends GeneratableElement {

	public String location;

	public CustomElement(ModElement element) {
		super(element);
	}

	public String getLocation() {
		if(location.isBlank() || location.isEmpty()) return "";
		if(location.startsWith("/")) location.replaceFirst("/", "");
		if(location.endsWith("/")) return location;
		return location + "/";
	}

	public String getPackage() {
		if(location.isBlank() || location.isEmpty()) return "";
		String output = location;
		if(location.endsWith("/")) output = location.substring(0,location.length() - 2);
		if(output.startsWith("/")) return output.replace("/", ".");
		return "." + output.replace("/", ".");
	}

}
