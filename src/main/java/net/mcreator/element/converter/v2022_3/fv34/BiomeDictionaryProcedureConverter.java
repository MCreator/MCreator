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

package net.mcreator.element.converter.v2022_3.fv34;

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
import java.util.HashMap;
import java.util.Map;

public class BiomeDictionaryProcedureConverter implements IConverter {
	private static final Logger LOG = LogManager.getLogger("BiomeDictionaryProcedureConverter");

	private static final Map<String, String> conversions = new HashMap<>() {{
		put("FOREST", "is_forest");
		put("MESA", "has_structure/mineshaft_mesa");
		put("PLAINS", "has_structure/village_plains");
		put("MOUNTAIN", "is_mountain");
		put("HILLS", "is_hill");
		put("SWAMP", "has_structure/swamp_hut");
		put("SANDY", "sandy");
		put("SNOWY", "snowy");
		put("WASTELAND", "wasteland");
		put("BEACH", "is_beach");
		put("VOID", "void");
		put("HOT", "hot");
		put("COLD", "cold");
		put("SPARSE", "sparse");
		put("DENSE", "dense");
		put("WET", "wet");
		put("DRY", "dry");
		put("SAVANNA", "is_savanna");
		put("CONIFEROUS", "coniferous");
		put("JUNGLE", "is_jungle");
		put("SPOOKY", "spooky");
		put("DEAD", "dead");
		put("LUSH", "lush");
		put("NETHER", "is_nether");
		put("END", "is_end");
		put("MUSHROOM", "mushroom");
		put("MAGICAL", "magical");
		put("RARE", "rare");
		put("OCEAN", "is_ocean");
		put("RIVER", "is_river");
		put("WATER", "water");
		put("PLATEAU", "plateau");
		put("MODIFIED", "modified");
		put("OVERWORLD", "is_overworld");
	}};

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Procedure procedure = (Procedure) input;
		try {
			procedure.procedurexml = fixXML(procedure.procedurexml);
		} catch (Exception e) {
			LOG.warn("Failed to convert biome dictionary to tag for procedure " + input.getModElement().getName());
		}
		return procedure;
	}

	@Override public int getVersionConvertingTo() {
		return 34;
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
			String type = element.getAttribute("type");
			if (type != null && type.equals("world_data_biomeat_dictionary")) {
				String tagType = "minecraft:none";

				Element biomedictField = XMLUtil.getFirstChildrenWithName(element, "field");
				if (biomedictField != null) {
					element.removeChild(biomedictField); // Moved to text input tag

					if (conversions.containsKey(biomedictField.getTextContent()))
						tagType = "minecraft:" + conversions.get(biomedictField.getTextContent());
				}

				Element tagValue = bh.createValue("tag", bh.createBlock("text", bh.createField("TEXT", tagType)));

				element.setAttribute("type", "world_data_biomeat_tag");
				element.appendChild(tagValue);
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));

		return writer.getBuffer().toString();
	}
}
