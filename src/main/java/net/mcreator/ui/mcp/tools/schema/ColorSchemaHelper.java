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
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

final class ColorSchemaHelper {

	static CustomDefinition createDefinition(SchemaGenerationContext context) {
		SchemaGeneratorConfig config = context.getGeneratorConfig();
		ObjectNode properties = config.createObjectNode();
		properties.set("value", context.createDefinition(context.getTypeContext().resolve(Integer.class)));

		ArrayNode required = config.createArrayNode();
		required.add("value");

		ObjectNode schema = config.createObjectNode().put(context.getKeyword(SchemaKeyword.TAG_TYPE), "object")
				.set(context.getKeyword(SchemaKeyword.TAG_PROPERTIES), properties)
				.set(context.getKeyword(SchemaKeyword.TAG_REQUIRED), required);
		return new CustomDefinition(schema, CustomDefinition.DefinitionType.INLINE,
				CustomDefinition.AttributeInclusion.YES);
	}

}
