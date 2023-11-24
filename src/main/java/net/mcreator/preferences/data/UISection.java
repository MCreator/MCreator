/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.preferences.data;

import net.mcreator.io.OS;
import net.mcreator.preferences.PreferencesEntry;
import net.mcreator.preferences.PreferencesSection;
import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.ColorEntry;
import net.mcreator.preferences.entries.LocaleEntry;
import net.mcreator.preferences.entries.StringEntry;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.ColorScheme;

import java.util.Locale;

public class UISection extends PreferencesSection {

	public PreferencesEntry<Locale> language;
	public ColorEntry interfaceAccentColor;
	public StringEntry backgroundSource;
	public BooleanEntry aaText;
	public StringEntry textAntialiasingType;
	public BooleanEntry usemacOSMenuBar;
	public BooleanEntry useNativeFileChooser;
	public BooleanEntry expandSectionsByDefault;
	public BooleanEntry use2DAcceleration;
	public BooleanEntry autoReloadTabs;
	public BooleanEntry remindOfUnsavedChanges;
	public BooleanEntry discordRichPresenceEnable;

	public UISection(String preferencesIdentifier) {
		super(preferencesIdentifier);

		language = addEntry(new LocaleEntry("language", L10N.DEFAULT_LOCALE));
		interfaceAccentColor = addEntry(new ColorEntry("interfaceAccentColor", ColorScheme.MAIN_TINT_DEFAULT));
		backgroundSource = addEntry(
				new StringEntry("backgroundSource", "All", "All", "Current theme", "Custom", "None"));
		aaText = addEntry(new BooleanEntry("aaText", true));
		textAntialiasingType = addEntry(
				new StringEntry("textAntialiasingType", "on", "on", "off", "gasp", "lcd", "lcd_hbgr", "lcd_vrgb",
						"lcd_vbgr"));
		usemacOSMenuBar = addEntry(new BooleanEntry("useMacOSMenuBar", true));
		useNativeFileChooser = addEntry(new BooleanEntry("useNativeFileChooser", OS.getOS() == OS.WINDOWS));
		expandSectionsByDefault = addEntry(new BooleanEntry("expandSectionsByDefault", false));
		use2DAcceleration = addEntry(new BooleanEntry("use2DAcceleration", false));
		autoReloadTabs = addEntry(new BooleanEntry("autoReloadTabs", true));
		remindOfUnsavedChanges = addEntry(new BooleanEntry("remindOfUnsavedChanges", false));
		discordRichPresenceEnable = addEntry(new BooleanEntry("discordRichPresenceEnable", true));
	}

	@Override public String getSectionKey() {
		return "ui";
	}

}
