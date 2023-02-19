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

	@Override default Collection<? extends MappableElement> getUsedElementMappings() {
		return getComponentsOfType("InputSlot").stream().map(e -> ((InputSlot) e).inputLimit)
				.collect(Collectors.toList());
	}

	@Override default Collection<? extends Procedure> getUsedProcedures() {
		Collection<Procedure> procedures = new ArrayList<>();
		getComponentsOfType("EntityModel").forEach(e -> {
			procedures.add(((EntityModel) e).entityModel);
			procedures.add(((EntityModel) e).displayCondition);
		});
		getComponentsOfType("Label").forEach(e -> {
			procedures.add(((Label) e).text);
			procedures.add(((Label) e).displayCondition);
		});
		getComponentsOfType("Checkbox").forEach(e -> procedures.add(((Checkbox) e).isCheckedProcedure));
		getComponentsOfType("ImageButton").forEach(e -> {
			procedures.add(((ImageButton) e).onClick);
			procedures.add(((ImageButton) e).displayCondition);
		});
		getComponentsOfType("Button").forEach(e -> {
			procedures.add(((Button) e).onClick);
			procedures.add(((Button) e).displayCondition);
		});
		getComponentsOfType("Image").forEach(e -> procedures.add(((Image) e).displayCondition));
		getComponentsOfType("Slot").forEach(e -> {
			procedures.add(((Slot) e).disablePickup);
			procedures.add(((Slot) e).onSlotChanged);
			procedures.add(((Slot) e).onTakenFromSlot);
			procedures.add(((Slot) e).onStackTransfer);
		});
		getComponentsOfType("InputSlot").forEach(e -> procedures.add(((InputSlot) e).disablePlacement));
		return procedures;
	}

	@Override default Collection<String> getTextures(TextureType type) {
		if (type == TextureType.SCREEN) {
			List<String> textures = new ArrayList<>();
			getComponentsOfType("Image").forEach(e -> textures.add(((Image) e).image));
			getComponentsOfType("ImageButton").forEach(e -> {
				textures.add(((ImageButton) e).image);
				textures.add(((ImageButton) e).hoveredImage);
			});
			return textures;
		}
		return Collections.emptyList();
	}
}