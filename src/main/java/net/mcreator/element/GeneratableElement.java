/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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
import net.mcreator.Launcher;
import net.mcreator.element.converter.ConverterRegistry;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.procedure.RetvalProcedure;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.ui.minecraft.states.StateMap;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GeneratableElement {

	public static final int formatVersion = 48;

	private static final Logger LOG = LogManager.getLogger("Generatable Element");

	private transient ModElement element;

	private transient boolean conversionApplied = false;

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

	public boolean wasConversionApplied() {
		return conversionApplied;
	}

	public final boolean performQuickValidation() {
		for (Field field : getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(Nonnull.class)) {
				field.setAccessible(true);
				try {
					if (field.get(this) == null) {
						LOG.warn("Field " + field.getName() + " of mod element " + this.element.getName()
								+ " is null, but should not be. Assuming invalid generatable element.");
						return false;
					}
				} catch (IllegalAccessException ignored) {
				}
			}
		}

		return true;
	}

	public boolean isUnknown() {
		return false;
	}

	public static class GSONAdapter
			implements JsonSerializer<GeneratableElement>, JsonDeserializer<GeneratableElement> {

		private static final Gson gson;

		static {
			GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().setLenient();

			RetvalProcedure.GSON_ADAPTERS.forEach(gsonBuilder::registerTypeAdapter);

			gsonBuilder.registerTypeAdapter(StateMap.class, new StateMap.GSONAdapter());

			gson = gsonBuilder.create();
		}

		@Nonnull private final Workspace workspace;

		public GSONAdapter(@Nonnull Workspace workspace) {
			this.workspace = workspace;
		}

		@Override
		public GeneratableElement deserialize(JsonElement jsonElement, Type type,
				JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			ModElement lastModElement = workspace.getModElementManager().getLastElementInConversion();

			String newType = switch (jsonElement.getAsJsonObject().get("_type").getAsString()) {
				case "gun" -> "rangeditem";
				case "mob" -> "livingentity";
				default -> jsonElement.getAsJsonObject().get("_type").getAsString();
			};

			int importedFormatVersion = jsonDeserializationContext.deserialize(jsonElement.getAsJsonObject().get("_fv"),
					Integer.class);

			// If GE was stored with newer FV, we can not deserialize it (we still allow this on development builds for testing purposes)
			if (importedFormatVersion > formatVersion) {
				if (Launcher.version.isDevelopment()) {
					LOG.info("Mod element " + lastModElement.getName() + " was saved in FV " + importedFormatVersion
							+ " but current FV is " + GeneratableElement.formatVersion + ". Things may not work well");
				} else {
					LOG.warn("Mod element " + lastModElement.getName() + " was saved in FV " + importedFormatVersion
							+ " but current FV is " + GeneratableElement.formatVersion
							+ " so we can not deserialize it");
					return null;
				}
			}

			try {
				ModElementType<?> modElementType = ModElementTypeLoader.getModElementType(newType);

				JsonObject jsonObject = jsonElement.getAsJsonObject().get("definition").getAsJsonObject();
				if (jsonObject.keySet().isEmpty()) {
					LOG.warn("Mod element " + lastModElement.getName() + " (" + modElementType
							+ ") has no definition so we can not deserialize it");
					return null;
				}

				final GeneratableElement[] generatableElement = {
						gson.fromJson(jsonObject, modElementType.getModElementStorageClass()) };

				generatableElement[0].setModElement(lastModElement); // set the mod element reference
				passWorkspaceToFields(generatableElement[0], workspace);

				List<IConverter> converters = ConverterRegistry.getConvertersForModElementType(modElementType);
				if (converters != null) {
					AtomicInteger versionIncrementer = new AtomicInteger(importedFormatVersion);
					converters.stream().filter(converter -> importedFormatVersion < converter.getVersionConvertingTo())
							.sorted().forEach(converter -> {
								LOG.debug("Converting " + lastModElement.getName() + " (" + modElementType + ") from FV"
										+ versionIncrementer.get() + " to FV" + converter.getVersionConvertingTo() + " using "
										+ converter.getClass().getSimpleName());
								generatableElement[0] = converter.convert(this.workspace, generatableElement[0], jsonElement);
								generatableElement[0].conversionApplied = true;
								versionIncrementer.set(converter.getVersionConvertingTo());
							});
				}

				return generatableElement[0];
			} catch (IllegalArgumentException e) { // we may be dealing with mod element type no longer existing
				IConverter converter = ConverterRegistry.getConverterForModElementType(newType);
				if (converter != null && importedFormatVersion < converter.getVersionConvertingTo()) {
					try {
						GeneratableElement result = converter.convert(this.workspace, new Unknown(lastModElement),
								jsonElement);
						if (result != null) {
							workspace.removeModElement(lastModElement);

							result.getModElement()
									.setParentFolder(FolderElement.dummyFromPath(lastModElement.getFolderPath()));
							workspace.getModElementManager().storeModElementPicture(result);
							workspace.addModElement(result.getModElement());
							workspace.getGenerator().generateElement(result);
							workspace.getModElementManager().storeModElement(result);

							LOG.debug("Converted mod element " + lastModElement.getName() + " (" + newType + ") to "
									+ result.getModElement().getType().getRegistryName() + " using "
									+ converter.getClass().getSimpleName());
						} else {
							LOG.debug("Converted mod element " + lastModElement.getName() + " (" + newType
									+ ") to data format that is not a mod element using " + converter.getClass()
									.getSimpleName());
						}
					} catch (Exception e2) {
						LOG.warn("Failed to convert mod element " + lastModElement.getName() + " of type " + newType
								+ " to a potential alternative.", e2);
					}
				}

				return null;
			} catch (Exception e) {
				LOG.warn("Failed to deserialize mod element " + lastModElement.getName(), e);
				return null;
			}
		}

		@Override
		public JsonElement serialize(GeneratableElement modElement, Type type,
				JsonSerializationContext jsonSerializationContext) {
			JsonObject root = new JsonObject();
			root.add("_fv", new JsonPrimitive(GeneratableElement.formatVersion));
			root.add("_type", gson.toJsonTree(modElement.getModElement().getType().getRegistryName()));

			JsonObject definition = gson.toJsonTree(modElement).getAsJsonObject();

			if (definition.keySet().isEmpty()) {
				LOG.warn("Mod element " + modElement.getModElement().getName() + " (" + modElement.getModElement()
						.getType() + ") has no definition so we can't serialize it");
				return null;
			}

			root.add("definition", definition);

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
					LOG.warn("Failed to pass workspace to field " + field.getName() + " of object " + object.getClass()
							.getSimpleName());
				}
			}
		}

	}

	public static final class Unknown extends GeneratableElement {

		public Unknown(ModElement element) {
			super(element);
		}

		@Override
		public boolean isUnknown() {
			return true;
		}
	}

}
