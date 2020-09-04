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

package net.mcreator.ui.datapack.recipe;

import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.ImagePanel;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.MCItemHolder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CraftingReciepeMaker extends JPanel {

	public JSpinner sp;
	public MCItemHolder cb1;
	public MCItemHolder cb2;
	public MCItemHolder cb3;
	public MCItemHolder cb4;
	public MCItemHolder cb5;
	public MCItemHolder cb6;
	public MCItemHolder cb7;
	public MCItemHolder cb8;
	public MCItemHolder cb9;
	public MCItemHolder cb10;

	private final JLabel shapeless = new JLabel(UIRES.get("recipe.shapeless"));

	public CraftingReciepeMaker(MCreator mcreator, MCItem.ListProvider itemsWithTags, MCItem.ListProvider items) {
		ImagePanel ip = new ImagePanel(UIRES.get("recipe.crafting").getImage());
		ip.fitToImage();
		ip.setLayout(null);

		JLabel cb = new JLabel();
		cb.setBackground(new Color(139, 139, 139));
		cb.setHorizontalAlignment(SwingConstants.CENTER);

		cb1 = new MCItemHolder(mcreator, itemsWithTags, true);
		cb2 = new MCItemHolder(mcreator, itemsWithTags, true);
		cb3 = new MCItemHolder(mcreator, itemsWithTags, true);
		cb4 = new MCItemHolder(mcreator, itemsWithTags, true);
		cb5 = new MCItemHolder(mcreator, itemsWithTags, true);
		cb6 = new MCItemHolder(mcreator, itemsWithTags, true);
		cb7 = new MCItemHolder(mcreator, itemsWithTags, true);
		cb8 = new MCItemHolder(mcreator, itemsWithTags, true);
		cb9 = new MCItemHolder(mcreator, itemsWithTags, true);

		cb10 = new MCItemHolder(mcreator, items);

		cb1.setMargin(new Insets(0, 0, 0, 0));
		cb2.setMargin(new Insets(0, 0, 0, 0));
		cb3.setMargin(new Insets(0, 0, 0, 0));
		cb4.setMargin(new Insets(0, 0, 0, 0));
		cb5.setMargin(new Insets(0, 0, 0, 0));
		cb6.setMargin(new Insets(0, 0, 0, 0));
		cb7.setMargin(new Insets(0, 0, 0, 0));
		cb8.setMargin(new Insets(0, 0, 0, 0));
		cb9.setMargin(new Insets(0, 0, 0, 0));

		cb10.setMargin(new Insets(0, 0, 0, 0));

		cb1.setBounds(51, 29, 28, 28);
		cb2.setBounds(51, 60, 28, 28);
		cb3.setBounds(51, 90, 28, 28);
		cb4.setBounds(82, 29, 28, 28);
		cb5.setBounds(82, 60, 28, 28);
		cb6.setBounds(82, 90, 28, 28);
		cb7.setBounds(112, 29, 28, 28);
		cb8.setBounds(112, 60, 28, 28);
		cb9.setBounds(112, 90, 28, 28);
		cb10.setBounds(212, 60, 28, 28);

		cb.setBounds(205, 53, 40, 40);

		JLabel drop = new JLabel("1");
		JButton export = new JButton(UIRES.get("18px.export"));

		export.setContentAreaFilled(false);
		export.setMargin(new Insets(0, 0, 0, 0));
		export.setBounds(260, 13, 24, 24);
		export.setFocusPainted(false);
		export.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ip.add(export);
		export.addActionListener(event -> {
			export.setVisible(false);
			cb1.setValidationShownFlag(false);
			cb2.setValidationShownFlag(false);
			cb3.setValidationShownFlag(false);
			cb4.setValidationShownFlag(false);
			cb5.setValidationShownFlag(false);
			cb6.setValidationShownFlag(false);
			cb7.setValidationShownFlag(false);
			cb8.setValidationShownFlag(false);
			cb9.setValidationShownFlag(false);
			cb10.setValidationShownFlag(false);
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
			cb1.setValidationShownFlag(true);
			cb2.setValidationShownFlag(true);
			cb3.setValidationShownFlag(true);
			cb4.setValidationShownFlag(true);
			cb5.setValidationShownFlag(true);
			cb6.setValidationShownFlag(true);
			cb7.setValidationShownFlag(true);
			cb8.setValidationShownFlag(true);
			cb9.setValidationShownFlag(true);
			cb10.setValidationShownFlag(true);
			sp.setVisible(true);

		});

		sp = new JSpinner(new SpinnerNumberModel(1, 1, 64, 1));
		sp.setBounds(212, 109, 38, 17);
		ip.add(sp);

		drop.setBounds(212, 109, 38, 17);
		drop.setVisible(false);
		drop.setForeground(Color.white);
		ip.add(ComponentUtils.deriveFont(drop, 16));

		ip.add(cb1);
		ip.add(cb2);
		ip.add(cb3);
		ip.add(cb4);
		ip.add(cb5);
		ip.add(cb6);
		ip.add(cb7);
		ip.add(cb8);
		ip.add(cb9);

		ip.add(cb10);

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
}
