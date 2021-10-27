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

import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.dialogs.StringSelectorDialog;
import net.mcreator.ui.init.L10N;

import java.util.List;

public class DimensionListField extends JItemListField<String> {

	public DimensionListField(MCreator mcreator) {
		super(mcreator);
	}

	@Override protected List<String> getElementsToAdd() {
		return StringSelectorDialog.openMultiSelectorDialog(mcreator, ElementUtil::loadAllDimensions,
				L10N.t("dialog.list_field.dimension_title"), L10N.t("dialog.list_field.dimension_message"));
	}
}
