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

import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.MCItemHolder;

import java.awt.*;

public class BrewingRecipeMaker extends AbstractRecipeMaker {

	public final MCItemHolder cb1;
	public final MCItemHolder cb2;
	public final MCItemHolder cb3;

	public BrewingRecipeMaker(MCreator mcreator, MCItem.ListProvider itemsWithTagsAndPotions,
			MCItem.ListProvider itemsWithTags, MCItem.ListProvider itemsWithPotions) {
		super(UIRES.get("recipe.brewing").getImage());

		cb1 = new MCItemHolder(mcreator, itemsWithTagsAndPotions, true, true);
		cb2 = new MCItemHolder(mcreator, itemsWithTags, true);
		cb3 = new MCItemHolder(mcreator, itemsWithPotions, false, true);

		cb1.setBounds(65, 88, 28, 28);
		cb2.setBounds(65, 26, 28, 28);
		cb3.setBounds(210, 50, 41, 41);

		imagePanel.add(cb1);
		imagePanel.add(cb2);
		imagePanel.add(cb3);

		setPreferredSize(new Dimension(306, 145));
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		cb1.setEnabled(enabled);
		cb2.setEnabled(enabled);
		cb3.setEnabled(enabled);
	}

	@Override protected void setupImageExport(boolean exportedYet) {
		cb1.setValidationShownFlag(exportedYet);
		cb2.setValidationShownFlag(exportedYet);
		cb3.setValidationShownFlag(exportedYet);
	}

}