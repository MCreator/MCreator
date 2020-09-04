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

package net.mcreator.workspace.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcreator.io.FileIO;

import java.io.File;
import java.util.Map;

public class TexturedModel extends Model {

	private static final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

	private TextureMapping textureMapping;

	public TexturedModel(File file, TextureMapping textureMapping) {
		super(file);
		this.textureMapping = textureMapping;
	}

	public TexturedModel(File file, String textureMappingName) {
		super(file);
		Map<String, TextureMapping> textureMappingMap = getTextureMappingsForModel(this);
		if (textureMappingMap != null) {
			this.textureMapping = textureMappingMap.get(textureMappingName);
			if (this.textureMapping == null) // try to fallback to default if desired mapping is not found
				this.textureMapping = textureMappingMap.get("default");
		}
	}

	public TextureMapping getTextureMapping() {
		return textureMapping;
	}

	public static Map<String, TextureMapping> getTextureMappingsForModel(Model model) {
		try {
			if (model.type == Type.JSON && model.getFiles().length == 2) {
				TextureMappings mappings = gson
						.fromJson(FileIO.readFileToString(model.getFiles()[1]), TextureMappings.class);
				return mappings.mappings;
			} else if (model.type == Type.OBJ && model.getFiles().length == 3) {
				TextureMappings mappings = gson
						.fromJson(FileIO.readFileToString(model.getFiles()[2]), TextureMappings.class);
				return mappings.mappings;
			}
		} catch (Exception ignored) {
		}
		return null;
	}

	public static String getJSONForTextureMapping(Map<String, TextureMapping> textureMappingMap) {
		return gson.toJson(new TextureMappings(textureMappingMap));
	}

	@Override public String toString() {
		return "<html>" + readableName + "<br><small>Texture mapping: " + textureMapping.name;
	}

	@Override public String getReadableName() {
		if (textureMapping != null)
			return readableName + ":" + textureMapping.name;
		else
			return null;
	}

	@Override public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof TexturedModel)
			return ((Model) obj).getReadableName().equals(this.getReadableName()) && ((Model) obj).type == this.type
					&& ((TexturedModel) obj).textureMapping.name.equals(this.textureMapping.name);
		return false;
	}

	@Override public int hashCode() {
		return (this.getReadableName() + this.type.name() + this.textureMapping.name).hashCode();
	}

	private static class TextureMappings {
		Map<String, TextureMapping> mappings;

		TextureMappings(Map<String, TextureMapping> mappings) {
			this.mappings = mappings;
		}
	}

	public static class TextureMapping {

		// key: texture id, value: texture resource location
		private Map<String, String> map;
		private String name;

		public TextureMapping(String name, Map<String, String> textureMap) {
			this.map = textureMap;
			this.name = name;
		}

		public Map<String, String> getTextureMap() {
			return map;
		}

		@Override public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (obj instanceof TextureMapping)
				return ((TextureMapping) obj).name.equals(name);
			return false;
		}

		@Override public int hashCode() {
			return name.hashCode();
		}

	}

}
