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

package net.mcreator.themes;

import java.awt.*;

/**
 * <p>A ColorScheme is an object defining the look of MCreator in general.
 * This object contains only the 5 main colors used by the software. However, A ColorScheme
 * can also defines the style and colors of {@link net.mcreator.ui.blockly.BlocklyPanel} and {@link net.mcreator.ui.ide.RSyntaxTextAreaStyler}
 * if it is defined inside a plugin.</p>
 */
public class ColorScheme {

	private final String altBackgroundColor;
	private final String backgroundColor;
	private final String secondAltBackgroundColor;
	private final String altForegroundColor;
	private final String foregroundColor;

	/**
	 * <p>Creates a new ColorScheme with parameters</p>
	 *
	 * @param altBackgroundColor Second background color used (e.g. workspace background)
	 * @param backgroundColor    Main color of the user (e.g. main menu and top bar)
	 * @param actionColor        Background of components (e.g. text fields, checkboxes and sound selectors)
	 * @param altForegroundColor Secondary text color
	 * @param foregroundColor    Color used for most of texts
	 */
	public ColorScheme(String altBackgroundColor, String backgroundColor, String actionColor,
			String altForegroundColor, String foregroundColor) {
		this.altBackgroundColor = altBackgroundColor;
		this.backgroundColor = backgroundColor;
		this.secondAltBackgroundColor = actionColor;
		this.altForegroundColor = altForegroundColor;
		this.foregroundColor = foregroundColor;
	}

	public Color getSecondAltBackgroundColor() {
		return Color.decode(secondAltBackgroundColor);
	}

	public Color getBackgroundColor() {
		return Color.decode(backgroundColor);
	}

	public Color getAltBackgroundColor() {
		return Color.decode(altBackgroundColor);
	}

	public Color getAltForegroundColor() {
		return Color.decode(altForegroundColor);
	}

	public Color getForegroundColor() {
		return Color.decode(foregroundColor);
	}
}
