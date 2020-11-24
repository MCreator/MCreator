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

import org.apache.commons.lang3.ArrayUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class XMLUtil {

	public static List<Element> getDirectChildren(Element element) {
		return getChildrenWithName(element, (String[]) null);
	}

	public static List<Element> getChildrenWithName(Element element, String... names) {
		List<Element> elements = new ArrayList<>();
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (names == null || (names.length == 1 && names[0].equals(node.getNodeName())) || ArrayUtils
						.contains(names, node.getNodeName()))
					elements.add((Element) node);
			}
		}
		return elements;
	}

	public static List<Element> getAllChildrenWithName(Element element, String... names) {
		List<Element> elements = new ArrayList<>();
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (names == null || (names.length == 1 && names[0].equals(node.getNodeName())) || ArrayUtils
						.contains(names, node.getNodeName()))
					elements.add((Element) node);
				for (Element grandElement : getAllChildrenWithName((Element) node, names)) {
					elements.add(grandElement);
				}
			}
		}
		return elements;
	}

	public static Element getFirstChildrenWithName(Element element, String... names) {
		List<Element> elements = getChildrenWithName(element, names);
		if (elements.size() > 0) {
			return elements.get(0);
		} else {
			return null;
		}
	}

}
