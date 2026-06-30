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

import net.mcreator.blockly.IBlockGenerator;
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
		@SchemaDescription("Optional Java regex filter to limit returned list size.") @Nullable public String filter;

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
				Lists supported Blockly blocks for blocklyEditorType %s or returns block info and JSON definition for blockRegistryName.\
				Good to get list of all supported blocks for a given editor type (except for start blocks) before using blockRegistryName.""".formatted(
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

				for (BlocklyEditorType type : BlocklyEditorType.getTypes().stream().map(BlocklyEditorType::fromName)
						.toList()) {
					if (type != null && type.startBlockName() != null && type.startBlockName()
							.equals(input.blockRegistryName)) {
						yield CompletableFuture.completedFuture(ToolResult.error(
								"blockRegistryName is a start block and cannot be queried: "
										+ input.blockRegistryName));
					}
				}

				String blockRegistryName = input.blockRegistryName.trim();
				String standardBlockXml = getSupportedStandardBlocklyBlockXml(editorType, blockRegistryName);
				if (standardBlockXml != null) {
					Map<String, Object> blockData = new HashMap<>();
					blockData.put("blockExampleXML", standardBlockXml);
					yield CompletableFuture.completedFuture(ToolResult.object(blockData));
				}

				List<ToolboxBlock> allBlocks = BlocklyLoader.INSTANCE.getAllToolboxBlocksFor(
						mcreator.getGeneratorConfiguration(), editorType);
				ToolboxBlock block = allBlocks.stream().filter(b -> b.getMachineName().equals(blockRegistryName))
						.findFirst().orElse(null);
				if (block == null) {
					yield CompletableFuture.completedFuture(
							ToolResult.error("Unknown block: " + input.blockRegistryName));
				}
				if (!isBlockSupported(mcreator, block)) {
					yield CompletableFuture.completedFuture(
							ToolResult.error("Block is not supported by the current workspace generator"));
				}
				Map<String, Object> blockData = new HashMap<>();
				blockData.put("blocklyBlockJSONDefinition", block.getBlocklyJSON());
				blockData.put("blockExampleXML", block.getToolboxTestXML());
				blockData.put("blockType", block.getType());
				yield CompletableFuture.completedFuture(ToolResult.object(blockData));
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

	@Nullable
	private static String getSupportedStandardBlocklyBlockXml(BlocklyEditorType editorType, String blockRegistryName) {
		String exampleXml = STANDARD_BLOCKLY_BLOCKS.get(blockRegistryName);
		if (exampleXml == null) {
			return null;
		}
		List<IBlockGenerator> generators = InternalBlocksLoader.getInternalBlocks(editorType);
		if (generators == null) {
			return null;
		}
		for (IBlockGenerator generator : generators) {
			if (generator.getBlockJSONDefinitions() != null) {
				continue;
			}
			for (String supportedBlock : generator.getSupportedBlocks()) {
				if (supportedBlock.equals(blockRegistryName)) {
					return exampleXml;
				}
			}
		}
		return null;
	}

	/**
	 * Standard Blockly blocks registered in {@link InternalBlocksLoader} without MCreator JSON definitions.
	 */
	private static final Map<String, String> STANDARD_BLOCKLY_BLOCKS = Map.ofEntries(
			//@formatter:off
			Map.entry("controls_if", """
					<block type="controls_if">
					  <value name="IF0"></value>
					  <statement name="DO0"></statement>
					</block>"""),
			Map.entry("controls_while", """
					<block type="controls_while">
					  <value name="BOOL"></value>
					  <statement name="DO"></statement>
					</block>"""),
			Map.entry("controls_repeat_ext", """
					<block type="controls_repeat_ext">
					  <value name="TIMES"></value>
					  <statement name="DO"></statement>
					</block>"""),
			Map.entry("text_print", """
					<block type="text_print">
					  <value name="TEXT"></value>
					</block>"""),
			Map.entry("logic_boolean", """
					<block type="logic_boolean">
					  <field name="BOOL"></field>
					</block>"""),
			Map.entry("logic_negate", """
					<block type="logic_negate">
					  <value name="BOOL"></value>
					</block>"""),
			Map.entry("math_number", """
					<block type="math_number">
					  <field name="NUM"></field>
					</block>"""),
			Map.entry("text", """
					<block type="text">
					  <field name="TEXT"></field>
					</block>"""),
			Map.entry("text_join", """
					<block type="text_join">
					  <mutation items="2"></mutation>
					  <value name="ADD0"></value>
					  <value name="ADD1"></value>
					</block>"""),
			Map.entry("text_length", """
					<block type="text_length">
					  <value name="VALUE"></value>
					</block>""")
			//@formatter:on
	);

}
