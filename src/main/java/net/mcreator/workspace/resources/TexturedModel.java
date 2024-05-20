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
import net.mcreator.ui.init.L10N;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TexturedModel extends Model {

	private static final Logger LOG = LogManager.getLogger(TexturedModel.class);

	private static final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

	private TextureMapping textureMapping;

	protected TexturedModel(File file, TextureMapping textureMapping) throws ModelException {
		super(file);
		this.textureMapping = textureMapping;
	}

	protected TexturedModel(File file, String textureMappingName) throws ModelException {
		super(file);
		Map<String, TextureMapping> textureMappingMap = getTextureMappingsForModel(this);
		if (textureMappingMap != null) {
			this.textureMapping = textureMappingMap.get(textureMappingName);
			if (this.textureMapping == null) // try to fall back to default if desired mapping is not found
				this.textureMapping = textureMappingMap.get("default");
		}
	}

	public TextureMapping getTextureMapping() {
		return textureMapping;
	}

	public static List<Model> getModelTextureMapVariations(Model m) {
		List<Model> variations = new ArrayList<>();
		if (m.getFiles() != null && m.getFile() != null) {
			Map<String, TextureMapping> textureMappingMap = getTextureMappingsForModel(m);
			if (textureMappingMap != null) {
				// we add all variations of texture mappings for model
				for (TextureMapping mapping : textureMappingMap.values()) {
					try {
						variations.add(new TexturedModel(m.getFile(), mapping));
					} catch (ModelException ignored) {
					}
				}
			} else {
				variations.add(m);
			}
		}
		return variations;
	}

	public static Map<String, TextureMapping> getTextureMappingsForModel(Model model) {
		try {
			if (model.type == Type.JSON && model.getFiles().length == 2) {
				TextureMappings mappings = gson.fromJson(FileIO.readFileToString(model.getFiles()[1]),
						TextureMappings.class);
				return mappings.mappings;
			} else if (model.type == Type.OBJ && model.getFiles().length == 3) {
				TextureMappings mappings = gson.fromJson(FileIO.readFileToString(model.getFiles()[2]),
						TextureMappings.class);
				return mappings.mappings;
			}
		} catch (Exception e) {
			LOG.warn("Failed to load texture mappings for model: {}", model.getReadableName(), e);
		}
		return null;
	}

	public static String getJSONForTextureMapping(Map<String, TextureMapping> textureMappingMap) {
		return gson.toJson(new TextureMappings(textureMappingMap));
	}

	@Override public String toString() {
		return L10N.t("workspace.3dmodel.description", readableName, textureMapping.name);
	}

	@Override @Nonnull public String getReadableName() {
		if (textureMapping != null)
			return readableName + ":" + textureMapping.name;
		else
			return super.getReadableName();
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

	private record TextureMappings(Map<String, TextureMapping> mappings) {}

	public static final class TextureMapping {

		// key: texture id, value: texture resource location
		private final Map<String, String> map;
		private final String name;

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
