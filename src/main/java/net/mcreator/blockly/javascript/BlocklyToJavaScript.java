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

package net.mcreator.blockly.javascript;

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.javascript.blocks.*;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BlocklyToJavaScript extends BlocklyToCode {

	private String externalTrigger;

	/**
	 * @param workspace         <p>The {@link Workspace} executing the code</p>
	 * @param sourceXML         <p>The XML code used by Blockly</p>
	 * @param templateGenerator <p>The folder location in each {@link net.mcreator.generator.Generator} containing the code template files<p>
	 */
	public BlocklyToJavaScript(Workspace workspace, ModElement parent, String sourceXML,
			TemplateGenerator templateGenerator, IBlockGenerator... externalGenerators)
			throws TemplateGeneratorException {
		super(workspace, parent, BlocklyEditorType.SCRIPT, sourceXML, templateGenerator, externalGenerators);
	}

	@Override protected void preBlocksPlacement(Document doc, Element startBlock) {
		if (doc != null) {
			// first we load data from the start block
			Element trigger = XMLUtil.getFirstChildrenWithName(
					BlocklyBlockUtil.getStartBlock(doc, getEditorType().startBlockName()), "field");
			if (trigger != null && !trigger.getTextContent().equals("no_ext_trigger")) {
				externalTrigger = trigger.getTextContent();
			} else {
				addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.scripts.missing_trigger")));
			}
		}
	}

	public String getExternalTrigger() {
		return externalTrigger;
	}

	/**
	 * <p>This method is executed after the constructor is called, before the code is generated</p>
	 */
	@Override protected void beforeGenerate() {
		// add standard procedural blocks
		blockGenerators.add(new PrintTextBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.IfBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.JavaCodeProceduralBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.FlowControlBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.LoopBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.TernaryOperatorBlock());

		// add standard output blocks
		blockGenerators.add(new net.mcreator.blockly.java.blocks.TextBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.BooleanBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.LogicNegateBlock());
		blockGenerators.add(new NumberBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.TextJoinBlock());
		blockGenerators.add(new NumberConstantsBlock());
		blockGenerators.add(new NumberBinaryOperationsBlock());
		blockGenerators.add(new SingularMathOperationsBlock());
		blockGenerators.add(new LogicBinaryOperationsBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.JavaCodeOutputBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.CustomDependencyBlock());

		// add Minecraft-related blocks
		blockGenerators.add(new net.mcreator.blockly.java.blocks.CoordinateBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.EventOrTargetEntityDependencyBlock());
		blockGenerators.add(new net.mcreator.blockly.java.blocks.SourceEntityDependencyBlock());
		blockGenerators.add(new MCItemBlock());
	}

}
