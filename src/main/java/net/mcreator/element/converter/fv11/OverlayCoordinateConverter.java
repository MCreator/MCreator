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

package net.mcreator.element.converter.fv11;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.types.Overlay;
import net.mcreator.workspace.Workspace;

public class OverlayCoordinateConverter implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Overlay gui = (Overlay) input;

		for (GUIComponent component : gui.components) {
			component.x = convert(component.getX());
			component.y = convert(component.getY());
		}

		return gui;
	}

	private int convert(int original) {
		return (int) Math.round(original / 2.0);
	}

	@Override public int getVersionConvertingTo() {
		return 11;
	}

}
