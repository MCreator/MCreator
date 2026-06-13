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
import com.fasterxml.classmate.ResolvedType;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.generator.Module;
import net.mcreator.blockly.data.BlocklyXML;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.parts.procedure.*;
import net.mcreator.element.types.interfaces.LimitedOptions;
import net.mcreator.element.types.interfaces.NonNullMappable;
import net.mcreator.element.types.interfaces.Numeric;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class GeneratableElementModule implements Module {

	@Override public void applyToConfigBuilder(SchemaGeneratorConfigBuilder builder) {
		SchemaGeneratorConfigPart<FieldScope> fieldConfigPart = builder.forFields();
		fieldConfigPart.withNullableCheck(this::isNullable);
		fieldConfigPart.withEnumResolver(this::resolveEnum);
		fieldConfigPart.withNumberInclusiveMinimumResolver(this::resolveMinimum);
		fieldConfigPart.withNumberInclusiveMaximumResolver(this::resolveMaximum);
		fieldConfigPart.withDefaultResolver(this::resolveDefault);
		fieldConfigPart.withInstanceAttributeOverride(this::applyCustomAttributes);
		fieldConfigPart.withRequiredCheck(this::isRequired);

		fieldConfigPart.withTargetTypeOverridesResolver(this::resolveMapOverrides);

		SchemaGeneratorConfigPart<MethodScope> methodConfigPart = builder.forMethods();
		methodConfigPart.withIgnoreCheck(_ -> true); // do not include methods

		Stream.of(Nullable.class, Nonnull.class, LimitedOptions.class, Numeric.class, NonNullMappable.class,
				BlocklyXML.class).forEach(annotationType -> builder.withAnnotationInclusionOverride(annotationType,
				AnnotationInclusion.INCLUDE_AND_INHERIT));

		builder.forTypesInGeneral().withCustomDefinitionProvider(this::provideCustomDefinition);
	}

	@Nullable private List<ResolvedType> resolveMapOverrides(FieldScope field) {
		ResolvedType type = field.getType();
		if (type.isInstanceOf(Map.class) && type.getErasedType() != Map.class) {
			List<ResolvedType> mapParams = type.typeParametersFor(Map.class);
			ResolvedType genericMapType = field.getContext().resolve(Map.class, mapParams.toArray(new ResolvedType[0]));
			return Collections.singletonList(genericMapType);
		}
		return null;
	}

	@SuppressWarnings("unchecked") @Nullable
	private CustomDefinition provideCustomDefinition(ResolvedType type, SchemaGenerationContext context) {
		Class<?> erasedType = type.getErasedType();
		if (RetvalProcedure.class.isAssignableFrom(erasedType)) {
			return this.createRetvalProcedureDefinition(type, context);
		} else if (Procedure.class.isAssignableFrom(erasedType)) {
			return this.createDataListDefinition(context, "procedure");
		} else if (MappableElement.class.isAssignableFrom(erasedType)) {
			return this.createDataListDefinition(context,
					guessDataListName((Class<? extends MappableElement>) erasedType));
		} else if (TextureHolder.class.isAssignableFrom(erasedType)) {
			return this.createDataListDefinition(context, null);
		}
		return null;
	}

	private String guessDataListName(Class<? extends MappableElement> mappableElementClass) {
		try {
			var constructor = mappableElementClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			var instance = constructor.newInstance();
			return instance.getMappingSource();
		} catch (Exception e) {
			return mappableElementClass.getSimpleName().toLowerCase().replace("entry", "");
		}
	}

	private CustomDefinition createDataListDefinition(SchemaGenerationContext context, @Nullable String dataListHint) {
		ObjectNode schema = context.createDefinitionReference(context.getTypeContext().resolve(String.class));
		if (dataListHint != null) {
			schema.put("datalist", dataListHint);
		}
		return new CustomDefinition(schema, CustomDefinition.DefinitionType.INLINE,
				CustomDefinition.AttributeInclusion.YES);
	}

	private CustomDefinition createRetvalProcedureDefinition(ResolvedType type, SchemaGenerationContext context) {
		ResolvedType fixedValueType = this.resolveRetvalFixedValueType(type, context);
		if (fixedValueType == null) {
			return null;
		}

		SchemaGeneratorConfig config = context.getGeneratorConfig();
		ArrayNode oneOf = config.createArrayNode();
		oneOf.add(context.createDefinition(fixedValueType));
		oneOf.add(this.createRetvalProcedureObjectForm(context, fixedValueType));

		ObjectNode schema = config.createObjectNode().set(context.getKeyword(SchemaKeyword.TAG_ONEOF), oneOf);
		return new CustomDefinition(schema, CustomDefinition.DefinitionType.INLINE,
				CustomDefinition.AttributeInclusion.YES);
	}

	private ObjectNode createRetvalProcedureObjectForm(SchemaGenerationContext context, ResolvedType fixedValueType) {
		SchemaGeneratorConfig config = context.getGeneratorConfig();
		ObjectNode properties = config.createObjectNode();
		properties.set("name", context.createDefinitionReference(context.getTypeContext().resolve(String.class)));
		properties.set("fixedValue", context.createDefinition(fixedValueType));

		ArrayNode required = config.createArrayNode();
		required.add("fixedValue");

		return config.createObjectNode().put(context.getKeyword(SchemaKeyword.TAG_TYPE), "object")
				.set(context.getKeyword(SchemaKeyword.TAG_PROPERTIES), properties)
				.set(context.getKeyword(SchemaKeyword.TAG_REQUIRED), required);
	}

	@Nullable private ResolvedType resolveRetvalFixedValueType(ResolvedType type, SchemaGenerationContext context) {
		Class<?> erasedType = type.getErasedType();
		TypeContext typeContext = context.getTypeContext();

		if (LogicProcedure.class.isAssignableFrom(erasedType)) {
			return typeContext.resolve(Boolean.class);
		}
		if (NumberProcedure.class.isAssignableFrom(erasedType)) {
			return typeContext.resolve(Double.class);
		}
		if (StringProcedure.class.isAssignableFrom(erasedType)) {
			return typeContext.resolve(String.class);
		}
		if (StringListProcedure.class.isAssignableFrom(erasedType)) {
			return typeContext.resolve(ArrayList.class, String.class);
		}

		return null;
	}

	@SuppressWarnings("RedundantIfStatement") private boolean isRequired(FieldScope fieldScope) {
		if (this.getAnnotationFromFieldOrGetter(fieldScope, Nonnull.class) != null) {
			return true; // Nonnull fields are required
		}

		if (this.getAnnotationFromFieldOrGetter(fieldScope, BlocklyXML.class) != null) {
			return true; // BlocklyXML fields are required
		}

		if (RetvalProcedure.class.isAssignableFrom(fieldScope.getType().getErasedType())) {
			return true; // Retval procedure requires at least fixed value
		} else if (Procedure.class.isAssignableFrom(fieldScope.getType().getErasedType())) {
			return false; // Procedure fields are always optional
		}

		if (this.getAnnotationFromFieldOrGetter(fieldScope, NonNullMappable.class) != null) {
			return true; // NonNullMappable fields are required
		} else if (this.getAnnotationFromFieldOrGetter(fieldScope, LimitedOptions.class) != null) {
			return true; // LimitedOptions fields are required
		} else if (this.getAnnotationFromFieldOrGetter(fieldScope, Numeric.class) != null) {
			return true; // Numeric fields are required
		}

		return false;
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

		BlocklyXML blocklyXML = this.getAnnotationFromFieldOrGetter(member, BlocklyXML.class);
		if (blocklyXML != null) {
			return blocklyXML.defaultXML();
		}

		TextureReference textureReference = this.getAnnotationFromFieldOrGetter(member, TextureReference.class);
		if (textureReference != null && textureReference.defaultValues() != null
				&& textureReference.defaultValues().length > 0) {
			return textureReference.defaultValues()[0];
		}

		ModElementReference modElementReference = this.getAnnotationFromFieldOrGetter(member,
				ModElementReference.class);
		if (modElementReference != null && modElementReference.defaultValues() != null
				&& modElementReference.defaultValues().length > 0) {
			return modElementReference.defaultValues()[0];
		}

		return null;
	}

	private void applyCustomAttributes(ObjectNode node, MemberScope<?, ?> member, SchemaGenerationContext context) {
		LimitedOptions limitedOptions = this.getAnnotationFromFieldOrGetter(member, LimitedOptions.class);
		if (limitedOptions != null) {
			if (this.isNumericType(member.getType().getErasedType())) {
				node.put("min", 0);
				node.put("max", limitedOptions.value().length - 1);
			}
		}

		Numeric numeric = this.getAnnotationFromFieldOrGetter(member, Numeric.class);
		if (numeric != null) {
			if (numeric.allowMinMaxEqual()) {
				node.put("allowMinMaxEqual", true);
			}
		}

		TextureReference textureReference = this.getAnnotationFromFieldOrGetter(member, TextureReference.class);
		if (textureReference != null) {
			node.put("textureType", textureReference.value().getID());
		}

		ModElementReference modElementReference = this.getAnnotationFromFieldOrGetter(member,
				ModElementReference.class);
		if (modElementReference != null) {
			List<String> acceptedTypes = new ArrayList<>();
			for (Class<? extends GeneratableElement> type : modElementReference.acceptedTypes()) {
				acceptedTypes.add(type.getSimpleName());
			}
			ArrayNode arrayNode = node.putArray("acceptedElementTypes");
			for (String acceptedType : acceptedTypes) {
				arrayNode.add(acceptedType);
			}
		}

		BlocklyXML blocklyXML = this.getAnnotationFromFieldOrGetter(member, BlocklyXML.class);
		if (blocklyXML != null) {
			node.put("blocklyXML", "This field requires valid Blockly XML");
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
