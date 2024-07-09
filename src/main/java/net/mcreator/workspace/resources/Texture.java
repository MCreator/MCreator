/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.workspace.resources;

import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nullable;
import javax.swing.*;

public abstract class Texture {

	protected final TextureType textureType;

	protected final String textureName;

	Texture(TextureType textureType, String textureName) {
		this.textureType = textureType;
		this.textureName = textureName;
	}

	public String getTextureName() {
		return textureName;
	}

	@Override public final boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Texture texture))
			return false;
		return textureType == texture.textureType && textureName.equals(texture.textureName);
	}

	@Override public int hashCode() {
		int result = textureType.hashCode();
		result = 31 * result + textureName.hashCode();
		return result;
	}

	public abstract ImageIcon getTextureIcon(Workspace workspace);

	@Nullable public static Texture fromName(Workspace workspace, TextureType textureType, String name) {
		if (name == null || name.isBlank())
			return null;

		if (name.indexOf(':') == -1)
			return new CustomTexture(textureType, workspace.getFolderManager().getTextureFile(name, textureType));
		else
			return ExternalTexture.getTexture(workspace, textureType, name);
	}

	public static final class Dummy extends Texture {

		public Dummy(TextureType textureType, String textureName) {
			super(textureType, textureName);
		}

		@Override public ImageIcon getTextureIcon(Workspace workspace) {
			return null;
		}

	}

}
