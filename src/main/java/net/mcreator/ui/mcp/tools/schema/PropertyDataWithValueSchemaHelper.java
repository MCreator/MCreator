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

import com.github.victools.jsonschema.generator.CustomDefinition;
import com.github.victools.jsonschema.generator.SchemaGenerationContext;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaKeyword;
import net.mcreator.ui.minecraft.states.PropertyData;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

final class PropertyDataWithValueSchemaHelper {

	static CustomDefinition createDefinition(SchemaGenerationContext context) {
		SchemaGeneratorConfig config = context.getGeneratorConfig();
		ArrayNode oneOf = config.createArrayNode();
		for (String typeId : PropertyData.getTypeMappings().keySet()) {
			oneOf.add(createVariant(context, typeId));
		}

		ObjectNode schema = config.createObjectNode().set(context.getKeyword(SchemaKeyword.TAG_ONEOF), oneOf);
		return new CustomDefinition(schema, CustomDefinition.DefinitionType.INLINE,
				CustomDefinition.AttributeInclusion.YES);
	}

	private static ObjectNode createVariant(SchemaGenerationContext context, String typeId) {
		SchemaGeneratorConfig config = context.getGeneratorConfig();
		ObjectNode properties = config.createObjectNode();
		properties.set("type", config.createObjectNode().put(context.getKeyword(SchemaKeyword.TAG_CONST), typeId));
		properties.set("name", context.createDefinitionReference(context.getTypeContext().resolve(String.class)));

		switch (typeId) {
			case "integer", "number" -> {
				Class<?> boundType = typeId.equals("integer") ? Integer.class : Double.class;
				properties.set("min", context.createDefinition(context.getTypeContext().resolve(boundType)));
				properties.set("max", context.createDefinition(context.getTypeContext().resolve(boundType)));
			}
			case "string" -> {
				ObjectNode arrayDataSchema = config.createObjectNode().put(context.getKeyword(SchemaKeyword.TAG_TYPE),
						"array");
				arrayDataSchema.set(context.getKeyword(SchemaKeyword.TAG_ITEMS),
						context.createDefinitionReference(context.getTypeContext().resolve(String.class)));
				properties.set("arrayData", arrayDataSchema);
			}
		}

		properties.set("value",
				context.createDefinition(context.getTypeContext().resolve(resolveValueType(typeId))));

		ArrayNode required = config.createArrayNode();
		required.add("type");
		required.add("value");

		return config.createObjectNode().put(context.getKeyword(SchemaKeyword.TAG_TYPE), "object")
				.set(context.getKeyword(SchemaKeyword.TAG_PROPERTIES), properties)
				.set(context.getKeyword(SchemaKeyword.TAG_REQUIRED), required);
	}

	private static Class<?> resolveValueType(String typeId) {
		return switch (typeId) {
			case "logic" -> Boolean.class;
			case "integer" -> Integer.class;
			case "number" -> Double.class;
			case "string" -> String.class;
			default -> throw new IllegalArgumentException("Unknown property data type: " + typeId);
		};
	}

}
