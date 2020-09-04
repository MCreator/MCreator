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
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SocialButtons extends JLabel {

	public SocialButtons() {
		setIcon(UIRES.get("social"));
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				int offset = 0;
				if (getBorder() instanceof EmptyBorder) {
					EmptyBorder border = (EmptyBorder) getBorder();
					offset = border.getBorderInsets().left;
				}
				int x = mouseEvent.getX() - offset;
				if (x >= 0 && x <= 16) {
					DesktopUtils.browseSafe("https://www.facebook.com/PyloDEV/");
				} else if (x >= 20 && x <= 36) {
					DesktopUtils.browseSafe("https://www.youtube.com/subscription_center?add_user=PyloGroup");
				} else if (x >= 40 && x <= 56) {
					DesktopUtils.browseSafe("https://twitter.com/PyloDEV");
				} else if (x >= 60 && x <= 76) {
					DesktopUtils.browseSafe("https://www.instagram.com/pylocompany/");
				} else if (x >= 80 && x <= 96) {
					DesktopUtils.browseSafe("https://github.com/Pylo");
				}
			}
		});
	}

}
