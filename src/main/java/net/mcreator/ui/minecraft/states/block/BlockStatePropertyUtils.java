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

import net.mcreator.element.ModElementType;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class BlockStatePropertyUtils {

	public static final int MAX_PROPERTY_COMBINATIONS = 2000;

	public static int getNumberOfPropertyCombinations(List<PropertyData<?>> properties) {
		int result = 1;
		for (PropertyData<?> property : properties)
			result *= getPossiblePropertyValues(property).size();
		return result;
	}

	public static List<Object> getPossiblePropertyValues(PropertyData<?> propertyData) {
		return switch (propertyData) {
			case PropertyData.LogicType ignored -> List.of(true, false);
			case PropertyData.IntegerType integerType -> {
				List<Object> values = new ArrayList<>();
				for (int i = integerType.getMin(); i <= integerType.getMax(); i++)
					values.add(i);
				yield values;
			}
			case PropertyData.StringType stringType -> {
				if (stringType.getArrayData() != null)
					yield Arrays.stream(stringType.getArrayData()).map(e -> (Object) e).toList();
				else
					throw new RuntimeException("Strings without array data are not supported");
			}
			default -> throw new RuntimeException("Unsupported property type: " + propertyData.getClass());
		};
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

	public static Map<String, List<String>> getBlockBaseProperties(GeneratorConfiguration generatorConfiguration) {
		Map<?, ?> definition = generatorConfiguration.getDefinitionsProvider()
				.getModElementDefinition(ModElementType.BLOCK);
		if (definition == null)
			return Collections.emptyMap();

		if (definition.get("block_base_properties") instanceof Map<?, ?> raw) {
			Map<String, List<String>> retval = new HashMap<>();
			for (Map.Entry<?, ?> entry : raw.entrySet()) {
				if (entry.getValue() instanceof List<?> list)
					retval.put((String) entry.getKey(), list.stream().map(Object::toString).toList());
				else
					retval.put((String) entry.getKey(), Collections.singletonList(entry.getValue().toString()));
			}
			return retval;
		}

		return Collections.emptyMap();
	}

}