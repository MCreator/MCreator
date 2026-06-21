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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import tools.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ModElementSchemaTool extends MCreatorMcpTool<ModElementSchemaTool.Args> {

	public static class Args {
		public String elementType;
	}

	private final SchemaGenerator generator;
	private final Gson gson = new Gson();

	private static final Map<ModElementType<?>, JsonObject> SCHEMA_CACHE = new HashMap<>();

	public ModElementSchemaTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, ModElementSchemaTool.Args.class);

		SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12,
				OptionPreset.PLAIN_JSON);
		configBuilder.with(Option.EXTRA_OPEN_API_FORMAT_VALUES);
		configBuilder.with(Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES);
		configBuilder.with(new GeneratableElementModule());
		SchemaGeneratorConfig config = configBuilder.build();
		this.generator = new SchemaGenerator(config);
	}

	@Override public String getName() {
		return "get_mod_element_schema";
	}

	@Override public String getDescription() {
		return """
				Provides JSON schema for selected mod element type JSON definition.\
				Fill out all required fields, if not sure, use default values.""";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, ModElementSchemaTool.Args input) {
		if (input.elementType == null) {
			return CompletableFuture.completedFuture(ToolResult.error("Element type must be provided"));
		}
		ModElementType<?> type = ModElementTypeLoader.getModElementType(input.elementType.toLowerCase(Locale.ROOT));
		return CompletableFuture.completedFuture(ToolResult.object(generateSchema(type)));
	}

	public JsonObject generateSchema(ModElementType<?> type) {
		return SCHEMA_CACHE.computeIfAbsent(type, t -> {
			Class<? extends GeneratableElement> elementClass = t.getModElementStorageClass();
			ObjectNode jsonNode = generator.generateSchema(elementClass);
			return gson.fromJson(jsonNode.toString(), JsonObject.class);
		});
	}

}

