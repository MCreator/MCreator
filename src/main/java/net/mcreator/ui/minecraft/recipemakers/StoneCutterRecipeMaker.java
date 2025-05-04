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
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.MCItemHolder;

import javax.swing.*;
import java.awt.*;

public class StoneCutterRecipeMaker extends AbstractRecipeMaker {

	public final JSpinner sp;
	public final MCItemHolder cb1;
	public final MCItemHolder cb2;
	public final JLabel drop = new JLabel("1");

	public StoneCutterRecipeMaker(MCreator mcreator, MCItem.ListProvider itemsWithTags, MCItem.ListProvider items) {
		super(UIRES.get("recipe.stonecutter").getImage());

		cb1 = new MCItemHolder(mcreator, itemsWithTags, true);
		cb2 = new MCItemHolder(mcreator, items);

		cb1.setBounds(97, 61, 28, 28);
		cb2.setBounds(200, 61, 28, 28);

		imagePanel.add(cb1);
		imagePanel.add(cb2);

		sp = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
		sp.setBounds(196, 108, 53, 22);
		imagePanel.add(sp);

		drop.setBounds(203, 109, 38, 17);
		drop.setVisible(false);
		drop.setForeground(Color.white);
		imagePanel.add(ComponentUtils.deriveFont(drop, 16));

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
		sp.setEnabled(enabled);
	}

	@Override protected void setupImageExport(boolean exportedYet) {
		cb1.setValidationShownFlag(exportedYet);
		cb2.setValidationShownFlag(exportedYet);
		sp.setVisible(exportedYet);
		drop.setText(sp.getValue().toString());
		drop.setVisible(!exportedYet);
	}
}
