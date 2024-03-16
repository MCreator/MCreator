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

import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.workspace.elements.TagElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class AddCommonTagsDialog {

	public static void open(MCreator mcreator) {
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.tools.inject_tags.title"), true);
		dialog.setLayout(new BorderLayout(10, 10));
		dialog.setIconImage(UIRES.get("16px.injecttags").getImage());

		dialog.add("North", PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.tools.inject_tags.text_top")));

		JPanel blockTags = new JPanel(new GridLayout(0, 1, 2, 2));
		JPanel itemTags = new JPanel(new GridLayout(0, 1, 2, 2));
		JPanel entityTags = new JPanel(new GridLayout(0, 1, 2, 2));
		JPanel biomeTags = new JPanel(new GridLayout(0, 1, 2, 2));
		JPanel functionTags = new JPanel(new GridLayout(0, 1, 2, 2));
		JPanel damageTypeTags = new JPanel(new GridLayout(0, 1, 2, 2));

		JTabbedPane tabPanel = new JTabbedPane();

		tabPanel.add(L10N.t("tag.type.blocks"), makePage(blockTags));
		tabPanel.add(L10N.t("tag.type.items"), makePage(itemTags));
		tabPanel.add(L10N.t("tag.type.entities"), makePage(entityTags));
		tabPanel.add(L10N.t("tag.type.biomes"), makePage(biomeTags));
		tabPanel.add(L10N.t("tag.type.functions"), makePage(functionTags));
		tabPanel.add(L10N.t("tag.type.damage_types"), makePage(damageTypeTags));

		dialog.add("Center", tabPanel);

		JButton ok = L10N.button("dialog.tools.inject_tags.confirm");
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		cancel.addActionListener(e -> dialog.setVisible(false));
		dialog.add("South", PanelUtils.join(ok, cancel));

		List<Consumer<Boolean>> callables = new ArrayList<>();

		callables.add(addTag(mcreator, blockTags, "dirt", "minecraft", TagType.BLOCKS, true));
		callables.add(addTag(mcreator, blockTags, "logs", "minecraft", TagType.BLOCKS, true));
		callables.add(addTag(mcreator, blockTags, "fences", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "wooden_fences", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "walls", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "small_flowers", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "tall_flowers", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "bee_growables", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "valid_spawn", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "impermeable", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "beacon_base_blocks", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "leaves", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "climbable", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "fire", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "dragon_immune", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "wither_immune", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "animals_spawnable_on", "minecraft", TagType.BLOCKS, false));
		callables.add(addTag(mcreator, blockTags, "prevent_mob_spawning_inside", "minecraft", TagType.BLOCKS, false));

		callables.add(addTag(mcreator, itemTags, "arrows", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "planks", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "flowers", "minecraft", TagType.ITEMS, false));
		callables.add(addTag(mcreator, itemTags, "small_flowers", "minecraft", TagType.ITEMS, false));

		callables.add(addTag(mcreator, entityTags, "arrows", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "impact_projectiles", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "beehive_inhabitors", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "raiders", "minecraft", TagType.ENTITIES, false));
		callables.add(addTag(mcreator, entityTags, "skeletons", "minecraft", TagType.ENTITIES, false));

		callables.add(addTag(mcreator, biomeTags, "is_overworld", "minecraft", TagType.BIOMES, true));
		callables.add(addTag(mcreator, biomeTags, "is_nether", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "is_end", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "is_ocean", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "is_mountain", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "is_river", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "is_hill", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "is_forest", "minecraft", TagType.BIOMES, false));
		callables.add(addTag(mcreator, biomeTags, "is_savanna", "minecraft", TagType.BIOMES, false));

		callables.add(addTag(mcreator, functionTags, "tick", "minecraft", TagType.FUNCTIONS, false));
		callables.add(addTag(mcreator, functionTags, "load", "minecraft", TagType.FUNCTIONS, false));

		callables.add(addTag(mcreator, damageTypeTags, "is_drowning", "minecraft", TagType.DAMAGE_TYPES, false));
		callables.add(addTag(mcreator, damageTypeTags, "is_explosion", "minecraft", TagType.DAMAGE_TYPES, false));
		callables.add(addTag(mcreator, damageTypeTags, "is_fall", "minecraft", TagType.DAMAGE_TYPES, false));
		callables.add(addTag(mcreator, damageTypeTags, "is_fire", "minecraft", TagType.DAMAGE_TYPES, false));
		callables.add(addTag(mcreator, damageTypeTags, "is_freezing", "minecraft", TagType.DAMAGE_TYPES, false));
		callables.add(addTag(mcreator, damageTypeTags, "is_projectile", "minecraft", TagType.DAMAGE_TYPES, false));
		callables.add(addTag(mcreator, damageTypeTags, "bypasses_armor", "minecraft", TagType.DAMAGE_TYPES, false));
		callables.add(addTag(mcreator, damageTypeTags, "bypasses_cooldown", "minecraft", TagType.DAMAGE_TYPES, false));
		callables.add(addTag(mcreator, damageTypeTags, "bypasses_effects", "minecraft", TagType.DAMAGE_TYPES, false));
		callables.add(
				addTag(mcreator, damageTypeTags, "bypasses_enchantments", "minecraft", TagType.DAMAGE_TYPES, false));
		callables.add(addTag(mcreator, damageTypeTags, "bypasses_shield", "minecraft", TagType.DAMAGE_TYPES, false));

		ok.addActionListener(e -> {
			dialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			callables.forEach(c -> c.accept(false));
			mcreator.mv.reloadElementsInCurrentTab();
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

	private static Consumer<Boolean> addTag(MCreator mcreator, JPanel panel, String name, String namespace,
			TagType type, boolean checked) {
		TagElement tagElement = new TagElement(type, namespace + ":" + name);

		boolean existing = mcreator.getWorkspace().getTagElements().containsKey(tagElement);

		JCheckBox box = new JCheckBox("<html><kbd>" + namespace + ":" + name + "</kbd><small><br>" + L10N.t(
				"dialog.tools.inject_tags.tag." + type.name().toLowerCase(Locale.ENGLISH) + "." + namespace + "."
						+ name));
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
