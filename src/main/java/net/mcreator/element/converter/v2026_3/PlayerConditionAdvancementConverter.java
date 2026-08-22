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

package net.mcreator.element.converter.v2026_3;

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

public class PlayerConditionAdvancementConverter implements IConverter {
	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput)
			throws Exception {
		Achievement advancement = (Achievement) input;

		advancement.triggerxml = fixXML(advancement.triggerxml);

		return advancement;
	}

	private String fixXML(String xml) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newDefaultInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("block");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			String type = element.getAttribute("type");
			List<String> supportedBlocks = List.of("block_placed", "dimension_entered", "dimension_left",
					"entity_hurt_player", "item_consumed", "item_damaged", "item_enchanted", "item_in_inventory",
					"item_with_duration_used", "player_effect_changed", "recipe_crafted", "recipe_unlocked", "tick");
			if (supportedBlocks.contains(type)) {
				Element value = doc.createElement("value");
				value.setAttribute("name", "player");
				Element playerCondition = doc.createElement("block");
				playerCondition.setAttribute("type", "player_condition_predicate");

				Element predicateMutation = doc.createElement("mutation");
				predicateMutation.setAttribute("inputs", "0");
				playerCondition.appendChild(predicateMutation);

				value.appendChild(playerCondition);
				element.appendChild(value);
			} else if (type.equals("biome_entered")) {
				element.setAttribute("type", "second");

				Element fieldBiome = XMLUtil.getFirstChildrenWithName(element, "field");
				String biome = fieldBiome != null ? fieldBiome.getTextContent() : "plains";
				element.removeChild(fieldBiome);

				Element value = doc.createElement("value");
				value.setAttribute("name", "player");
				value.appendChild(createPredicateBlock(doc, biome));

				element.appendChild(value);
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		return writer.getBuffer().toString();
	}

	private Element createPredicateBlock(Document doc, String biome) {
		Element predicateBlock = doc.createElement("block");
		predicateBlock.setAttribute("type", "player_condition_predicate");

		Element predicateMutation = doc.createElement("mutation");
		predicateMutation.setAttribute("inputs", "1");
		predicateBlock.appendChild(predicateMutation);

		Element locationComponent = doc.createElement("value");
		locationComponent.setAttribute("name", "predicateComponent0");
		locationComponent.appendChild(createLocationComponent(doc, biome));
		predicateBlock.appendChild(locationComponent);

		return predicateBlock;
	}

	private Element createLocationComponent(Document doc, String biome) {
		Element locationComponent = doc.createElement("block");
		locationComponent.setAttribute("type", "player_predicate_component_location");

		Element predicateMutation = doc.createElement("mutation");
		predicateMutation.setAttribute("inputs", "1");
		locationComponent.appendChild(predicateMutation);

		Element paramField = doc.createElement("value");
		paramField.setAttribute("name", "locationComponent0");
		paramField.appendChild(createBiomeLocationParameter(doc, biome));
		locationComponent.appendChild(paramField);

		return locationComponent;
	}

	private Element createBiomeLocationParameter(Document doc, String biome) {
		Element biomeParam = doc.createElement("block");
		biomeParam.setAttribute("type", "location_component_predicate_biomes");

		Element predicateMutation = doc.createElement("mutation");
		predicateMutation.setAttribute("inputs", "1");
		biomeParam.appendChild(predicateMutation);

		Element biomeField = doc.createElement("field");
		biomeField.setAttribute("name", "biome0");
		biomeField.setTextContent(biome);
		biomeParam.appendChild(biomeField);

		return biomeParam;
	}

	@Override public int getVersionConvertingTo() {
		return 90;
	}
}
