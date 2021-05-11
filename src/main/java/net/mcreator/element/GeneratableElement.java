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

package net.mcreator.element;

import com.google.gson.*;
import net.mcreator.element.converter.ConverterRegistry;
import net.mcreator.element.converter.IConverter;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

public abstract class GeneratableElement {

	private static final Logger LOG = LogManager.getLogger("Generatable Element");

	private transient ModElement element;

	public static final transient int formatVersion = 19;

	public GeneratableElement(ModElement element) {
		if (element != null)
			setModElement(element);
	}

	public ModElement getModElement() {
		return element;
	}

	public void setModElement(ModElement element) {
		this.element = element;
	}

	/**
	 * @return BufferedImage of mod element preview or null if default mod element icon should be used
	 */
	public BufferedImage generateModElementPicture() {
		return null;
	}

	/**
	 * This method should take care of generating additional mod
	 * element resources for cases such as GUI mod element
	 */
	public void finalizeModElementGeneration() {
	}

	/**
	 * Override this to add additional data to the element data model
	 *
	 * @return null if no additional data, or IAdditionalTemplateDataProvider implementation
	 */
	public @Nullable IAdditionalTemplateDataProvider getAdditionalTemplateData() {
		return null;
	}

	public static class GSONAdapter
			implements JsonSerializer<GeneratableElement>, JsonDeserializer<GeneratableElement> {

		protected static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().setLenient()
				.create();

		@NotNull private final Workspace workspace;

		private ModElement lastModElement;

		public GSONAdapter(@NotNull Workspace workspace) {
			this.workspace = workspace;
		}

		public void setLastModElement(ModElement lastModElement) {
			this.lastModElement = lastModElement;
		}

		@Override
		public GeneratableElement deserialize(JsonElement jsonElement, Type type,
				JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			ModElementType modElementType = jsonDeserializationContext
					.deserialize(jsonElement.getAsJsonObject().get("_type"), ModElementType.class);
			int importedFormatVersion = jsonDeserializationContext
					.deserialize(jsonElement.getAsJsonObject().get("_fv"), Integer.class);

			final GeneratableElement[] generatableElement = {
					gson.fromJson(jsonElement.getAsJsonObject().get("definition"),
							ModElementTypeRegistry.REGISTRY.get(modElementType).getModElementStorageClass()) };

			generatableElement[0].setModElement(this.lastModElement); // set the mod element reference
			passWorkspaceToFields(generatableElement[0], workspace);

			if (importedFormatVersion != GeneratableElement.formatVersion) {
				List<IConverter> converters = ConverterRegistry.getConvertersForModElementType(modElementType);
				if (converters != null) {
					converters.stream().filter(converter -> importedFormatVersion < converter.getVersionConvertingTo())
							.sorted().forEach(converter -> {
						LOG.debug("Converting mod element " + this.lastModElement.getName() + " (" + modElementType
								+ ") from FV" + importedFormatVersion + " to FV" + converter.getVersionConvertingTo());
						generatableElement[0] = converter.convert(this.workspace, generatableElement[0], jsonElement);
					});
				}
			}

			return generatableElement[0];
		}

		@Override
		public JsonElement serialize(GeneratableElement modElement, Type type,
				JsonSerializationContext jsonSerializationContext) {
			JsonObject root = new JsonObject();
			root.add("_fv", new JsonPrimitive(GeneratableElement.formatVersion));
			root.add("_type", gson.toJsonTree(modElement.getModElement().getType()));
			root.add("definition", gson.toJsonTree(modElement));
			return root;
		}

		private void passWorkspaceToFields(Object object, Workspace workspace) {
			for (Field field : object.getClass().getDeclaredFields()) {
				field.setAccessible(true);

				if (Modifier.isTransient(field.getModifiers()))
					continue;

				try {
					Object subobject = field.get(object);
					if (subobject instanceof MappableElement) {
						((MappableElement) subobject).mapper.setWorkspace(workspace);
					} else if (subobject instanceof Object[]) {
						for (Object element : ((Object[]) subobject)) {
							if (element instanceof MappableElement) {
								((MappableElement) element).mapper.setWorkspace(workspace);
							} else {
								if (element != null && element.getClass().getPackage().getName()
										.startsWith("net.mcreator"))
									passWorkspaceToFields(element, workspace);
							}
						}
					} else if (subobject instanceof Iterable) {
						for (Object element : ((Iterable<?>) subobject)) {
							if (element instanceof MappableElement) {
								((MappableElement) element).mapper.setWorkspace(workspace);
							} else {
								if (element != null && element.getClass().getPackage().getName()
										.startsWith("net.mcreator"))
									passWorkspaceToFields(element, workspace);
							}
						}
					} else {
						if (subobject != null && subobject.getClass().getPackage().getName().startsWith("net.mcreator"))
							passWorkspaceToFields(subobject, workspace);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
