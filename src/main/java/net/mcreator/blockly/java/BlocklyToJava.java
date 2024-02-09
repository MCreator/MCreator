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

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.java.blocks.*;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.ParseException;
import java.util.List;

public class BlocklyToJava extends BlocklyToCode {

	protected final Logger LOG = LogManager.getLogger("Blockly2Java");
	protected final BlocklyVariables variableGenerator = new BlocklyVariables(this);

	/**
	 * @param workspace         <p>The {@link Workspace} executing the code</p>
	 * @param blocklyEditorType <p>Blockly editor type</p>
	 * @param sourceXML         <p>The XML code used by Blockly</p>
	 * @param templateGenerator <p>The folder location in each {@link net.mcreator.generator.Generator} containing the code template files<p>
	 */
	public BlocklyToJava(Workspace workspace, ModElement parent, BlocklyEditorType blocklyEditorType, String sourceXML,
			TemplateGenerator templateGenerator, IBlockGenerator... externalGenerators)
			throws TemplateGeneratorException {
		super(workspace, parent, blocklyEditorType, templateGenerator, externalGenerators);

		preInitialization();

		if (sourceXML != null) {
			try {
				final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.parse(new InputSource(new StringReader(sourceXML)));
				doc.getDocumentElement().normalize();

				Element start_block = BlocklyBlockUtil.getStartBlock(doc, blocklyEditorType.startBlockName());

				// if there is no start block, we return empty string
				if (start_block == null)
					throw new ParseException("Could not find start block!", -1);

				// we execute extra actions needed before placing blocks
				preBlocksPlacement(doc, start_block);

				// find all blocks placed under start block
				List<Element> base_blocks = BlocklyBlockUtil.getBlockProcedureStartingWithNext(start_block);
				processBlockProcedure(base_blocks);

				// we execute extra actions needed after blocks are placed
				postBlocksPlacement(doc, start_block, base_blocks);

			} catch (TemplateGeneratorException e) {
				throw e;
			} catch (Exception e) {
				LOG.error("Failed to parse Blockly XML", e);
				addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.exception_compiling", e.getMessage())));
			}
		} else {
			addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR, L10N.t("blockly.errors.editor_not_ready")));
		}
	}

	/**
	 * <p>This method contains the code needing to be executed before blocks are placed.</p>
	 *
	 * @param doc        Blockly XML document
	 * @param startBlock The basic block of the editor used to get other blocks.
	 */
	protected void preBlocksPlacement(Document doc, Element startBlock) throws TemplateGeneratorException {}

	/**
	 * <p>This method contains the code needing to be executed after blocks are placed.</p>
	 *
	 * @param doc        Blockly XML document
	 * @param startBlock The basic block of the editor used to get other blocks.
	 * @param baseBlocks A list of all blocks placed under start block.
	 */
	protected void postBlocksPlacement(Document doc, Element startBlock, List<Element> baseBlocks) {}

	/**
	 * <p>This method is executed after the constructor is called, before the code is executed.</p>
	 */
	protected void preInitialization() {
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

		// add Minecraft related blocks
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
	}
}
