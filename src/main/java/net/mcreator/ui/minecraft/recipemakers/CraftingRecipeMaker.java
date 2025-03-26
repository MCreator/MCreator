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
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.ImagePanel;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.MCItemHolder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class CraftingRecipeMaker extends JPanel {

	public final JSpinner sp;
	public final MCItemHolder[] recipeSlots = new MCItemHolder[9];
	public final MCItemHolder outputItem;

	private final JLabel shapeless = new JLabel(UIRES.get("recipe.shapeless"));

	private final JButton export = new JButton(UIRES.get("18px.export"));

	private MItemBlock lastItemBlock = null;

	public CraftingRecipeMaker(MCreator mcreator, MCItem.ListProvider itemsWithTags, MCItem.ListProvider items) {
		ImagePanel ip = new ImagePanel(UIRES.get("recipe.crafting").getImage());
		ip.fitToImage();
		ip.setLayout(null);

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
		ip.add(sp);

		JLabel drop = new JLabel("1");

		export.setContentAreaFilled(false);
		export.setMargin(new Insets(0, 0, 0, 0));
		export.setBounds(260, 13, 24, 24);
		export.setFocusPainted(false);
		export.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ip.add(export);
		export.addActionListener(event -> {
			export.setVisible(false);
			for (int i = 0; i < 9; i++) {
				recipeSlots[i].setValidationShownFlag(false);
			}
			outputItem.setValidationShownFlag(false);
			sp.setVisible(false);
			drop.setText(sp.getValue().toString());
			drop.setVisible(true);
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			BufferedImage im = new BufferedImage(ip.getWidth(), ip.getHeight(), BufferedImage.TYPE_INT_ARGB);
			ip.paint(im.getGraphics());
			File fl = FileDialogs.getSaveDialog(null, new String[] { ".png" });
			if (fl != null)
				FileIO.writeImageToPNGFile(im, fl);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			export.setVisible(true);
			drop.setVisible(false);
			for (int i = 0; i < 9; i++) {
				recipeSlots[i].setValidationShownFlag(true);
			}
			outputItem.setValidationShownFlag(true);
			sp.setVisible(true);

		});

		drop.setBounds(212, 109, 38, 17);
		drop.setVisible(false);
		drop.setForeground(Color.white);
		ip.add(ComponentUtils.deriveFont(drop, 16));

		for (int i = 0; i < 9; i++) {
			ip.add(recipeSlots[i]);
		}

		ip.add(outputItem);

		shapeless.setVisible(false);
		shapeless.setBounds(156, 97, 23, 19);

		ip.add(shapeless);
		ip.add(cb);

		add(ip);
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
		export.setEnabled(enabled);
	}

}
