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

import net.mcreator.element.ModElementType;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;
import net.mcreator.workspace.resources.Model;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ListTool extends MCreatorMcpTool<ListTool.Args> {

	public static class Args {
		public ListType type;

		public enum ListType {
			//@formatter:off
			WORKSPACE_SETTINGS,
			// workspace elements
			MOD_ELEMENTS, SUPPORTED_MOD_ELEMENT_TYPES,  MOD_VARIABLES, VARIABLE_TYPES, MOD_TAGS,
			// data lists
			BLOCKS, BLOCKS_AND_ITEMS, BLOCKS_AND_ITEMS_AND_TAGS, PROCEDURES,
			// textures
			BLOCK_TEXTURES, ITEM_TEXTURES, ENTITY_TEXTURES, EFFECT_TEXTURES, PARTICLE_TEXTURES, SCREEN_TEXTURES, ARMOR_TEXTURES, OTHER_TEXTURES,
			// models
			MODELS_JSON, MODELS_JAVA, MODELS_OBJ
			//@formatter:on
		}
	}

	public ListTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "list_workspace";
	}

	@Override public String getDescription() {
		return "Provides list of specified workspace elements or resources. Use those lists as hard source of truth.";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		return switch (input.type) {
			case Args.ListType.MOD_ELEMENTS -> CompletableFuture.completedFuture(ToolResult.collection(
					mcreator.getWorkspace().getModElements().stream().map(NameAndType::new).toList()));
			case Args.ListType.MOD_VARIABLES -> CompletableFuture.completedFuture(
					ToolResult.collection(mcreator.getWorkspace().getVariableElements()));
			case Args.ListType.MOD_TAGS ->
					CompletableFuture.completedFuture(ToolResult.object(mcreator.getWorkspace().getTagElements()));
			case Args.ListType.SUPPORTED_MOD_ELEMENT_TYPES -> CompletableFuture.completedFuture(ToolResult.collection(
					mcreator.getGeneratorStats().getSupportedModElementTypes().stream()
							.map(ModElementType::getRegistryName).toList()));
			case Args.ListType.VARIABLE_TYPES -> CompletableFuture.completedFuture(ToolResult.collection(
					VariableTypeLoader.INSTANCE.getGlobalVariableTypes(mcreator.getGeneratorConfiguration())));
			case Args.ListType.WORKSPACE_SETTINGS -> CompletableFuture.completedFuture(
					ToolResult.object(mcreator.getWorkspace().getWorkspaceSettings()));
			case Args.ListType.BLOCKS_AND_ITEMS -> CompletableFuture.completedFuture(
					ToolResult.collection(dataListNames(ElementUtil.loadBlocksAndItems(mcreator.getWorkspace()))));
			case Args.ListType.BLOCKS -> CompletableFuture.completedFuture(
					ToolResult.collection(dataListNames(ElementUtil.loadBlocks(mcreator.getWorkspace()))));
			case Args.ListType.BLOCKS_AND_ITEMS_AND_TAGS -> CompletableFuture.completedFuture(ToolResult.collection(
					dataListNames(ElementUtil.loadBlocksAndItemsAndTags(mcreator.getWorkspace()))));
			case Args.ListType.PROCEDURES -> CompletableFuture.completedFuture(ToolResult.collection(
					mcreator.getWorkspace().getModElementsByType(ModElementType.PROCEDURE).stream()
							.map(ModElement::getRegistryName).toList()));
			case Args.ListType.BLOCK_TEXTURES -> CompletableFuture.completedFuture(ToolResult.collection(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.BLOCK))));
			case Args.ListType.ITEM_TEXTURES -> CompletableFuture.completedFuture(ToolResult.collection(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.ITEM))));
			case Args.ListType.ENTITY_TEXTURES -> CompletableFuture.completedFuture(ToolResult.collection(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.ENTITY))));
			case Args.ListType.EFFECT_TEXTURES -> CompletableFuture.completedFuture(ToolResult.collection(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.EFFECT))));
			case Args.ListType.PARTICLE_TEXTURES -> CompletableFuture.completedFuture(ToolResult.collection(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.PARTICLE))));
			case Args.ListType.SCREEN_TEXTURES -> CompletableFuture.completedFuture(ToolResult.collection(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.SCREEN))));
			case Args.ListType.ARMOR_TEXTURES -> CompletableFuture.completedFuture(ToolResult.collection(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.ARMOR))));
			case Args.ListType.OTHER_TEXTURES -> CompletableFuture.completedFuture(ToolResult.collection(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.OTHER))));
			case Args.ListType.MODELS_JSON -> CompletableFuture.completedFuture(ToolResult.collection(
					Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
							.filter(el -> el.getType() == Model.Type.JSON).collect(Collectors.toList())));
			case Args.ListType.MODELS_JAVA -> CompletableFuture.completedFuture(
					ToolResult.collection(Model.getJavaModels(mcreator.getWorkspace())));
			case Args.ListType.MODELS_OBJ -> CompletableFuture.completedFuture(ToolResult.collection(
					Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
							.filter(el -> el.getType() == Model.Type.OBJ).collect(Collectors.toList())));
		};
	}

	private static List<String> dataListNames(List<? extends DataListEntry> entries) {
		return entries.stream().map(DataListEntry::getName).toList();
	}

	private static List<String> textureFileNames(List<File> textureFiles) {
		return textureFiles.stream().map(File::getName).toList();
	}

	private record NameAndType(String registryName, String type) {
		NameAndType(ModElement element) {
			this(element.getRegistryName(), element.getType().getRegistryName());
		}
	}

}
