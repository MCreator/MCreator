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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

	/**
	 * Calculate the HUE of a block from RGB
	 *
	 * @param var The variable element to use
	 * @return Built-in color or HUE for other blocks
	 */
	public static String getHUEFromRGB(VariableElementType var) {
		//Built-in colors
		if (var.getType().equalsIgnoreCase("string"))
			return "\"%{BKY_TEXTS_HUE}\"";
		else if (var.getType().equalsIgnoreCase("number"))
			return "\"%{BKY_MATH_HUE}\"";
		else if (var.getType().equalsIgnoreCase("logic"))
			return "\"%{BKY_LOGIC_HUE}\"";

		//Custom colors
		float[] hsbValues = new float[] { (float) var.getColor().getRed() / 255,
				(float) var.getColor().getGreen() / 255, (float) var.getColor().getBlue() / 255 };
		//We find the minimum and the maximal values
		float min = Math.min(hsbValues[0], hsbValues[1]);
		min = Math.min(min, hsbValues[2]);
		float max = Math.max(hsbValues[0], hsbValues[1]);
		max = Math.max(max, hsbValues[2]);

		float ret;
		//If Red is the maximal value
		if (max == hsbValues[0]) {
			ret = (hsbValues[1] - hsbValues[2]) / (max - min);
			//If Green is the maximal value
		} else if (max == hsbValues[1]) {
			ret = 2 + (hsbValues[2] - hsbValues[0]) / (max - min);
			//If Blue is the maximal value
		} else {
			ret = 4 + (hsbValues[0] - hsbValues[1]) / (max - min);
		}

		//We multiply Hue by 60 for a degree value
		ret = ret * 60;

		//If Hue is negative, we add 360 deg
		if (ret < 0)
			ret += 360;
		return String.valueOf(Math.round(ret));
	}

}
