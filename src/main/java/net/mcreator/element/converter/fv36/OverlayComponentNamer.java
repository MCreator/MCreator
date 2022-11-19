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

package net.mcreator.element.converter.fv36;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.Label;
import net.mcreator.element.types.Overlay;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.dialogs.wysiwyg.AbstractWYSIWYGDialog;
import net.mcreator.workspace.Workspace;

public class OverlayComponentNamer implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Overlay gui = (Overlay) input;

		for (GUIComponent component : gui.components) {
			if (component instanceof Label label) {
				String baseName;
				if (label.text.getName() != null) { // string procedure
					baseName = "proc_" + RegistryNameFixer.fromCamelCase(label.text.getName());
				} else { // fixed text
					baseName = label.text.getFixedValue();
				}
				label.name = AbstractWYSIWYGDialog.textToMachineName(gui.components, "label_", baseName);
			}
		}
		return gui;
	}

	@Override public int getVersionConvertingTo() {
		return 36;
	}

}
