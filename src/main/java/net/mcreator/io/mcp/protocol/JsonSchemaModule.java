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

package net.mcreator.io.mcp.protocol;

import com.fasterxml.classmate.AnnotationInclusion;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.generator.Module;
import tools.jackson.databind.node.ObjectNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.stream.Stream;

public class JsonSchemaModule implements Module {

	@Override public void applyToConfigBuilder(SchemaGeneratorConfigBuilder builder) {
		SchemaGeneratorConfigPart<FieldScope> fieldConfigPart = builder.forFields();
		fieldConfigPart.withNullableCheck(this::isNullable);
		fieldConfigPart.withInstanceAttributeOverride(this::applyCustomAttributes);
		fieldConfigPart.withRequiredCheck(this::isRequired);

		SchemaGeneratorConfigPart<MethodScope> methodConfigPart = builder.forMethods();
		methodConfigPart.withIgnoreCheck(_ -> true); // do not include methods

		Stream.of(Nullable.class, Nonnull.class, SchemaDescription.class).forEach(
				annotationType -> builder.withAnnotationInclusionOverride(annotationType,
						AnnotationInclusion.INCLUDE_AND_INHERIT));
	}

	private boolean isRequired(FieldScope fieldScope) {
		return this.getAnnotationFromFieldOrGetter(fieldScope, Nullable.class) == null; // only not required if nullable
	}

	private void applyCustomAttributes(ObjectNode node, MemberScope<?, ?> member, SchemaGenerationContext context) {
		String fieldName = member.getName().toLowerCase();
		String jsonHint = null;
		if (member.getType().isInstanceOf(Map.class) && fieldName.contains("json")) {
			jsonHint = "Must be a JSON object, not a JSON string.";
		}

		SchemaDescription schemaDescription = member.getAnnotation(SchemaDescription.class);
		if (schemaDescription != null || jsonHint != null) {
			String description = schemaDescription != null ? schemaDescription.value() : "";
			if (jsonHint != null) {
				description = description.isEmpty() ? jsonHint : description + " " + jsonHint;
			}
			node.put("description", description);
		}
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

