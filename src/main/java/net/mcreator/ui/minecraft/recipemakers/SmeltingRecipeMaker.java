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

package net.mcreator.ui.minecraft.recipemakers;

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.MCItemHolder;

import java.awt.*;

public class SmeltingRecipeMaker extends AbstractRecipeMaker {

	public final MCItemHolder cb1;
	public final MCItemHolder cb2;

	public SmeltingRecipeMaker(MCreator mcreator, MCItem.ListProvider itemsWithTags, MCItem.ListProvider items) {
		super(UIRES.get("recipe.furnace").getImage());

		cb1 = new MCItemHolder(mcreator, itemsWithTags, true);
		cb2 = new MCItemHolder(mcreator, items);

		cb1.setBounds(97, 30, 28, 28);
		cb2.setBounds(200, 61, 28, 28);

		imagePanel.add(cb1);
		imagePanel.add(cb2);

		setPreferredSize(new Dimension(306, 145));
	}

	public MItemBlock getBlock() {
		return cb1.getBlock();
	}

	public MItemBlock getBlock2() {
		return cb2.getBlock();
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		cb1.setEnabled(enabled);
		cb2.setEnabled(enabled);
	}

	@Override protected void setupImageExport(boolean exportedYet) {
		cb1.setValidationShownFlag(exportedYet);
		cb2.setValidationShownFlag(exportedYet);
	}
}
