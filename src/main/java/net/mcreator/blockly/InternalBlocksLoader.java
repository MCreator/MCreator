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

package net.mcreator.blockly;

import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.blockly.java.blocks.*;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.blockly.BlocklyPanel;

import java.util.*;
import java.util.stream.Collectors;

public class InternalBlocksLoader {

	private static final Map<BlocklyEditorType, List<IBlockGenerator>> internalBlocks = new HashMap<>();

	private static final Map<BlocklyEditorType, List<ToolboxBlock>> internalToolboxBlocks = new HashMap<>();

	public static void preload() {
		internalBlocks.put(BlocklyEditorType.PROCEDURE, getAllForJava());
		internalBlocks.put(BlocklyEditorType.AI_TASK, getAllForJava());
		internalBlocks.put(BlocklyEditorType.COMMAND_ARG, getAllForJava());
		internalBlocks.put(BlocklyEditorType.FEATURE, List.of(new net.mcreator.blockly.datapack.blocks.MCItemBlock()));
		internalBlocks.put(BlocklyEditorType.JSON_TRIGGER,
				List.of(new net.mcreator.blockly.datapack.blocks.NumberBlock(),
						new net.mcreator.blockly.datapack.blocks.MCItemBlock()));
		internalBlocks.put(BlocklyEditorType.SCRIPT, getAllForJavaScript());
		internalBlocks.put(BlocklyEditorType.ENCHANTMENT_EFFECTS,
				List.of(new net.mcreator.blockly.datapack.blocks.NumberBlock(),
						new net.mcreator.blockly.datapack.blocks.MCItemBlock()));

		for (Map.Entry<BlocklyEditorType, List<IBlockGenerator>> entry : internalBlocks.entrySet()) {
			internalToolboxBlocks.computeIfAbsent(entry.getKey(), _ -> new ArrayList<>())
					.addAll(entry.getValue().stream().map(ToolboxBlock::getToolboxBlocksFor).flatMap(Collection::stream)
							.toList());
		}
	}

	public static List<IBlockGenerator> getInternalBlocks(BlocklyEditorType blocklyEditorType) {
		return internalBlocks.get(blocklyEditorType);
	}

	public static List<ToolboxBlock> getInternalToolboxBlocks(BlocklyEditorType blocklyEditorType) {
		return internalToolboxBlocks.get(blocklyEditorType);
	}

	public static Set<String> getAllInternalBlockTypeIDs() {
		return internalBlocks.values().stream().flatMap(Collection::stream)
				.flatMap(generator -> Arrays.stream(generator.getSupportedBlocks())).collect(Collectors.toSet());
	}

	public static void loadBlocksAndCategoriesInPanel(BlocklyPanel pane) {
		List<ToolboxBlock> blockGenerators = getInternalToolboxBlocks(pane.getType());
		if (blockGenerators == null)
			return;

		List<String> definitions = new ArrayList<>();
		for (ToolboxBlock toolboxBlock : blockGenerators) {
			definitions.add(toolboxBlock.getBlocklyJSON().toString());
		}

		pane.executeLocalScript("Blockly.defineBlocksWithJsonArray([" + String.join(",", definitions) + "])");
	}

	private static List<IBlockGenerator> getAllForJava() {
		List<IBlockGenerator> blockGenerators = new ArrayList<>();

		// add standard procedural blocks
		blockGenerators.add(new PrintTextBlock());
		blockGenerators.add(new IfBlock());
		blockGenerators.add(new LoopBlock());
		blockGenerators.add(new FlowControlBlock());
		blockGenerators.add(new SingularMathOperationsBlock());
		blockGenerators.add(new NumberFromTextBlock());
		blockGenerators.add(new JavaCodeProceduralBlock());
		blockGenerators.add(new ProcedureCallBlock());
		blockGenerators.add(new ProcedureRetvalBlock());

		// add standard output blocks
		blockGenerators.add(new LogicBinaryOperationsBlock());
		blockGenerators.add(new NumberBinaryOperationsBlock());
		blockGenerators.add(new TextBinaryOperationsBlock());
		blockGenerators.add(new LogicNegateBlock());
		blockGenerators.add(new NullComparisonBlock());
		blockGenerators.add(new BooleanBlock());
		blockGenerators.add(new NumberBlock());
		blockGenerators.add(new NumberConstantsBlock());
		blockGenerators.add(new TextBlock());
		blockGenerators.add(new TextNewLineBlock());
		blockGenerators.add(new TextReplace());
		blockGenerators.add(new TextReplaceRegex());
		blockGenerators.add(new TextFormatNumber());
		blockGenerators.add(new TextContains());
		blockGenerators.add(new TextMatches());
		blockGenerators.add(new TextSubstring());
		blockGenerators.add(new TextJoinBlock());
		blockGenerators.add(new TextLengthBlock());
		blockGenerators.add(new TextIndexOfBlock());
		blockGenerators.add(new TextIsEmptyBlock());
		blockGenerators.add(new TextTrimBlock());
		blockGenerators.add(new TextUppercaseBlock());
		blockGenerators.add(new TextLowercaseBlock());
		blockGenerators.add(new TextStartsWithBlock());
		blockGenerators.add(new TextEndsWithBlock());
		blockGenerators.add(new CustomDependencyBlock());
		blockGenerators.add(new JavaCodeOutputBlock());
		blockGenerators.add(new TernaryOperatorBlock());
		blockGenerators.add(new TimeAsStringBlock());
		blockGenerators.add(new TimeToFormattedString());
		blockGenerators.add(new TimeSecondsBlock());
		blockGenerators.add(new TimeMinutesBlock());
		blockGenerators.add(new TimeHoursBlock());
		blockGenerators.add(new TimeDayOfWeekBlock());
		blockGenerators.add(new TimeDayOfMonthBlock());
		blockGenerators.add(new TimeMonthBlock());
		blockGenerators.add(new TimeYearBlock());
		blockGenerators.add(new TimeWeekOfYearBlock());
		blockGenerators.add(new DebugMarkerBlock());

		// add Minecraft-related blocks
		blockGenerators.add(new CoordinateBlock());
		blockGenerators.add(new EventOrTargetEntityDependencyBlock());
		blockGenerators.add(new SourceEntityDependencyBlock());
		blockGenerators.add(new EntityIteratorDependencyBlock());
		blockGenerators.add(new ImmediateSourceEntityDependencyBlock());
		blockGenerators.add(new DamageSourceDependencyBlock());
		blockGenerators.add(new DirectionDependencyBlock());
		blockGenerators.add(new DirectionConstantBlock());
		blockGenerators.add(new NullBlock());
		blockGenerators.add(new MCItemBlock());
		blockGenerators.add(new CancelEventBlock());
		blockGenerators.add(new SetEventResultBlock());
		blockGenerators.add(new SetVariableBlock());
		blockGenerators.add(new GetVariableBlock());
		blockGenerators.add(new ReturnBlock());
		blockGenerators.add(new EventParameterSetBlock());

		return blockGenerators;
	}

	private static List<IBlockGenerator> getAllForJavaScript() {
		List<IBlockGenerator> blockGenerators = new ArrayList<>();

		// add standard procedural blocks
		blockGenerators.add(new net.mcreator.blockly.javascript.blocks.PrintTextBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.IfBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.JavaCodeProceduralBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.FlowControlBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.LoopBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.TernaryOperatorBlock());
		blockGenerators.add(new net.mcreator.blockly.javascript.blocks.SetVariableBlock());

		// add standard output blocks
		blockGenerators.add(new net.mcreator.blockly.java.blocks.TextBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.BooleanBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.LogicNegateBlock());
		blockGenerators.add(new net.mcreator.blockly.javascript.blocks.NumberBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.TextJoinBlock());
		blockGenerators.add(new net.mcreator.blockly.javascript.blocks.NumberConstantsBlock());
		blockGenerators.add(new net.mcreator.blockly.javascript.blocks.NumberBinaryOperationsBlock());
		blockGenerators.add(new net.mcreator.blockly.javascript.blocks.SingularMathOperationsBlock());
		blockGenerators.add(new net.mcreator.blockly.javascript.blocks.LogicBinaryOperationsBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.JavaCodeOutputBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.CustomDependencyBlock());
		blockGenerators.add(new net.mcreator.blockly.javascript.blocks.GetVariableBlock());

		// add Minecraft-related blocks
		blockGenerators.add(new net.mcreator.blockly.java.blocks.CoordinateBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.EventOrTargetEntityDependencyBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.SourceEntityDependencyBlock());
		blockGenerators.add(new net.mcreator.blockly.javascript.blocks.MCItemBlock());

		return blockGenerators;
	}

}
