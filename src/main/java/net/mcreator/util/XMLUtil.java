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

package net.mcreator.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class XMLUtil {

	public static List<Element> getDirectChildren(Element element) {
		return getChildrenWithName(null, element);
	}

	public static List<Element> getChildrenWithName(String name, Element element) {
		List<Element> elements = new ArrayList<>();
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (name == null || node.getNodeName().equals(name))
					elements.add((Element) node);
			}
		}
		return elements;
	}

	public static Element getFirstChildrenWithName(String name, Element element) {
		List<Element> elements = getChildrenWithName(name, element);
		if (elements.size() > 0) {
			return elements.get(0);
		} else {
			return null;
		}
	}

}
