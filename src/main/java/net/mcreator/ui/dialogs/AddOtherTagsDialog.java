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

package net.mcreator.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.workspace.elements.TagElement;

public class AddOtherTagsDialog {

	public static void open(MCreator mcreator) {
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.tools.inject_other_tags.title"), true);
		dialog.setLayout(new BorderLayout(10, 10));
		dialog.setIconImage(UIRES.get("16px.injecttags").getImage());

		dialog.add("North", PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.tools.inject_tags.text_top")));

		JPanel blockTags = new JPanel(new GridLayout(0, 1, 2, 2));
		JPanel itemTags = new JPanel(new GridLayout(0, 1, 2, 2));
		JPanel entityTags = new JPanel(new GridLayout(0, 1, 2, 2));
		JPanel biomeTags = new JPanel(new GridLayout(0, 1, 2, 2));
		JPanel structureTags = new JPanel(new GridLayout(0, 1, 2, 2));
//		JPanel functionTags = new JPanel(new GridLayout(0, 1, 2, 2));
//		JPanel damageTypeTags = new JPanel(new GridLayout(0, 1, 2, 2));
		JPanel enchantmentTags = new JPanel(new GridLayout(0, 1, 2, 2));

		JTabbedPane tabPanel = new JTabbedPane();

		tabPanel.add(L10N.t("tag.type.blocks"), makePage(blockTags));
		tabPanel.add(L10N.t("tag.type.items"), makePage(itemTags));
		tabPanel.add(L10N.t("tag.type.entities"), makePage(entityTags));
		tabPanel.add(L10N.t("tag.type.biomes"), makePage(biomeTags));
		tabPanel.add(L10N.t("tag.type.structures"), makePage(structureTags));
//		tabPanel.add(L10N.t("tag.type.functions"), makePage(functionTags));
//		tabPanel.add(L10N.t("tag.type.damage_types"), makePage(damageTypeTags));
		tabPanel.add(L10N.t("tag.type.enchantments"), makePage(enchantmentTags));

		dialog.add("Center", tabPanel);

		JButton ok = L10N.button("dialog.tools.inject_tags.confirm");
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		cancel.addActionListener(e -> dialog.setVisible(false));
		dialog.add("South", PanelUtils.join(ok, cancel));

		List<Consumer<Boolean>> callables = new ArrayList<>();

		//@formatter:off
		callables.add(addTag(mcreator, blockTags, "mineable/axe", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "mineable/hoe", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "mineable/pickaxe", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "mineable/shovel", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "air", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "all_hanging_signs", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "all_signs", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "animals_spawnable_on", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "anvil", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "banners", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "base_stone_nether", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "base_stone_overworld", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "beds", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "beehives", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "buttons", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "campfires", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "candles", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "crops", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "doors", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "dragon_transparent", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "enchantment_power_transmitter", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "enderman_holdable", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "fall_damage_resetting", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "maintains_farmland", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "moss_replaceable", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "nylium", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "occludes_vibration_signals", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "piglin_repellents", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "portals", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "rails", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "signs", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "soul_fire_base_blocks", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "sword_efficient", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "unstable_bottom_center", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "wool", "minecraft", TagType.BLOCKS, false));

		callables.add(addTag(mcreator, itemTags, "axes", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "axolotl_food", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "beacon_payment_items", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "bee_food", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "brewing_fuel", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "chicken_food", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "coals", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "creeper_drop_music_discs", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "creeper_igniters", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "dampens_vibrations", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "enchantable/armor", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "enchantable/crossbow", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "enchantable/fishing", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "enchantable/mace", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "enchantable/trident", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "fishes", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "piglin_food", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "piglin_loved", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "piglin_food", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "piglin_repellents", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "piglin_safe_armor", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "rabbit_food", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "sheep_food", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "smelts_to_glass", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "soul_fire_base_blocks", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "stone_crafting_materials", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "stone_tool_materials", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "villager_plantable_seeds", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "wolf_food", "minecraft", TagType.ITEMS, false));

		callables.add(addTag(mcreator, entityTags, "aquatic", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "arthropod", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "axolotl_always_hostiles", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "axolotl_hunt_targets", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "can_turn_in_boats", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "deflects_projectiles", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "dismounts_underwater", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "fall_damage_immune", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "freeze_hurts_extra_types", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "freeze_immune_entity_types", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "frog_food", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "ignores_poison_and_regen", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "illager_friends", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "inverted_healing_and_harm", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "redirectable_projectiles", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "sensitive_to_bane_of_arthropods", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "sensitive_to_impaling", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "sensitive_to_smite", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "wither_friends", "minecraft", TagType.ENTITIES, false));

		callables.add(addTag(mcreator, biomeTags, "has_closer_water_fog", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "increased_fire_burnout", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "is_badlands", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "is_beach", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "is_deep_ocean", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "is_jungle", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "is_mountain", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "is_taiga", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "mineshaft_blocking", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "more_frequent_drowned_spawns", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "produces_corals_from_bonemeal", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "reduce_water_ambient_spawns", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "snow_golem_melts", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "stronghold_biased_to", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "water_on_map_outlines", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "water_on_map_outlines", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "without_zombie_sieges", "minecraft", TagType.BIOMES, false));

		callables.add(addTag(mcreator, structureTags, "cats_spawn_in", "minecraft", TagType.STRUCTURES, false));
		callables.add(addTag(mcreator, structureTags, "dolphin_located", "minecraft", TagType.STRUCTURES, false));
		callables.add(addTag(mcreator, structureTags, "eye_of_ender_located", "minecraft", TagType.STRUCTURES, false));
		callables.add(addTag(mcreator, structureTags, "mineshaft", "minecraft", TagType.STRUCTURES, false));
		callables.add(addTag(mcreator, structureTags, "ocean_ruin", "minecraft", TagType.STRUCTURES, false));
		callables.add(addTag(mcreator, structureTags, "on_treasure_maps", "minecraft", TagType.STRUCTURES, false));
		callables.add(addTag(mcreator, structureTags, "ruined_portal", "minecraft", TagType.STRUCTURES, false));
		callables.add(addTag(mcreator, structureTags, "shipwreck", "minecraft", TagType.STRUCTURES, false));
		callables.add(addTag(mcreator, structureTags, "village", "minecraft", TagType.STRUCTURES, false));

		callables.add(addTag(mcreator, enchantmentTags, "exclusive_set/armor", "minecraft", TagType.ENCHANTMENTS, false));
		callables.add(addTag(mcreator, enchantmentTags, "exclusive_set/boots", "minecraft", TagType.ENCHANTMENTS, false));
		callables.add(addTag(mcreator, enchantmentTags, "exclusive_set/bow", "minecraft", TagType.ENCHANTMENTS, false));
		callables.add(addTag(mcreator, enchantmentTags, "exclusive_set/crossbow", "minecraft", TagType.ENCHANTMENTS, false));
		callables.add(addTag(mcreator, enchantmentTags, "exclusive_set/damage", "minecraft", TagType.ENCHANTMENTS, false));
		callables.add(addTag(mcreator, enchantmentTags, "exclusive_set/mining", "minecraft", TagType.ENCHANTMENTS, false));
		callables.add(addTag(mcreator, enchantmentTags, "exclusive_set/riptide", "minecraft", TagType.ENCHANTMENTS, false));
		callables.add(addTag(mcreator, enchantmentTags, "double_trade_price", "minecraft", TagType.ENCHANTMENTS, false));
		callables.add(addTag(mcreator, enchantmentTags, "in_enchanting_table", "minecraft", TagType.ENCHANTMENTS, false));
		callables.add(addTag(mcreator, enchantmentTags, "on_mob_spawn_equipment", "minecraft", TagType.ENCHANTMENTS, false));
		callables.add(addTag(mcreator, enchantmentTags, "on_random_loot", "minecraft", TagType.ENCHANTMENTS, false));
		callables.add(addTag(mcreator, enchantmentTags, "on_traded_equipment", "minecraft", TagType.ENCHANTMENTS, false));
		callables.add(addTag(mcreator, enchantmentTags, "prevents_ice_melting", "minecraft", TagType.ENCHANTMENTS, false));

		//@formatter:on

		ok.addActionListener(e -> {
			dialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			callables.forEach(c -> c.accept(false));
			mcreator.reloadWorkspaceTabContents();
			dialog.setCursor(Cursor.getDefaultCursor());
			dialog.setVisible(false);
		});

		dialog.getRootPane().setDefaultButton(ok);
		dialog.setSize(740, 420);
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);
	}

	private static JScrollPane makePage(JPanel panel) {
		JScrollPane page = new JScrollPane(PanelUtils.pullElementUp(panel));
		page.getVerticalScrollBar().setUnitIncrement(10);
		return page;
	}

	private static Consumer<Boolean> addTag(MCreator mcreator, JPanel panel, String name, @SuppressWarnings("SameParameterValue") String namespace,
			TagType type, boolean checked) {
		TagElement tagElement = new TagElement(type, namespace + ":" + name);

		boolean existing = mcreator.getWorkspace().getTagElements().containsKey(tagElement);

		JCheckBox box = new JCheckBox("<html><kbd>" + namespace + ":" + name + "</kbd><small><br>" + L10N.t(
				"dialog.tools.inject_tags.tag." + type.name().toLowerCase(Locale.ENGLISH) + "." + namespace + "."
						+ name.replace("/", ".")));
		box.setSelected(checked);

		JPanel wrap = new JPanel(new GridLayout());
		wrap.add(box);
		wrap.setBorder(BorderFactory.createMatteBorder(0, 8, 0, 0, type.getColor()));
		panel.add(wrap);

		if (existing)
			box.setEnabled(false);

		return altcondition -> {
			if (box.isSelected() || altcondition) {
				if (!mcreator.getWorkspace().getTagElements().containsKey(tagElement)) {
					mcreator.getWorkspace().addTagElement(tagElement);
				}
			}
		};
	}

}
