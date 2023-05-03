/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.element.parts.procedure;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused") public class Procedure {

	private final String name;

	public transient boolean exists = false;

	public Procedure(String name) {
		this.name = name;
	}

	@Nullable public String getName() {
		return name;
	}

	public List<Dependency> getDependencies(Workspace workspace) {
		ModElement modElement = workspace.getModElementByName(name);
		if (modElement != null) {
			GeneratableElement generatableElement = modElement.getGeneratableElement();
			if (generatableElement instanceof net.mcreator.element.types.Procedure) {
				this.exists = true;
				return ((net.mcreator.element.types.Procedure) generatableElement).getDependencies();
			}
		}

		this.exists = false;
		return Collections.emptyList();
	}

	public String getReturnValueType(Workspace workspace) {
		GeneratableElement generatableElement = workspace.getModElementByName(name).getGeneratableElement();
		if (generatableElement instanceof net.mcreator.element.types.Procedure) {
			try {
				return ((net.mcreator.element.types.Procedure) generatableElement).getBlocklyToProcedure(
						new HashMap<>()).getReturnType().getName();
			} catch (Exception ignored) {
			}
		}

		return "none";
	}

	public static boolean isElementUsingProcedure(Object element, String procedureName) {
		boolean isCallingThisProcedure = false;

		for (Field field : element.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				Object value = field.get(element);
				if (value instanceof Procedure) {
					if (((Procedure) value).name == null)
						continue;

					if (((Procedure) value).name.equals(procedureName)) {
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
