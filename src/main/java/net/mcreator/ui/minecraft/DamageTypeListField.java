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

package net.mcreator.ui.minecraft;

import net.mcreator.element.parts.DamageTypeEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.dialogs.AddTagDialog;
import net.mcreator.ui.dialogs.DataListSelectorDialog;
import net.mcreator.ui.init.L10N;

import java.util.ArrayList;
import java.util.List;

public class DamageTypeListField extends JItemListField<DamageTypeEntry> {

	public DamageTypeListField(MCreator mcreator, boolean allowTags) {
		super(mcreator, false, allowTags);
	}

	@Override protected List<DamageTypeEntry> getElementsToAdd() {
		return DataListSelectorDialog.openMultiSelectorDialog(mcreator,
						w -> ElementUtil.loadDataListAndElements(w, "damagesources", true, null, "damagetype"),
						L10N.t("dialog.list_field.damage_type_list_title"),
						L10N.t("dialog.list_field.damage_type_list_message")).stream()
				.map(e -> new DamageTypeEntry(mcreator.getWorkspace(), e)).toList();
	}

	@Override protected List<DamageTypeEntry> getTagsToAdd() {
		List<DamageTypeEntry> tags = new ArrayList<>();

		String tag = AddTagDialog.openAddTagDialog(mcreator, mcreator, TagType.DAMAGE_TYPES, "is_drowning",
				"is_explosion", "is_fall", "is_fire", "is_freezing", "is_lightning", "is_projectile", "bypasses_armor",
				"bypasses_effects", "bypasses_enchantments", "bypasses_shield");
		if (tag != null)
			tags.add(new DamageTypeEntry(mcreator.getWorkspace(), "#" + tag));

		return tags;
	}

}
