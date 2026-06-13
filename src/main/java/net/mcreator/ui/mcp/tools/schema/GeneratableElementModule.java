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

import com.fasterxml.classmate.AnnotationInclusion;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.generator.Module;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.RetvalProcedure;
import net.mcreator.element.types.interfaces.LimitedOptions;
import net.mcreator.element.types.interfaces.NonNullMappable;
import net.mcreator.element.types.interfaces.Numeric;
import tools.jackson.databind.node.ObjectNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

public class GeneratableElementModule implements Module {

	@Override public void applyToConfigBuilder(SchemaGeneratorConfigBuilder builder) {
		SchemaGeneratorConfigPart<FieldScope> fieldConfigPart = builder.forFields();
		this.applyToConfigPart(fieldConfigPart);
		fieldConfigPart.withRequiredCheck(this::isRequired);

		SchemaGeneratorConfigPart<MethodScope> methodConfigPart = builder.forMethods();
		methodConfigPart.withIgnoreCheck(_ -> true); // do not include methods

		Stream.of(Nullable.class, Nonnull.class, LimitedOptions.class, Numeric.class, NonNullMappable.class).forEach(
				annotationType -> builder.withAnnotationInclusionOverride(annotationType,
						AnnotationInclusion.INCLUDE_AND_INHERIT));

		builder.forTypesInGeneral().withCustomDefinitionProvider((type, context) -> {
			// TODO: Procedure (simple string), RetvalProcedure (name is optional), MappableElement (simple string) custom schema
			return null;
		});
	}

	@SuppressWarnings("RedundantIfStatement") private boolean isRequired(FieldScope fieldScope) {
		if (this.isNullable(fieldScope) == Boolean.TRUE) {
			return false;
		}

		if (RetvalProcedure.class.isAssignableFrom(fieldScope.getType().getErasedType())) {
			return true; // Retval procedure requires at least fixed value
		} else if (Procedure.class.isAssignableFrom(fieldScope.getType().getErasedType())) {
			return false; // Procedure fields are always optional
		}

		return true;
	}

	private void applyToConfigPart(SchemaGeneratorConfigPart<?> configPart) {
		configPart.withNullableCheck(this::isNullable);

		configPart.withEnumResolver(this::resolveEnum);
		configPart.withNumberInclusiveMinimumResolver(this::resolveMinimum);
		configPart.withNumberInclusiveMaximumResolver(this::resolveMaximum);
		configPart.withDefaultResolver(this::resolveDefault);
		configPart.withInstanceAttributeOverride(this::applyCustomAttributes);
	}

	@Nullable private List<String> resolveEnum(MemberScope<?, ?> member) {
		LimitedOptions limitedOptions = this.getAnnotationFromFieldOrGetter(member, LimitedOptions.class);
		if (limitedOptions == null) {
			return null;
		}

		if (member.getType().getErasedType() == String.class) {
			return List.of(limitedOptions.value());
		}

		return null;
	}

	@Nullable private BigDecimal resolveMinimum(MemberScope<?, ?> member) {
		Numeric numeric = this.getAnnotationFromFieldOrGetter(member, Numeric.class);
		return numeric != null ? BigDecimal.valueOf(numeric.min()) : null;
	}

	@Nullable private BigDecimal resolveMaximum(MemberScope<?, ?> member) {
		Numeric numeric = this.getAnnotationFromFieldOrGetter(member, Numeric.class);
		return numeric != null ? BigDecimal.valueOf(numeric.max()) : null;
	}

	@Nullable private Object resolveDefault(MemberScope<?, ?> member) {
		Numeric numeric = this.getAnnotationFromFieldOrGetter(member, Numeric.class);
		if (numeric != null) {
			return this.castNumericDefault(member.getType().getErasedType(), numeric.init());
		}

		NonNullMappable nonNullMappable = this.getAnnotationFromFieldOrGetter(member, NonNullMappable.class);
		if (nonNullMappable != null) {
			return nonNullMappable.value();
		}

		LimitedOptions limitedOptions = this.getAnnotationFromFieldOrGetter(member, LimitedOptions.class);
		if (limitedOptions != null && member.getType().getErasedType() == String.class) {
			return limitedOptions.value().length > 0 ? limitedOptions.value()[0] : null;
		}

		return null;
	}

	private void applyCustomAttributes(ObjectNode node, MemberScope<?, ?> member, SchemaGenerationContext context) {
		LimitedOptions limitedOptions = this.getAnnotationFromFieldOrGetter(member, LimitedOptions.class);
		if (limitedOptions != null) {
			if (this.isNumericType(member.getType().getErasedType())) {
				node.put("minimum", 0);
				node.put("maximum", limitedOptions.value().length - 1);
			}
		}

		Numeric numeric = this.getAnnotationFromFieldOrGetter(member, Numeric.class);
		if (numeric != null) {
			if (numeric.allowMinMaxEqual()) {
				node.put("x-mcreator-allowMinMaxEqual", true);
			}
		}
	}

	private Object castNumericDefault(Class<?> type, double value) {
		if (type == int.class || type == Integer.class) {
			return (int) value;
		}
		if (type == long.class || type == Long.class) {
			return (long) value;
		}
		if (type == float.class || type == Float.class) {
			return (float) value;
		}
		return value;
	}

	private boolean isNumericType(Class<?> type) {
		return Number.class.isAssignableFrom(type) || type == int.class || type == long.class || type == float.class
				|| type == double.class || type == short.class || type == byte.class;
	}

	protected Boolean isNullable(MemberScope<?, ?> member) {
		Boolean result;
		if (member.isFakeContainerItemScope()) {
			result = null;
		} else if (this.getAnnotationFromFieldOrGetter(member, Nonnull.class) != null) {
			result = Boolean.FALSE;
		} else if (this.getAnnotationFromFieldOrGetter(member, Nullable.class) != null) {
			result = Boolean.TRUE;
		} else {
			result = null;
		}
		return result;
	}

	@Nullable
	private <A extends Annotation> A getAnnotationFromFieldOrGetter(MemberScope<?, ?> member,
			Class<A> annotationClass) {
		A containerItemAnnotation = member.getContainerItemAnnotationConsideringFieldAndGetterIfSupported(
				annotationClass, _ -> false);
		if (containerItemAnnotation != null)
			return containerItemAnnotation;
		return member.getAnnotationConsideringFieldAndGetterIfSupported(annotationClass, _ -> false);
	}

}
