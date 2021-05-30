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

import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.elements.VariableElementType;
import net.mcreator.workspace.elements.VariableElementTypeLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlocklyBlockUtil {

	/**
	 * Returns the start block (trigger block) for the given XML document
	 *
	 * @param document XML document to look in
	 * @return Start block XML element or null if not found
	 */
	public static Element getStartBlock(Document document, String typeBlockName) {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		NodeList blocks;
		try {
			blocks = (NodeList) xpath.evaluate("block", document.getDocumentElement(), XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			return null;
		}
		Element start_block = null;
		for (int i = 0; i < blocks.getLength() && start_block == null; i++) {
			if (blocks.item(i) instanceof Element) {
				Element block = (Element) blocks.item(i);
				if (block.getAttribute("type").equals(typeBlockName)) {
					start_block = block;
				}
			}
		}
		return start_block;
	}

	public static List<Element> getBlockProcedureStartingWithBlock(Element start_block) {
		List<Element> base_blocks = new ArrayList<>();
		List<Element> nextblock = XMLUtil.getChildrenWithName(start_block, "block");
		if (nextblock.size() != 0) {
			Element block = nextblock.get(0);
			if (block != null) {
				base_blocks.add(block);
				// after we have first block, we can find all other blocks
				base_blocks.addAll(getBlockProcedureStartingWithNext(block));
			}
		}
		return base_blocks;
	}

	/**
	 * Returns the list of blocks that are directly arranged in the procedural order for the
	 * given start block under which to look for next elements
	 * <p>
	 * NOTE: The first element under given start_block must be "next" in order for this method
	 * to work.
	 *
	 * @param start_block The block under which to look for elements
	 * @return List of blocks under given block in execute order
	 */
	public static List<Element> getBlockProcedureStartingWithNext(Element start_block) {
		Element current = start_block;
		List<Element> base_blocks = new ArrayList<>();
		while (true) {
			// get next element if there is one
			List<Element> nextchildren = XMLUtil.getChildrenWithName(current, "next");
			if (nextchildren.size() == 0)
				break;
			Element next = nextchildren.get(0);
			if (next == null)
				break;

			List<Element> nextblock = XMLUtil.getChildrenWithName(next, "block");
			if (nextblock.size() == 0)
				break;
			Element block = nextblock.get(0);
			if (block != null) {
				base_blocks.add(block);
				current = block;
			}
		}
		return base_blocks;
	}

	/**
	 * Calculates block's full color out of its hue value.
	 *
	 * @param hue The block's hue number
	 * @return Block's render number
	 */
	public static Color getBlockColorFromHUE(int hue) {
		return Color.getHSBColor(hue / 360f, 0.37f, 0.6f);
	}

	public static String addProcedureBlocksToCategories(String toolbox_xml) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(Objects.requireNonNull(
					ClassLoader.getSystemClassLoader().getResource("blockly/toolbox_procedure.xml")).getFile()));
			String line;
			StringBuilder newLine;
			while ((line = r.readLine()) != null) {
				if (line.contains("category.custom_variables")) {
					newLine = new StringBuilder(line);
					for (VariableElementType varType : VariableElementTypeLoader.INSTANCE.getVariableTypes()) {
						newLine.append("\n<block type=\"variables_get_").append(varType.getName())
								.append("\"/>\n<block type=\"variables_set_").append(varType.getName()).append("\"/>");
					}
					if (!newLine.toString().equals(line)) {
						toolbox_xml = toolbox_xml.replace(line, newLine.toString());
					}
					//We check for the last line so we can add blocks after the other blocks
				} else if (line.contains("<custom-advanced/>")) {
					newLine = new StringBuilder();
					for (VariableElementType varType : VariableElementTypeLoader.INSTANCE.getVariableTypes()) {
						newLine.append("\n<block type=\"custom_dependency_").append(varType.getName())
								.append("\"/>\n<block type=\"procedure_retval_").append(varType.getName())
								.append("\"/>");
					}
					newLine.append(line);
					if (!newLine.toString().equals(line)) {
						toolbox_xml = toolbox_xml.replace(line, newLine.toString());
					}
				} else if (line.contains("<block type=\"controls_repeat_ext\"/>")) {
					newLine = new StringBuilder();
					for (VariableElementType varType : VariableElementTypeLoader.INSTANCE.getVariableTypes()) {
						newLine.append("\n<block type=\"return_").append(varType.getName()).append("\"/>");
					}
					newLine.append(line);
					if (!newLine.toString().equals(line)) {
						toolbox_xml = toolbox_xml.replace(line, newLine.toString());
					}
				}
			}
			return toolbox_xml;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return toolbox_xml;
	}

}
