/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2025_4;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Achievement;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.Workspace;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class ItemInInventoryTriggerConverter implements IConverter {
	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput)
			throws Exception {
		Achievement advancement = (Achievement) input;
		advancement.triggerxml = fixXML(advancement.triggerxml);
		return advancement;
	}

	private String fixXML(String xml) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new InputSource(new StringReader(xml)));
		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("block");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			String type = element.getAttribute("type");

			if (type.equals("item_in_inventory")) {
				List<Element> values = XMLUtil.getChildrenWithName(element, "value");
				Element item = null;
				Element amountL = null;
				Element amountH = null;
				for (Element e : values) {
					if (e != null && e.hasAttribute("name")) {
						switch (e.getAttribute("name")) {
						case "item" -> item = e;
						case "amount_l" -> amountL = e;
						case "amount_h" -> amountH = e;
						}
					}
				}

				if (item != null) {
					Element predicateBlock = createTag(doc, "block", "type", "item_predicate");

					Element mutationNode = createTag(doc, "mutation", "inputs", "0");
					mutationNode.setAttribute("inputs", "0");
					Element minFieldNode = createTag(doc, "field", "name", "min");
					if (amountL != null)
						minFieldNode.setTextContent(getNumberInputValue(amountL));
					Element maxFieldNode = createTag(doc, "field", "name", "max");
					if (amountH != null)
						maxFieldNode.setTextContent(getNumberInputValue(amountH));

					// We can now add parameters
					predicateBlock.appendChild(item);
					predicateBlock.appendChild(mutationNode);
					if (amountL != null)
						predicateBlock.appendChild(minFieldNode);
					if (amountH != null)
						predicateBlock.appendChild(maxFieldNode);

					// We remove old unnecessary inputs
					element.removeChild(amountL);
					element.removeChild(amountH);

					// We can now replace the old MCItem selector input by our new Item predicate block input
					Element newItem = createTag(doc, "value", "name", "item");
					newItem.appendChild(predicateBlock);
					element.appendChild(newItem);
				}
			}
		}

		StringWriter writer = new StringWriter();
		TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(writer));

		return writer.getBuffer().toString();
	}

	private Element createTag(Document doc, String tagName, String attributeName, String attributeValue) {
		Element element = doc.createElement(tagName);
		element.setAttribute(attributeName, attributeValue);

		return element;
	}

	// This is the structure we're supposed to work with: <value name="amount_l"><block type="math_number"><field name="NUM">1</field></block></value>
	// We want to get and return the `1`
	@Nullable
	private String getNumberInputValue(@Nonnull Element element) {
		Element block = XMLUtil.getFirstChildrenWithName(element, "block");
		if (block != null) {
			Element field = XMLUtil.getFirstChildrenWithName(block, "field");
			if (field != null)
				return field.getTextContent();
		}
		return null;
	}

	@Override public int getVersionConvertingTo() {
		return 81;
	}
}
