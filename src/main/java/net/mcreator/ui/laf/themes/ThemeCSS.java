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

package net.mcreator.ui.laf.themes;

import java.awt.*;

public class ThemeCSS {

	/**
	 * Generates a CSS string with all theme colors as CSS variables using a multiline text block.
	 */
	public static String generateCSS(Theme theme) {
		//@formatter:off
		return String.format("""
            :root {
                --backgroundColor: %s;
                --backgroundColorTransparent: %s;
                --backgroundColorBrighter: %s;
                --altBackgroundColor: %s;
                --secondAltBackgroundColor: %s;
                --foregroundColor: %s;
                --altForegroundColor: %s;
                --interfaceAccentColor: %s;
            }
            """,
				toCssColor(theme.getBackgroundColor()),
				toCssColor(theme.getBackgroundColor()) + "E4",
				toCssColor(theme.getBackgroundColor().brighter()),
				toCssColor(theme.getAltBackgroundColor()),
				toCssColor(theme.getSecondAltBackgroundColor()),
				toCssColor(theme.getForegroundColor()),
				toCssColor(theme.getAltForegroundColor()),
				toCssColor(theme.getInterfaceAccentColor())
		);
		//@formatter:on
	}

	private static String toCssColor(Color color) {
		return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
	}

}
