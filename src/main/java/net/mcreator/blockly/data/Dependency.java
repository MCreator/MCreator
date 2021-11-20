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

package net.mcreator.blockly.data;

import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.VariableTypeLoader;

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
		return getColor(type);
	}

	public String getDependencyBlockXml() {
		StringBuilder blockXml = new StringBuilder("<xml xmlns=\"http://www.w3.org/1999/xhtml\">");
		switch (name) {
		case "x":
			blockXml.append("<block type=\"coord_x\"></block>");
			break;
		case "y":
			blockXml.append("<block type=\"coord_y\"></block>");
			break;
		case "z":
			blockXml.append("<block type=\"coord_z\"></block>");
			break;
		case "entity":
			blockXml.append("<block type=\"entity_from_deps\"></block>");
			break;
		case "sourceentity":
			blockXml.append("<block type=\"source_entity_from_deps\"></block>");
			break;
		case "immediatesourceentity":
			blockXml.append("<block type=\"immediate_source_entity_from_deps\"></block>");
			break;
		case "direction":
			blockXml.append("<block type=\"direction_from_deps\"></block>");
			break;
		case "itemstack":
			blockXml.append("<block type=\"itemstack_to_mcitem\"></block>");
			break;
		case "blockstate":
			blockXml.append("<block type=\"blockstate_from_deps\"></block>");
			break;
		default:
			if (VariableTypeLoader.INSTANCE.fromName(type) != null) {
				blockXml.append("<block type=\"custom_dependency_");
				blockXml.append(type);
				blockXml.append("\"><field name=\"NAME\">");
				blockXml.append(name);
				blockXml.append("</field></block>");
			} else
				return null;
		}
		blockXml.append("</xml>");
		return blockXml.toString();
	}

	public static Color getColor(String type) {
		// Check if the type is a loaded variable and then, get its HUE color
		if (VariableTypeLoader.INSTANCE.fromName(type) != null) {
			return VariableTypeLoader.INSTANCE.fromName(type).getBlocklyColor();
		}

		// Return a color for other dependency types
		return switch (type) {
			case "world" -> new Color(0x998160);
			case "entity" -> new Color(0x608a99);
			case "map" -> new Color(0x8FD980);
			case "advancement" -> new Color(0x68712E);
			case "dimensiontype" -> new Color(0x609963);
			default -> Color.white;
		};
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
