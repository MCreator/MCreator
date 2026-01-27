// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
// Modifications by Pylo and opensource contributors

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
