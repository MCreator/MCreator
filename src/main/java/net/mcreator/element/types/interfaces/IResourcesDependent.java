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

package net.mcreator.element.types.interfaces;

import net.mcreator.element.parts.Sound;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.resources.Model;

import java.util.*;

/**
 * These methods are used by {@link net.mcreator.workspace.ReferencesFinder ReferencesFinder} to acquire all resources
 * of various types found in the mod element type storage class implementing this interface.
 */
public interface IResourcesDependent {

	/**
	 * @param type The type of textures that need to be collected.
	 * @return List of names of all files holding textures of provided type used by mod element instance.
	 */
	default Collection<String> getTextures(TextureType type) {
		return Collections.emptyList();
	}

	/**
	 * @return List of all models used by mod element instance.
	 */
	default Collection<Model> getModels() {
		return Collections.emptyList();
	}

	/**
	 * @return List of all sounds used by mod element instance.
	 */
	default Collection<Sound> getSounds() {
		return Collections.emptyList();
	}

	/**
	 * @return List of names of all structure files used by mod element instance.
	 */
	default Collection<String> getStructures() {
		return Collections.emptyList();
	}
}