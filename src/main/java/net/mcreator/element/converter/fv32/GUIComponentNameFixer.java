/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.element.converter.fv32;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.gui.Button;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.IMachineNamedComponent;
import net.mcreator.element.types.GUI;
import net.mcreator.io.Transliteration;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.workspace.Workspace;

import java.util.Set;
import java.util.stream.Collectors;

public class GUIComponentNameFixer implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		GUI gui = (GUI) input;
		Set<String> usedNames = gui.components.stream().filter(e -> e instanceof IMachineNamedComponent)
				.map(e -> e.name).collect(Collectors.toSet());
		for (GUIComponent component : gui.components) {
			if (component instanceof Button) {
				String name = RegistryNameFixer.fromCamelCase(Transliteration.transliterateString(
						((Button) component).text.replace(" ", "_").replaceAll("[^A-Za-z0-9._]*", "")));
				int i = 1;
				while (usedNames.contains(name + i)) // if output name is taken, we pick another one
					i++;
				component.name = name + i;
				usedNames.add(component.name);
			}
		}
		return gui;
	}

	@Override public int getVersionConvertingTo() {
		return 32;
	}
}
