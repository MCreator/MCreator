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

import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.image.ImageUtils;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {

	@Nonnull private final Image originalImage;

	private BufferedImage cached;
	private Dimension cachedSize;

	private boolean keepRatio = false;
	private boolean fitToWidth = false;

	private int offsetY = 0;

	public ImagePanel(@Nonnull Image originalImage) {
		this.originalImage = originalImage;
		setOpaque(true);
		setBackground(Theme.current().getBackgroundColor());
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
		invalidateCache();
	}

	public void setKeepRatio(boolean flag) {
		this.keepRatio = flag;
		invalidateCache();
	}

	public void setFitToWidth(boolean fitToWidth) {
		this.fitToWidth = fitToWidth;
		invalidateCache();
	}

	private void invalidateCache() {
		cached = null;
		cachedSize = null;
	}

	@Override protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Dimension size = getSize();

		if (cached == null || !size.equals(cachedSize)) {
			cachedSize = size;
			cached = renderImage(size);
		}

		g.drawImage(cached, 0, 0, null);
	}

	public void fitToImage() {
		Dimension size = new Dimension(originalImage.getWidth(null), originalImage.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
	}

	private BufferedImage renderImage(Dimension size) {
		if (size.width <= 0 || size.height <= 0)
			return null;

		int w = size.width;
		int h = size.height;

		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bi.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		int drawY = offsetY;

		if (fitToWidth) {
			int iw = originalImage.getWidth(this);
			int ih = originalImage.getHeight(this);
			if (iw <= 0 || ih <= 0) {
				g2.dispose();
				return bi;
			}

			int scaledH = (int) ((float) w * ((float) ih / iw));

			if (drawY < -scaledH)
				drawY = -scaledH;
			if (drawY > h)
				drawY = h - 1;

			g2.drawImage(originalImage, 0, drawY, w, scaledH, null);
		} else if (!keepRatio) {
			g2.drawImage(originalImage, 0, drawY, w, h, null);
		} else {
			Image covered = ImageUtils.cover(originalImage, size);
			g2.drawImage(covered, 0, drawY, null);
		}

		g2.dispose();
		return bi;
	}

}