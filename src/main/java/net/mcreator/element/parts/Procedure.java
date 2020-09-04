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

import net.mcreator.blockly.Dependency;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.Workspace;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class Procedure {

	private String name;

	public transient boolean exists = false;

	public Procedure(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<Dependency> getDependencies(Workspace workspace) {
		GeneratableElement generatableElement = workspace.getModElementByName(name).getGeneratableElement();
		if (generatableElement instanceof net.mcreator.element.types.Procedure) {
			this.exists = true;
			return ((net.mcreator.element.types.Procedure) generatableElement).getDependencies();
		}

		this.exists = false;
		return Collections.emptyList();
	}

	public static boolean isElementUsingProcedure(Object element, String procedureName) {
		boolean isCallingThisProcedure = false;

		for (Field field : element.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				Object value = field.get(element);
				if (value instanceof Procedure) {
					if (((Procedure) value).getName().equals(procedureName)) {
						isCallingThisProcedure = true;
						break;
					}
				}
			} catch (IllegalAccessException ignored) {
			}
		}

		return isCallingThisProcedure;
	}

}
