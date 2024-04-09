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

import net.mcreator.ui.init.UIRES;
import net.mcreator.util.DesktopUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SocialButtons extends JPanel {

	public SocialButtons() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		add(createButton("yt", "https://www.youtube.com/subscription_center?add_user=PyloGroup"));
		add(new JEmptyBox(4, 4));
		add(createButton("tw", "https://twitter.com/PyloDEV"));
		add(new JEmptyBox(4, 4));
		add(createButton("rd", "https://www.reddit.com/r/MCreator/"));
		add(new JEmptyBox(4, 4));
		add(createButton("ig", "https://www.instagram.com/pylocompany/"));
		add(new JEmptyBox(4, 4));
		add(createButton("gh", "https://github.com/MCreator/MCreator"));
		add(new JEmptyBox(4, 4));
		add(createButton("fb", "https://www.facebook.com/PyloDEV"));
		setOpaque(false);
	}

	private JLabel createButton(String icon, String url) {
		JLabel label = new JLabel(UIRES.SVG.getBuiltIn("social." + icon, 16, 18));
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				DesktopUtils.browseSafe(url);
			}
		});
		return label;
	}

}
