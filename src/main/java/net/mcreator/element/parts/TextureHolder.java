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

package net.mcreator.element.parts;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.resources.Texture;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Type;

@JsonAdapter(TextureHolder.GSONAdapter.class) public class TextureHolder implements IWorkspaceDependent {

	@Nullable private final String texture;

	private transient Workspace workspace;

	// Private constructor to satisfy final fields
	private TextureHolder(@Nullable String texture) {
		this.texture = FilenameUtils.removeExtension(texture);
	}

	public TextureHolder(Workspace workspace, @Nullable Texture textureObj) {
		this(textureObj != null ? textureObj.getTextureName() : null);
		this.workspace = workspace;
	}

	/**
	 * A way to "convert" a texture string to a TextureHolder object.
	 * <p/>
	 * We recommend omitting .png extension, but if provided, it will be removed.
	 *
	 * @param workspace Workspace the texture belongs to
	 * @param texture   Texture name
	 */
	public TextureHolder(Workspace workspace, @Nullable String texture) {
		this(texture);
		this.workspace = workspace;
	}

	public Texture toTexture(TextureType textureType) {
		return Texture.fromName(workspace, textureType, texture);
	}

	public ImageIcon getImageIcon(TextureType textureType) {
		Texture texture = toTexture(textureType);
		if (texture == null)
			return new EmptyIcon.ImageIcon(16, 16);
		return texture.getTextureIcon(workspace);
	}

	public Image getImage(TextureType textureType) {
		return this.getImageIcon(textureType).getImage();
	}

	public boolean isEmpty() {
		return texture == null || texture.isBlank();
	}

	public String namespace() {
		if (texture == null)
			return "";

		if (texture.contains(":"))
			return texture.split(":")[0];
		return workspace.getWorkspaceSettings().getModID();
	}

	public String name() {
		if (texture == null)
			return "";

		if (texture.contains(":"))
			return texture.split(":")[1];
		return texture;
	}

	public String getFullTextureName() {
		return texture == null ? "" : texture;
	}

	public String format(String pattern) {
		return pattern.formatted(namespace(), name());
	}

	@Override public String toString() {
		return getFullTextureName();
	}

	@Override public void setWorkspace(@Nullable Workspace workspace) {
		this.workspace = workspace;
	}

	@Nullable @Override public Workspace getWorkspace() {
		return workspace;
	}

	public static class GSONAdapter implements JsonSerializer<TextureHolder>, JsonDeserializer<TextureHolder> {

		@Override public TextureHolder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			try {
				// remove extension call for legacy reasons if previously .png was stored as part of texture name
				return new TextureHolder(FilenameUtils.removeExtension(json.getAsString()));
			} catch (Exception e) {
				return new TextureHolder(null);
			}
		}

		@Override
		public JsonElement serialize(TextureHolder textureHolder, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(
					textureHolder.texture == null ? "" : FilenameUtils.removeExtension(textureHolder.texture));
		}
	}

}
