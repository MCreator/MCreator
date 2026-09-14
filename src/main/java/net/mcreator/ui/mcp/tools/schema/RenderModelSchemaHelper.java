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

import com.github.victools.jsonschema.generator.MemberScope;
import com.github.victools.jsonschema.generator.SchemaGenerationContext;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import net.mcreator.element.types.Block;
import net.mcreator.element.types.Item;
import net.mcreator.element.types.Plant;
import net.mcreator.element.types.Tool;
import net.mcreator.element.types.bedrock.BEBlock;
import net.mcreator.ui.modgui.BlockGUI;
import net.mcreator.ui.modgui.ItemGUI;
import net.mcreator.ui.modgui.PlantGUI;
import net.mcreator.ui.modgui.ToolGUI;
import net.mcreator.ui.modgui.bedrock.BEBlockGUI;
import net.mcreator.workspace.resources.Model;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import javax.annotation.Nullable;
import java.util.List;

final class RenderModelSchemaHelper {

	@Nullable private static List<Option> optionsFor(Class<?> declaringType, String fieldName) {
		if (declaringType == Block.class) {
			return blockOptions();
		} else if (declaringType == Block.StateEntry.class) {
			return blockStateOptions();
		} else if (declaringType == Item.class) {
			return itemOptions();
		} else if (declaringType == Item.StateEntry.class) {
			return itemStateOptions();
		} else if (declaringType == Plant.class) {
			return plantOptions();
		} else if (declaringType == BEBlock.class) {
			return beBlockOptions();
		} else if (declaringType == Tool.class) {
			if ("blockingRenderType".equals(fieldName)) {
				return toolBlockingOptions();
			}
			return toolOptions();
		}
		return null;
	}

	static void applyFieldAttributes(ObjectNode node, MemberScope<?, ?> member, SchemaGenerationContext context) {
		String fieldName = member.getDeclaredName();
		if (!"renderType".equals(fieldName) && !"blockingRenderType".equals(fieldName)) {
			return;
		}

		List<Option> options = optionsFor(member.getDeclaringType().getErasedType(), member.getDeclaredName());
		if (options == null) {
			return;
		}

		SchemaGeneratorConfig config = context.getGeneratorConfig();
		ArrayNode array = config.createArrayNode();
		for (Option option : options) {
			ObjectNode entry = config.createObjectNode();
			entry.put("renderType", option.renderType());
			entry.put("customModelName", option.customModelName());
			array.add(entry);
		}

		node.set("modelOptions", array);
		node.put("description", "Set renderType and customModelName to a matching pair from modelOptions.");
	}

	private static List<Option> blockOptions() {
		//@formatter:off
		return List.of(
				option(10, BlockGUI.normal),
				option(11, BlockGUI.singleTexture),
				option(110, BlockGUI.singleTexture),
				option(12, BlockGUI.cross),
				option(120, BlockGUI.cross),
				option(13, BlockGUI.crop),
				option(14, BlockGUI.grassBlock),
				option(15, BlockGUI.pottedPlantModel),
				option(150, BlockGUI.pottedPlantModel),
				option(2, "<JSON model name>"),
				option(3, "<OBJ model name>"),
				option(4, "<JAVA model name>")
		);
		//@formatter:on
	}

	private static List<Option> blockStateOptions() {
		//@formatter:off
		return List.of(
				option(10, BlockGUI.normal),
				option(11, BlockGUI.singleTexture),
				option(110, BlockGUI.singleTexture),
				option(12, BlockGUI.cross),
				option(120, BlockGUI.cross),
				option(13, BlockGUI.crop),
				option(2, "<JSON model name>"),
				option(3, "<OBJ model name>")
		);
		//@formatter:on
	}

	private static List<Option> itemOptions() {
		//@formatter:off
		return List.of(
				option(0, ItemGUI.normal),
				option(0, ItemGUI.tool),
				option(0, ItemGUI.rangedItem),
				option(1, "<JSON model name>"),
				option(2, "<OBJ model name>"),
				option(3, "<JAVA model name>")
		);
		//@formatter:on
	}

	private static List<Option> itemStateOptions() {
		//@formatter:off
		return List.of(
				option(0, ItemGUI.normal),
				option(0, ItemGUI.tool),
				option(0, ItemGUI.rangedItem),
				option(1, "<JSON model name>"),
				option(2, "<OBJ model name>")
		);
		//@formatter:on
	}

	private static List<Option> plantOptions() {
		//@formatter:off
		return List.of(
				option(12, PlantGUI.cross),
				option(120, PlantGUI.cross),
				option(13, PlantGUI.crop),
				option(2, "<JSON model name>"),
				option(3, "<OBJ model name>")
		);
		//@formatter:on
	}

	private static List<Option> toolOptions() {
		//@formatter:off
		return List.of(
				option(0, ToolGUI.normal),
				option(1, "<JSON model name>"),
				option(2, "<OBJ model name>")
		);
		//@formatter:on
	}

	private static List<Option> toolBlockingOptions() {
		//@formatter:off
		return List.of(
				option(0, ToolGUI.normalBlocking),
				option(1, "<JSON model name>"),
				option(2, "<OBJ model name>")
		);
		//@formatter:on
	}

	private static List<Option> beBlockOptions() {
		//@formatter:off
		return List.of(
				option(10, BEBlockGUI.normal),
				option(11, BEBlockGUI.cross),
				option(12, BEBlockGUI.singleTexture),
				option(2, "<BEDROCK model name>")
		);
		//@formatter:on
	}

	private static Option option(int renderType, Model model) {
		return new Option(renderType, model.getReadableName());
	}

	private static Option option(int renderType, String customModelName) {
		return new Option(renderType, customModelName);
	}

	private record Option(int renderType, String customModelName) {}

}
