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

package net.mcreator.element.util;

import net.mcreator.blockly.data.BlocklyXML;
import net.mcreator.element.types.interfaces.LimitedOptionsField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationUtils {

	private static final Logger LOG = LogManager.getLogger(AnnotationUtils.class);

	public static String getBlocklyXMLDefaultValue(Class<?> type, String field) {
		return getAnnotation(type, field, BlocklyXML.class).defaultXML();
	}

	public static List<String> getLimitedOptionsList(Class<?> type, String field) {
		return List.of(getAnnotation(type, field, LimitedOptionsField.class).value());
	}

	private static final Map<FieldKey, Annotation> CACHE = new ConcurrentHashMap<>();

	@Nonnull
	public static <T extends Annotation> T getAnnotation(Class<?> type, String field, Class<T> annotationClass) {
		FieldKey key = FieldKey.of(type, field);
		Annotation annotation = CACHE.computeIfAbsent(key, k -> {
			try {
				Field declaredField = k.owner.getDeclaredField(k.field);
				return declaredField.getAnnotation(annotationClass);
			} catch (Exception e) {
				LOG.warn("Failed to get annotation {} for {}.{}", annotationClass.getName(), type.getName(), field, e);
				return null;
			}
		});
		if (annotation == null)
			throw new IllegalArgumentException(
					"Field " + field + " in class " + type.getName() + " does not have annotation "
							+ annotationClass.getName());
		return annotationClass.cast(annotation);
	}

	private record FieldKey(Class<?> owner, String field) {
		private static FieldKey of(Class<?> owner, String field) {
			return new FieldKey(owner, field);
		}

		@Override public boolean equals(Object o) {
			if (!(o instanceof FieldKey(Class<?> owner1, String field1)))
				return false;
			return Objects.equals(field, field1) && Objects.equals(owner, owner1);
		}

		@Override public int hashCode() {
			int result = Objects.hashCode(owner);
			result = 31 * result + Objects.hashCode(field);
			return result;
		}

	}

}
