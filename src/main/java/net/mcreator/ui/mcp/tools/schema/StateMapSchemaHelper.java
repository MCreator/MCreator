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

import com.github.victools.jsonschema.generator.*;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

final class StateMapSchemaHelper {

	static CustomDefinition createDefinition(SchemaGenerationContext context) {
		SchemaGeneratorConfig config = context.getGeneratorConfig();

		ObjectNode properties = config.createObjectNode();
		properties.set("property", PropertyDataSchemaHelper.createDefinition(context).getValue());
		properties.set("value", createValueDefinition(context));

		ArrayNode required = config.createArrayNode();
		required.add("property");
		required.add("value");

		ObjectNode itemSchema = config.createObjectNode().put(context.getKeyword(SchemaKeyword.TAG_TYPE), "object")
				.set(context.getKeyword(SchemaKeyword.TAG_PROPERTIES), properties)
				.set(context.getKeyword(SchemaKeyword.TAG_REQUIRED), required);

		ObjectNode schema = config.createObjectNode().put(context.getKeyword(SchemaKeyword.TAG_TYPE), "array")
				.set(context.getKeyword(SchemaKeyword.TAG_ITEMS), itemSchema);

		return new CustomDefinition(schema, CustomDefinition.DefinitionType.INLINE,
				CustomDefinition.AttributeInclusion.YES);
	}

	private static ObjectNode createValueDefinition(SchemaGenerationContext context) {
		SchemaGeneratorConfig config = context.getGeneratorConfig();
		TypeContext typeContext = context.getTypeContext();
		ArrayNode oneOf = config.createArrayNode();
		oneOf.add(context.createDefinition(typeContext.resolve(Boolean.class)));
		oneOf.add(context.createDefinition(typeContext.resolve(Integer.class)));
		oneOf.add(context.createDefinition(typeContext.resolve(Double.class)));
		oneOf.add(context.createDefinition(typeContext.resolve(String.class)));
		return config.createObjectNode().set(context.getKeyword(SchemaKeyword.TAG_ONEOF), oneOf);
	}

}
