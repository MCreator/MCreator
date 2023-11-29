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

import net.mcreator.element.parts.IWorkspaceDependent;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public abstract class MappableElement implements IWorkspaceDependent {

	private static final Logger LOG = LogManager.getLogger("Mappable Element");

	private final String value;

	protected transient final NameMapper mapper;

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
		try {
			return mapper.getMapping(value);
		} catch (Exception e) {
			LOG.fatal("Failed to map value to the mappable element. Value: " + value + ", mapper: "
					+ mapper.getMappingSource(), e);
			return value;
		}
	}

	public String getUnmappedValue() {
		return value;
	}

	public boolean canProperlyMap() {
		String mapped = mapper.getMapping(value);
		return !mapped.contains("@") && !mapped.contains(
				NameMapper.UNKNOWN_ELEMENT); // if there are still @tokens, we failed to map some values
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

}
