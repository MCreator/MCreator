/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2024_3;

import net.mcreator.element.converter.ProcedureConverter;
import net.mcreator.element.types.Procedure;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

public class WorldDependencyAdder extends ProcedureConverter {

	private static final List<String> BLOCKS_THAT_NOW_REQUIRE_WORLD = Arrays.asList("item_damage",
			"item_add_enhancement", "item_get_enhancement", "itemstack_has_enchantment",
			"itemstack_remove_specific_enchantment", "item_enchanted_with_xp");

	@Override public int getVersionConvertingTo() {
		return 68;
	}

	@Override protected String fixXML(Procedure procedure, String xml) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("block");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			if (BLOCKS_THAT_NOW_REQUIRE_WORLD.contains(element.getAttribute("type"))) {
				// These blocks now report world dependency requirement by themselves so we only need to raise the flag here
				// to update dependencies list during conversion
				reportDependenciesChanged();
			}
		}

		return xml;
	}

}

