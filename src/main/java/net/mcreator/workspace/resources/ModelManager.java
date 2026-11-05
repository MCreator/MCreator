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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caches workspace model data that is expensive to load (e.g. texture mappings).
 * Code paths that modify model files in the workspace need to call {@link #invalidateCache()} or {@link #invalidateCache(File)}
 * to ensure that the cache is updated on the next request.
 */
public final class ModelManager {

	private final Map<File, Map<String, TexturedModel.TextureMapping>> textureMappingsCache = new ConcurrentHashMap<>();

	@Nonnull private final Workspace workspace;

	public ModelManager(@Nonnull Workspace workspace) {
		this.workspace = workspace;
	}

	/**
	 * Returns texture mappings of the given model, loading them from the texture mappings file on the first request.
	 * The returned map is shared and must not be modified.
	 *
	 * @param model Model to get texture mappings for
	 * @return Texture mappings keyed by mapping name or null if the model has no texture mappings
	 */
	@Nullable public Map<String, TexturedModel.TextureMapping> getTextureMappingsForModel(Model model) {
		File modelFile = model.getFile();
		if (modelFile == null)
			return null;

		Map<String, TexturedModel.TextureMapping> mappings = textureMappingsCache.computeIfAbsent(modelFile,
				_ -> TexturedModel.loadTextureMappingsForModel(workspace, model));
		return mappings == null ? null : Collections.unmodifiableMap(mappings);
	}

	/**
	 * Invalidates cached data of the given model so it is reloaded from the workspace on the next request
	 *
	 * @param modelFile Model file as returned by {@link Model#getFile()}
	 */
	public void invalidateCache(@Nullable File modelFile) {
		if (modelFile != null)
			textureMappingsCache.remove(modelFile);
	}

	/**
	 * Invalidates the model cache so model data is reloaded from the workspace on the next request
	 */
	public void invalidateCache() {
		textureMappingsCache.clear();
	}

}
