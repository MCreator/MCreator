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

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.types.interfaces.LimitedOptions;
import net.mcreator.element.types.interfaces.NonNullMappable;
import net.mcreator.element.types.interfaces.Numeric;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.util.TestUtil;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class provides validation functionality for generatable elements and their fields.
 * It leverages reflection to inspect and validate fields recursively and attempts to
 * correct invalid values when possible based on attached annotations.
 */
public class GEValidator {

	private static final Logger LOG = LogManager.getLogger(GEValidator.class);

	/**
	 * Validates the given generatable element and recursively checks all nested
	 * custom objects, collections, maps, and arrays using reflection.
	 * <p/>
	 * Supported validation annotations:
	 * - {@link Nonnull}: field value must not be {@code null} (exception)
	 * - {@link Numeric}: numeric value must be within allowed bounds (tries to correct)
	 * - {@link LimitedOptions}: value must match one of the allowed options (tries to correct)
	 * <p/>
	 *
	 * @param element the element to validate
	 * @throws ValidationException if validation fails or reflective access fails
	 */
	public static void validateAndTryToCorrect(GeneratableElement element) throws ValidationException {
		performValidation(element, element);
	}

	private static final Map<Class<?>, List<CachedField>> FIELD_CACHE = new ConcurrentHashMap<>();

	private static List<CachedField> getFields(Class<?> clazz) {
		return FIELD_CACHE.computeIfAbsent(clazz, c -> {
			Field[] fields = c.getDeclaredFields();
			return Arrays.stream(fields).filter(f -> !Modifier.isStatic(f.getModifiers()))
					.filter(f -> !Modifier.isTransient(f.getModifiers())).peek(f -> f.setAccessible(true))
					.map(CachedField::new).collect(Collectors.toList());
		});
	}

	private static void performValidation(GeneratableElement element, @Nullable Object input)
			throws ValidationException {
		if (input == null) {
			return; // nothing to validate
		}

		for (CachedField cachedField : getFields(input.getClass())) {
			Object fieldValue;
			Field field = cachedField.field();
			try {
				fieldValue = field.get(input);
			} catch (IllegalAccessException e) {
				throw new ValidationException(
						"Failed to access field " + field.getName() + " of mod element " + element.getModElement()
								.getName(), e);
			}

			validateFieldAndTryToCorrect(element, cachedField, fieldValue, input);

			if (fieldValue == null) {
				continue; // no need to check null values for nested validation
			}

			if (fieldValue instanceof Iterable<?> list) { // list of values
				for (Object item : list) {
					performValidation(element, item);
				}
			} else if (fieldValue instanceof Map<?, ?> map) { // map with values
				for (Map.Entry<?, ?> entry : map.entrySet()) {
					performValidation(element, entry.getKey());
					performValidation(element, entry.getValue());
				}
			} else if (fieldValue.getClass().isArray()) { // array of values
				int length = Array.getLength(fieldValue);
				for (int i = 0; i < length; i++) {
					performValidation(element, Array.get(fieldValue, i));
				}
			} else if (GeneratableElement.isDataModelObject(
					fieldValue)) { // value of unknown type but from MCreator system, do recursive check
				performValidation(element, fieldValue);
			}
		}
	}

	private static void validateFieldAndTryToCorrect(GeneratableElement element, CachedField field,
			@Nullable Object fieldValue, Object fieldHolder) throws ValidationException {
		Field javaField = field.field();
		try {
			if (fieldValue == null) {
				if (field.notNullable()) {
					throw new ValidationException(
							"Field " + javaField.getName() + " of mod element " + element.getModElement().getName()
									+ " is null, but should not be.");
				}

				if (field.isAnnotationPresent(NonNullMappable.class)) {
					NonNullMappable annotation = field.getAnnotation(NonNullMappable.class);
					if (MappableElement.class.isAssignableFrom(field.getType())) {
						LOG.debug(
								"Field {} of mod element {} is null but needs to have a value. Setting it to default value '{}'.",
								field.getName(), element.getModElement().getName(), annotation.value());
						TestUtil.failIfTestingEnvironmentIgnoreIf("net.mcreator.integration.WorkspaceConvertersTest");

						// Construct field object instance and set its value
						@SuppressWarnings("unchecked") Constructor<? extends MappableElement> constructor = (Constructor<? extends MappableElement>) field.getType()
								.getDeclaredConstructor(Workspace.class, String.class);
						constructor.setAccessible(true);
						field.set(fieldHolder,
								constructor.newInstance(element.getModElement().getWorkspace(), annotation.value()));
					}
				}

				// no further validations can be done since this field is null
				return;
			}

			// Validations for cases where fieldValue is not null below

			if (field.numeric() != null) {
				if (fieldValue instanceof Number number) {
					Numeric annotation = field.numeric();
					if (annotation.optional() && number.doubleValue() == 0) {
						return; // skip validation for optional numeric fields if value is 0 (default)
					}

					if (number.doubleValue() < annotation.min()) {
						LOG.debug(
								"Field {} of mod element {} has value {} which is less than minimum {}. Setting it to minimum.",
								javaField.getName(), element.getModElement().getName(), number, annotation.min());
						javaField.set(fieldHolder, castNumber(javaField.getType(), annotation.min()));
						TestUtil.failIfTestingEnvironmentIgnoreIf("net.mcreator.integration.WorkspaceConvertersTest");
					} else if (number.doubleValue() > annotation.max()) {
						LOG.debug(
								"Field {} of mod element {} has value {} which is greater than maximum {}. Setting it to maximum.",
								javaField.getName(), element.getModElement().getName(), number, annotation.max());
						javaField.set(fieldHolder, castNumber(javaField.getType(), annotation.max()));
						TestUtil.failIfTestingEnvironmentIgnoreIf("net.mcreator.integration.WorkspaceConvertersTest");

						field.set(fieldHolder, castNumber(field.getType(), annotation.max()));
					}
				} else {
					throw new ValidationException(
							"Field " + javaField.getName() + " of mod element " + element.getModElement().getName()
									+ " is annotated with @Numeric but is not a number.");
				}
			}

			if (field.limitedOptions() != null) {
				LimitedOptionsCache limited = field.limitedOptions();
				if (limited.allowCustom()) {
					return; // skip validation if custom values are allowed
				}

				if (fieldValue instanceof String string) {
					if (!limited.allowed().contains(string)) {
						String firstOption = limited.allowed().getFirst();
						LOG.debug(
								"Field {} of mod element {} has value '{}' which is not allowed. Setting it to the first option '{}'.",
								javaField.getName(), element.getModElement().getName(), string, firstOption);
						javaField.set(fieldHolder, firstOption);
						TestUtil.failIfTestingEnvironmentIgnoreIf("net.mcreator.integration.WorkspaceConvertersTest");

						field.set(fieldHolder, options[0]);
					}
				} else if (fieldValue instanceof Integer index) {
					int optionsLength = limited.allowed().size();
					if (index < 0 || index >= optionsLength) {
						LOG.debug(
								"Field {} of mod element {} has index value {} which is out of bounds for options. Setting it to 0.",
								javaField.getName(), element.getModElement().getName(), index);
						javaField.set(fieldHolder, 0);
					}
				} else {
					throw new ValidationException(
							"Field " + javaField.getName() + " of mod element " + element.getModElement().getName()
									+ " is annotated with @LimitedOptions but is not a string or number.");
				}
			}
		} catch (IllegalAccessException e) {
			throw new ValidationException(
					"Failed to access field " + javaField.getName() + " of mod element " + element.getModElement()
							.getName(), e);
		} catch (InvocationTargetException | NoSuchMethodException | InstantiationException | ClassCastException e) {
			throw new ValidationException(
					"Failed to construct default value for field " + field.getName() + " of mod element "
							+ element.getModElement().getName(), e);
		}
	}

	private record LimitedOptionsCache(boolean allowCustom, LinkedHashSet<String> allowed) {
		private LimitedOptionsCache(LimitedOptions annotation) {
			String[] options = annotation.value();
			this(annotation.allowCustom(), new LinkedHashSet<>(Arrays.asList(options)));
		}
	}

	private record CachedField(Field field, boolean notNullable, @Nullable Numeric numeric,
	                           @Nullable LimitedOptionsCache limitedOptions) {
		private CachedField(Field field) {
			LimitedOptions limitedOptions = field.getAnnotation(LimitedOptions.class);
			this(field, field.isAnnotationPresent(Nonnull.class), field.getAnnotation(Numeric.class),
					limitedOptions != null ? new LimitedOptionsCache(limitedOptions) : null);
		}
	}

	private static Object castNumber(Class<?> type, double value) {
		if (type == int.class || type == Integer.class)
			return (int) value;
		if (type == long.class || type == Long.class)
			return (long) value;
		if (type == float.class || type == Float.class)
			return (float) value;
		if (type == short.class || type == Short.class)
			return (short) value;
		if (type == byte.class || type == Byte.class)
			return (byte) value;

		return value; // double / Double fallback
	}

	public static final class ValidationException extends Exception {

		public ValidationException(String message, Throwable cause) {
			super(message, cause);
		}

		public ValidationException(String message) {
			super(message);
		}

	}

}
