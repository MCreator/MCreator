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

package net.mcreator.element.types.interfaces;

import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.Label;
import net.mcreator.element.parts.gui.Tooltip;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused") public interface IGUI {

	List<GUIComponent> getComponents();

	default Collection<GUIComponent> getComponentsOfType(String type) {
		return getComponents().stream().filter(c -> c.getClass().getSimpleName().equals(type)).toList();
	}

	default Collection<Label> getFixedTextLabels() {
		return getComponentsOfType("Label").stream().map(c -> (Label) c).filter(c -> c.text.getName() == null).toList();
	}

	default Collection<Tooltip> getFixedTooltips() {
		return getComponentsOfType("Tooltip").stream().map(c -> (Tooltip) c).filter(c -> c.text.getName() == null).toList();
	}
}