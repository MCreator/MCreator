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
import net.mcreator.io.mcp.protocol.SchemaDescription;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CreateTextureTool extends MCreatorMcpTool<CreateTextureTool.Args> {

	public static class Args {
		public Action actionType;
		@Nullable public String name;
		@Nullable public String type;
		@SchemaDescription("Image maker template layers for CREATE") @Nullable public List<Layer> layers;
		@SchemaDescription("Base64 encoded PNG image for IMPORT") @Nullable public String data;

		public enum Action {
			CREATE, IMPORT, LIST_TEMPLATES
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
				Creates a workspace texture by compositing image maker template layers (CREATE), imports a texture from \
				base64 encoded PNG data (IMPORT), or lists available template names (LIST_TEMPLATES). \
				CREATE and IMPORT require name and type (block/item/entity/effect/particle/screen/other). \
				CREATE needs at least one layer, IMPORT needs data.""";
	}

	@Override protected Boolean getReadOnlyHint() {
		return false;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		if (input.actionType == null) {
			return CompletableFuture.completedFuture(ToolResult.error("actionType must be provided"));
		}

		return switch (input.actionType) {
			case LIST_TEMPLATES -> CompletableFuture.completedFuture(listTemplates());
			case CREATE -> CompletableFuture.completedFuture(exportTexture(mcreator, input, () -> {
				if (input.layers == null || input.layers.isEmpty()) {
					throw new IllegalArgumentException("At least one layer must be provided");
				}
				return composeLayers(input.layers);
			}));
			case IMPORT -> CompletableFuture.completedFuture(
					exportTexture(mcreator, input, () -> decodeImage(input.data)));
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

	private static ToolResult exportTexture(MCreator mcreator, Args input, ImageSource imageSource) {
		if (input.name == null || input.name.isBlank()) {
			return ToolResult.error("Texture name must be provided");
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
		} else if (!mcreator.getWorkspace().getFolderManager().isFileInWorkspace(exportFile)) {
			return ToolResult.error("Texture file is not in the workspace");
		}

		try {
			BufferedImage image = imageSource.get();
			FileIO.writeImageToPNGFile(image, exportFile);
			new ImageIcon(exportFile.getAbsolutePath()).getImage().flush();
			ThreadUtil.runOnSwingThreadAndWait(mcreator::reloadWorkspaceTabContents);

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

	private static BufferedImage decodeImage(@Nullable String data) throws IOException {
		if (data == null || data.isBlank()) {
			throw new IllegalArgumentException("Base64 PNG data must be provided");
		}
		String base64 = data.trim();
		if (base64.startsWith("data:")) { // strip data URL prefix if present
			base64 = base64.substring(base64.indexOf(',') + 1);
		}
		byte[] bytes;
		try {
			bytes = Base64.getMimeDecoder().decode(base64);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Data is not valid base64");
		}
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		if (image == null) {
			throw new IllegalArgumentException("Data is not a valid PNG image");
		}
		return image;
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

	@FunctionalInterface private interface ImageSource {
		BufferedImage get() throws IOException;
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
