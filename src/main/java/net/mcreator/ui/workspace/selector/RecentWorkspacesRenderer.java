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

package net.mcreator.ui.workspace.selector;

import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.ui.laf.MCreatorTheme;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

class RecentWorkspacesRenderer extends JLabel implements ListCellRenderer<RecentWorkspaceEntry> {

	private String version = null;
	private boolean isSelected = false;

	private final Font rotatedFont;

	RecentWorkspacesRenderer() {
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(-90), 0, 0);
		rotatedFont = getFont().deriveFont(11.0f).deriveFont(affineTransform);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends RecentWorkspaceEntry> list,
			RecentWorkspaceEntry value, int index, boolean isSelected, boolean cellHasFocus) {
		this.version = value.getMCRVersion();
		this.isSelected = isSelected;

		String path = value.getPath().getParentFile().getAbsolutePath().replace("\\", "/");

		setOpaque(true);

		setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		setForeground(isSelected ?
				(Color) UIManager.get("MCreatorLAF.MAIN_TINT") :
				(Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));

		setFont(MCreatorTheme.secondary_font.deriveFont(16.0f));

		setBorder(BorderFactory.createEmptyBorder(2, version != null ? 23 : 5, 3, 0));

		if (value.getType() != GeneratorFlavor.UNKNOWN) {
			ImageIcon icon = new ImageIcon(
					ImageUtils.darken(ImageUtils.toBufferedImage(value.getType().getIcon().getImage())));

			setIcon(isSelected ?
					ImageUtils.colorize(icon, (Color) UIManager.get("MCreatorLAF.MAIN_TINT"), false) :
					icon);

			setIconTextGap(8);
			setText("<html><font style=\"font-size: 15px;\">" + StringUtils.abbreviateString(value.getName(), 18)
					+ "</font><small><br>" + StringUtils.abbreviateStringInverse(path, 30));
		} else {
			setIcon(null);

			setIconTextGap(0);
			setText("<html><font style=\"font-size: 15px;\">" + StringUtils.abbreviateString(value.getName(), 20)
					+ "</font><small><br>" + StringUtils.abbreviateStringInverse(path, 37));
		}

		return this;
	}

	@Override protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (version != null) {
			Graphics2D g2 = (Graphics2D) g;

			g2.setColor(isSelected ?
					(Color) UIManager.get("MCreatorLAF.MAIN_TINT") :
					(Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			String[] parts = version.split("\\.");
			g2.setFont(rotatedFont);
			g2.drawString(parts[0] + "." + parts[1], 15, 40);
		}
	}

}
