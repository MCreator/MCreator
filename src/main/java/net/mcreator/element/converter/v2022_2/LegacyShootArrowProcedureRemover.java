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

package net.mcreator.element.converter.v2022_2;

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

public class LegacyShootArrowProcedureRemover implements IConverter {
	private static final Logger LOG = LogManager.getLogger("LegacyShootArrowProcedureRemover");

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Procedure procedure = (Procedure) input;
		try {
			procedure.procedurexml = fixXML(procedure.procedurexml);
		} catch (Exception e) {
			LOG.warn("Failed to remove legacy shoot arrow blocks for procedure " + input.getModElement().getName());
		}
		return procedure;
	}

	@Override public int getVersionConvertingTo() {
		return 33;
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
			if (type != null && type.equals("shoot_arrow")) {
				Element projectileField = XMLUtil.getFirstChildrenWithName(element, "field");
				if (projectileField != null) {
					element.removeChild(projectileField); // This is moved to the arrow block
					projectileField.setAttribute("name", "projectile");
				} else { // projectile filed is null, really old procedure blocks did not have arrow field
					projectileField = bh.createField("projectile", "Arrow");
					projectileField.setAttribute("name", "projectile");
				}

				Element shooterValue = null, damageValue = null, knockbackValue = null;
				for (Element value : XMLUtil.getChildrenWithName(element, "value")) {
					switch (value.getAttribute("name")) {
					// This value is copied to the arrow block
					case "entity" -> {
						shooterValue = (Element) value.cloneNode(true);
						shooterValue.setAttribute("name", "shooter");
					}
					// These values are moved to the arrow block
					case "damage" -> {
						damageValue = value;
						element.removeChild(value);
					}
					case "knockback" -> {
						knockbackValue = value;
						element.removeChild(value);
					}
					}
				}

				Element fireField = bh.createField("fire", "FALSE");
				Element particlesField = bh.createField("particles", "FALSE");
				Element pickupField = bh.createField("pickup", "DISALLOWED");
				Element piercingValue = bh.createValue("piercing",
						bh.createBlock("math_number", bh.createField("NUM", "0")));

				Element arrowProjectileBlock = bh.createBlock("projectiles_arrow", projectileField, fireField,
						particlesField, pickupField, damageValue, knockbackValue, piercingValue, shooterValue);

				Element inaccuracyValue = bh.createValue("inaccuracy",
						bh.createBlock("math_number", bh.createField("NUM", "0")));

				element.setAttribute("type", "projectile_shoot_from_entity");
				element.appendChild(bh.createValue("projectile", arrowProjectileBlock));
				element.appendChild(inaccuracyValue);
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));

		return writer.getBuffer().toString();
	}
}
