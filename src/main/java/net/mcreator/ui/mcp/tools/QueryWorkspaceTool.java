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
import net.mcreator.io.mcp.protocol.SchemaDescription;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.mcp.tools.utils.CollectionFilter;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import net.mcreator.workspace.resources.Model;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class QueryWorkspaceTool extends MCreatorMcpTool<QueryWorkspaceTool.Args> {

	public static class Args {
		public ListType type;
		@SchemaDescription("Optional Java regex filter to limit returned list size. Matches element name and type where applicable.")
		@Nullable public String filter;

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
			MODELS_JSON, MODELS_JAVA, MODELS_OBJ, MODELS_BEDROCK
			//@formatter:on
		}
	}

	public QueryWorkspaceTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "query_workspace";
	}

	@Override public String getDescription() {
		return "Provides list of specified workspace elements or resources. Use those lists as hard source of truth.";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		return switch (input.type) {
			case Args.ListType.WORKSPACE_SETTINGS -> CompletableFuture.completedFuture(
					ToolResult.object(mcreator.getWorkspace().getWorkspaceSettings()));
			case Args.ListType.MOD_ELEMENTS -> completed(CollectionFilter.apply(
					mcreator.getWorkspace().getModElements().stream().map(NameAndType::new).toList(), input.filter,
					element -> element.registryName() + " " + element.type()));
			case Args.ListType.SUPPORTED_MOD_ELEMENT_TYPES -> completed(CollectionFilter.applyStrings(
					mcreator.getGeneratorStats().getSupportedModElementTypes().stream()
							.map(ModElementType::getRegistryName).toList(), input.filter));
			case Args.ListType.MOD_VARIABLES -> completed(
					CollectionFilter.apply(mcreator.getWorkspace().getVariableElements(), input.filter,
							QueryWorkspaceTool::variableFilterText));
			case Args.ListType.VARIABLE_TYPES -> completed(CollectionFilter.applyStrings(
					VariableTypeLoader.INSTANCE.getGlobalVariableTypes(mcreator.getGeneratorConfiguration()).stream()
							.filter(t -> t.isSupportedInWorkspace(mcreator.getWorkspace())).map(VariableType::getName)
							.toList(), input.filter));
			case Args.ListType.MOD_TAGS ->
					CompletableFuture.completedFuture(ToolResult.object(mcreator.getWorkspace().getTagElements()));
			case Args.ListType.BLOCKS -> completed(
					CollectionFilter.apply(dataListEntries(ElementUtil.loadBlocks(mcreator.getWorkspace())),
							input.filter, DataListTool.DataListEntryInfo::toString));
			case Args.ListType.BLOCKS_AND_ITEMS -> completed(
					CollectionFilter.apply(dataListEntries(ElementUtil.loadBlocksAndItems(mcreator.getWorkspace())),
							input.filter, DataListTool.DataListEntryInfo::toString));
			case Args.ListType.BLOCKS_AND_ITEMS_AND_TAGS -> completed(CollectionFilter.apply(
					dataListEntries(ElementUtil.loadBlocksAndItemsAndTags(mcreator.getWorkspace())), input.filter,
					DataListTool.DataListEntryInfo::toString));
			case Args.ListType.PROCEDURES -> completed(CollectionFilter.applyStrings(
					mcreator.getWorkspace().getModElementsByType(ModElementType.PROCEDURE).stream()
							.map(ModElement::getName).toList(), input.filter));
			case Args.ListType.BLOCK_TEXTURES -> completed(CollectionFilter.applyStrings(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.BLOCK)), input.filter));
			case Args.ListType.ITEM_TEXTURES -> completed(CollectionFilter.applyStrings(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.ITEM)), input.filter));
			case Args.ListType.ENTITY_TEXTURES -> completed(CollectionFilter.applyStrings(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.ENTITY)), input.filter));
			case Args.ListType.EFFECT_TEXTURES -> completed(CollectionFilter.applyStrings(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.EFFECT)), input.filter));
			case Args.ListType.PARTICLE_TEXTURES -> completed(CollectionFilter.applyStrings(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.PARTICLE)), input.filter));
			case Args.ListType.SCREEN_TEXTURES -> completed(CollectionFilter.applyStrings(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.SCREEN)), input.filter));
			case Args.ListType.ARMOR_TEXTURES -> completed(CollectionFilter.applyStrings(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.ARMOR)), input.filter));
			case Args.ListType.OTHER_TEXTURES -> completed(CollectionFilter.applyStrings(
					textureFileNames(mcreator.getFolderManager().getTexturesList(TextureType.OTHER)), input.filter));
			case Args.ListType.MODELS_JSON -> completed(CollectionFilter.applyStrings(
					Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
							.filter(el -> el.getType() == Model.Type.JSON).map(Model::getReadableName).toList(),
					input.filter));
			case Args.ListType.MODELS_JAVA -> completed(CollectionFilter.applyStrings(
					Model.getJavaModels(mcreator.getWorkspace()).stream().map(Model::getReadableName).toList(),
					input.filter));
			case Args.ListType.MODELS_OBJ -> completed(CollectionFilter.applyStrings(
					Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
							.filter(el -> el.getType() == Model.Type.OBJ).map(Model::getReadableName).toList(),
					input.filter));
			case Args.ListType.MODELS_BEDROCK -> completed(CollectionFilter.applyStrings(
					Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
							.filter(el -> el.getType() == Model.Type.BEDROCK).map(Model::getReadableName).toList(),
					input.filter));
		};
	}

	private static String variableFilterText(VariableElement variable) {
		return variable.getName() + " " + variable.getTypeString() + " " + variable.getScope();
	}

	private static List<DataListTool.DataListEntryInfo> dataListEntries(List<? extends DataListEntry> entries) {
		return entries.stream().map(DataListTool.DataListEntryInfo::new).toList();
	}

	private static List<String> textureFileNames(List<File> textureFiles) {
		return textureFiles.stream().map(File::getName).toList();
	}

	private record NameAndType(String registryName, String type) {
		NameAndType(ModElement element) {
			this(element.getName(), element.getType().getRegistryName());
		}
	}

}
