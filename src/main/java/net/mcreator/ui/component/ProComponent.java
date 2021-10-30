/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;

public class ProComponent extends JPanel {

	private final Image warning = ImageUtils.colorize(UIRES.get("16px.lock"), new Color(0xff981d), false).getImage();

	public ProComponent(Component origin, int additional) {
		setLayout(new GridLayout());
		switch (additional) {
		case 0 -> add(origin);
		case 1 -> add(PanelUtils.centerInPanel(origin));
		case 2 -> add(PanelUtils.join(FlowLayout.LEFT, origin));
		}
		setOpaque(false);

		origin.setEnabled(false);
	}

	@Override public void paint(Graphics g) {
		super.paint(g);

		g.setColor(new Color(0.3f, 0.3f, 0, 0.4f));
		g.fillRect(0, 0, getWidth(), getHeight());

		int x = (this.getWidth() - warning.getWidth(null)) / 2;
		int y = (this.getHeight() - warning.getHeight(null)) / 2;

		if (getWidth() > 200) {
			g.setFont(g.getFont().deriveFont(12f));
			g.drawImage(warning, x - g.getFontMetrics().stringWidth("Only available in full version") / 2, y, null);
			g.setColor((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			g.drawString("Only available in full version",
					x - g.getFontMetrics().stringWidth("Only available in full version") / 2 + 18 + 4, y + 13);
		} else {
			g.drawImage(warning, x, y, null);
		}
	}
}
