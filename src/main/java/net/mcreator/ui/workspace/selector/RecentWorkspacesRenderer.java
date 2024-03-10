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
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Objects;

class RecentWorkspacesRenderer extends JPanel implements ListCellRenderer<RecentWorkspaceEntry> {

	private String version = null;
	private boolean isSelected = false;

	private final Font rotatedFont;

	private final JLabel nameLabel = new JLabel();
	private final JLabel pathLabel = new JLabel();
	private final JLabel iconLabel = new JLabel();

	RecentWorkspacesRenderer() {
		super(new BorderLayout(7, 0));

		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(-90), 0, 0);
		rotatedFont = getFont().deriveFont(11.0f).deriveFont(affineTransform);

		setBackground(Theme.current().getSecondAltBackgroundColor());

		nameLabel.setFont(getFont().deriveFont(20.0f));
		pathLabel.setFont(getFont().deriveFont(11.0f));

		add("West", iconLabel);
		add("Center", PanelUtils.northAndCenterElement(nameLabel, pathLabel));
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends RecentWorkspaceEntry> list,
			RecentWorkspaceEntry value, int index, boolean isSelected, boolean cellHasFocus) {
		this.version = value.getMCRVersion();
		this.isSelected = isSelected;

		String path = value.getPath().getParentFile().getAbsolutePath().replace("\\", "/");

		nameLabel.setForeground(
				isSelected ? Theme.current().getInterfaceAccentColor() : Theme.current().getAltForegroundColor());
		pathLabel.setForeground(nameLabel.getForeground());

		setToolTipText(L10N.t("dialog.workspace_selector.recent_workspace", value.getName(), path,
				Objects.requireNonNullElse(value.getMCRVersion(), L10N.t("common.not_applicable"))));
		setBorder(BorderFactory.createEmptyBorder(2, version != null ? 23 : 5, 3, 0));

		if (value.getType() != GeneratorFlavor.UNKNOWN) {
			ImageIcon icon = new ImageIcon(
					ImageUtils.darken(ImageUtils.toBufferedImage(value.getType().getIcon().getImage())));

			iconLabel.setIcon(
					isSelected ? ImageUtils.colorize(icon, Theme.current().getInterfaceAccentColor(), false) : icon);
			nameLabel.setText(StringUtils.abbreviateString(value.getName(), 17));
			pathLabel.setText(StringUtils.abbreviateStringInverse(path, 26));
		} else {
			iconLabel.setIcon(null);
			nameLabel.setText(StringUtils.abbreviateString(value.getName(), 19));
			pathLabel.setText(StringUtils.abbreviateStringInverse(path, 34));
		}

		return this;
	}

	@Override protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (version != null) {
			Graphics2D g2 = (Graphics2D) g;

			g2.setColor(
					isSelected ? Theme.current().getInterfaceAccentColor() : Theme.current().getAltForegroundColor());

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			String[] parts = version.split("\\.");
			g2.setFont(rotatedFont);
			g2.drawString(parts[0] + "." + parts[1], 15, 40);
		}
	}

}
