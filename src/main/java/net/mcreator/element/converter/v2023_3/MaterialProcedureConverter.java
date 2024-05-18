/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2023_3;

import net.mcreator.element.converter.ProcedureConverter;
import net.mcreator.element.types.Procedure;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class MaterialProcedureConverter extends ProcedureConverter {

	private static final Map<String, String> materialToProcedureBlockMap = new HashMap<>() {{
		// AIR
		// STRUCTURE_VOID
		put("PORTAL",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:portals</field></block></value></block>");
		put("CARPET",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:wool_carpets</field></block></value></block>");
		put("PLANTS",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:replaceable_plants</field></block></value></block>");
		put("OCEAN_PLANT",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:underwater_bonemeals</field></block></value></block>");
		put("TALL_PLANTS",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:tall_flowers</field></block></value></block>");
		// NETHER_PLANTS
		// SEA_GRASS
		// WATER
		// BUBBLE_COLUMN
		// LAVA
		put("SNOW",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:snow</field></block></value></block>");
		put("FIRE",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:fire</field></block></value></block>");
		// MISCELLANEOUS
		// WEB
		// SCULK
		// BUILDABLE_GLASS
		// CLAY
		put("EARTH",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:dirt</field></block></value></block>");
		put("GRASS",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:animals_spawnable_on</field></block></value></block>");
		// PACKED_ICE
		put("SAND",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:sand</field></block></value></block>");
		// SPONGE
		put("SHULKER",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:shulker_boxes</field></block></value></block>");
		put("WOOD",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:mineable/axe</field></block></value></block>");
		// NETHER_WOOD
		// BAMBOO_SAPLING
		// BAMBOO
		put("CLOTH",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:wool</field></block></value></block>");
		// TNT
		put("LEAVES",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:leaves</field></block></value></block>");
		put("GLASS",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:glass</field></block></value></block>");
		put("ICE",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:ice</field></block></value></block>");
		// CACTUS
		put("ROCK",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:mineable/pickaxe</field></block></value></block>");
		put("IRON",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:ores/iron</field></block></value></block>");
		// CRAFTED_SNOW
		put("ANVIL",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:anvil</field></block></value></block>");
		// BARRIER
		// PISTON
		// MOSS_BLOCK
		// GOURD
		// DRAGON_EGG
		// CAKE
		put("AMETHYST",
				"<block type=\"block_is_tagged_in\"><value name=\"a\">[BLOCK]</value><value name=\"b\"><block type=\"text\"><field name=\"TEXT\">minecraft:storage_blocks/amethyst</field></block></value></block>");
		// POWDER_SNOW
		// FROGSPAWN
		// FROGLIGHT
		// DECORATED_POT
	}};

	@Override public int getVersionConvertingTo() {
		return 45;
	}

	@Override protected String fixXML(Procedure procedure, String xml) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("block");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			if ("compare_mcblock_material".equals(element.getAttribute("type"))) {

				String material = null;
				for (Element value : XMLUtil.getChildrenWithName(element, "field")) {
					if (value.getAttribute("name").equals("material")) {
						material = value.getTextContent();
					}
				}

				Element blockToTest = null;
				for (Element value : XMLUtil.getChildrenWithName(element, "value")) {
					if (value.getAttribute("name").equals("a")) {
						blockToTest = XMLUtil.getDirectChildren(value).getFirst();
					}
				}

				String newBlockCode = "<block type=\"logic_boolean\"><field name=\"BOOL\">FALSE</field></block>";

				if (material != null && blockToTest != null) {
					if (materialToProcedureBlockMap.containsKey(material)) {
						newBlockCode = materialToProcedureBlockMap.get(material)
								.replace("[BLOCK]", elementToText(blockToTest));
					}
				}

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document newElementDoc = builder.parse(new InputSource(new StringReader(newBlockCode)));
				Element newElement = newElementDoc.getDocumentElement();
				Node importedNode = doc.importNode(newElement, true);
				Node clonedNode = doc.adoptNode(importedNode);
				Element parentElement = (Element) element.getParentNode();
				parentElement.replaceChild(clonedNode, element);
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));

		return writer.getBuffer().toString();
	}

	private String elementToText(Element element) throws Exception {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(element), new StreamResult(writer));
		return writer.getBuffer().toString();
	}

}