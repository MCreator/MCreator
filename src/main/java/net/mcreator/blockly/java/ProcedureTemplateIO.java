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
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.util.XMLUtil;
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

public class ProcedureTemplateIO {

	public static void exportBlocklySetup(String blocklyXML, File file, BlocklyEditorType blocklyEditorType)
			throws ParseException, ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(blocklyXML)));
		doc.getDocumentElement().normalize();

		Element start_block = BlocklyBlockUtil.getStartBlock(doc, blocklyEditorType.getStartBlockName());

		// if there is no start block, we return empty string
		if (start_block == null)
			throw new ParseException("Could not find start block!", -1);

		Element next = XMLUtil.getFirstChildrenWithName(start_block, "next");
		Element nextBlock = next == null ? null : XMLUtil.getFirstChildrenWithName(next, "block");

		Element input = XMLUtil.getFirstChildrenWithName(start_block, "value");
		Element inputBlock = input == null ? null : XMLUtil.getFirstChildrenWithName(input, "block");

		if (nextBlock == null && inputBlock == null)
			throw new ParseException("Could not export block!", -1);

		exportBlocklyXML(nextBlock, inputBlock, file);
	}

	private static void exportBlocklyXML(Element nextBlock, Element inputBlock, File file) {
		var nonNullBlock = nextBlock == null ? inputBlock : nextBlock;
		DOMImplementationLS lsImpl = (DOMImplementationLS) nonNullBlock.getOwnerDocument().getImplementation()
				.getFeature("LS", "3.0");
		LSSerializer serializer = lsImpl.createLSSerializer();
		serializer.getDomConfig().setParameter("xml-declaration", false);

		BinaryStringIO.writeStringToFile("""
			<xml xmlns="http://www.w3.org/1999/xhtml">%s%s</xml>""".formatted(
					inputBlock == null ? "" : serializer.writeToString(inputBlock).replaceAll("[\n\r\t]", ""),
					nextBlock == null ? "" : serializer.writeToString(nextBlock).replaceAll("[\n\r\t]", "")), file);
	}

	public static String importBlocklyXML(File template) {
		return BinaryStringIO.readFileToString(template).replace("variables_get_text",
						"variables_get_string") // The same converter as fv21.ProcedureVariablesConverter, but it converts all Blockly templates
				.replace("variables_set_text", "variables_set_string")
				.replace("custom_dependency_text", "custom_dependency_string")
				.replace("procedure_retval_text", "procedure_retval_string").replace("return_text", "return_string");
	}

	public static String importBlocklyXML(String template) {
		return BinaryStringIO.readResourceToString(template);
	}

}
