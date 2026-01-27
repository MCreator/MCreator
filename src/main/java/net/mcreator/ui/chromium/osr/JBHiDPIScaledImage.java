/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.chromium.osr;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.ImageObserver;

public class JBHiDPIScaledImage {

	@Nonnull private final Image delegate;

	private final double userWidth;
	private final double userHeight;

	private JBHiDPIScaledImage(@Nonnull Image image, double width, double height) {
		this.delegate = image;
		this.userWidth = width;
		this.userHeight = height;
	}

	public int getWidth() {
		return (int) Math.round(userWidth);
	}

	public int getHeight() {
		return (int) Math.round(userHeight);
	}

	@Nonnull public Image getDelegate() {
		return delegate;
	}

	public static @Nonnull JBHiDPIScaledImage createFrom(@Nonnull Image image, double scale, ImageObserver observer) {
		int w = image.getWidth(observer);
		int h = image.getHeight(observer);
		return new JBHiDPIScaledImage(image, w / scale, h / scale);
	}

	public static void drawImage(Graphics2D g, JBHiDPIScaledImage hidpiImage, int x, int y, ImageObserver observer) {
		g.drawImage(hidpiImage.getDelegate(), x, y, (int) hidpiImage.userWidth, (int) hidpiImage.userHeight, observer);
	}

}
