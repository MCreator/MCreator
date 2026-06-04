/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.blockly.java;

import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.java.blocks.*;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

public class BlocklyToJava extends BlocklyToCode {

	/**
	 * @param workspace         <p>The {@link Workspace} executing the code</p>
	 * @param blocklyEditorType <p>Blockly editor type</p>
	 * @param sourceXML         <p>The XML code used by Blockly</p>
	 * @param templateGenerator <p>The folder location in each {@link net.mcreator.generator.Generator} containing the code template files<p>
	 */
	public BlocklyToJava(Workspace workspace, ModElement parent, BlocklyEditorType blocklyEditorType, String sourceXML,
			TemplateGenerator templateGenerator, IBlockGenerator... externalGenerators)
			throws TemplateGeneratorException {
		super(workspace, parent, blocklyEditorType, sourceXML, templateGenerator, externalGenerators);
	}

	/**
	 * <p>This method is executed after the constructor is called, before the code is generated</p>
	 */
	@Override protected void beforeGenerate() {
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
	}

}
