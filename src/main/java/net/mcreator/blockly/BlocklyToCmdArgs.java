/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2021 Pylo and contributors
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

import net.mcreator.blockly.java.BlocklyToJava;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.ParseException;

public class BlocklyToCmdArgs extends BlocklyToJava {

	private static final Logger LOG = LogManager.getLogger("Blockly2CmdArgs");

	public BlocklyToCmdArgs(Workspace workspace, String sourceXML, TemplateGenerator templateGenerator,
			IBlockGenerator... externalGenerators) throws TemplateGeneratorException {
		super(workspace, templateGenerator, externalGenerators);

		if (sourceXML != null) {
			try {
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.parse(new InputSource(new StringReader(sourceXML)));
				doc.getDocumentElement().normalize();

				Element start_block = BlocklyBlockUtil.getStartBlock(doc, "args_start");

				// if there is no start block, we return empty string
				if (start_block == null)
					throw new ParseException("Could not find start block!", -1);

				// find all blocks placed under start block
				processBlockProcedure(BlocklyBlockUtil.getBlockProcedureStartingWithNext(start_block));
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						"Exception while compiling blocks: " + e.getMessage()));
			}
		}
	}

}
