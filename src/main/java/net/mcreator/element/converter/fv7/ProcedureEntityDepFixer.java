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

package net.mcreator.element.converter.fv7;

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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProcedureEntityDepFixer implements IConverter {

	private static final Logger LOG = LogManager.getLogger("ProcedureEntityDepFixer");

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
		return 7;
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
			if (type != null && entity_tofix_types.contains(type)) {
				Element value = doc.createElement("value");
				value.setAttribute("name", "entity");
				Element deps_block = doc.createElement("block");
				deps_block.setAttribute("type", "entity_from_deps");
				value.appendChild(deps_block);
				element.appendChild(value);
			}
			if (type != null && item_tofix_types.contains(type)) {
				Element value = doc.createElement("value");
				value.setAttribute("name", "item");
				Element deps_block = doc.createElement("block");
				deps_block.setAttribute("type", "itemstack_to_mcitem");
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

	private final Set<String> item_tofix_types = new HashSet<>(
			Arrays.asList("item_add_enhancement", "item_cooldown_for", "item_damage", "item_get_damage",
					"item_get_max_damage", "item_set_damage", "item_set_display_name"));

	private final Set<String> entity_tofix_types = new HashSet<>(
			Arrays.asList("block_simulate_right_click", "deal_damage", "entity_add_achievement", "entity_add_item",
					"entity_add_potion", "entity_add_potion_advanced", "entity_add_xp", "entity_add_xp_level",
					"entity_allow_building", "entity_allow_flying", "entity_armor_value", "entity_canusecommand",
					"entity_clear_inventory", "entity_clearpotions", "entity_close_gui", "entity_despawn",
					"entity_dimension_id", "entity_direction", "entity_direction_value", "entity_disable_damage",
					"entity_execute_command", "entity_extinguish", "entity_foodlevel", "entity_get_armor_slot_item",
					"entity_get_scoreboard_score", "entity_has_achievement", "entity_has_item_inventory",
					"entity_haspotioneffect", "entity_health", "entity_isbeingridden", "entity_isburning",
					"entity_iscreative", "entity_isriding", "entity_issneaking", "entity_issprinting",
					"entity_iteminhand", "entity_iteminoffhand", "entity_lookpos_x", "entity_lookpos_y",
					"entity_lookpos_z", "entity_name", "entity_nbt_logic_get", "entity_nbt_logic_set",
					"entity_nbt_num_get", "entity_nbt_num_set", "entity_nbt_text_get", "entity_nbt_text_set",
					"entity_override_fall", "entity_pitch", "entity_pos_x", "entity_pos_y", "entity_pos_z",
					"entity_remove_item", "entity_remove_xp_level", "entity_run_function", "entity_send_chat",
					"entity_set_armor_slot_item", "entity_set_display_name", "entity_set_fire", "entity_set_flying",
					"entity_set_foodlevel", "entity_set_gamemode", "entity_set_health", "entity_set_mainhand_item",
					"entity_set_movement", "entity_set_offhand_item", "entity_set_rotation",
					"entity_set_scoreboard_score", "entity_set_spawn", "entity_setinweb", "entity_swing_mainhand",
					"entity_swing_offhand", "entity_switch_dimension", "entity_vel_x", "entity_vel_y", "entity_vel_z",
					"entity_xplevel", "gui_clear_slot", "gui_damage_item", "gui_get_amount_inslot",
					"gui_get_item_inslot", "gui_remove_items", "gui_set_items", "item_cooldown_for", "move_entity",
					"shoot_arrow"));

}