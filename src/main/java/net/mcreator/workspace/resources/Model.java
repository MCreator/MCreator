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

import net.mcreator.workspace.Workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Model {

	File[] file;
	String readableName;
	Type type;

	public Model(File file) {
		if (file == null || !file.isFile() || !file.getName().contains("."))
			return;

		if (file.getName().endsWith(".obj")) {
			File mtl = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('.')) + ".mtl");
			File textures = new File(file.getAbsolutePath() + ".textures");
			if (mtl.isFile() && textures.isFile()) {
				this.file = new File[3];
				this.file[0] = file;
				this.file[1] = mtl;
				this.file[2] = textures;
				this.type = Type.OBJ;
			} else if (mtl.isFile()) {
				this.file = new File[2];
				this.file[0] = file;
				this.file[1] = mtl;
				this.type = Type.OBJ;
			}
		} else if (file.getName().endsWith(".json")) {
			File textures = new File(file.getAbsolutePath() + ".textures");
			if (textures.isFile()) {
				this.file = new File[2];
				this.file[0] = file;
				this.file[1] = textures;
				this.type = Type.JSON;
			}
		} else {
			this.file = new File[1];
			this.file[0] = file;
			if (file.getName().endsWith(".java"))
				this.type = Type.JAVA;
			else if (file.getName().endsWith(".mcm"))
				this.type = Type.MCREATOR;
		}

		if (this.file == null)
			return;

		this.readableName = this.file[0].getName().substring(0, this.file[0].getName().lastIndexOf('.'));
	}

	public File getFile() {
		return file[0];
	}

	public File[] getFiles() {
		return file;
	}

	public String getReadableName() {
		return readableName;
	}

	public Type getType() {
		return type;
	}

	@Override public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Model)
			return ((Model) obj).getReadableName().equals(getReadableName()) && ((Model) obj).type == type;
		return false;
	}

	@Override public int hashCode() {
		return (getReadableName() + type.name()).hashCode();
	}

	@Override public String toString() {
		return readableName;
	}

	public static Model getModelByParams(Workspace workspace, String name, Type type) {
		String textureMap = null;
		if (name.contains(":")) {
			String[] data = name.split(":");
			name = data[0].trim();
			textureMap = data[1].trim();
		}
		if (type == Type.BUILTIN)
			return new BuiltInModel(name);
		else if (type == Type.JSON)
			return new TexturedModel(new File(workspace.getFolderManager().getModelsDir(), name + ".json"), textureMap);
		else if (type == Type.OBJ) {
			Model objModel = new Model(new File(workspace.getFolderManager().getModelsDir(), name + ".obj"));
			if (textureMap != null)
				objModel = new TexturedModel(new File(workspace.getFolderManager().getModelsDir(), name + ".obj"),
						textureMap);
			return objModel;
		} else if (type == Type.JAVA)
			return new Model(new File(workspace.getFolderManager().getModelsDir(), name + ".java"));
		else if (type == Type.MCREATOR)
			return new TexturedModel(new File(workspace.getFolderManager().getModelsDir(), name + ".mcm"), textureMap);
		return null;
	}

	public static List<Model> getModelsWithTextureMaps(Workspace workspace) {
		List<Model> models = new ArrayList<>();
		File[] candidates = workspace.getFolderManager().getModelsDir().listFiles();
		for (File f : candidates != null ? candidates : new File[0]) {
			Model m = new Model(f);
			if (m.getType() != null) {
				Map<String, TexturedModel.TextureMapping> textureMappingMap = TexturedModel
						.getTextureMappingsForModel(m);
				if (textureMappingMap != null) {
					// we add all variations of texturemappings for model
					for (Map.Entry<String, TexturedModel.TextureMapping> entry : textureMappingMap.entrySet()) {
						models.add(new TexturedModel(f, entry.getValue()));
					}
				} else {
					models.add(m);
				}
			}
		}
		return models;
	}

	public static List<Model> getModels(Workspace workspace) {
		List<Model> models = new ArrayList<>();
		File[] candidates = workspace.getFolderManager().getModelsDir().listFiles();
		for (File f : candidates != null ? candidates : new File[0]) {
			Model m = new Model(f);
			if (m.getType() != null)
				models.add(m);
		}
		return models;
	}

	public static class BuiltInModel extends Model {

		public BuiltInModel(String name) {
			super(null);
			this.file = null;
			this.readableName = name;
			this.type = Type.BUILTIN;
		}
	}

	public enum Type {
		JSON, OBJ, JAVA, MCREATOR, BUILTIN
	}

}
