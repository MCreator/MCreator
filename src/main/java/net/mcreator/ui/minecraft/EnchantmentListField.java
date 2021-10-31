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
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.dialogs.DataListSelectorDialog;
import net.mcreator.ui.init.L10N;

import java.util.List;

public class EnchantmentListField extends JItemListField<Enchantment> {

	public EnchantmentListField(MCreator mcreator) {
		super(mcreator);
	}

	@Override protected List<Enchantment> getElementsToAdd() {
		return DataListSelectorDialog.openMultiSelectorDialog(mcreator, ElementUtil::loadAllEnchantments,
						L10N.t("dialog.list_field.enchantment_title"), L10N.t("dialog.list_field.enchantment_message")).stream()
				.map(e -> new Enchantment(mcreator.getWorkspace(), e)).toList();
	}

}
