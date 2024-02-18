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

import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.dialogs.AddTagDialog;
import net.mcreator.ui.dialogs.DataListSelectorDialog;
import net.mcreator.ui.init.L10N;

import java.util.ArrayList;
import java.util.List;

public class BiomeListField extends JItemListField<BiomeEntry> {

	public BiomeListField(MCreator mcreator) {
		super(mcreator, false);
	}

	public BiomeListField(MCreator mcreator, boolean allowTags) {
		super(mcreator, false, allowTags);
	}

	@Override protected List<BiomeEntry> getElementsToAdd() {
		return DataListSelectorDialog.openMultiSelectorDialog(mcreator, ElementUtil::loadAllBiomes,
						L10N.t("dialog.list_field.biome_list_title"), L10N.t("dialog.list_field.biome_list_message")).stream()
				.map(e -> new BiomeEntry(mcreator.getWorkspace(), e)).toList();
	}

	@Override protected List<BiomeEntry> getTagsToAdd() {
		List<BiomeEntry> tags = new ArrayList<>();

		String tag = AddTagDialog.openAddTagDialog(mcreator, mcreator, TagType.BIOMES, "is_overworld", "is_nether",
				"is_end", "is_badlands", "is_beach", "is_deep_ocean", "is_forest", "is_hill", "is_jungle",
				"is_mountain", "is_ocean", "is_river", "is_savanna", "is_taiga");
		if (tag != null)
			tags.add(new BiomeEntry(mcreator.getWorkspace(), "#" + tag));

		return tags;
	}

}
