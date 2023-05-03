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

package net.mcreator.element.converter.fv8;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Procedure;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

public class OpenGUIProcedureDepFixer implements IConverter {

	private static final Logger LOG = LogManager.getLogger("OpenGUIProcedureDepFixer");

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Procedure procedure = (Procedure) input;
		try {
			procedure.procedurexml = fixXML(procedure.procedurexml);
		} catch (Exception e) {
			LOG.warn("Failed to fix entity dependency for procedure " + input.getModElement().getName());
		}
		return procedure;
	}

	@Override public int getVersionConvertingTo() {
		return 8;
	}

	protected String fixXML(String xml) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("block");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			String type = element.getAttribute("type");
			if (type != null && type.equals("entity_open_gui")) {
				Element value = doc.createElement("value");
				value.setAttribute("name", "entity");
				Element deps_block = doc.createElement("block");
				deps_block.setAttribute("type", "entity_from_deps");
				value.appendChild(deps_block);
				element.appendChild(value);

				value = doc.createElement("value");
				value.setAttribute("name", "x");
				deps_block = doc.createElement("block");
				deps_block.setAttribute("type", "coord_x");
				value.appendChild(deps_block);
				element.appendChild(value);

				value = doc.createElement("value");
				value.setAttribute("name", "y");
				deps_block = doc.createElement("block");
				deps_block.setAttribute("type", "coord_y");
				value.appendChild(deps_block);
				element.appendChild(value);

				value = doc.createElement("value");
				value.setAttribute("name", "z");
				deps_block = doc.createElement("block");
				deps_block.setAttribute("type", "coord_z");
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

}