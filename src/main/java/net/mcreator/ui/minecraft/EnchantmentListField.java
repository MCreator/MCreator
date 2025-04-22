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

package net.mcreator.ui.minecraft;

import net.mcreator.element.parts.Enchantment;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.dialogs.AddTagDialog;
import net.mcreator.ui.dialogs.DataListSelectorDialog;
import net.mcreator.ui.init.L10N;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EnchantmentListField extends JItemListField<Enchantment> {

	public EnchantmentListField(MCreator mcreator) {
		super(mcreator, false);
	}

	public EnchantmentListField(MCreator mcreator, boolean allowTags) {
		super(mcreator, false);
		if (allowTags)
			allowTags();
	}

	@Override protected List<Enchantment> getElementsToAdd() {
		return DataListSelectorDialog.openMultiSelectorDialog(mcreator, ElementUtil::loadAllEnchantments,
						L10N.t("dialog.list_field.enchantment_title"), L10N.t("dialog.list_field.enchantment_message")).stream()
				.map(e -> new Enchantment(mcreator.getWorkspace(), e)).toList();
	}

	@Override protected List<Enchantment> getTagsToAdd() {
		List<Enchantment> tags = new ArrayList<>();

		String tag = AddTagDialog.openAddTagDialog(mcreator, mcreator, TagType.ENCHANTMENTS, "treasure", "non_treasure",
				"curse", "tradeable", "in_enchanting_table", "on_random_loot", "exclusive_set/mining",
				"exclusive_set/damage", "exclusive_set/armor");
		if (tag != null)
			tags.add(new Enchantment(mcreator.getWorkspace(), "#" + tag));

		return tags;
	}

	@Nullable @Override protected Enchantment fromExternalToElement(String external) {
		return new Enchantment(mcreator.getWorkspace(), NameMapper.EXTERNAL_PREFIX + external);
	}

}
