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
import net.mcreator.element.parts.procedure.RetvalProcedure;
import net.mcreator.element.types.CustomElement;
import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.io.FileIO;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ModElementManager is not thread safe
 */
public final class ModElementManager {

	private static final Logger LOG = LogManager.getLogger("ModElementManager");

	private final Gson gson;
	private final GeneratableElement.GSONAdapter gsonAdapter;

	private final Map<ModElement, GeneratableElement> cache = new ConcurrentHashMap<>();

	@Nonnull private final Workspace workspace;

	@Nullable private ModElement modElementInConversion = null;

	public ModElementManager(@Nonnull Workspace workspace) {
		this.workspace = workspace;

		this.gsonAdapter = new GeneratableElement.GSONAdapter(this.workspace);

		GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(GeneratableElement.class,
				this.gsonAdapter).disableHtmlEscaping().setPrettyPrinting().setLenient();

		RetvalProcedure.GSON_ADAPTERS.forEach(gsonBuilder::registerTypeAdapter);

		this.gson = gsonBuilder.create();
	}

	/**
	 * Also stores to cache. Is thread safe.
	 *
	 * @param element GeneratableElement to convert to store
	 */
	public void storeModElement(GeneratableElement element) {
		if (element == null) {
			LOG.warn("Attempted to store null generatable element. Something went wrong previously for this to happen!");
			return;
		}

		cache.put(element.getModElement(), element);

		FileIO.writeStringToFile(generatableElementToJSON(element),
				new File(workspace.getFolderManager().getModElementsDir(),
						element.getModElement().getName() + ".mod.json"));
	}

	public void removeModElement(ModElement element) {
		cache.remove(element);

		// first we ask generator to remove all related files
		if (element.getType() != ModElementType.UNKNOWN) {
			GeneratableElement generatableElement = element.getGeneratableElement();
			if (generatableElement != null && workspace.getGenerator() != null)
				workspace.getGenerator().removeElementFilesAndLangKeys(generatableElement);
			else
				LOG.warn("Failed to remove element files for element " + element);
		}

		// after we don't need the definition anymore, remove actual files
		new File(workspace.getFolderManager().getModElementsDir(), element.getName() + ".mod.json").delete();
		new File(workspace.getFolderManager().getModElementPicturesCacheDir(), element.getName() + ".png").delete();
	}

	/**
	 * Mod element passed here will be used to prevent circular reference when converting the generatable element.
	 * Make sure to call this method again with null argument after the conversion is done or this ME will not be
	 * loadable anymore in the current session.
	 *
	 * @param modElementInConversion ME being converted or null if conversion is complete
	 */
	public void setModElementInConversion(@Nullable ModElement modElementInConversion) {
		this.modElementInConversion = modElementInConversion;
	}

	GeneratableElement loadGeneratableElement(ModElement element) {
		// To prevent circular reference (and thus stack overflow), we return Unknown GE if we are loading the
		// mod element that is being converted as otherwise this will try to start the conversion again
		if (element.equals(modElementInConversion))
			return new GeneratableElement.Unknown(element);

		if (element.getType() == ModElementType.CODE) {
			return new CustomElement(element);
		}

		if (cache.containsKey(element))
			return cache.get(element);

		File genFile = new File(workspace.getFolderManager().getModElementsDir(), element.getName() + ".mod.json");

		if (!genFile.isFile())
			return null;

		String importJSON = FileIO.readFileToString(genFile);

		GeneratableElement generatableElement = fromJSONtoGeneratableElement(importJSON, element);
		if (generatableElement != null && element.getType() != ModElementType.UNKNOWN) {
			// Make sure after conversion and importing, no Nonnull fields are null to prevent NPEs and check GE integrity
			if (!generatableElement.performQuickValidation())
				return null;

			// Store the mod element in case the conversion was applied
			if (generatableElement.wasConversionApplied())
				storeModElement(generatableElement);

			// Add it to the cache
			cache.put(element, generatableElement);
		}

		return generatableElement;
	}

	public String generatableElementToJSON(GeneratableElement element) {
		return gson.toJson(element);
	}

	public GeneratableElement fromJSONtoGeneratableElement(String json, ModElement modElement) {
		try {
			this.gsonAdapter.setLastModElement(modElement);
			return gson.fromJson(json, GeneratableElement.class);
		} catch (JsonSyntaxException e) {
			LOG.warn("Failed to load generatable element " + modElement.getName()
					+ " from JSON. This can lead to errors further down the road!", e);
			return null;
		}
	}

	public boolean requiresElementGradleBuild(GeneratableElement generatableElement) {
		List<GeneratorTemplate> templates = new ArrayList<>(workspace.getGenerator()
				.getGlobalTemplatesListForModElementType(generatableElement.getModElement().getType(), false,
						new AtomicInteger()));

		templates.addAll(workspace.getGenerator().getModElementGeneratorTemplatesList(generatableElement));

		for (GeneratorTemplate template : templates) {
			String writer = (String) template.getTemplateDefinition().get("writer");
			if (writer == null || writer.equals("java"))
				return true;
		}

		return false;
	}

	/**
	 * This method should be called after the mod element is generated by Generator
	 * So potential metadata fields of ModElement are properly loaded.
	 *
	 * @param element GeneratableElement to generate the picture for
	 */
	public void storeModElementPicture(GeneratableElement element) {
		try {
			BufferedImage modImage = element.generateModElementPicture();
			if (modImage != null)
				FileIO.writeImageToPNGFile(modImage,
						new File(workspace.getFolderManager().getModElementPicturesCacheDir(),
								element.getModElement().getName() + ".png"));
		} catch (Exception e1) {
			LOG.warn("Failed to generate mod element picture for " + element.getModElement().getName(), e1);
		}
	}

	public static ImageIcon getModElementIcon(ModElement element) {
		ImageIcon icon = element.getElementIcon();
		if (icon == null || icon.getImage() == null || icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0)
			icon = element.getType().getIcon();
		return icon;
	}

	/**
	 * Invalidates the cache of this manager. May be used by some plugins.
	 */
	@SuppressWarnings("unused") public void invalidateCache() {
		cache.clear();
	}

}
