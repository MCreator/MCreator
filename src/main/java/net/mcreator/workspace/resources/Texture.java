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
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

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

		return null;
	}

	@Nonnull public static ImageIcon getImageIcon(Workspace workspace, TextureType textureType, String name) {
		Texture texture = fromName(workspace, textureType, name);
		if (texture == null)
			return new EmptyIcon.ImageIcon(16,16);

		return texture.getTextureIcon(workspace);
	}

	@Nonnull public static Image getImage(Workspace workspace, TextureType textureType, String name) {
		return getImageIcon(workspace, textureType, name).getImage();
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
