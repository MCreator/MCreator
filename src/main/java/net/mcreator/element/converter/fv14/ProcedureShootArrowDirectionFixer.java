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

package net.mcreator.element.converter.fv14;

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

public class ProcedureShootArrowDirectionFixer implements IConverter {

	private static final Logger LOG = LogManager.getLogger("ProcedureShootArrowDirectionFixer");

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Procedure procedure = (Procedure) input;

		try {
			procedure.procedurexml = fixXML(procedure.procedurexml);
		} catch (Exception e) {
			LOG.warn("Failed to fix string dependency for procedure " + input.getModElement().getName());
		}

		return procedure;
	}

	@Override public int getVersionConvertingTo() {
		return 14;
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
			if (type != null && type.equals("shoot_arrow")) {
				Element value_yaw = doc.createElement("value");
				value_yaw.setAttribute("name", "yaw");
				Element shoot_yaw = doc.createElement("block");
				shoot_yaw.setAttribute("type", "entity_direction");

				Element value_yaw_entity = doc.createElement("value");
				value_yaw_entity.setAttribute("name", "entity");
				Element shoot_yaw_entity = doc.createElement("block");
				shoot_yaw_entity.setAttribute("type", "entity_from_deps");

				value_yaw_entity.appendChild(shoot_yaw_entity);
				shoot_yaw.appendChild(value_yaw_entity);
				value_yaw.appendChild(shoot_yaw);
				element.appendChild(value_yaw);

				Element value_pitch = doc.createElement("value");
				value_pitch.setAttribute("name", "pitch");
				Element shoot_pitch = doc.createElement("block");
				shoot_pitch.setAttribute("type", "entity_pitch");

				Element value_pitch_entity = doc.createElement("value");
				value_pitch_entity.setAttribute("name", "entity");
				Element shoot_pitch_entity = doc.createElement("block");
				shoot_pitch_entity.setAttribute("type", "entity_from_deps");

				value_pitch_entity.appendChild(shoot_pitch_entity);
				shoot_pitch.appendChild(value_pitch_entity);
				value_pitch.appendChild(shoot_pitch);
				element.appendChild(value_pitch);
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));

		return writer.getBuffer().toString();
	}

}