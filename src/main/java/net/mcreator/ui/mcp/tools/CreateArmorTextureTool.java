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

package net.mcreator.ui.mcp.tools;

import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.ArmorMakerTexturesCache;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.views.ArmorImageMakerView;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CreateArmorTextureTool extends MCreatorMcpTool<CreateArmorTextureTool.Args> {

	public static class Args {
		public Action actionType;
		@Nullable public String name;
		@Nullable public String template;
		@Nullable public String color;
		@Nullable public Boolean lockSaturationBrightness;

		public enum Action {
			CREATE, LIST_TEMPLATES
		}
	}

	public CreateArmorTextureTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "create_armor_texture";
	}

	@Override public String getDescription() {
		return """
				Creates a full armor texture set (layer textures and item icons) using armor image maker templates, or lists available template names.
				Use actionType CREATE with name, template, and color. Optional lockSaturationBrightness.""";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		if (input.actionType == null) {
			return CompletableFuture.completedFuture(ToolResult.error("actionType must be provided"));
		}

		return switch (input.actionType) {
			case LIST_TEMPLATES -> CompletableFuture.completedFuture(listTemplates());
			case CREATE -> CompletableFuture.completedFuture(createArmorTexture(mcreator, input));
		};
	}

	private static ToolResult listTemplates() {
		List<String> templates = Arrays.asList(ArmorMakerTexturesCache.getTemplateNames());
		templates.sort(Comparator.naturalOrder());
		return ToolResult.collection(templates);
	}

	private static ToolResult createArmorTexture(MCreator mcreator, Args input) {
		if (input.name == null || input.name.isBlank()) {
			return ToolResult.error("Armor texture name must be provided");
		}
		if (input.color == null || input.color.isBlank()) {
			return ToolResult.error("Color must be provided as a hex value (e.g. #FF5500)");
		}

		String fixedName = RegistryNameFixer.fix(input.name);
		if (fixedName.isEmpty()) {
			return ToolResult.error("Invalid armor texture name");
		}

		String template = input.template;
		if (template == null || template.isBlank()) {
			template = "Standard";
		} else if (!isKnownTemplate(template)) {
			return ToolResult.error("Unknown armor template: " + template);
		}

		Color color;
		try {
			color = Color.decode(input.color.trim());
		} catch (NumberFormatException e) {
			return ToolResult.error("Invalid color value: " + input.color);
		}

		File[] armorLayers = mcreator.getFolderManager().getArmorTextureFilesForName(fixedName);
		if (armorLayers[0].isFile() || armorLayers[1].isFile()) {
			return ToolResult.error("Armor texture with this name already exists");
		}

		boolean lockSaturationBrightness = input.lockSaturationBrightness != null && input.lockSaturationBrightness;
		boolean colorizeType = !lockSaturationBrightness;

		try {
			ArmorImageMakerView.generateArmorImages(mcreator.getWorkspace(), fixedName, template, color, colorizeType);
			mcreator.reloadWorkspaceTabContents();

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("name", fixedName);
			response.put("template", template);
			response.put("armorLayerTextures", List.of(armorLayers[0].getName(), armorLayers[1].getName()));
			response.put("itemTextures",
					List.of(fixedName + "_head", fixedName + "_body", fixedName + "_leggings", fixedName + "_boots"));
			return ToolResult.object(response);
		} catch (Exception e) {
			return ToolResult.error("Failed to create armor texture: " + e.getMessage());
		}
	}

	private static boolean isKnownTemplate(String template) {
		for (String knownTemplate : ArmorMakerTexturesCache.getTemplateNames()) {
			if (knownTemplate.equalsIgnoreCase(template.trim())) {
				return true;
			}
		}
		return false;
	}
}
