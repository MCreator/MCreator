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

package net.mcreator.element.converter.fv9;

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
import java.util.HashMap;

public class ProcedureGlobalTriggerFixer implements IConverter {

	private static final Logger LOG = LogManager.getLogger("ProcedureGlobalTriggerFixer");

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Procedure procedure = (Procedure) input;

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(procedure.procedurexml)));
			doc.getDocumentElement().normalize();

			NodeList nodeList = doc.getElementsByTagName("field");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element) nodeList.item(i);
				String type = element.getAttribute("name");
				if ("trigger".equals(type)) {
					String orig = element.getTextContent();
					if ("No additional trigger".equals(orig)) {
						element.setTextContent("no_ext_trigger");
					} else {
						if (EXTERNAL_TRIGGERS_LEGACY_NAMEMAP.containsKey(orig)) {
							element.setTextContent(EXTERNAL_TRIGGERS_LEGACY_NAMEMAP.get(orig));
						}
					}
				}
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));

			procedure.procedurexml = writer.getBuffer().toString();
		} catch (Exception e) {
			LOG.warn("Failed to fix entity dependency for procedure " + input.getModElement().getName());
		}

		return procedure;
	}

	@Override public int getVersionConvertingTo() {
		return 9;
	}

	private static final HashMap<String, String> EXTERNAL_TRIGGERS_LEGACY_NAMEMAP = new HashMap<String, String>() {{
		put("A block is broken", "block_break");
		put("A block is placed", "block_place");
		put("An explosion occurs", "explosion_occurs");
		put("Bonemeal is used", "bonemeal_used");
		put("Bucket is filled", "bucket_filled");
		put("Command executed", "command_executed");
		put("Entity attacked", "entity_attacked");
		put("Entity dies", "entity_dies");
		put("Entity joins the world", "entity_joins_world");
		put("Entity picks up item", "entity_item_pickup");
		put("Entity spawns", "entity_spawns");
		put("Entity travels to a dimension", "entity_travels_to_dimension");
		put("Gem dropped", "gem_dropped");
		put("Gem expires", "gem_expired");
		put("MCreator Link: Custom message received", "mcreator_link_message_received");
		put("MCreator Link: Digital pin changed", "mcreator_link_pin_changed");
		put("MCreator Link: New device connected", "mcreator_link_device_connected");
		put("Minecraft loads a world", "minecraft_world_loaded");
		put("Minecraft unloads a world", "minecraft_world_unloaded");
		put("Mod client-side loaded", "mod_clientload");
		put("Mod loaded in the game", "mod_load");
		put("Mod server-side loaded", "mod_serverload");
		put("On player tick update", "player_ticks");
		put("On world tick update", "world_ticks");
		put("Player goes to bed", "player_in_bed");
		put("Player joins the world", "player_log_in");
		put("Player leaves the world", "player_log_out");
		put("Player left clicks block", "player_left_click_block");
		put("Player respawns", "player_respawn");
		put("Player right clicks block", "player_right_click_block");
		put("Player right clicks on entity", "player_right_click_entity");
		put("Player right clicks with item", "player_right_click_item");
		put("Player sent chat", "chat_sent");
		put("Player uses hoe", "player_use_hoe");
		put("Player wakes up", "player_wakes_up");
		put("Sapling grows", "sapling_glow");
		put("Something is crafted", "item_crafted");
		put("Something is smelted", "item_smelted");
	}};

}
