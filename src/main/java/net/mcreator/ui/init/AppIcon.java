/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.init;

import net.mcreator.Launcher;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AppIcon {

	public static ImageIcon getAppIcon(int width, int height) {

		boolean isSnapshot = Launcher.version.isSnapshot();
		boolean isMacOS26 = isMacOS26OrNewer();

		if (isSnapshot && isMacOS26) {
			return UIRES.SVG.getBuiltIn("icon_liquid_glass_eap", width, height);
		} else if (isSnapshot) {
			return UIRES.SVG.getBuiltIn("icon_eap", width, height);
		} else if (isMacOS26) {
			return UIRES.SVG.getBuiltIn("icon_liquid_glass", width, height);
		} else {
			return UIRES.SVG.getBuiltIn("icon", width, height);
		}
	}

	private static boolean isMacOS26OrNewer() {
		String osName = System.getProperty("os.name").toLowerCase();
		String osVersion = System.getProperty("os.version");

		try {
			return osName.contains("mac") && Integer.parseInt(osVersion.split("\\.")[0]) >= 26;
		} catch (Exception e) {
			return false;
		}
	}

	public static java.util.List<Image> getAppIcons() {
		return List.of(getAppIcon(16, 16).getImage(), getAppIcon(32, 32).getImage(), getAppIcon(64, 64).getImage(),
				getAppIcon(128, 128).getImage(), getAppIcon(256, 256).getImage());
	}

}
