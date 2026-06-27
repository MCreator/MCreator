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
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.types.interfaces.LimitedOptions;
import net.mcreator.element.types.interfaces.NonNullIf;
import net.mcreator.element.types.interfaces.NonNullMappable;
import net.mcreator.element.types.interfaces.Numeric;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.generator.template.TemplateExpressionParser;
import net.mcreator.util.TestUtil;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class provides validation functionality for generatable elements and their fields.
 * It leverages reflection to inspect and validate fields recursively and attempts to
 * correct invalid values when possible based on attached annotations.
 */
public class GEValidator {

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
	 * @param element       the element to validate
	 * @param validationLog optional consumer to log validation messages; if {@code null}, no logging is performed
	 * @throws ValidationException if validation fails in non-recoverable way or reflective access fails
	 */
	public static void validateAndTryToCorrect(GeneratableElement element, @Nullable Consumer<String> validationLog)
			throws ValidationException {
		if (validationLog == null) {
			validationLog = _ -> {};
		}

		performValidation(element, element, validationLog);
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

	private static void performValidation(GeneratableElement element, @Nullable Object input,
			Consumer<String> validationLog) throws ValidationException {
		if (input == null) {
			return; // nothing to validate
		}

		for (CachedField cachedField : getFields(input.getClass())) {
			Object fieldValue;
			Field field = cachedField.field();
			try {
				fieldValue = field.get(input);
			} catch (IllegalAccessException e) {
				throw new ValidationException("Failed to access field %s of mod element %s".formatted(field.getName(),
						element.getModElement().getName()), e);
			}

			validateFieldAndTryToCorrect(element, cachedField, fieldValue, input, validationLog);

			// Further unpack and validate if applicable
			unpackAndValidate(element, fieldValue, validationLog);
		}
	}

	private static void unpackAndValidate(GeneratableElement element, @Nullable Object value,
			Consumer<String> validationLog) throws ValidationException {
		if (value == null) {
			return;
		}

		if (value instanceof Iterable<?> list) { // list of values
			for (Object item : list) {
				unpackAndValidate(element, item, validationLog);
			}
		} else if (value instanceof Map<?, ?> map) { // map with values
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				unpackAndValidate(element, entry.getKey(), validationLog);
				unpackAndValidate(element, entry.getValue(), validationLog);
			}
		} else if (value.getClass().isArray()) { // array of values
			int length = Array.getLength(value);
			for (int i = 0; i < length; i++) {
				unpackAndValidate(element, Array.get(value, i), validationLog);
			}
		} else if (GeneratableElement.isDataModelObject(value.getClass())) {
			// Data model object. Pass it back to the reflection scanner.
			performValidation(element, value, validationLog);
		}
	}

	private static void validateFieldAndTryToCorrect(GeneratableElement element, CachedField field,
			@Nullable Object fieldValue, Object fieldHolder, Consumer<String> validationLog)
			throws ValidationException {
		Field javaField = field.field();
		try {
			if (fieldValue == null) {
				if (field.nullable() && field.notNullable()) {
					throw new ValidationException(
							"Field %s of mod element %s is annotated with both @Nullable and @Nonnull.".formatted(
									javaField.getName(), element.getModElement().getName()));
				}

				if (field.nullable()) {
					return; // field is nullable, no further validation needed
				}

				if (field.notNullable()) {
					throw new ValidationException(
							"Field %s of mod element %s is null, but should not be.".formatted(javaField.getName(),
									element.getModElement().getName()));
				}

				NonNullMappable annotation = field.nonNullMappable();
				if (annotation != null) {
					if (MappableElement.class.isAssignableFrom(javaField.getType())) {
						validationLog.accept(
								"Field %s of mod element %s is null but needs to have a value. Setting it to default value '%s'.".formatted(
										javaField.getName(), element.getModElement().getName(), annotation.value()));
						TestUtil.failIfTestingEnvironmentIgnoreIf("net.mcreator.integration.WorkspaceConvertersTest");

						// Construct field object instance and set its value
						@SuppressWarnings("unchecked") Constructor<? extends MappableElement> constructor = (Constructor<? extends MappableElement>) javaField.getType()
								.getDeclaredConstructor(Workspace.class, String.class);
						constructor.setAccessible(true);
						javaField.set(fieldHolder,
								constructor.newInstance(element.getModElement().getWorkspace(), annotation.value()));
					}
				}

				if (field.limitedOptions() != null && field.field().getType() == String.class) {
					LimitedOptionsCache limited = field.limitedOptions();
					String firstOption = limited.allowed().getFirst();
					validationLog.accept(
							"Field %s of mod element %s is null but needs to have a value. Setting it to the first option '%s'.".formatted(
									javaField.getName(), element.getModElement().getName(), firstOption));
					javaField.set(fieldHolder, firstOption);
					TestUtil.failIfTestingEnvironmentIgnoreIf("net.mcreator.integration.WorkspaceConvertersTest");
				}

				NonNullIf nonNullIf = field.nonNullIf();
				if (nonNullIf != null) {
					boolean isNonNull = false;
					for (String condition : nonNullIf.value()) {
						if (TemplateExpressionParser.parseCondition(
								element.getModElement().getWorkspace().getGenerator(), condition, fieldHolder)) {
							isNonNull = true;
							break;
						}
					}
					if (isNonNull) {
						validationLog.accept(
								"Field %s of mod element %s is null but should be if any of these conditions matches: '%s'.".formatted(
										javaField.getName(), element.getModElement().getName(),
										Arrays.toString(nonNullIf.value())));
						// Fail this one even for converters tests as converters should make sure this can't happen
						TestUtil.failIfTestingEnvironment();
					}
				}

				// no further validations can be done since this field is/was null
				return;
			}

			// Validations for cases where fieldValue is not null below

			// If field is String and notNullable and string is blank, fail valiation
			if (fieldValue instanceof String string && field.notNullable() && string.isBlank()) {
				throw new ValidationException(
						"Field %s of mod element %s is blank, but should not be.".formatted(javaField.getName(),
								element.getModElement().getName()));
			}

			if (field.numeric() != null) {
				if (fieldValue instanceof Number number) {
					Numeric annotation = field.numeric();
					if (annotation.optional() && number.doubleValue() == 0) {
						return; // skip validation for optional numeric fields if value is 0 (default)
					}

					// If 0 and out of range, set to default value, otherwise clamp to range
					if (number.doubleValue() < annotation.min()) {
						if (number.doubleValue() == 0) {
							validationLog.accept(
									"Field %s of mod element %s has value %s which is less than minimum %s. Setting it to default value.".formatted(
											javaField.getName(), element.getModElement().getName(), number,
											annotation.min()));
							javaField.set(fieldHolder, castNumber(javaField.getType(), annotation.init()));
						} else {
							validationLog.accept(
									"Field %s of mod element %s has value %s which is less than minimum %s. Setting it to minimum.".formatted(
											javaField.getName(), element.getModElement().getName(), number,
											annotation.min()));
							javaField.set(fieldHolder, castNumber(javaField.getType(), annotation.min()));
						}
						TestUtil.failIfTestingEnvironmentIgnoreIf("net.mcreator.integration.WorkspaceConvertersTest");
					} else if (number.doubleValue() > annotation.max()) {
						if (number.doubleValue() == 0) {
							validationLog.accept(
									"Field %s of mod element %s has value %s which is greater than maximum %s. Setting it to default value.".formatted(
											javaField.getName(), element.getModElement().getName(), number,
											annotation.max()));
							javaField.set(fieldHolder, castNumber(javaField.getType(), annotation.init()));
						} else {
							validationLog.accept(
									"Field %s of mod element %s has value %s which is greater than maximum %s. Setting it to maximum.".formatted(
											javaField.getName(), element.getModElement().getName(), number,
											annotation.max()));
							javaField.set(fieldHolder, castNumber(javaField.getType(), annotation.max()));
						}
						TestUtil.failIfTestingEnvironmentIgnoreIf("net.mcreator.integration.WorkspaceConvertersTest");
					}
				} else {
					throw new ValidationException(
							"Field %s of mod element %s is annotated with @Numeric but is not a number.".formatted(
									javaField.getName(), element.getModElement().getName()));
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
						validationLog.accept(
								"Field %s of mod element %s has value '%s' which is not allowed. Setting it to the first option '%s'.".formatted(
										javaField.getName(), element.getModElement().getName(), string, firstOption));
						javaField.set(fieldHolder, firstOption);
						TestUtil.failIfTestingEnvironmentIgnoreIf("net.mcreator.integration.WorkspaceConvertersTest");
					}
				} else if (fieldValue instanceof Integer index) {
					int optionsLength = limited.allowed().size();
					if (index < 0 || index >= optionsLength) {
						validationLog.accept(
								"Field %s of mod element %s has index value %d which is out of bounds for options. Setting it to 0.".formatted(
										javaField.getName(), element.getModElement().getName(), index));
						javaField.set(fieldHolder, 0);
					}
				} else {
					throw new ValidationException(
							"Field %s of mod element %s is annotated with @LimitedOptions but is not a string or number.".formatted(
									javaField.getName(), element.getModElement().getName()));
				}
			}
		} catch (IllegalAccessException e) {
			throw new ValidationException("Failed to access field %s of mod element %s".formatted(javaField.getName(),
					element.getModElement().getName()), e);
		} catch (InvocationTargetException | NoSuchMethodException | InstantiationException | ClassCastException e) {
			throw new ValidationException(
					"Failed to construct default value for field %s of mod element %s".formatted(javaField.getName(),
							element.getModElement().getName()), e);
		}
	}

	private record LimitedOptionsCache(boolean allowCustom, LinkedHashSet<String> allowed) {
		private LimitedOptionsCache(LimitedOptions annotation) {
			String[] options = annotation.value();
			this(annotation.allowCustom(), new LinkedHashSet<>(Arrays.asList(options)));
		}
	}

	private record CachedField(Field field, boolean notNullable, boolean nullable, @Nullable Numeric numeric,
	                           @Nullable NonNullMappable nonNullMappable, @Nullable LimitedOptionsCache limitedOptions,
	                           @Nullable NonNullIf nonNullIf) {
		private CachedField(Field field) {
			LimitedOptions limitedOptions = field.getAnnotation(LimitedOptions.class);
			this(field, field.isAnnotationPresent(Nonnull.class) || field.isAnnotationPresent(BlocklyXML.class),
					field.isAnnotationPresent(Nullable.class), field.getAnnotation(Numeric.class),
					field.getAnnotation(NonNullMappable.class),
					limitedOptions != null ? new LimitedOptionsCache(limitedOptions) : null,
					field.getAnnotation(NonNullIf.class));
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
