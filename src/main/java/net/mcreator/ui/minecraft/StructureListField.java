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

import net.mcreator.element.parts.StructureEntry;
import net.mcreator.element.parts.StructureEntry;
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

public class StructureListField extends JItemListField<StructureEntry> {

	public StructureListField(MCreator mcreator) {
		super(mcreator, false);
	}

	public StructureListField(MCreator mcreator, boolean allowTags) {
		this(mcreator);
		if (allowTags)
			allowTags();
	}

	@Override protected List<StructureEntry> getElementsToAdd() {
		return DataListSelectorDialog.openMultiSelectorDialog(mcreator, ElementUtil::loadAllStructures,
						L10N.t("dialog.list_field.structure_list_title"), L10N.t("dialog.list_field.structure_list_message")).stream()
				.map(e -> new StructureEntry(mcreator.getWorkspace(), e)).toList();
	}

	@Override protected List<StructureEntry> getTagsToAdd() {
		List<StructureEntry> tags = new ArrayList<>();

		String tag = AddTagDialog.openAddTagDialog(mcreator, mcreator, TagType.STRUCTURES);
		if (tag != null)
			tags.add(new StructureEntry(mcreator.getWorkspace(), "#" + tag));

		return tags;
	}

	@Nullable @Override protected StructureEntry fromExternalToElement(String external) {
		return new StructureEntry(mcreator.getWorkspace(), NameMapper.EXTERNAL_PREFIX + external);
	}

}
