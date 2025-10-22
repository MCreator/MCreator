/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.states.block;

import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class BlockStatePropertyUtils {

	public static final int MAX_PROPERTY_COMBINATIONS = 4000;

	public static int getNumberOfPropertyCombinations(List<PropertyData<?>> properties) {
		int result = 1;
		for (PropertyData<?> propertyData : properties) {
			switch (propertyData) {
			case PropertyData.LogicType ignored -> result *= 2; // logic has two possible values
			case PropertyData.IntegerType integerType -> result *= integerType.getMax() - integerType.getMin() + 1;
			case PropertyData.StringType stringType -> {
				if (stringType.getArrayData() != null)
					result *= stringType.getArrayData().length;
				else
					throw new RuntimeException("Strings without array data are not supported");
			}
			default -> throw new RuntimeException("Unsupported property type: " + propertyData.getClass());
			}
		}
		return result;
	}

	@Nonnull public static String propertyRegistryName(PropertyData<?> data) {
		if (data.getName().startsWith(NameMapper.MCREATOR_PREFIX))
			return data.getName().replace(NameMapper.MCREATOR_PREFIX, "");
		DataListEntry dle = DataListLoader.loadDataMap("blockstateproperties").get(data.getName());
		if (dle != null && dle.getOther() instanceof Map<?, ?> other && other.get(
				"registry_name") instanceof String registryName)
			return registryName;
		return data.getName();
	}

	@Nullable public static PropertyDataWithValue<?> fromDataListEntry(@Nonnull DataListEntry property) {
		if (!(property.getOther() instanceof Map<?, ?> other) || other.get("registry_name") == null)
			return null;

		switch (property.getType()) {
		case "Logic" -> {
			return new PropertyDataWithValue<>(new PropertyData.LogicType(property.getName()), null);
		}
		case "Integer" -> {
			int min = Integer.parseInt((String) other.get("min"));
			int max = Integer.parseInt((String) other.get("max"));
			return new PropertyDataWithValue<>(new PropertyData.IntegerType(property.getName(), min, max), null);
		}
		case "Enum" -> {
			String[] data;
			if (other.get("values") instanceof List<?> values)
				data = values.stream().map(Object::toString).toArray(String[]::new);
			else
				data = ElementUtil.getDataListAsStringArray((String) other.get("values"));
			return new PropertyDataWithValue<>(new PropertyData.StringType(property.getName(), data), null);
		}
		case null, default -> {
			return null;
		}
		}
	}

}