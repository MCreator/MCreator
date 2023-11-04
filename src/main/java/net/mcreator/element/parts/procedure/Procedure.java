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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused") public class Procedure {

	private static final Logger LOG = LogManager.getLogger(Procedure.class);

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
			// when deserializing, at this point, workspace may not be applied to the ME yet, so we do it now just in case
			modElement.setWorkspace(workspace);
			GeneratableElement generatableElement = modElement.getGeneratableElement();
			if (generatableElement instanceof net.mcreator.element.types.Procedure) {
				this.exists = true;
				return ((net.mcreator.element.types.Procedure) generatableElement).getDependencies();
			}
		} else {
			LOG.warn("Procedure " + name + " not found while trying to extract dependencies!");
		}

		this.exists = false;
		return Collections.emptyList();
	}

	public String getReturnValueType(Workspace workspace) {
		ModElement modElement = workspace.getModElementByName(name);
		if (modElement != null) { // procedure ME may be removed and thus cause NPE here
			GeneratableElement generatableElement = modElement.getGeneratableElement();
			if (generatableElement instanceof net.mcreator.element.types.Procedure) {
				try {
					return ((net.mcreator.element.types.Procedure) generatableElement).getBlocklyToProcedure(
							new HashMap<>()).getReturnType().getName();
				} catch (Exception ignored) {
				}
			}
		}

		return "none";
	}

}
