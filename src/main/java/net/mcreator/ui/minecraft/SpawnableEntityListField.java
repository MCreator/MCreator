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

import net.mcreator.element.parts.EntityEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.dialogs.AddTagDialog;
import net.mcreator.ui.dialogs.DataListSelectorDialog;
import net.mcreator.ui.init.L10N;

import java.util.ArrayList;
import java.util.List;

public class SpawnableEntityListField extends JItemListField<EntityEntry> {

	public SpawnableEntityListField(MCreator mcreator) {
		super(mcreator, false);
	}

	public SpawnableEntityListField(MCreator mcreator, boolean allowTags) {
		super(mcreator, false, allowTags);
	}

	@Override protected List<EntityEntry> getElementsToAdd() {
		return DataListSelectorDialog.openMultiSelectorDialog(mcreator, ElementUtil::loadAllSpawnableEntities,
						L10N.t("dialog.list_field.entity_title"), L10N.t("dialog.list_field.entity_message")).stream()
				.map(e -> new EntityEntry(mcreator.getWorkspace(), e)).toList();
	}

	@Override protected List<EntityEntry> getTagsToAdd() {
		List<EntityEntry> tags = new ArrayList<>();

		String tag = AddTagDialog.openAddTagDialog(mcreator, mcreator, TagType.ENTITIES, "bosses", "raiders",
				"fall_damage_immune", "powder_snow_walkable_mobs");
		if (tag != null)
			tags.add(new EntityEntry(mcreator.getWorkspace(), "#" + tag));

		return tags;
	}

}
