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

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.GeneratableElement;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.WorkspaceFileManager;
import net.mcreator.workspace.elements.ModElement;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Procedure extends GeneratableElement {

	public String procedurexml;

	private transient List<Dependency> dependencies = null;

	public Procedure(ModElement element) {
		super(element);
	}

	public List<Dependency> getDependencies() {
		if (dependencies == null)
			reloadDependencies();

		return dependencies;
	}

	public void reloadDependencies() {
		dependencies = new ArrayList<>();
		List<?> dependenciesList = (List<?>) getModElement().getMetadata("dependencies");
		for (Object depobj : dependenciesList) {
			Dependency dependency = WorkspaceFileManager.gson
					.fromJson(WorkspaceFileManager.gson.toJsonTree(depobj).getAsJsonObject(), Dependency.class);
			dependencies.add(dependency);
		}
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview
				.generateProcedurePreviewPicture(getModElement().getWorkspace(), procedurexml, getDependencies());
	}
}
