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

package net.mcreator.ui.laf.themes;

import net.mcreator.preferences.PreferencesManager;
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

	public static final Color MAIN_TINT_DEFAULT = new Color(0x93c54b);

	private String backgroundColor;
	private String altBackgroundColor;
	private String secondAltBackgroundColor;

	private String foregroundColor;
	private String altForegroundColor;

	@Nullable private String interfaceAccentColor;

	// Caches
	private transient Color backgroundColorCache;
	private transient Color altBackgroundColorCache;
	private transient Color secondAltBackgroundColorCache;
	private transient Color foregroundColorCache;
	private transient Color altForegroundColorCache;
	private transient Color interfaceAccentColorCache;

	protected void init() {
		this.backgroundColorCache = Color.decode(backgroundColor);
		this.altBackgroundColorCache = Color.decode(altBackgroundColor);
		this.secondAltBackgroundColorCache = Color.decode(secondAltBackgroundColor);
		this.foregroundColorCache = Color.decode(foregroundColor);
		this.altForegroundColorCache = Color.decode(altForegroundColor);

		interfaceAccentColorCache = PreferencesManager.PREFERENCES.ui.interfaceAccentColor.get();
		if (interfaceAccentColor != null) {
			try {
				interfaceAccentColorCache = Color.decode(interfaceAccentColor);
			} catch (NumberFormatException ignored) {
			}
		}
	}

	/**
	 * @return Background of UI panels
	 */
	public Color getBackgroundColor() {
		return backgroundColorCache;
	}

	/**
	 * @return Background of components (e.g. text fields, checkboxes and sound selectors)
	 */
	public Color getAltBackgroundColor() {
		return altBackgroundColorCache;
	}

	/**
	 * @return Second background color used (e.g. workspace background)
	 */
	public Color getSecondAltBackgroundColor() {
		return secondAltBackgroundColorCache;
	}

	/**
	 * @return <p>Secondary text color </p>
	 */
	public Color getAltForegroundColor() {
		return altForegroundColorCache;
	}

	/**
	 * @return <p>Color used for most of texts </p>
	 */
	public Color getForegroundColor() {
		return foregroundColorCache;
	}

	/**
	 * @return <p>Returns the interfaceAccentColor if defined by theme, otherwise the one defined by the user in {@link PreferencesData}</p>
	 */
	public Color getInterfaceAccentColor() {
		return interfaceAccentColorCache;
	}

}
