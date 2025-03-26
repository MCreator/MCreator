/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

import net.mcreator.io.FileIO;
import net.mcreator.ui.component.ImagePanel;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public abstract class AbstractRecipeMaker extends JPanel {

	protected final JButton export = new JButton(UIRES.get("18px.export"));
	protected final ImagePanel imagePanel;

	protected AbstractRecipeMaker(Image backgroundImage) {
		imagePanel = new ImagePanel(backgroundImage);
		imagePanel.fitToImage();
		imagePanel.setLayout(null);

		export.setContentAreaFilled(false);
		export.setMargin(new Insets(0, 0, 0, 0));
		export.setBounds(260, 13, 24, 24);
		export.setFocusPainted(false);
		export.setCursor(new Cursor(Cursor.HAND_CURSOR));
		imagePanel.add(export);

		export.addActionListener(event -> {
			export.setVisible(false);
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			setupImageExport(false);
			BufferedImage outputImage =
					new BufferedImage(imagePanel.getWidth(), imagePanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
			imagePanel.paint(outputImage.getGraphics());
			File file = FileDialogs.getSaveDialog(null, new String[] { ".png" });
			if (file != null)
				FileIO.writeImageToPNGFile(outputImage, file);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			export.setVisible(true);
			setupImageExport(true);
		});

		add(imagePanel);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		export.setEnabled(enabled);
	}

	protected abstract void setupImageExport(boolean exportedYet);
}
