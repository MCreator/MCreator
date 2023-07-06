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

import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;

public abstract class MappableElement {

	private static final Logger LOG = LogManager.getLogger("Mappable Element");

	protected String value;

	public transient NameMapper mapper;

	public MappableElement(NameMapper mapper) {
		this.mapper = mapper;
	}

	@Override public String toString() {
		return getMappedValue();
	}

	public String getMappedValue() {
		try {
			return mapper.getMapping(value);
		} catch (Exception e) {
			LOG.fatal("Failed to map value to the mappable element. Value: " + value + ", mapper: "
					+ mapper.mappingSource, e);
			return value;
		}
	}

	public void setValue(String value) {
		this.value = value;
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
		Map<String, DataListEntry> dataListEntryMap = DataListLoader.loadDataMap(mapper.mappingSource);
		if (dataListEntryMap != null) {
			if (dataListEntryMap.containsKey(getUnmappedValue())) {
				return Optional.of(dataListEntryMap.get(getUnmappedValue()));
			}
		}

		return Optional.empty();
	}

	@Override public int hashCode() {
		return value.hashCode();
	}

	@Override public boolean equals(Object element) {
		return element instanceof MappableElement && value.equals(((MappableElement) element).value);
	}

	public static class Unique extends MappableElement {

		public Unique(MappableElement original) {
			super(original.mapper);
			this.value = original.value;
		}

		@Override public int hashCode() {
			return getMappedValue().hashCode();
		}

		@Override public boolean equals(Object element) {
			return element instanceof MappableElement && getMappedValue().equals(
					((MappableElement) element).getMappedValue());
		}

	}

}
