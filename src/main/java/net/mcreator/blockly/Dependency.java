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

package net.mcreator.blockly;

import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.workspace.Workspace;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Dependency implements Comparable<Dependency> {

	private String name;
	private String type;

	public Dependency(String name, String type) {
		this.name = name;
		this.type = type;
	}

	@Override public int compareTo(Dependency o) {
		return (type + ":" + name).compareTo(o.type + ":" + o.name);
	}

	@Override public boolean equals(Object o) {
		if (o instanceof Dependency)
			return ((Dependency) o).name.equals(name);
		return false;
	}

	@Override public int hashCode() {
		return name.hashCode();
	}

	public String getType(Workspace workspace) {
		return new NameMapper(workspace, "types").getMapping(type);
	}

	public String getRawType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		switch (type) {
		case "number":
			return new Color(0x606999).brighter();
		case "boolean":
			return new Color(0x607c99).brighter();
		case "world":
			return new Color(0x998160).brighter();
		case "entity":
			return new Color(0x608a99).brighter();
		case "itemstack":
			return new Color(0x996069).brighter();
		case "map":
			return new Color(143, 217, 128);
		case "string":
			return new Color(0x609986);
		case "direction":
			return new Color(0x997360).brighter();
		case "advancement":
			return new Color(0x68712E).brighter();
		case "projectileentity":
			return new Color(0x608a99).brighter();
		default:
			return Color.white;
		}
	}

	public static Dependency[] fromString(String input) {
		List<Dependency> retval = new ArrayList<>();
		String[] deps = input.split("/");
		for (String dep : deps) {
			String[] depdata = dep.split(":");
			retval.add(new Dependency(depdata[0], depdata[1]));
		}
		return retval.toArray(new Dependency[0]);
	}
}
