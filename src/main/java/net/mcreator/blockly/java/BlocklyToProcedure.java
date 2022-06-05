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

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.java.blocks.ReturnBlock;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableType;
import org.apache.commons.lang3.ArrayUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class BlocklyToProcedure extends BlocklyToJava {

	private String externalTrigger;
	private List<VariableElement> variables;
	private VariableType returnType;

	public BlocklyToProcedure(Workspace workspace, String sourceXML, TemplateGenerator templateGenerator,
			IBlockGenerator... externalGenerators) throws TemplateGeneratorException {
		super(workspace, BlocklyEditorType.PROCEDURE, sourceXML, templateGenerator, externalGenerators);
	}

	@Override public void preBlocksPlacement(Document doc, Element startBlock) {
		if (doc != null) {
			// first we load data from startblock
			Element trigger = XMLUtil.getFirstChildrenWithName(BlocklyBlockUtil.getStartBlock(doc, "event_trigger"),
					"field");
			if (trigger != null && !trigger.getTextContent().equals("no_ext_trigger")) {
				externalTrigger = trigger.getTextContent();
			}

			// then we add custom local variables
			Element variables = XMLUtil.getFirstChildrenWithName(doc.getDocumentElement(), "variables");
			this.variables = variableGenerator.processLocalVariables(variables);
		}
	}

	@Override public void postBlocksPlacement(Document doc, Element startBlock, List<Element> baseBlocks) {
		if (getReturnType() != null) {
			if (!ArrayUtils.contains(new ReturnBlock().getSupportedBlocks(), lastProceduralBlockType)) {
				addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.invalid_return_block")));
			}
		}
	}

	public VariableType getReturnType() {
		return returnType;
	}

	public void setReturnType(VariableType returnType) {
		this.returnType = returnType;
	}

	public String getExternalTrigger() {
		return externalTrigger;
	}

	public List<VariableElement> getLocalVariables() {
		return variables;
	}

}
