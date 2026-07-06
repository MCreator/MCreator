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
				Element deps_block = doc.createElement("block");
				deps_block.setAttribute("type", "player_condition_none");
				value.appendChild(deps_block);
				element.appendChild(value);
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		return writer.getBuffer().toString();
	}

	@Override public int getVersionConvertingTo() {
		return 90;
	}
}
