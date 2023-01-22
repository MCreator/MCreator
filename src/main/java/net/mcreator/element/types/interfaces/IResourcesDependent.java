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

public interface IResourcesDependent {

	default Collection<String> getTextures(TextureType type) {
		return Collections.emptyList();
	}

	default Collection<Model> getModels() {
		return Collections.emptyList();
	}

	default Collection<Sound> getSounds() {
		return Collections.emptyList();
	}

	default Collection<String> getStructures() {
		return Collections.emptyList();
	}
}