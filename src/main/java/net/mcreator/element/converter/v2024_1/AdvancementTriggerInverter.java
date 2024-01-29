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

package net.mcreator.element.converter.v2024_1;

import com.google.gson.JsonElement;
import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Achievement;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.util.BlocklyHelper;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class AdvancementTriggerInverter implements IConverter {
	private static final Logger LOG = LogManager.getLogger("AdvancementTriggerInverter");

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Achievement advancement = (Achievement) input;
		try {
			advancement.triggerxml = fixXML(advancement.triggerxml);
		} catch (Exception e) {
			LOG.warn("Failed to convert advancement " + input.getModElement().getName(), e);
		}
		return advancement;
	}

	@Override public int getVersionConvertingTo() {
		return 59;
	}

	private String fixXML(String xml) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		doc.getDocumentElement().normalize();
		BlocklyHelper bh = new BlocklyHelper(doc);

		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		NodeList blocks = (NodeList) xpath.evaluate("block", doc.getDocumentElement(), XPathConstants.NODESET);

		Element trigger = null;
		for (int i = 0; i < blocks.getLength(); i++) {
			Element candidate = (Element) blocks.item(i);

			List<Element> children = BlocklyBlockUtil.getBlockProcedureStartingWithNext(candidate);
			if (children.size() == 1 && children.get(0).getAttribute("type")
					.equals(BlocklyEditorType.JSON_TRIGGER.startBlockName())) {
				trigger = candidate;
				break;
			}
		}

		Element start_block = bh.createBlock(BlocklyEditorType.JSON_TRIGGER.startBlockName());
		start_block.setAttribute("deletable", "false");
		start_block.setAttribute("x", "40");
		start_block.setAttribute("y", "80");
		Element next = doc.createElement("next");
		if (trigger != null)
			trigger.removeChild(XMLUtil.getFirstChildrenWithName(trigger, "next"));
		else
			trigger = bh.createBlock("custom_trigger");
		next.appendChild(trigger);
		start_block.appendChild(next);
		doc.getDocumentElement().appendChild(start_block);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));

		return writer.getBuffer().toString();
	}
}
