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

import net.mcreator.blockly.InternalBlocksLoader;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.DynamicBlockLoader;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.io.mcp.protocol.SchemaDescription;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.mcp.tools.utils.CollectionFilter;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BlocklyBlocksTool extends MCreatorMcpTool<BlocklyBlocksTool.Args> {

	public static class Args {
		public QueryType type;
		@Nullable public String blocklyEditorType;
		@Nullable public String blockRegistryName;
		@SchemaDescription("Optional Java regex filter to limit returned list size.")
		@Nullable public String filter;

		public enum QueryType {
			LIST_BLOCKS, GET_BLOCK
		}
	}

	public BlocklyBlocksTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "blockly_blocks";
	}

	@Override public String getDescription() {
		return """
				Lists supported custom Blockly blocks for blocklyEditorType %s or returns block JSON definition for blockRegistryName.\
				Good to get list of all supported blocks for a given editor type before using blockRegistryName.\
				Other standard blocks (if, while, etc.) of default Blockly editor may not be listed but may be supported.""".formatted(
				BlocklyEditorType.getTypes());
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		return switch (input.type) {
			case LIST_BLOCKS -> {
				BlocklyEditorType editorType = parseEditorType(input.blocklyEditorType);
				if (editorType == null) {
					yield CompletableFuture.completedFuture(ToolResult.error("Invalid or missing blocklyEditorType"));
				}
				yield completed(CollectionFilter.applyStrings(
						getSupportedBlockRegistryNames(mcreator, editorType).stream().sorted(Comparator.naturalOrder())
								.toList(), input.filter));
			}
			case GET_BLOCK -> {
				if (input.blockRegistryName == null || input.blockRegistryName.isBlank()) {
					yield CompletableFuture.completedFuture(ToolResult.error("blockRegistryName is required"));
				}
				BlocklyEditorType editorType = parseEditorType(input.blocklyEditorType);
				if (editorType == null) {
					yield CompletableFuture.completedFuture(ToolResult.error("Invalid or missing blocklyEditorType"));
				}

				// TODO: return some note for standard bloxkly blocks that don't have definition in MCreator (if, while, etc.)

				List<ToolboxBlock> allBlocks = BlocklyLoader.INSTANCE.getAllToolboxBlocksFor(
						mcreator.getGeneratorConfiguration(), editorType);
				ToolboxBlock block = allBlocks.stream()
						.filter(b -> b.getMachineName().equals(input.blockRegistryName.trim())).findFirst()
						.orElse(null);
				if (block == null) {
					yield CompletableFuture.completedFuture(
							ToolResult.error("Unknown block: " + input.blockRegistryName));
				}
				if (!isBlockSupported(mcreator, block)) {
					yield CompletableFuture.completedFuture(
							ToolResult.error("Block is not supported by the current workspace generator"));
				}
				// TODO: return more than just blockly JSON?
				yield CompletableFuture.completedFuture(ToolResult.object(block.getBlocklyJSON()));
			}
		};
	}

	@Nullable private static BlocklyEditorType parseEditorType(@Nullable String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		return BlocklyEditorType.fromName(raw.trim());
	}

	private static Collection<String> getSupportedBlockRegistryNames(MCreator mcreator, BlocklyEditorType editorType) {
		Set<String> supportedBlocks = new HashSet<>();
		supportedBlocks.addAll(mcreator.getGeneratorStats().getBlocklyBlocks(editorType));
		supportedBlocks.addAll(InternalBlocksLoader.getAllInternalBlockTypeIDs());
		supportedBlocks.addAll(DynamicBlockLoader.getDynamicBlocks(editorType).stream()
				.filter(block -> block.shouldLoad(mcreator.getGeneratorConfiguration()))
				.map(ToolboxBlock::getMachineName).toList());
		return supportedBlocks;
	}

	private static boolean isBlockSupported(MCreator mcreator, ToolboxBlock block) {
		if (block.getRequiredAPIs() != null) {
			for (String requiredApi : block.getRequiredAPIs()) {
				if (!mcreator.getWorkspace().getWorkspaceSettings().getMCreatorDependencies().contains(requiredApi)) {
					return false;
				}
			}
		}
		return true;
	}

}
