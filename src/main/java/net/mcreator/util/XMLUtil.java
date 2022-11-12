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

	/**
	 * Returns a list of all child elements of the given element with the given name
	 *
	 * @param element Element to list children for
	 * @param names Names of children to consider. If null, all children will be listed. If multiple names are provided, only
	 *              the first name that matches any children will be considered and children with other names will be discarded.
	 * @return List of child elements
	 */
	public static List<Element> getChildrenWithName(Element element, String... names) {
		List<Element> elements = new ArrayList<>();
		NodeList nodeList = element.getChildNodes();

		if (names == null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					elements.add((Element) node);
				}
			}
		} else {
			for (String name : names) {
				boolean foundMatch = false;
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(name)) {
						elements.add((Element) node);
						foundMatch = true;
					}
				}
				if (foundMatch)
					break; // we only process the first name the match was found under
			}
		}

		return elements;
	}

	public static List<Element> getDirectChildren(Element element) {
		return getChildrenWithName(element, (String[]) null);
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
