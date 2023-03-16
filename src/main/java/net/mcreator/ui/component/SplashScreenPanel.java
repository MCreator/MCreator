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

public class SplashScreenPanel extends JPanel {

	private Image img, shadow, crop;
	private int cornerRadius;
	private int shadowRadius;
	private int extendBorder;
	private boolean snapshot;
	private Color snapshotColor;

	public SplashScreenPanel(Image img, int cornerRadius, int shadowRadius, int extendBorder, boolean snapshot,
			Color snapshotColor) {
		this.img = img;
		this.cornerRadius = cornerRadius;
		this.shadowRadius = shadowRadius;
		this.extendBorder = extendBorder;
		this.snapshot = snapshot;
		this.snapshotColor = snapshotColor;
		fitToImage();
		setOpaque(false);
		regenerateEffects();
	}

	public void fitToImage() {
		Dimension size = new Dimension(img.getWidth(null) + 2 * getBorderExtension(),
				img.getHeight(null) + 2 * getBorderExtension());
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
	}

	Dimension getImageSize() {
		return new Dimension(getSize().width - 2 * getBorderExtension(), getSize().height - 2 * getBorderExtension());
	}

	public int getBorderExtension() {
		return shadowRadius + extendBorder;
	}

	public void setImage(Image img) {
		this.img = img;
		repaint();
	}

	private void regenerateEffects() {
		shadow = ImageUtils.generateShadow(cornerRadius, shadowRadius, extendBorder, getImageSize().width,
				getImageSize().height);
		if (snapshot)
			crop = ImageUtils.generateSquircle(snapshotColor, 2, cornerRadius, getImageSize().width,
					getImageSize().height, this);
		else

			crop = ImageUtils.cropSquircle(img, 2, cornerRadius, getImageSize().width, getImageSize().height, this);
	}

	@Override public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		if (img != null) {
			g2d.drawImage(shadow, 0, 0, this);
			g2d.drawImage(crop, getBorderExtension(), getBorderExtension(), this);
		}
	}

}
