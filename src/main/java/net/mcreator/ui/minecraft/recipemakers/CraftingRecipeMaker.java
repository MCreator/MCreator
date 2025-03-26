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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CraftingRecipeMaker extends AbstractRecipeMaker {

	public final JSpinner sp;
	public final MCItemHolder[] recipeSlots = new MCItemHolder[9];
	public final MCItemHolder outputItem;

	private final JLabel shapeless = new JLabel(UIRES.get("recipe.shapeless"));

	private final JLabel drop = new JLabel("1");

	private MItemBlock lastItemBlock = null;

	public CraftingRecipeMaker(MCreator mcreator, MCItem.ListProvider itemsWithTags, MCItem.ListProvider items) {
		super(UIRES.get("recipe.crafting").getImage());

		JLabel cb = new JLabel();
		cb.setBackground(new Color(139, 139, 139));
		cb.setHorizontalAlignment(SwingConstants.CENTER);

		MouseAdapter cloneAdapter = new MouseAdapter() {
			private static final int buttonsDownMask =
					MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;

			@Override public void mouseEntered(MouseEvent e) {
				if (e.getSource() instanceof MCItemHolder mcItemHolder) {
					if ((e.getModifiersEx() & buttonsDownMask) != 0 && lastItemBlock != null) {
						mcItemHolder.setBlock(lastItemBlock);
					}
				}
			}

			@Override public void mousePressed(MouseEvent e) {
				if (e.getSource() instanceof MCItemHolder mcItemHolder) {
					lastItemBlock = mcItemHolder.getBlock();
				}
			}
		};

		for (int i = 0; i < 9; i++) {
			recipeSlots[i] = new MCItemHolder(mcreator, itemsWithTags, true);
			recipeSlots[i].addMouseListener(cloneAdapter);
			recipeSlots[i].setMargin(new Insets(0, 0, 0, 0));
			recipeSlots[i].setBounds(51 + 31 * (i % 3), 29 + 31 * (i / 3), 28, 28);
		}

		outputItem = new MCItemHolder(mcreator, items);
		outputItem.setMargin(new Insets(0, 0, 0, 0));
		outputItem.setBounds(212, 60, 28, 28);

		cb.setBounds(205, 53, 40, 40);

		sp = new JSpinner(new SpinnerNumberModel(1, 1, 64, 1));
		sp.setBounds(210, 109, 42, 17);
		imagePanel.add(sp);

		drop.setBounds(212, 109, 38, 17);
		drop.setVisible(false);
		drop.setForeground(Color.white);
		imagePanel.add(ComponentUtils.deriveFont(drop, 16));

		for (int i = 0; i < 9; i++) {
			imagePanel.add(recipeSlots[i]);
		}
		imagePanel.add(outputItem);

		shapeless.setVisible(false);
		shapeless.setBounds(156, 97, 23, 19);

		imagePanel.add(shapeless);
		imagePanel.add(cb);

		setPreferredSize(new Dimension(300, 145));
	}

	public void setShapeless(boolean shapeless) {
		this.shapeless.setVisible(shapeless);
	}

	public boolean hasInputItems() {
		for (int i = 0; i < 9; i++) {
			if (recipeSlots[i].containsItem())
				return true;
		}
		return false;
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (int i = 0; i < 9; i++) {
			recipeSlots[i].setEnabled(enabled);
		}
		outputItem.setEnabled(enabled);
		sp.setEnabled(enabled);
		shapeless.setEnabled(enabled);
	}

	@Override protected void setupImageExport(boolean exportedYet) {
		for (int i = 0; i < 9; i++) {
			recipeSlots[i].setValidationShownFlag(exportedYet);
		}
		outputItem.setValidationShownFlag(exportedYet);
		sp.setVisible(exportedYet);
		drop.setText(sp.getValue().toString());
		drop.setVisible(!exportedYet);
	}
}
