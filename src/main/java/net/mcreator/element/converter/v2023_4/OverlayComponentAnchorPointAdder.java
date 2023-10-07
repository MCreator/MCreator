/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2023_4;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.types.Overlay;
import net.mcreator.workspace.Workspace;

public class OverlayComponentAnchorPointAdder implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Overlay overlay = (Overlay) input;
		for (GUIComponent component : overlay.getComponents()) {
			component.anchorPoint = GUIComponent.AnchorPoint.CENTER;
		}
		return overlay;
	}

	@Override public int getVersionConvertingTo() {
		return 53;
	}
}
