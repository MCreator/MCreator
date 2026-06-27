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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import net.mcreator.blockly.data.BlocklyXML;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.procedure.*;
import net.mcreator.element.types.Biome;
import net.mcreator.element.types.interfaces.LimitedOptions;
import net.mcreator.element.types.interfaces.NonNullMappable;
import net.mcreator.element.types.interfaces.Numeric;
import net.mcreator.element.util.GEValidator;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;
import net.mcreator.ui.minecraft.states.StateMap;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class GeneratableElementModule implements Module {

	private static final Gson DEFAULT_VALUE_GSON = new GsonBuilder().disableHtmlEscaping()
			.setStrictness(Strictness.LENIENT)
			.registerTypeHierarchyAdapter(MappableElement.class, new MappableElement.GSONAdapter())
			.registerTypeAdapter(StateMap.class, new StateMap.GSONAdapter()).create();

	private final Map<Class<?>, Object> defaultInstanceCache = new HashMap<>();

	@Override public void applyToConfigBuilder(SchemaGeneratorConfigBuilder builder) {
		SchemaGeneratorConfigPart<FieldScope> fieldConfigPart = builder.forFields();
		fieldConfigPart.withNullableCheck(this::isNullable);
		fieldConfigPart.withEnumResolver(this::resolveEnum);
		fieldConfigPart.withNumberInclusiveMinimumResolver(this::resolveMinimum);
		fieldConfigPart.withNumberInclusiveMaximumResolver(this::resolveMaximum);
		fieldConfigPart.withDefaultResolver(this::resolveDefault);
		fieldConfigPart.withInstanceAttributeOverride(this::applyCustomAttributes);
		fieldConfigPart.withRequiredCheck(this::isRequired);

		SchemaGeneratorConfigPart<MethodScope> methodConfigPart = builder.forMethods();
		methodConfigPart.withIgnoreCheck(_ -> true); // do not include methods

		Stream.of(Nullable.class, Nonnull.class, LimitedOptions.class, Numeric.class, NonNullMappable.class,
				BlocklyXML.class).forEach(annotationType -> builder.withAnnotationInclusionOverride(annotationType,
				AnnotationInclusion.INCLUDE_AND_INHERIT));

		builder.forTypesInGeneral().withCustomDefinitionProvider(this::provideCustomDefinition);
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
		} else if (erasedType == GUIComponent.class) {
			return GUIComponentSchemaHelper.createDefinition(context);
		} else if (erasedType == PropertyData.class) {
			return PropertyDataSchemaHelper.createDefinition(context);
		} else if (erasedType == PropertyDataWithValue.class) {
			return PropertyDataWithValueSchemaHelper.createDefinition(context);
		} else if (erasedType == StateMap.class) {
			return StateMapSchemaHelper.createDefinition(context);
		} else if (erasedType == Color.class) {
			return ColorSchemaHelper.createDefinition(context);
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
		if (this.getAnnotationFromFieldOrGetter(fieldScope, Nullable.class) != null) {
			return false; // Nullable fields are not required
		}

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

		if (Color.class.isAssignableFrom(fieldScope.getType().getErasedType())) {
			return true; // Color fields are always required unless marked with Nullable
		}

		if (MappableElement.class.isAssignableFrom(fieldScope.getType().getErasedType())) {
			return true; // MappableElement fields are required unless marked with Nullable
		}

		if (this.getAnnotationFromFieldOrGetter(fieldScope, LimitedOptions.class) != null) {
			return false; // LimitedOptions fields are optional since GEValidator will set default value if missing
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
		if (this.getAnnotationFromFieldOrGetter(member, Nullable.class) != null) {
			return null; // Nullable fields are not required, so we assume no default value
		}

		// Procedure fields are always null by default
		if (RetvalProcedure.class.isAssignableFrom(member.getType().getErasedType())) {
			return null;
		} else if (Procedure.class.isAssignableFrom(member.getType().getErasedType())) {
			return null;
		}

		if (MappableElement.class.isAssignableFrom(member.getType().getErasedType())) {
			NonNullMappable nonNullMappable = this.getAnnotationFromFieldOrGetter(member, NonNullMappable.class);
			if (nonNullMappable != null) {
				return nonNullMappable.value();
			} else {
				return "";
			}
		}

		Numeric numeric = this.getAnnotationFromFieldOrGetter(member, Numeric.class);
		if (numeric != null) {
			return GEValidator.castNumber(member.getType().getErasedType(), numeric.init());
		}

		LimitedOptions limitedOptions = this.getAnnotationFromFieldOrGetter(member, LimitedOptions.class);
		if (limitedOptions != null && member.getType().getErasedType() == String.class) {
			return limitedOptions.value().length > 0 ? limitedOptions.value()[0] : null;
		}

		BlocklyXML blocklyXML = this.getAnnotationFromFieldOrGetter(member, BlocklyXML.class);
		if (blocklyXML != null) {
			String suggestedXML = blocklyXML.suggestedXML();
			if (suggestedXML != null && !suggestedXML.isEmpty()) {
				return suggestedXML;
			}
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

		// If no other method of detecting default values is available, we attempt to instantiate the class and retrieve the default value from the field
		if (!member.isFakeContainerItemScope()) {
			try {
				Class<?> clazz = member.getDeclaringType().getErasedType();
				if (!defaultInstanceCache.containsKey(clazz)) {
					try {
						Constructor<?> constructor = clazz.getDeclaredConstructor();
						constructor.setAccessible(true);
						defaultInstanceCache.put(clazz, constructor.newInstance());
					} catch (Exception e) {
						defaultInstanceCache.put(clazz, null);
					}
				}
				Object instance = defaultInstanceCache.get(clazz);
				if (instance != null) {
					if (member.getRawMember() instanceof Field rawField) {
						rawField.setAccessible(true);
						Object fieldValue = rawField.get(instance);
						if (fieldValue != null) {
							return DEFAULT_VALUE_GSON.fromJson(DEFAULT_VALUE_GSON.toJson(fieldValue), Object.class);
						}
					}
				}
			} catch (Exception _) {
			}
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

			if (limitedOptions.allowCustom()) {
				node.put("allowCustomValues", true);
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
			node.put("blocklyEditorType", blocklyXML.name());
			appendDescription(node, """
					Valid Blockly XML string. Use blockly_templates for reference XML and blockly_blocks for available blocks.\
					For trigger-based editors, use blockly_triggers with blocklyEditorType=%s.""".formatted(
					blocklyXML.name()));
		}

		if (member.getDeclaringType().getErasedType() == Biome.class && "defaultFeatures".equals(
				member.getDeclaredName())) {
			node.put("datalist", "defaultfeatures");
		}

		RenderModelSchemaHelper.applyFieldAttributes(node, member, context);
	}

	private boolean isNumericType(Class<?> type) {
		return Number.class.isAssignableFrom(type) || type == int.class || type == long.class || type == float.class
				|| type == double.class || type == short.class || type == byte.class;
	}

	private static void appendDescription(ObjectNode node, String addition) {
		if (node.has("description")) {
			node.put("description", node.get("description").asString() + " " + addition);
		} else {
			node.put("description", addition);
		}
	}

	protected Boolean isNullable(MemberScope<?, ?> member) {
		Boolean result;
		if (member.isFakeContainerItemScope()) {
			result = null;
		} else if (this.getAnnotationFromFieldOrGetter(member, Nonnull.class) != null
				|| this.getAnnotationFromFieldOrGetter(member, BlocklyXML.class) != null) {
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
