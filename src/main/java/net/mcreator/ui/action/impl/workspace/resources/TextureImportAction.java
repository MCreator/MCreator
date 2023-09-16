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

package net.mcreator.ui.action.impl.workspace.resources;

import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.workspace.resources.TextureType;

import javax.annotation.Nullable;

public class TextureImportAction extends TextureAction {

	public TextureImportAction(ActionRegistry actionRegistry, String name, @Nullable TextureType textureType) {
		super(actionRegistry, name,
				e -> TextureImportDialogs.importMultipleTextures(actionRegistry.getMCreator(), textureType),
				textureType);
	}

}
