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

package net.mcreator.ui.component;

import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {

	private Image img;

	private boolean keepRatio = false;
	private boolean original = false;
	private boolean fitToWidth = false;

	private final Color defaultColor = (Color) UIManager.get("MCreatorLAF.DARK_ACCENT");

	private int offsetY = 0;

	public ImagePanel(Image img) {
		this.img = img;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public void fitToImage() {
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
	}

	public void setImage(Image img) {
		this.img = img;
		repaint();
	}

	public void setRenderOriginal(boolean flag) {
		this.original = flag;
	}

	public void setKeepRatio(boolean flag) {
		this.keepRatio = flag;
	}

	public void setFitToWidth(boolean fitToWidth) {
		this.fitToWidth = fitToWidth;
	}

	@Override public void paintComponent(Graphics g) {
		g.setColor(defaultColor);
		g.fillRect(0, 0, getSize().width, getSize().height);

		if (img != null) {
			if (original)
				g.drawImage(img, 0, offsetY, this);
			else if (fitToWidth)
				g.drawImage(img, 0, offsetY, getSize().width,
						(int) ((float) getSize().width * ((float) img.getHeight(this) / (float) img.getWidth(this))),
						this);
			else if (!keepRatio)
				g.drawImage(img, 0, offsetY, getSize().width, getSize().height, this);
			else
				g.drawImage(ImageUtils.cover(img, getSize()), 0, offsetY, this);

		}
	}

}
