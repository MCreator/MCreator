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
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.VariableElementType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.ParseException;
import java.util.List;

public class BlocklyToProcedure extends BlocklyToJava {

	private static final Logger LOG = LogManager.getLogger("Blockly2Procedure");

	private String externalTrigger = null;
	private List<String> variables;
	private VariableElementType returnType = null;

	public BlocklyToProcedure(Workspace workspace, String sourceXML, TemplateGenerator templateGenerator,
			IBlockGenerator... externalGenerators) throws TemplateGeneratorException {
		super(workspace, templateGenerator, externalGenerators);

		BlocklyVariables variableGenerator = new BlocklyVariables(this);

		if (sourceXML != null) {
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(new InputSource(new StringReader(sourceXML)));
				doc.getDocumentElement().normalize();

				Element start_block = BlocklyBlockUtil.getStartBlock(doc, "event_trigger");

				// if there is no start block, we return empty string
				if (start_block == null)
					throw new ParseException("Could not find start block!", -1);

				// first we load data from startblock
				Element trigger = XMLUtil.getFirstChildrenWithName(start_block, "field");
				if (trigger != null && !trigger.getTextContent().equals("no_ext_trigger")) {
					externalTrigger = trigger.getTextContent();
				}

				// first we add custom local variables
				Element variables = XMLUtil.getFirstChildrenWithName(doc.getDocumentElement(), "variables");
				this.variables = variableGenerator.processLocalVariables(variables);

				// find all blocks placed under start block
				List<Element> base_blocks = BlocklyBlockUtil.getBlockProcedureStartingWithNext(start_block);
				processBlockProcedure(base_blocks);

				if (getReturnType() != null) {
					if (!ArrayUtils.contains(new ReturnBlock().getSupportedBlocks(), lastProceduralBlockType)) {
						addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
								"Procedure that uses return value must always end with a valid return block"));
					}
				}
			} catch (TemplateGeneratorException e) {
				throw e;
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						"Exception while compiling blocks: " + e.getMessage()));
			}
		}
	}

	public VariableElementType getReturnType() {
		return returnType;
	}

	public void setReturnType(VariableElementType returnType) {
		this.returnType = returnType;
	}

	public String getExternalTrigger() {
		return externalTrigger;
	}

	public List<String> getVariables() {
		return variables;
	}

}
