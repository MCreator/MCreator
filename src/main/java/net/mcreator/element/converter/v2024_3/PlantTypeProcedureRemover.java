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
import org.w3c.dom.Node;
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

public class PlantTypeProcedureRemover extends ProcedureConverter {

	// Starting Minecraft 1.20.6, there is no harvest level concept anymore,
	// so there is no conversion route but to use placeholder value of 0 in those cases
	private static final String PLACEHOLDER_BLOCK = """
			<block type="logic_boolean">
				<field name="BOOL">FALSE</field>
				<comment h="92" w="220">Plant type system no longer exists so this block was automatically replaced with placeholder value of false</comment>
			</block>
			""";

	@Override public int getVersionConvertingTo() {
		return 67;
	}

	@Override protected String fixXML(Procedure procedure, String xml) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("block");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			if ("blockat_plant_type".equals(element.getAttribute("type"))) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document newElementDoc = builder.parse(new InputSource(new StringReader(PLACEHOLDER_BLOCK)));
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

}
