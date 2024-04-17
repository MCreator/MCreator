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

package net.mcreator.element.converter.v2024_2;

import net.mcreator.element.converter.ProcedureConverter;
import net.mcreator.element.types.Procedure;
import net.mcreator.util.BlocklyHelper;
import net.mcreator.util.XMLUtil;
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

public class CallProcedureAtBlockConverter extends ProcedureConverter {

	@Override public int getVersionConvertingTo() {
		return 61;
	}

	@Override protected String fixXML(Procedure procedure, String xml) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		doc.getDocumentElement().normalize();
		BlocklyHelper bh = new BlocklyHelper(doc);

		NodeList nodeList = doc.getElementsByTagName("block");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			if ("call_procedure_at".equals(element.getAttribute("type"))) {
				// Rename the value inputs
				for (Element value : XMLUtil.getChildrenWithName(element, "value")) {
					switch (value.getAttribute("name")) {
						case "x" -> value.setAttribute("name", "arg0");
						case "y" -> value.setAttribute("name", "arg1");
						case "z" -> value.setAttribute("name", "arg2");
					}
				}

				// Create the mutation element
				Element mutationXML = doc.createElement("mutation");
				mutationXML.setAttribute("inputs", "3");

				// Append the mutation and field elements
				element.appendChild(mutationXML);
				element.appendChild(bh.createField("name0", "x"));
				element.appendChild(bh.createField("name1", "y"));
				element.appendChild(bh.createField("name2", "z"));

				// Convert the block
				element.setAttribute("type", "call_procedure");
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));

		return writer.getBuffer().toString();
	}
}
