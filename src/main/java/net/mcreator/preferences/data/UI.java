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
import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.ColorEntry;
import net.mcreator.preferences.entries.PreferenceEntry;
import net.mcreator.preferences.entries.StringEntry;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.MCreatorTheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class UI {
	public PreferenceEntry<Locale> language;
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

	public UI() {
		language = Preferences.register(new PreferenceEntry<>("language", L10N.DEFAULT_LOCALE, Preferences.UI) {
			@Override public JComponent getComponent(Window parent, Consumer<EventObject> fct) {
				List<Locale> locales = new ArrayList<>(L10N.getSupportedLocales());
				locales.sort((a, b) -> {
					int sa = L10N.getUITextsLocaleSupport(a) + L10N.getHelpTipsSupport(a);
					int sb = L10N.getUITextsLocaleSupport(b) + L10N.getHelpTipsSupport(b);
					if (sa == sb)
						return a.getDisplayName().compareTo(b.getDisplayName());

					return sb - sa;
				});
				JComboBox<Locale> box = new JComboBox<>(locales.toArray(new Locale[0]));
				box.setRenderer(new PreferencesDialog.LocaleListRenderer());
				box.setSelectedItem(this.value);
				box.addActionListener(fct::accept);
				return box;
			}
		});
		interfaceAccentColor = Preferences.register(
				new ColorEntry("interfaceAccentColor", MCreatorTheme.MAIN_TINT_DEFAULT, Preferences.UI));
		backgroundSource = Preferences.register(
				new StringEntry("backgroundSource", "All", Preferences.UI, "All", "Current theme", "Custom", "None"));
		aaText = Preferences.register(new BooleanEntry("aaText", true, Preferences.UI));
		textAntialiasingType = Preferences.register(
				new StringEntry("textAntialiasingType", "All", Preferences.UI, "on", "off", "gasp", "lcd", "lcd_hbgr",
						"lcd_vrgb", "lcd_vbgr"));
		usemacOSMenuBar = Preferences.register(new BooleanEntry("useMacOSMenuBar", true, Preferences.UI));
		useNativeFileChooser = Preferences.register(
				new BooleanEntry("useNativeFileChooser", OS.getOS() == OS.WINDOWS, Preferences.UI));
		expandSectionsByDefault = Preferences.register(
				new BooleanEntry("expandSectionsByDefault", false, Preferences.UI));
		use2DAcceleration = Preferences.register(new BooleanEntry("use2DAcceleration", false, Preferences.UI));
		autoReloadTabs = Preferences.register(new BooleanEntry("autoReloadTabs", true, Preferences.UI));
		remindOfUnsavedChanges = Preferences.register(
				new BooleanEntry("remindOfUnsavedChanges", false, Preferences.UI));
		discordRichPresenceEnable = Preferences.register(
				new BooleanEntry("discordRichPresenceEnable", true, Preferences.UI));
	}
}
