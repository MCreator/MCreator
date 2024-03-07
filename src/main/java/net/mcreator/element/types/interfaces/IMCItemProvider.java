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

import net.mcreator.minecraft.MCItem;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.util.List;

public interface IMCItemProvider {
	/**
	 * This method determines what MCItems are provided by this generatable element
	 * <p>
	 * WARNING: Calls to this method are generally not thread safe.
	 * Implementations can call MCItem.Custom::new that calls getBlockIconBasedOnName
	 * which calls ModElement#getGeneratableElement that is not thread safe
	 *
	 * @return A list of MCItems provided by this generatable element
	 */
	List<MCItem> providedMCItems();

	/**
	 * This method determines what icon should be used for a custom MCItem, according to its eventual suffix
	 *
	 * @param workspace The current workspace
	 * @param suffix    The suffix of the given MCItem (for example, "bucket" for fluid buckets).
	 *                  If this MCItem has no suffix, this will be the empty string.
	 * @return An ImageIcon to use for the given MCItem, or null to use the element preview image.
	 */
	default ImageIcon getIconForMCItem(Workspace workspace, String suffix) {
		return null;
	}
}