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

package net.mcreator.ui.laf;

import net.mcreator.preferences.PreferencesManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.lang.reflect.Field;

public class MCreatorLookAndFeel extends MetalLookAndFeel {

	private static final Logger LOG = LogManager.getLogger("Look and Feel");

	private final AbstractMCreatorTheme theme;

	public MCreatorLookAndFeel() {
		if (PreferencesManager.PREFERENCES.ui.interfaceTheme.equals("Light imageTheme")) {
			setCurrentTheme(theme = new LightMCreatorTheme());
		} else {
			setCurrentTheme(theme = new DarkMCreatorTheme());
		}
	}

	@Override protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
	}

	@Override public UIDefaults getDefaults() {
		try {
			final Field keyField = HTMLEditorKit.class.getDeclaredField("DEFAULT_STYLES_KEY");
			keyField.setAccessible(true);
			final Object key = keyField.get(null);

			Object appContext = Class.forName("sun.awt.AppContext").getMethod("getAppContext").invoke(null);

			StyleSheet defaultStyles = (StyleSheet) appContext.getClass().getMethod("get", Object.class)
					.invoke(appContext, key);

			if (defaultStyles != null) {
				defaultStyles.addRule("* {color: white;} font, b, i, strong, p, div, li, ul, ol {color: #" + Integer
						.toHexString(theme.getBrightColor().getRGB()).substring(2)
						+ ";} body {color: white;} html {color: #" + Integer
						.toHexString(theme.getBrightColor().getRGB()).substring(2) + ";} a {color: #" + Integer
						.toHexString(theme.getMainTint().getRGB()).substring(2) + ";}");

				appContext.getClass().getMethod("put", Object.class, Object.class)
						.invoke(appContext, key, defaultStyles);
			}
		} catch (Throwable throwable) {
			LOG.error("Failed to apply custom CSS style sheets. Interface will look broken!", throwable);
		}
		return super.getDefaults();
	}

	@Override public String getName() {
		return "MCreator";
	}

	@Override public String getID() {
		return "MCreator";
	}

	@Override public String getDescription() {
		return "MCreator Look and Feel";
	}

	@Override public boolean isNativeLookAndFeel() {
		return false;
	}

	@Override public boolean isSupportedLookAndFeel() {
		return true;
	}

}
