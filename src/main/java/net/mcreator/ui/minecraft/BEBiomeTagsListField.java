/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

import net.mcreator.element.ModElementType;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.dialogs.StringSelectorDialog;
import net.mcreator.ui.init.L10N;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BEBiomeTagsListField extends JItemListField<String> {

	private final boolean includeAddonTags;

	public BEBiomeTagsListField(MCreator mcreator, boolean includeAddonTags) {
		super(mcreator);
		this.includeAddonTags = includeAddonTags;
	}

	@Override protected List<String> getElementsToAdd() {
		List<String> biomeTags = includeAddonTags ?
				mcreator.getWorkspace().getModElements().stream().filter(me -> me.getType() == ModElementType.BEBIOME)
						.map(me -> mcreator.getWorkspace().getWorkspaceSettings().getModID() + ":"
								+ me.getRegistryName()).collect(Collectors.toList()) :
				new ArrayList<>();
		biomeTags.addAll(Arrays.stream(ElementUtil.getDataListAsStringArray("be_biometags")).toList());
		return StringSelectorDialog.openMultiSelectorDialog(mcreator, w -> biomeTags.toArray(new String[0]),
				L10N.t("dialog.list_field.bebiome_tags_list_title"),
				L10N.t("dialog.list_field.bebiome_tags_list_message"));
	}
}
