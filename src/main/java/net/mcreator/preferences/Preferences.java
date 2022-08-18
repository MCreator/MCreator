/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.preferences;

import net.mcreator.io.OS;
import net.mcreator.preferences.entry.StringEntry;
import net.mcreator.preferences.entry.PreferenceEntry;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.MCreatorTheme;

import java.awt.*;
import java.util.Locale;

import static net.mcreator.preferences.entry.PreferenceSection.UI;

public class Preferences {

	// UI
	public PreferenceEntry<Locale> language;
	public PreferenceEntry<Color> interfaceAccentColor;
	public PreferenceEntry<String> backgroundSource;
	public PreferenceEntry<Boolean> aaText;
	public PreferenceEntry<String> textAntialiasingType;
	public PreferenceEntry<Boolean> usemacOSMenuBar;
	public PreferenceEntry<Boolean> useNativeFileChooser;
	public PreferenceEntry<Boolean> expandSectionsByDefault;
	public PreferenceEntry<Boolean> use2DAcceleration;
	public PreferenceEntry<Boolean> autoReloadTabs;
	public PreferenceEntry<Boolean> remindOfUnsavedChanges;
	public PreferenceEntry<Boolean> discordRichPresenceEnable;

	
	public Preferences() {
		// UI
		language = PreferencesRegistry.register(new PreferenceEntry<>("language", L10N.DEFAULT_LOCALE, UI));
		interfaceAccentColor = PreferencesRegistry.register(new PreferenceEntry<>("interfaceAccentColor", MCreatorTheme.MAIN_TINT_DEFAULT, UI));
		backgroundSource = PreferencesRegistry.register(new StringEntry("backgroundSource", "All", UI,
				"All", "Current theme", "Custom", "None"));
		aaText = PreferencesRegistry.register(new PreferenceEntry<>("aatext", true, UI));
		textAntialiasingType = PreferencesRegistry.register(new StringEntry("textAntialiasingType", "All", UI,
				"on", "off", "gasp", "lcd", "lcd_hbgr", "lcd_vrgb", "lcd_vbgr"));
		usemacOSMenuBar = PreferencesRegistry.register(new PreferenceEntry<>("usemacOSMenuBar", true, UI));
		useNativeFileChooser = PreferencesRegistry.register(new PreferenceEntry<>("useNativeFileChooser", OS.getOS() == OS.WINDOWS, UI));
		expandSectionsByDefault = PreferencesRegistry.register(new PreferenceEntry<>("expandSectionsByDefault", false, UI));
		use2DAcceleration = PreferencesRegistry.register(new PreferenceEntry<>("use2DAcceleration", false, UI));
		autoReloadTabs = PreferencesRegistry.register(new PreferenceEntry<>("autoReloadTabs", true, UI));
		remindOfUnsavedChanges = PreferencesRegistry.register(new PreferenceEntry<>("remindOfUnsavedChanges", false, UI));
		discordRichPresenceEnable = PreferencesRegistry.register(new PreferenceEntry<>("discordRichPresenceEnable", true, UI));
	}
}
