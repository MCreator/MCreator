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

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.io.mcp.protocol.SchemaDescription;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.java.JavaConventions;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.dialogs.tools.*;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PackMakerTool extends MCreatorMcpTool<PackMakerTool.Args> {

	public static class Args {
		@SchemaDescription("CREATE: generate a pack. LIST_SUPPORTED: list pack types supported by the current generator.")
		public Action actionType;
		@SchemaDescription("Pack type to create. MATERIAL creates ore, tools, and armor together.")
		@Nullable public PackType packType;
		@SchemaDescription("CamelCase base name for generated mod elements, e.g. Ruby or MyWood.")
		@Nullable public String name;
		@SchemaDescription("Accent color as hex string, e.g. #FF8800.")
		@Nullable public String color;
		@SchemaDescription("Power factor from 0.1 to 10. Defaults to 1.")
		@Nullable public Double powerFactor;
		@SchemaDescription("Material subtype for ORE and MATERIAL packs. Defaults to GEM_BASED.")
		@Nullable public MaterialSubtype materialSubtype;
		@SchemaDescription("Optional bark color hex for WOOD packs. Uses color when omitted.")
		@Nullable public String barkColor;
		@SchemaDescription("Base item for TOOL and ARMOR packs, e.g. CUSTOM:Ruby or Items.IRON_INGOT.")
		@Nullable public String baseItem;

		public enum Action {
			CREATE, LIST_SUPPORTED
		}

		public enum PackType {
			MATERIAL, ORE, TOOL, ARMOR, WOOD
		}

		public enum MaterialSubtype {
			GEM_BASED, DUST_BASED, INGOT_BASED
		}
	}

	public PackMakerTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "pack_maker";
	}

	@Override public String getDescription() {
		return """
				Creates mod element packs (material, ore, tool, armor, or wood) with textures and recipes.\
				Use list_supported to check which pack types the current generator supports.\
				Names must be valid CamelCase mod element names.""";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		if (input.actionType == null) {
			return CompletableFuture.completedFuture(ToolResult.error("actionType must be provided"));
		}

		return switch (input.actionType) {
			case LIST_SUPPORTED -> CompletableFuture.completedFuture(listSupported(mcreator));
			case CREATE -> CompletableFuture.completedFuture(createPack(mcreator, input));
		};
	}

	private static ToolResult listSupported(MCreator mcreator) {
		GeneratorConfiguration gc = mcreator.getGeneratorConfiguration();
		Map<Args.PackType, Boolean> supported = new LinkedHashMap<>();
		supported.put(Args.PackType.MATERIAL, MaterialPackMakerTool.isSupported(gc));
		supported.put(Args.PackType.ORE, OrePackMakerTool.isSupported(gc));
		supported.put(Args.PackType.TOOL, ToolPackMakerTool.isSupported(gc));
		supported.put(Args.PackType.ARMOR, ArmorPackMakerTool.isSupported(gc));
		supported.put(Args.PackType.WOOD, WoodPackMakerTool.isSupported(gc));
		return ToolResult.object(supported);
	}

	private static ToolResult createPack(MCreator mcreator, Args input) {
		if (input.packType == null) {
			return ToolResult.error("packType must be provided for CREATE");
		}

		GeneratorConfiguration gc = mcreator.getGeneratorConfiguration();
		if (!isPackTypeSupported(gc, input.packType)) {
			return ToolResult.error("Pack type is not supported by the current generator: " + input.packType);
		}

		String nameError = validateName(mcreator.getWorkspace(), input.name);
		if (nameError != null) {
			return ToolResult.error(nameError);
		}
		String name = JavaConventions.convertToValidClassName(Objects.requireNonNull(input.name));

		Color parsedColor = parseColor(input.color);
		if (parsedColor == null && input.color != null && !input.color.isBlank()) {
			return ToolResult.error("Invalid color: " + input.color);
		}
		Color color = parsedColor != null ? parsedColor : Color.decode("#FF8800");

		double powerFactor = input.powerFactor != null ? input.powerFactor : 1.0;
		if (powerFactor < 0.1 || powerFactor > 10) {
			return ToolResult.error("powerFactor must be between 0.1 and 10");
		}

		String materialType = toMaterialTypeString(
				input.materialSubtype != null ? input.materialSubtype : Args.MaterialSubtype.GEM_BASED);

		if ((input.packType == Args.PackType.TOOL || input.packType == Args.PackType.ARMOR)
				&& (input.baseItem == null || input.baseItem.isBlank())) {
			return ToolResult.error("baseItem must be provided for TOOL and ARMOR packs");
		}

		Color barkColor = color;
		if (input.packType == Args.PackType.WOOD && input.barkColor != null) {
			Color parsedBarkColor = parseColor(input.barkColor);
			if (parsedBarkColor == null) {
				return ToolResult.error("Invalid barkColor: " + input.barkColor);
			}
			barkColor = parsedBarkColor;
		}

		try {
			boolean created = switch (input.packType) {
				case MATERIAL -> MaterialPackMakerTool.addMaterialPackToWorkspace(null, mcreator,
						mcreator.getWorkspace(), name, materialType, color, powerFactor);
				case ORE -> OrePackMakerTool.addOrePackToWorkspace(null, mcreator, mcreator.getWorkspace(), name,
						materialType, color, powerFactor);
				case TOOL -> ToolPackMakerTool.addToolPackToWorkspace(null, mcreator, mcreator.getWorkspace(), name,
						Objects.requireNonNull(parseBaseItem(mcreator.getWorkspace(), input.baseItem)), color,
						powerFactor);
				case ARMOR -> ArmorPackMakerTool.addArmorPackToWorkspace(null, mcreator, mcreator.getWorkspace(), name,
						Objects.requireNonNull(parseBaseItem(mcreator.getWorkspace(), input.baseItem)), color,
						powerFactor);
				case WOOD -> WoodPackMakerTool.addWoodPackToWorkspace(null, mcreator, mcreator.getWorkspace(), name,
						color, barkColor, powerFactor);
			};

			if (!created) {
				return ToolResult.error("Failed to create pack. Required element names may already exist.");
			}
		} catch (Exception e) {
			return ToolResult.error("Failed to create pack: " + e.getMessage(), e);
		}

		mcreator.getGenerator().generateBase();
		mcreator.reloadWorkspaceTabContents();
		mcreator.getWorkspace().markDirty();

		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("packType", input.packType.name());
		response.put("name", name);
		return ToolResult.object(response);
	}

	@Nullable private static String validateName(Workspace workspace, @Nullable String name) {
		if (name == null || name.isBlank()) {
			return "name must be provided";
		}

		String fixed = JavaConventions.convertToValidClassName(name);
		if (fixed == null || fixed.isEmpty()) {
			return "Invalid mod element name";
		}

		for (String usedName : workspace.getWorkspaceInfo().getUsedElementNames()) {
			if (usedName.equalsIgnoreCase(fixed)) {
				return "Mod element with this name already exists";
			}
		}

		return null;
	}

	@Nullable private static Color parseColor(String color) {
		if (color == null || color.isBlank()) {
			return null;
		}
		try {
			return Color.decode(color.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Nullable private static MItemBlock parseBaseItem(Workspace workspace, @Nullable String baseItem) {
		if (baseItem == null || baseItem.isBlank()) {
			return null;
		}
		MItemBlock retval = new MItemBlock(workspace, baseItem.trim());
		if (retval.isValidReference()) {
			return retval;
		}
		return null;
	}

	private static String toMaterialTypeString(Args.MaterialSubtype subtype) {
		return switch (subtype) {
			case GEM_BASED -> "Gem based";
			case DUST_BASED -> "Dust based";
			case INGOT_BASED -> "Ingot based";
		};
	}

	private static boolean isPackTypeSupported(GeneratorConfiguration gc, Args.PackType packType) {
		return switch (packType) {
			case MATERIAL -> MaterialPackMakerTool.isSupported(gc);
			case ORE -> OrePackMakerTool.isSupported(gc);
			case TOOL -> ToolPackMakerTool.isSupported(gc);
			case ARMOR -> ArmorPackMakerTool.isSupported(gc);
			case WOOD -> WoodPackMakerTool.isSupported(gc);
		};
	}
}
