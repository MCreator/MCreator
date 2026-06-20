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

import net.mcreator.io.FileIO;
import net.mcreator.io.ResourcePointer;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CreateTextureTool extends MCreatorMcpTool<CreateTextureTool.Args> {

	// TODO: armor handling

	public static class Args {
		public Action actionType;
		@Nullable public String name;
		@Nullable public String type;
		@Nullable public List<Layer> layers;

		public enum Action {
			CREATE, LIST_TEMPLATES
		}
	}

	public static class Layer {
		public String template;
		@Nullable public Integer rotation;
		@Nullable public String color;
		@Nullable public Boolean lockSaturationBrightness;
	}

	public CreateTextureTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "create_texture";
	}

	@Override public String getDescription() {
		return """
				Creates a workspace texture by compositing image maker template layers, or lists available template names.\
				Use actionType CREATE with name, type (block/item/entity/effect/particle/screen/other), and at least one layer.""";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		if (input.actionType == null) {
			return CompletableFuture.completedFuture(ToolResult.error("actionType must be provided"));
		}

		return switch (input.actionType) {
			case LIST_TEMPLATES -> CompletableFuture.completedFuture(listTemplates());
			case CREATE -> CompletableFuture.completedFuture(createTexture(mcreator, input));
		};
	}

	private static ToolResult listTemplates() {
		List<String> templates = new ArrayList<>();
		for (ResourcePointer pointer : ImageMakerTexturesCache.CACHE.keySet()) {
			String name = pointer.toString();
			if (!name.contains("(no image)")) {
				templates.add(name);
			}
		}
		templates.sort(Comparator.naturalOrder());
		return ToolResult.collection(templates);
	}

	private static ToolResult createTexture(MCreator mcreator, Args input) {
		if (input.name == null || input.name.isBlank()) {
			return ToolResult.error("Texture name must be provided");
		}
		if (input.layers == null || input.layers.isEmpty()) {
			return ToolResult.error("At least one layer must be provided");
		}

		TextureType textureType = parseTextureType(input.type);
		if (textureType == null) {
			return ToolResult.error("Invalid texture type: " + input.type);
		}
		if (!textureType.isSupported(mcreator.getWorkspace())) {
			return ToolResult.error("Texture type is not supported by the current generator: " + input.type);
		}

		String fixedName = RegistryNameFixer.fix(input.name);
		if (fixedName.isEmpty()) {
			return ToolResult.error("Invalid texture name");
		}

		File exportFile = mcreator.getFolderManager().getTextureFile(fixedName, textureType);
		if (exportFile.isFile()) {
			return ToolResult.error("Texture with this name already exists");
		}

		try {
			BufferedImage image = composeLayers(input.layers);
			FileIO.writeImageToPNGFile(image, exportFile);
			new ImageIcon(exportFile.getAbsolutePath()).getImage().flush();
			mcreator.reloadWorkspaceTabContents();

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("name", fixedName);
			return ToolResult.object(response);
		} catch (IllegalArgumentException e) {
			return ToolResult.error(e.getMessage(), e);
		} catch (Exception e) {
			return ToolResult.error("Failed to create texture: " + e.getMessage(), e);
		}
	}

	private static BufferedImage composeLayers(List<Layer> layers) throws IOException {
		ImageIcon result = processLayer(layers.getFirst());
		for (int i = 1; i < layers.size(); i++) {
			result = ImageUtils.drawOver(result, processLayer(layers.get(i)));
		}
		return ImageUtils.toBufferedImage(result.getImage());
	}

	private static ImageIcon processLayer(Layer layer) throws IOException {
		if (layer.template == null || layer.template.isBlank()) {
			throw new IllegalArgumentException("Each layer must have a template name");
		}

		ResourcePointer pointer = findTemplate(layer.template);
		if (pointer == null) {
			throw new IllegalArgumentException("Unknown template: " + layer.template);
		}
		ImageIcon icon = new ImageIcon(ImageIO.read(pointer.getStream()));

		if (layer.rotation != null && layer.rotation != 0) {
			icon = ImageUtils.rotate(icon, layer.rotation % 360);
		}

		if (layer.color != null && !layer.color.isBlank()) {
			Color color = Color.decode(layer.color);
			boolean preserveSaturation = layer.lockSaturationBrightness != null && layer.lockSaturationBrightness;
			icon = ImageUtils.colorize(icon, color, !preserveSaturation);
		}

		return icon;
	}

	@Nullable private static ResourcePointer findTemplate(String templateName) {
		String lookup = templateName.trim();
		for (ResourcePointer pointer : ImageMakerTexturesCache.CACHE.keySet()) {
			if (pointer.toString().equalsIgnoreCase(lookup)) {
				return pointer;
			}
		}
		return null;
	}

	@Nullable private static TextureType parseTextureType(String type) {
		for (TextureType textureType : TextureType.values()) {
			if (textureType.getID().equalsIgnoreCase(type) || textureType.name().equalsIgnoreCase(type)) {
				return textureType;
			}
		}
		return null;
	}
}
