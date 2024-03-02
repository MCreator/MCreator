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

package net.mcreator.ui;

import net.mcreator.Launcher;
import net.mcreator.ui.component.ProgressBar;
import net.mcreator.ui.component.SplashScreenPanel;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BaseMultiResolutionImage;

public class SplashScreen extends JWindow {

	private final ProgressBar initloadprogress = new ProgressBar();
	private final JLabel loadstate = new JLabel();

	private final static int CORNER_RADIUS = 10;
	private final static int SHADOW_RADIUS = 15;
	private final static int EXTEND_BORDER = 3;

	public SplashScreen() {
		Font splashFont = new Font("Sans-Serif", Font.PLAIN, 13);

		SplashScreenPanel imagePanel = new SplashScreenPanel(getSplashImage(false), CORNER_RADIUS, SHADOW_RADIUS,
				EXTEND_BORDER, (Launcher.version != null && Launcher.version.isSnapshot()), new Color(50, 50, 50));
		int shadowPadding = imagePanel.getBorderExtension();

		imagePanel.setLayout(null);

		JLabel pylo = new JLabel(UIRES.SVG.getBuiltIn("pylo", 90, 24));
		pylo.setBounds(shadowPadding + 540 - 15 - 10, shadowPadding + 348 - 15 - 10, 90, 24);
		imagePanel.add(pylo);

		JLabel label = new JLabel(
				"<html><p>MCreator is a Minecraft mod making toolkit developed by Pylo. Minecraft is a</p>"
						+ "<p style='margin-top:-2'>registered trademark of Mojang. MCreator is not an official Minecraft product.</p>"
						+ "<p style='margin-top:-2'>It is not approved by or associated with Mojang or Microsoft.</p>");
		label.setFont(splashFont.deriveFont(10f));
		label.setForeground(Color.white);
		label.setBounds(shadowPadding + 30 + 10 - 4, shadowPadding + 330 - 10 - 10, 500, 45);
		imagePanel.add(label);

		JLabel logo = new JLabel(UIRES.SVG.getBuiltIn("logo", 350, 63));
		logo.setBounds(shadowPadding + 24 + 8 - 4, shadowPadding + 70, 350, 63);
		imagePanel.add(logo);

		JLabel version = new JLabel(
				"VERSION " + (Launcher.version != null ? Launcher.version.getMajorString() : "1234.5"));
		version.setFont(splashFont.deriveFont(18f));
		version.setForeground(Color.white);
		version.setBounds(shadowPadding + 30 + 10 - 4, shadowPadding + 129, 500, 45);
		imagePanel.add(version);

		if (Launcher.version != null && Launcher.version.isSnapshot()) {
			JLabel snapshot = new JLabel("Snapshot - not for production use!");
			snapshot.setFont(splashFont.deriveFont(14f));
			snapshot.setForeground(new Color(255, 92, 82));
			snapshot.setBounds(shadowPadding + 30 + 10 - 4, shadowPadding + 165, 500, 45);
			imagePanel.add(snapshot);
		}

		initloadprogress.setOpaque(false);
		initloadprogress.setForeground(Color.white);
		initloadprogress.setMaximalValue(100);
		initloadprogress.init();
		initloadprogress.setBounds(shadowPadding + 30 + 10 - 4, shadowPadding + 283 - 10, 568, 3);
		imagePanel.add(initloadprogress);

		loadstate.setFont(splashFont.deriveFont(12f));
		loadstate.setForeground(Color.white);
		loadstate.setBounds(shadowPadding + 30 + 10 - 4, shadowPadding + 283 - 39 - 10, 500, 45);
		imagePanel.add(loadstate);

		add(imagePanel);

		// Catch any exceptions that might get thrown when working with systems that don't support window transparency.
		try {
			setBackground(new Color(0, 0, 0, 0));
		} catch (Exception ignored) {
		}

		setSize(imagePanel.getSize());
		setLocationRelativeTo(null);
		setVisible(true);
		requestFocus();
		requestFocusInWindow();
		toFront();
	}

	public void setProgress(int percentage, String message) {
		SwingUtilities.invokeLater(() -> {
			initloadprogress.setCurrentValue(percentage);
			loadstate.setText(message);
		});
	}

	public static BaseMultiResolutionImage getSplashImage(boolean darken) {
		Image splash2x = UIRES.getBuiltIn("splash").getImage();
		Image splash1x = ImageUtils.resize(splash2x, splash2x.getWidth(null) / 2, splash2x.getHeight(null) / 2);
		if (darken) {
			splash1x = ImageUtils.darken(ImageUtils.toBufferedImage(splash1x));
			splash2x = ImageUtils.darken(ImageUtils.toBufferedImage(splash2x));
		}
		return new BaseMultiResolutionImage(splash1x, splash2x);
	}

}
