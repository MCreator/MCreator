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

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.ImagePanel;
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.MCItemHolder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CampfireCookingRecipeMaker extends JPanel {

	public MCItemHolder cb1;
	public MCItemHolder cb2;

	public CampfireCookingRecipeMaker(MCreator mcreator, MCItem.ListProvider itemsWithTags, MCItem.ListProvider items) {
		ImagePanel ip = new ImagePanel(UIRES.get("recipe.campfire_cooking").getImage());

		ip.fitToImage();
		ip.setLayout(null);

		cb1 = new MCItemHolder(mcreator, itemsWithTags, true);
		cb2 = new MCItemHolder(mcreator, items);

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
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			BufferedImage im = new BufferedImage(ip.getWidth(), ip.getHeight(), BufferedImage.TYPE_INT_ARGB);
			ip.paint(im.getGraphics());
			File fi = FileDialogs.getSaveDialog(null, new String[] { ".png" });
			if (fi != null)
				FileIO.writeImageToPNGFile(im, fi);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			export.setVisible(true);
			cb1.setValidationShownFlag(true);
			cb2.setValidationShownFlag(true);
		});

		cb1.setBounds(97, 30, 28, 28);
		cb2.setBounds(200, 61, 28, 28);

		ip.add(cb1);
		ip.add(cb2);

		add(ip);
		setPreferredSize(new Dimension(306, 145));
	}

	public MItemBlock getBlock() {
		return cb1.getBlock();
	}

	public MItemBlock getBlock2() {
		return cb2.getBlock();
	}

	public boolean has() {
		return cb1.containsItem() && cb2.containsItem();
	}

}
