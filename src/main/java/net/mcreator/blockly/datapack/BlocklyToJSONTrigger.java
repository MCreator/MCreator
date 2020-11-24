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

package net.mcreator.blockly.datapack;

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.datapack.blocks.MCItemBlock;
import net.mcreator.blockly.datapack.blocks.NumberBlock;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

public class BlocklyToJSONTrigger extends BlocklyToCode {

	private static final Logger LOG = LogManager.getLogger("Blockly2JSONTrigger");

	private boolean hasTrigger;

	public BlocklyToJSONTrigger(Workspace workspace, String sourceXML, TemplateGenerator templateGenerator,
			IBlockGenerator... externalGenerators) throws TemplateGeneratorException {
		super(workspace, templateGenerator, externalGenerators);

		blockGenerators.add(new NumberBlock());
		blockGenerators.add(new MCItemBlock());

		if (sourceXML != null) {
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(new InputSource(new StringReader(sourceXML)));
				doc.getDocumentElement().normalize();

				XPathFactory xpathFactory = XPathFactory.newInstance();
				XPath xpath = xpathFactory.newXPath();

				NodeList blocks = (NodeList) xpath.evaluate("block", doc.getDocumentElement(), XPathConstants.NODESET);

				hasTrigger = false;

				Element start_block = null;
				for (int i = 0; i < blocks.getLength(); i++) {
					Element start_block_candidate = (Element) blocks.item(i);

					List<Element> children = BlocklyBlockUtil.getBlockProcedureStartingWithNext(start_block_candidate);
					if (children.size() == 1) {
						if (children.get(0).getAttribute("type").equals("advancement_trigger")) {
							start_block = start_block_candidate;
						}
					}
				}

				if (start_block != null) {
					String type = start_block.getAttribute("type");
					if (!type.equals("advancement_trigger")) {
						hasTrigger = true;
						processBlockProcedure(Collections.singletonList(start_block));
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

	public boolean hasTrigger() {
		return hasTrigger;
	}

}
