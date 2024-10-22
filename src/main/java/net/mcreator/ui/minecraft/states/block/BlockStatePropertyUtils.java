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

import net.mcreator.ui.minecraft.states.PropertyData;

import java.util.List;

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

}