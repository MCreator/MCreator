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

import net.mcreator.preferences.data.PreferencesData;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * <p>A ColorScheme is an object defining the look of MCreator in general.
 * This object contains only the 5 main colors used by the software. However, A ColorScheme
 * can also defines the style and colors of {@link net.mcreator.ui.blockly.BlocklyPanel} and {@link net.mcreator.ui.ide.RSyntaxTextAreaStyler}
 * if it is defined inside a plugin.</p>
 */
public class ColorScheme {

	private String backgroundColor;
	private String altBackgroundColor;
	private String secondAltBackgroundColor;

	private String foregroundColor;
	private String altForegroundColor;

	@Nullable String interfaceAccentColor;

	/**
	 * @return Background of UI panels
	 */
	public Color getBackgroundColor() {
		return Color.decode(backgroundColor);
	}

	/**
	 * @return Background of components (e.g. text fields, checkboxes and sound selectors)
	 */
	public Color getAltBackgroundColor() {
		return Color.decode(altBackgroundColor);
	}

	/**
	 * @return Second background color used (e.g. workspace background)
	 */
	public Color getSecondAltBackgroundColor() {
		return Color.decode(secondAltBackgroundColor);
	}

	/**
	 * @return <p>Secondary text color </p>
	 */
	public Color getAltForegroundColor() {
		return Color.decode(altForegroundColor);
	}

	/**
	 * @return <p>Color used for most of texts </p>
	 */
	public Color getForegroundColor() {
		return Color.decode(foregroundColor);
	}

	/**
	 * @return <p>Overrides the interfaceAccentColor defined by the user in {@link PreferencesData}</p>
	 */
	@Nullable public String getInterfaceAccentColor() {
		return interfaceAccentColor;
	}
}
