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

import java.util.Map;

final class PropertyDataSchemaHelper {

	static CustomDefinition createDefinition(SchemaGenerationContext context) {
		SchemaGeneratorConfig config = context.getGeneratorConfig();
		ArrayNode oneOf = config.createArrayNode();
		for (Map.Entry<String, Class<? extends PropertyData<?>>> entry : PropertyData.getTypeMappings().entrySet()) {
			oneOf.add(createVariant(context, entry.getKey(), entry.getValue()));
		}

		ObjectNode schema = config.createObjectNode().set(context.getKeyword(SchemaKeyword.TAG_ONEOF), oneOf);
		return new CustomDefinition(schema, CustomDefinition.DefinitionType.INLINE,
				CustomDefinition.AttributeInclusion.YES);
	}

	private static ObjectNode createVariant(SchemaGenerationContext context, String typeId,
			Class<? extends PropertyData<?>> propertyClass) {
		SchemaGeneratorConfig config = context.getGeneratorConfig();
		ArrayNode allOf = config.createArrayNode();
		allOf.add(createTypeConstConstraint(context, typeId));
		allOf.add(context.createDefinition(context.getTypeContext().resolve(propertyClass)));
		return config.createObjectNode().set(context.getKeyword(SchemaKeyword.TAG_ALLOF), allOf);
	}

	private static ObjectNode createTypeConstConstraint(SchemaGenerationContext context, String typeId) {
		SchemaGeneratorConfig config = context.getGeneratorConfig();
		ObjectNode properties = config.createObjectNode();
		properties.set("type", config.createObjectNode().put(context.getKeyword(SchemaKeyword.TAG_CONST), typeId));

		ArrayNode required = config.createArrayNode();
		required.add("type");

		return config.createObjectNode().put(context.getKeyword(SchemaKeyword.TAG_TYPE), "object")
				.set(context.getKeyword(SchemaKeyword.TAG_PROPERTIES), properties)
				.set(context.getKeyword(SchemaKeyword.TAG_REQUIRED), required);
	}

}
