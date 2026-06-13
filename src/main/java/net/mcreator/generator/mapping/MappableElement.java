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

package net.mcreator.generator.mapping;

import com.google.gson.*;
import net.mcreator.element.parts.IWorkspaceDependent;
import net.mcreator.generator.GeneratorWrapper;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.util.TestUtil;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.TagElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public abstract class MappableElement implements IWorkspaceDependent {

	private static final Logger LOG = LogManager.getLogger("Mappable Element");

	private final String value;

	protected transient final NameMapper mapper;

	@Nullable private transient TagElement.Entry associatedTagEntry;

	public MappableElement(NameMapper mapper) {
		this.mapper = mapper;
		this.value = null;
	}

	public MappableElement(NameMapper mapper, String value) {
		this.mapper = mapper;
		this.value = value;
	}

	@Override public String toString() {
		return getMappedValue();
	}

	public boolean isEmpty() {
		return value == null || value.isEmpty();
	}

	public String getMappedValue() {
		return getMappedValue(0);
	}

	public String getMappedValue(int mappingTable) {
		try {
			return mapper.getMapping(value, mappingTable);
		} catch (Exception e) {
			LOG.fatal("Failed to map value to the mappable element. Value: {}, mapper: {}", value,
					mapper.getMappingSource(), e);
			return value;
		}
	}

	public String getUnmappedValue() {
		return value;
	}

	public NameMapper getNameMapper() {
		return mapper;
	}

	public String getMappingSource() {
		return mapper.getMappingSource();
	}

	/**
	 * @return true if the value exists in the workspace. Always returns true for vanilla elements,
	 * even if they are not supported in the selected Minecraft version. Returns false if the element is empty.
	 */
	public boolean isValidReference() {
		if (value == null || value.isEmpty())
			return false;

		Workspace workspace = getWorkspace();
		if (workspace == null) {
			return false;
		}
		return validateReference(value, workspace, mapper.getMappingSource());
	}

	/**
	 * @return true if the value exists in the workspace. Always returns true for vanilla elements,
	 * even if they are not supported in the selected Minecraft version.
	 */
	public static boolean validateReference(@Nonnull String value, @Nonnull Workspace workspace,
			@Nullable String mappingSource) {
		if (value.startsWith(NameMapper.MCREATOR_PREFIX)) {
			boolean retval = workspace.containsModElement(GeneratorWrapper.getElementPlainName(value));
			if (!retval) {
				LOG.warn("Broken reference found. Referencing non-existent element: {}", value);
				TestUtil.failIfTestingEnvironment();
			}
			return retval;
		} else if (mappingSource != null && !value.startsWith(NameMapper.EXTERNAL_PREFIX) && !value.startsWith("#")
				&& !value.startsWith("TAG:") && !TestUtil.isTestingEnvironment()) {
			Map<String, DataListEntry> dataListEntryMap = DataListLoader.loadDataMap(mappingSource);
			if (dataListEntryMap != null) {
				if (!dataListEntryMap.containsKey(value)) {
					LOG.warn("Broken vanilla reference found. Referencing non-existent element: {} from {}", value,
							mappingSource);
					return false;
				}
			}
		}
		return true;
	}

	public Optional<DataListEntry> getDataListEntry() {
		Map<String, DataListEntry> dataListEntryMap = DataListLoader.loadDataMap(mapper.getMappingSource());
		if (dataListEntryMap != null) {
			if (dataListEntryMap.containsKey(getUnmappedValue())) {
				return Optional.of(dataListEntryMap.get(getUnmappedValue()));
			}
		}

		return Optional.empty();
	}

	public String getDataListEntryType() {
		Optional<DataListEntry> dataListEntry = getDataListEntry();
		if (dataListEntry.isPresent()) {
			String type = dataListEntry.get().getType();
			return type != null ? type : "";
		}
		return "";
	}

	/**
	 * Sets the associated tag entry for this element. Used in tag UI
	 *
	 * @param associatedTagEntry the tag entry to be associated with this element, or null if no tag entry is being set
	 */
	public void setAssociatedTagEntry(@Nullable TagElement.Entry associatedTagEntry) {
		this.associatedTagEntry = associatedTagEntry;
	}

	@Nullable public TagElement.Entry getAssociatedTagEntry() {
		return associatedTagEntry;
	}

	/**
	 * Determines if the current element is managed within a tag.
	 *
	 * @return true if the associated tag entry exists and is flagged as managed; false otherwise.
	 */
	public boolean isManaged() {
		return associatedTagEntry != null && associatedTagEntry.isManaged();
	}

	@Override public void setWorkspace(@Nullable Workspace workspace) {
		mapper.setWorkspace(workspace);
	}

	@Nullable @Override public Workspace getWorkspace() {
		return mapper.getWorkspace();
	}

	@Override public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}

	@Override public boolean equals(Object element) {
		return element instanceof MappableElement && (value != null && value.equals(((MappableElement) element).value));
	}

	public static class GSONAdapter implements JsonSerializer<MappableElement>, JsonDeserializer<MappableElement> {

		private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setStrictness(Strictness.LENIENT)
				.create();

		@Override
		public MappableElement deserialize(JsonElement jsonElement, Type type,
				JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonPrimitive()) {
				JsonObject legacyObject = new JsonObject();
				legacyObject.addProperty("value", jsonElement.getAsString());
				return gson.fromJson(legacyObject, type);
			} else if (jsonElement.isJsonObject()) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				if (jsonObject.has("value")) {
					return gson.fromJson(jsonObject, type);
				}
			}
			throw new JsonParseException("Invalid mappable element JSON format");
		}

		@Override
		public JsonElement serialize(MappableElement element, Type type,
				JsonSerializationContext jsonSerializationContext) {
			if (element == null || element.value == null) {
				return JsonNull.INSTANCE;
			}

			return new JsonPrimitive(element.value);
		}

	}

}
