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

import net.mcreator.element.parts.TabEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.dialogs.DataListSelectorDialog;
import net.mcreator.ui.init.L10N;

import java.util.List;

public class TabListField extends JItemListField<TabEntry> {

	public TabListField(MCreator mcreator) {
		super(mcreator);
	}

	@Override protected List<TabEntry> getElementsToAdd() {
		return DataListSelectorDialog.openMultiSelectorDialog(mcreator, ElementUtil::loadAllTabs,
						L10N.t("dialog.list_field.tab_title"), L10N.t("dialog.list_field.tab_message")).stream()
				.map(e -> new TabEntry(mcreator.getWorkspace(), e)).toList();
	}
}
