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
import net.mcreator.element.converter.ConverterUtils;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.IWorkspaceDependent;
import net.mcreator.element.parts.procedure.RetvalProcedure;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.ui.minecraft.states.StateMap;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public abstract class GeneratableElement {

	public static final int formatVersion = 60;

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

			final String modElementTypeString = switch (jsonElement.getAsJsonObject().get("_type").getAsString()) {
				case "gun" -> "rangeditem";
				case "mob" -> "livingentity";
				default -> jsonElement.getAsJsonObject().get("_type").getAsString();
			};
			final int importedFormatVersion = jsonDeserializationContext.deserialize(
					jsonElement.getAsJsonObject().get("_fv"), Integer.class);

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
				ModElementType<?> modElementType = ModElementTypeLoader.getModElementType(modElementTypeString);

				JsonObject jsonObject = jsonElement.getAsJsonObject().get("definition").getAsJsonObject();
				if (jsonObject.keySet().isEmpty()) {
					LOG.warn("Mod element " + lastModElement.getName() + " (" + modElementType
							+ ") has no definition so we can not deserialize it");
					return null;
				}

				GeneratableElement generatableElement = gson.fromJson(jsonObject,
						modElementType.getModElementStorageClass());
				generatableElement.setModElement(lastModElement); // set the mod element reference

				// Populate workspace-dependant fields with workspace reference
				IWorkspaceDependent.processWorkspaceDependentObjects(generatableElement,
						workspaceDependent -> workspaceDependent.setWorkspace(workspace));

				List<IConverter> converters = ConverterRegistry.getConvertersForModElementType(modElementType);
				if (converters != null) {
					List<IConverter> applicableConverters = converters.stream()
							.filter(converter -> importedFormatVersion < converter.getVersionConvertingTo()).sorted()
							.toList();
					int currentFormatVersion = importedFormatVersion;
					for (IConverter converter : applicableConverters) {
						LOG.debug("Converting " + lastModElement.getName() + " (" + modElementType + ") from FV"
								+ currentFormatVersion + " to FV" + converter.getVersionConvertingTo() + " using "
								+ converter.getClass().getSimpleName());
						generatableElement = converter.convert(this.workspace, generatableElement, jsonElement);

						if (generatableElement == null
								|| generatableElement.getClass() != modElementType.getModElementStorageClass()) {
							ConverterUtils.convertElementToDifferentType(converter, lastModElement, generatableElement);
							return null;
						} else {
							generatableElement.conversionApplied = true;
							currentFormatVersion = converter.getVersionConvertingTo();
						}
					}
				}

				return generatableElement;
			} catch (IllegalArgumentException e) { // we may be dealing with mod element type no longer existing
				IConverter converter = ConverterRegistry.getConverterForModElementType(modElementTypeString);
				if (converter != null && importedFormatVersion < converter.getVersionConvertingTo()) {
					try {
						GeneratableElement result = converter.convert(this.workspace, new Unknown(lastModElement),
								jsonElement);
						ConverterUtils.convertElementToDifferentType(converter, lastModElement, result);
					} catch (Exception e2) {
						LOG.warn("Failed to convert mod element " + lastModElement.getName() + " of type "
								+ modElementTypeString + " to a potential alternative.", e2);
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

	}

	public static final class Unknown extends GeneratableElement {

		public Unknown(ModElement element) {
			super(element);
		}

		@Override public boolean isUnknown() {
			return true;
		}
	}

}
