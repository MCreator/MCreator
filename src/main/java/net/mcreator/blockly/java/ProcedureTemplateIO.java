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

package net.mcreator.blockly.java;

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.io.BinaryStringIO;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableElementType;
import net.mcreator.workspace.elements.VariableElementTypeLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcedureTemplateIO {

	public static void exportProcedure(String procedure, File file)
			throws ParseException, ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(procedure)));
		doc.getDocumentElement().normalize();

		Element start_block = BlocklyBlockUtil.getStartBlock(doc, "event_trigger");

		// if there is no start block, we return empty string
		if (start_block == null)
			throw new ParseException("Could not find start block!", -1);

		Element next = XMLUtil.getFirstChildrenWithName(start_block, "next");
		Element block = XMLUtil.getFirstChildrenWithName(next, "block");

		if (block == null)
			throw new ParseException("Could not export block!", -1);

		exportBlocklyXML(block, file);
	}

	public static void exportAITaskSetup(String procedure, File file)
			throws ParseException, ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(procedure)));
		doc.getDocumentElement().normalize();

		Element start_block = BlocklyBlockUtil.getStartBlock(doc, "aitasks_container");

		// if there is no start block, we return empty string
		if (start_block == null)
			throw new ParseException("Could not find start block!", -1);

		Element next = XMLUtil.getFirstChildrenWithName(start_block, "next");
		Element block = XMLUtil.getFirstChildrenWithName(next, "block");

		if (block == null)
			throw new ParseException("Could not export block!", -1);

		exportBlocklyXML(block, file);
	}

	private static void exportBlocklyXML(Element element, File file) {
		DOMImplementationLS lsImpl = (DOMImplementationLS) element.getOwnerDocument().getImplementation()
				.getFeature("LS", "3.0");
		LSSerializer serializer = lsImpl.createLSSerializer();
		serializer.getDomConfig().setParameter("xml-declaration", false);

		BinaryStringIO.writeStringToFile(
				"<xml xmlns=\"http://www.w3.org/1999/xhtml\">" + serializer.writeToString(element)
						.replaceAll("[\\n\\r\\t]", "") + "</xml>", file);
	}

	public static String importBlocklyXML(File template) {
		return BinaryStringIO.readFileToString(template);
	}

	public static String importBlocklyXML(String template) {
		return BinaryStringIO.readResourceToString(template);
	}

	public static Set<VariableElement> tryToExtractVariables(String xml) {
		Set<VariableElement> retval = new HashSet<>();
		for(VariableElementType elementType : VariableElementTypeLoader.INSTANCE.getVariableTypes()) {
			Matcher m = Pattern.compile("<block type=\"(?:variables_set_" + elementType.getName() +
					"|variables_get_" + elementType.getName() + ")\"><field name=\"VAR\">local:(.*?)</field>").matcher(xml);
			try {
				while (m.find()) {
					VariableElement element = new VariableElement();
					element.setName(m.group(1));
					element.setType(elementType);
					retval.add(element);
				}
			} catch (Exception ignored){
			}
		}
		return retval;
	}

}
