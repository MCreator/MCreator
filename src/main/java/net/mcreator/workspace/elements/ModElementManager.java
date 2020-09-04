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

package net.mcreator.workspace.elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.types.CustomElement;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.io.FileIO;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ModElementManager is not thread safe
 */
public class ModElementManager {

	private static final Logger LOG = LogManager.getLogger("ModElementManager");

	private final Gson gson;
	private final GeneratableElement.GSONAdapter gsonAdapter;

	private final Map<ModElement, GeneratableElement> cache = new ConcurrentHashMap<>();

	@NotNull private final Workspace workspace;

	public ModElementManager(@NotNull Workspace workspace) {
		this.workspace = workspace;

		this.gsonAdapter = new GeneratableElement.GSONAdapter(this.workspace);
		this.gson = new GsonBuilder().registerTypeHierarchyAdapter(GeneratableElement.class, this.gsonAdapter)
				.disableHtmlEscaping().setPrettyPrinting().setLenient().create();
	}

	public void invalidateCache() {
		cache.clear();
	}

	public void storeModElement(GeneratableElement element) {
		cache.put(element.getModElement(), element);

		FileIO.writeStringToFile(generatableElementToJSON(element),
				new File(workspace.getFolderManager().getModElementsDir(),
						element.getModElement().getName() + ".mod.json"));
	}

	GeneratableElement loadGeneratableElement(ModElement element) {
		if (element.getType() == ModElementType.CODE) {
			return new CustomElement(element);
		}

		if (cache.containsKey(element))
			return cache.get(element);

		File genFile = new File(workspace.getFolderManager().getModElementsDir(), element.getName() + ".mod.json");

		if (!genFile.isFile())
			return null;

		String importJSON = FileIO.readFileToString(genFile);

		return fromJSONtoGeneratableElement(importJSON, element);
	}

	public String generatableElementToJSON(GeneratableElement element) {
		return gson.toJson(element);
	}

	public GeneratableElement fromJSONtoGeneratableElement(String json, ModElement modElement) {
		try {
			this.gsonAdapter.setLastModElement(modElement);
			return gson.fromJson(json, GeneratableElement.class);
		} catch (JsonSyntaxException e) {
			LOG.warn("Failed to load generatable element from JSON. This can lead to errors further down the road!", e);
			return null;
		}
	}

	public boolean hasModElementGeneratableElement(ModElement element) {
		if (element == null)
			return false;

		// custom code mod element does not actually have one, but is provided by this manager
		if (element.getType() == ModElementType.CODE)
			return true;

		return new File(workspace.getFolderManager().getModElementsDir(), element.getName() + ".mod.json").isFile();
	}

	public boolean usesGeneratableElementJava(GeneratableElement generatableElement) {
		Generator generator = workspace.getGenerator();
		List<GeneratorTemplate> templates = generator
				.getModElementGeneratorTemplatesList(generatableElement.getModElement());
		if (templates != null)
			for (GeneratorTemplate template : templates) {
				String writer = (String) ((Map<?, ?>) template.getTemplateData()).get("writer");
				if (writer == null || writer.equals("java"))
					return true;
			}

		return false;
	}

	public void storeModElementPicture(GeneratableElement element) {
		try {
			BufferedImage modImage = element.generateModElementPicture();
			if (modImage != null)
				FileIO.writeImageToPNGFile(modImage,
						new File(workspace.getFolderManager().getModElementPicturesCacheDir(),
								element.getModElement().getName() + ".png"));
		} catch (Exception e1) {
			LOG.info("Failed to generate mod element picture for " + element.getModElement().getName());
		}
	}

}
