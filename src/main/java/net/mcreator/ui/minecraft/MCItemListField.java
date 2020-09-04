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

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.dialogs.MCItemSelectorDialog;

import java.util.List;
import java.util.stream.Collectors;

public class MCItemListField extends JItemListField<MItemBlock> {

	private final MCItem.ListProvider supplier;
	private final MCreator mcreator;

	public MCItemListField(MCreator mcreator, MCItem.ListProvider supplier) {
		this.supplier = supplier;
		this.mcreator = mcreator;
	}

	@Override public List<MItemBlock> getElementsToAdd() {
		return MCItemSelectorDialog.openMultiSelectorDialog(mcreator, supplier).stream()
				.map(e -> new MItemBlock(mcreator.getWorkspace(), e.getName())).collect(Collectors.toList());
	}

}
