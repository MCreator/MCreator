/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2026_2;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class ItemPredicateAdvancementConverter implements IConverter {

	@Override
	public final GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput)
			throws Exception {
		Achievement advancement = (Achievement) input;

		advancement.triggerxml = fixXML(advancement.triggerxml);

		return advancement;
	}

	private String fixXML(String xml) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("block");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			String type = element.getAttribute("type");
			switch (type) {
			case "item_enchanted" -> convertItemEnchantedTrigger(doc, element);
			case "item_damaged" -> convertItemDamagedAndInventoryTriggers(doc, element, true);
			case "item_in_inventory" -> convertItemDamagedAndInventoryTriggers(doc, element, false);
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		return writer.getBuffer().toString();
	}

	private void convertItemEnchantedTrigger(Document doc, Element element) {
		Element mutation = XMLUtil.getFirstChildrenWithName(element, "mutation");
		List<Element> enchantmentEntries = XMLUtil.getChildrenWithName(element, "value").stream()
				.filter(e -> e.hasAttribute("name") && e.getAttribute("name").startsWith("enchantment")).toList();

		if (mutation != null && mutation.hasAttribute("inputs")) {
			Element item = XMLUtil.getFirstChildrenWithName(element, "value");

			if (item != null && item.hasAttribute("name")) {
				Element enchantmentComponent = doc.createElement("block");
				enchantmentComponent.setAttribute("type", "data_component_predicate_enchantments");
				enchantmentComponent.appendChild(mutation);
				enchantmentEntries.forEach(enchantmentComponent::appendChild);

				item.appendChild(createPredicateBlock(doc, item, enchantmentComponent));
			}
		}
	}

	private void convertItemDamagedAndInventoryTriggers(Document doc, Element element, boolean isItemDamaged) {
		Element item = null;
		String min = "1", max = isItemDamaged ? "100" : "99";

		for (Element e : XMLUtil.getChildrenWithName(element, "value")) {
			if (e.hasAttribute("name")) {
				switch (e.getAttribute("name")) {
				case "item" -> item = e;
				case "amount_l" -> {
					Element block = XMLUtil.getFirstChildrenWithName(e, "block");
					if (block != null) {
						Element field = XMLUtil.getFirstChildrenWithName(block, "field");
						if (field != null) {
							min = field.getTextContent();
						}
					}
					element.removeChild(e);
				}
				case "amount_h" -> {
					Element block = XMLUtil.getFirstChildrenWithName(e, "block");
					if (block != null) {
						Element field = XMLUtil.getFirstChildrenWithName(block, "field");
						if (field != null) {
							max = field.getTextContent();
						}
					}
					element.removeChild(e);
				}
				}
			}
		}

		if (item != null) {
			Element predicateBlock = isItemDamaged ?
					createPredicateBlock(doc, item, createDamageComponent(doc, min, max)) :
					createPredicateBlock(doc, item, null, min, max);
			item.appendChild(predicateBlock);
		}
	}

	private Element createPredicateBlock(Document doc, Element item, Element dataComponent) {
		return createPredicateBlock(doc, item, dataComponent, "1", "99");
	}

	private Element createPredicateBlock(Document doc, Element item, Element dataComponent, String countMin,
			String countMax) {
		Element predicateBlock = doc.createElement("block");
		predicateBlock.setAttribute("type", "item_predicate");

		Element predicateMutation = doc.createElement("mutation");
		predicateMutation.setAttribute("inputs", dataComponent != null ? "1" : "0");
		predicateBlock.appendChild(predicateMutation);

		Element minField = doc.createElement("field");
		minField.setAttribute("name", "min");
		minField.setTextContent(countMin);
		predicateBlock.appendChild(minField);

		Element maxField = doc.createElement("field");
		maxField.setAttribute("name", "max");
		maxField.setTextContent(countMax);
		predicateBlock.appendChild(maxField);

		Element itemValue = doc.createElement("value");
		itemValue.setAttribute("name", "item");
		itemValue.appendChild(XMLUtil.getFirstChildrenWithName(item, "block"));
		predicateBlock.appendChild(itemValue);

		if (dataComponent != null) {
			Element predicateComponent = doc.createElement("value");
			predicateComponent.setAttribute("name", "predicateComponent0");
			predicateComponent.appendChild(dataComponent);
			predicateBlock.appendChild(predicateComponent);
		}

		return predicateBlock;
	}

	private Element createDamageComponent(Document doc, String min, String max) {
		Element damageComponent = doc.createElement("block");
		damageComponent.setAttribute("type", "data_component_predicate_damage");

		Element minField = doc.createElement("field");
		minField.setAttribute("name", "min");
		minField.setTextContent(min);
		damageComponent.appendChild(minField);

		Element maxField = doc.createElement("field");
		maxField.setAttribute("name", "max");
		maxField.setTextContent(max);
		damageComponent.appendChild(maxField);

		return damageComponent;
	}

	@Override public int getVersionConvertingTo() {
		return 87;
	}
}
