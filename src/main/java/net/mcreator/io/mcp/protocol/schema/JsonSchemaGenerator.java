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

package net.mcreator.io.mcp.protocol.schema;

import com.github.victools.jsonschema.generator.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import tools.jackson.databind.node.ObjectNode;

import java.lang.reflect.Type;

public class JsonSchemaGenerator {

    private final SchemaGenerator generator;
    private final Gson gson = new Gson();

    public JsonSchemaGenerator() {
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON);
        configBuilder.with(Option.EXTRA_OPEN_API_FORMAT_VALUES);
        SchemaGeneratorConfig config = configBuilder.build();
        this.generator = new SchemaGenerator(config);
    }

    public JsonObject generateSchema(Type type) {
        ObjectNode jsonNode = generator.generateSchema(type);
        return gson.fromJson(jsonNode.toString(), JsonObject.class);
    }
}
