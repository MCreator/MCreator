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

import net.mcreator.element.parts.gui.*;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.ui.workspace.resources.TextureType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused") public interface IGUI extends IOtherModElementsDependent, IResourcesDependent {

	List<GUIComponent> getComponents();

	default Collection<GUIComponent> getComponentsOfType(String type) {
		return getComponents().stream().filter(c -> c.getClass().getSimpleName().equals(type)).toList();
	}

	default Collection<Label> getFixedTextLabels() {
		return getComponentsOfType("Label").stream().map(c -> (Label) c).filter(c -> c.text.getName() == null).toList();
	}

	@Override default Collection<? extends MappableElement> getUsedModElements() {
		return getComponentsOfType("InputSlot").stream().map(e -> ((InputSlot) e).inputLimit)
				.collect(Collectors.toList());
	}

	@Override default Collection<? extends Procedure> getUsedProcedures() {
		Collection<Procedure> procedures = new ArrayList<>();
		getComponentsOfType("Label").forEach(e -> procedures.add(((Label) e).text));
		getComponentsOfType("Slot").forEach(e -> procedures.add(((Slot) e).disablePickup));
		getComponentsOfType("InputSlot").forEach(e -> procedures.add(((InputSlot) e).disablePlacement));
		return procedures;
	}

	@Override default Collection<String> getTextures(TextureType type) {
		return type == TextureType.SCREEN ?
				getComponentsOfType("Image").stream().map(e -> ((Image) e).image).collect(Collectors.toList()) :
				Collections.emptyList();
	}
}