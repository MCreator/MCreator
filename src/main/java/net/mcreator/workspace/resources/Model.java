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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Model {

	private static final Logger LOG = LogManager.getLogger(Model.class);

	@Nullable protected final File[] file;

	@Nonnull protected final String readableName;
	@Nonnull protected final Type type;

	protected Model(@Nonnull File file) throws ModelException {
		if (!file.isFile())
			throw new ModelException("Model file not found: " + file.getAbsolutePath());

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
			} else {
				throw new ModelException("OBJ file without MTL file is not supported");
			}
		} else if (file.getName().endsWith(".json")) {
			File textures = new File(file.getAbsolutePath() + ".textures");
			if (textures.isFile()) {
				this.file = new File[2];
				this.file[0] = file;
				this.file[1] = textures;
				this.type = Type.JSON;
			} else {
				throw new ModelException("JSON file without textures is not supported");
			}
		} else {
			this.file = new File[1];
			this.file[0] = file;
			if (file.getName().endsWith(".java")) {
				this.type = Type.JAVA;
			} else if (file.getName().endsWith(".mcm")) {
				this.type = Type.MCREATOR;
			} else {
				throw new ModelException("Unsupported model type");
			}
		}

		this.readableName = this.file[0].getName().substring(0, this.file[0].getName().lastIndexOf('.'));
	}

	/**
	 * Only for built-in models
	 *
	 * @param readableName name of the model
	 * @param type         type of the model
	 */
	private Model(@Nonnull String readableName, @Nonnull Type type) {
		this.file = null;
		this.readableName = readableName;
		this.type = type;
	}

	public File getFile() {
		return file != null ? file[0] : null;
	}

	public File[] getFiles() {
		return file;
	}

	@Nonnull public String getReadableName() {
		return readableName;
	}

	@Nonnull public Type getType() {
		return type;
	}

	@Override public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Model model)
			return model.getReadableName().equals(getReadableName()) && model.type == type;
		return false;
	}

	@Override public int hashCode() {
		return (getReadableName() + type.name()).hashCode();
	}

	@Override public String toString() {
		return readableName;
	}

	@Nullable public static Model getModelByParams(Workspace workspace, String name, Type type) {
		try {
			String textureMap = null;
			if (name.contains(":")) {
				String[] data = name.split(":");
				name = data[0].trim();
				textureMap = data[1].trim();
			}
			if (type == Type.BUILTIN)
				return new BuiltInModel(name);
			else if (type == Type.JSON)
				return new TexturedModel(new File(workspace.getFolderManager().getModelsDir(), name + ".json"),
						textureMap);
			else if (type == Type.OBJ) {
				Model objModel = new Model(new File(workspace.getFolderManager().getModelsDir(), name + ".obj"));
				if (textureMap != null)
					objModel = new TexturedModel(new File(workspace.getFolderManager().getModelsDir(), name + ".obj"),
							textureMap);
				return objModel;
			} else if (type == Type.JAVA) {
				for (String modelsKey : workspace.getGeneratorConfiguration().getCompatibleJavaModelKeys()) {
					File modelFile;
					if (modelsKey.equals("legacy")) {
						modelFile = new File(workspace.getFolderManager().getModelsDir(), name + ".java");
					} else {
						modelFile = new File(workspace.getFolderManager().getModelsDir(),
								modelsKey + "/" + name + ".java");
					}
					if (modelFile.isFile())
						return new Model(modelFile);
				}
				throw new ModelException("Model not found");
			} else if (type == Type.MCREATOR) {
				return new TexturedModel(new File(workspace.getFolderManager().getModelsDir(), name + ".mcm"),
						textureMap);
			}
		} catch (ModelException e) {
			LOG.warn("Failed to load model: {}for reason: {}", name, e.getMessage());
		}
		return null;
	}

	public static List<Model> getModelsWithTextureMaps(Workspace workspace) {
		List<Model> models = new ArrayList<>();
		File[] candidates = workspace.getFolderManager().getModelsDir().listFiles();
		for (File f : candidates != null ? candidates : new File[0]) {
			try {
				models.addAll(TexturedModel.getModelTextureMapVariations(new Model(f)));
			} catch (ModelException ignored) {
			}
		}
		return models;
	}

	public static List<Model> getModels(Workspace workspace) {
		Set<Model> models = new HashSet<>();

		File[] candidates = workspace.getFolderManager().getModelsDir().listFiles();
		for (File f : candidates != null ? candidates : new File[0]) {
			if (f.isDirectory())
				continue;

			// we load java models in a separate loop below
			if (f.getName().endsWith(".java"))
				continue;

			try {
				models.add(new Model(f));
			} catch (ModelException ignored) {
			}
		}

		models.addAll(getJavaModels(workspace));

		// only return valid models
		return models.stream().filter(model -> model.file != null).collect(Collectors.toList());
	}

	public static List<Model> getJavaModels(Workspace workspace) {
		return getJavaModels(workspace, workspace.getGeneratorConfiguration().getCompatibleJavaModelKeys());
	}

	public static List<Model> getJavaModels(Workspace workspace, List<String> compatibleJavaModelKeys) {
		Set<Model> models = new HashSet<>();

		for (String modelKey : compatibleJavaModelKeys) {
			File[] candidates;
			if (modelKey.equals("legacy")) {
				candidates = workspace.getFolderManager().getModelsDir().listFiles();
			} else {
				candidates = new File(workspace.getFolderManager().getModelsDir(), modelKey).listFiles();
			}

			for (File f : candidates != null ? candidates : new File[0]) {
				if (f.isDirectory())
					continue;

				try {
					models.add(new Model(f));
				} catch (ModelException ignored) {
				}
			}
		}

		// only return valid models
		return models.stream().filter(model -> model.file != null).collect(Collectors.toList());
	}

	public static final class BuiltInModel extends Model {

		public BuiltInModel(String name) {
			super(name, Type.BUILTIN);
		}

	}

	public enum Type {
		JSON, OBJ, JAVA, MCREATOR, BUILTIN
	}

}
