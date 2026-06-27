/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.mcp.tools.schema;

import com.fasterxml.classmate.ResolvedType;
import com.github.victools.jsonschema.generator.MemberScope;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import net.mcreator.element.util.GEValidator;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.ui.minecraft.states.StateMap;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class SchemaDefaultValueHelper {

	private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setStrictness(Strictness.LENIENT)
			.registerTypeHierarchyAdapter(MappableElement.class, new MappableElement.GSONAdapter())
			.registerTypeAdapter(StateMap.class, new StateMap.GSONAdapter()).create();

	static Object fromFieldValue(Object fieldValue, MemberScope<?, ?> member) {
		ResolvedType declaredType = member.getType();
		Class<?> runtimeType = fieldValue.getClass();
		Object jsonValue = GSON.fromJson(GSON.toJson(fieldValue), Object.class);
		return normalize(jsonValue, declaredType.getErasedType(), resolveListItemType(declaredType, runtimeType),
				runtimeType);
	}

	@Nullable private static Object normalize(@Nullable Object value, Class<?> type, @Nullable Class<?> listItemType,
			Class<?> beanType) {
		switch (value) {
		case null -> {
			return null;
		}
		case Number number -> {
			return GEValidator.castNumber(type, number.doubleValue());
		}
		case List<?> list -> {
			if (listItemType == null) {
				return value;
			}
			List<Object> normalized = new ArrayList<>(list.size());
			for (Object item : list) {
				normalized.add(normalize(item, listItemType, null, listItemType));
			}
			return normalized;
		}
		case Map<?, ?> map when !Map.class.isAssignableFrom(type) -> {
			Map<String, Object> normalized = new LinkedHashMap<>();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String key = String.valueOf(entry.getKey());
				Field field = findField(beanType, key);
				Object entryValue = entry.getValue();
				normalized.put(key,
						field != null ? normalize(entryValue, field.getType(), null, field.getType()) : entryValue);
			}
			return normalized;
		}
		default -> {
		}
		}
		return value;
	}

	@Nullable private static Class<?> resolveListItemType(ResolvedType declaredType, Class<?> runtimeType) {
		if (declaredType.getTypeParameters().size() == 1) {
			return declaredType.getTypeParameters().getFirst().getErasedType();
		}
		return runtimeType.isArray() ? runtimeType.getComponentType() : null;
	}

	@Nullable private static Field findField(Class<?> type, String name) {
		for (Class<?> current = type; current != null; current = current.getSuperclass()) {
			try {
				Field field = current.getDeclaredField(name);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException ignored) {
			}
		}
		return null;
	}

}
