/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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
import net.mcreator.workspace.Workspace;

public abstract class BlocklyToJava extends BlocklyToCode {

	public BlocklyToJava(Workspace workspace, TemplateGenerator templateGenerator,
			IBlockGenerator... externalGenerators) {
		super(workspace, templateGenerator, externalGenerators);

		// add standard procedural blocks
		blockGenerators.add(new PrintTextBlock());
		blockGenerators.add(new IfBlock());
		blockGenerators.add(new LoopBlock());
		blockGenerators.add(new FlowControlBlock());
		blockGenerators.add(new SingularMathOperationsBlock());
		blockGenerators.add(new NumberFromTextBlock());
		blockGenerators.add(new JavaCodeBlock());
		blockGenerators.add(new ProcedureCallBlock());
		blockGenerators.add(new ProcedureRetvalBlock());

		// add standard output blocks
		blockGenerators.add(new BinaryOperationsBlock());
		blockGenerators.add(new TextBinaryOperationsBlock());
		blockGenerators.add(new LogicNegateBlock());
		blockGenerators.add(new BooleanBlock());
		blockGenerators.add(new NumberBlock());
		blockGenerators.add(new NumberConstantsBlock());
		blockGenerators.add(new TextBlock());
		blockGenerators.add(new TextReplace());
		blockGenerators.add(new TextFormatNumber());
		blockGenerators.add(new TextContains());
		blockGenerators.add(new TextSubstring());
		blockGenerators.add(new TextJoinBlock());
		blockGenerators.add(new TextLengthBlock());
		blockGenerators.add(new CustomDependencyBlock());

		// add Minecraft related blocks
		blockGenerators.add(new CoordinateBlock());
		blockGenerators.add(new EventOrTargetEntityDependenyBlock());
		blockGenerators.add(new SourceEntityDependenyBlock());
		blockGenerators.add(new ImediateSourceEntityDependencyBlock());
		blockGenerators.add(new DirectionDependenyBlock());
		blockGenerators.add(new DirectionConstantBlock());
		blockGenerators.add(new NullBlock());
		blockGenerators.add(new MCItemBlock());
		blockGenerators.add(new CancelEventBlock());
		blockGenerators.add(new SetVariableBlock());
		blockGenerators.add(new GetVariableBlock());
		blockGenerators.add(new ReturnBlock());
	}

}
