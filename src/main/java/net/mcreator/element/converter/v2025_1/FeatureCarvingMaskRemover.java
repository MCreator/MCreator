/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.element.converter.v2025_1;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Feature;
import net.mcreator.util.BlocklyHelper;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.Workspace;
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

public class FeatureCarvingMaskRemover implements IConverter {

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput)
			throws Exception {
		Feature feature = (Feature) input;
		feature.featurexml = fixXML(feature.featurexml);
		return feature;
	}

	@Override public int getVersionConvertingTo() {
		return 74;
	}

	private String fixXML(String xml) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		doc.getDocumentElement().normalize();
		BlocklyHelper bh = new BlocklyHelper(doc);

		NodeList nodeList = doc.getElementsByTagName("block");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			if ("placement_carving_mask".equals(element.getAttribute("type"))) {
				// Convert carving mask placement to uniform height placement, remove old "carver" field
				element.setAttribute("type", "placement_height_uniform");
				Element field = XMLUtil.getFirstChildrenWithName(element, "field");
				element.removeChild(field);

				// Create vertical anchors
				Element minHeight = bh.createBlock("vertical_anchor_above_bottom", bh.createField("value", "5"));
				Element maxHeight = bh.createBlock("vertical_anchor_below_top", bh.createField("value", "5"));

				// Add min/max height values to this block
				element.appendChild(bh.createValue("min", minHeight));
				element.appendChild(bh.createValue("max", maxHeight));
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));

		return writer.getBuffer().toString();
	}
}
