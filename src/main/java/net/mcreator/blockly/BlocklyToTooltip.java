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

package net.mcreator.blockly;

import net.mcreator.blockly.java.BlocklyToJava;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class BlocklyToTooltip extends BlocklyToJava {

	private static final Logger LOG = LogManager.getLogger("Blockly2Tooltip");

	public BlocklyToTooltip(Workspace workspace, String sourceXML, TemplateGenerator templateGenerator,
			IBlockGenerator... externalGenerators) throws TemplateGeneratorException {
		super(workspace, templateGenerator, externalGenerators);

		if (sourceXML != null) {
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(new InputSource(new StringReader(sourceXML)));
				doc.getDocumentElement().normalize();

				Element start_block = BlocklyBlockUtil.getStartBlock(doc, "tooltip_start");

				// if there is no start block, we return empty string
				if (start_block == null) throw new ParseException("Could not find start block!", -1);

				// find all blocks placed under start block
				List<Element> base_blocks = BlocklyBlockUtil.getBlockProcedureStartingWithNext(start_block);
				processBlockProcedure(base_blocks);

			} catch (TemplateGeneratorException e) {
				throw e;
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						"Exception while compiling blocks: " + e.getMessage()));
			}
		}
	}

	/**
	 * A hard-coded method for finding translatable_text blocks from a list of elements and putting in a map
	 * @param map A map to put
	 * @param blocks A list of blockly blocks
	 */
	public static void processTooltipProcedure(Map<String, String> map, List<Element> blocks) {
		for (Element block : blocks) {
			String type = block.getAttribute("type");

			// If its a `if block` we do some extra processing and re-run the method
			if (type.equals("controls_if")) {
				List<Element> statements = XMLUtil.getChildrenWithName(block, "statement");
				for (Element block2 : statements) {
					List<Element> blocks2 = BlocklyBlockUtil.getBlockProcedureStartingWithBlock(block2);
					processTooltipProcedure(map, blocks2);
				}
			// If its the block we want, we get the fields and put them to the map
			} else if (type.equals("translatable_text")) {
				NodeList nodeList = block.getElementsByTagName("field");
				// Default text
				String untranslated_text = nodeList.item(0).getTextContent();
				// Translation key
				String translation_key = nodeList.item(1).getTextContent();
				map.put(untranslated_text, translation_key);
			} else {
				// nothing
			}
		}
	}
}
