/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.element.converter.fv39;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Procedure;
import net.mcreator.util.BlocklyHelper;
import net.mcreator.util.XMLUtil;
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

public class CallProcedureBlockConverter implements IConverter {
	private static final Logger LOG = LogManager.getLogger("CallProcedureBlockConverter");

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Procedure procedure = (Procedure) input;
		try {
			procedure.procedurexml = fixXML(procedure.procedurexml);
		} catch (Exception e) {
			LOG.warn("Failed to convert procedure " + input.getModElement().getName());
		}
		return procedure;
	}

	@Override public int getVersionConvertingTo() {
		return 39;
	}

	protected String fixXML(String xml) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		doc.getDocumentElement().normalize();
		BlocklyHelper bh = new BlocklyHelper(doc);

		NodeList nodeList = doc.getElementsByTagName("block");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			if ("call_procedure_at".equals(element.getAttribute("type"))) {
				Element xField = bh.createField("name0", "x");
				Element xValue = bh.createValue("arg0", null);
				Element yField = bh.createField("name1", "y");
				Element yValue = bh.createValue("arg1", null);
				Element zField = bh.createField("name2", "z");
				Element zValue = bh.createValue("arg2", null);

				for (Element value : XMLUtil.getChildrenWithName(element, "value")) {
					switch (value.getAttribute("name")) {
						case "x" -> xValue.appendChild(value);
						case "y" -> yValue.appendChild(value);
						case "z" -> zValue.appendChild(value);
					}
				}

				Element mutationXML = doc.createElement("mutation");
				mutationXML.setAttribute("params", "3");
				element.appendChild(mutationXML);

				Element callProcedureBlock = bh.createBlock("call_procedure", mutationXML, xField, xValue,
						yField, yValue, zField, zValue);
				doc.replaceChild(callProcedureBlock, element);
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));

		return writer.getBuffer().toString();
	}
}