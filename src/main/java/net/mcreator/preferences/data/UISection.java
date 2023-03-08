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

public class UISection {

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

	public UISection() {
		interfaceAccentColor = PreferencesData.register(
				new ColorEntry("interfaceAccentColor", MCreatorTheme.MAIN_TINT_DEFAULT, PreferencesData.UI));
		backgroundSource = PreferencesData.register(
				new StringEntry("backgroundSource", "All", PreferencesData.UI, "All", "Current theme", "Custom", "None"));
		aaText = PreferencesData.register(new BooleanEntry("aaText", true, PreferencesData.UI));
		textAntialiasingType = PreferencesData.register(
				new StringEntry("textAntialiasingType", "All", PreferencesData.UI, "on", "off", "gasp", "lcd", "lcd_hbgr",
						"lcd_vrgb", "lcd_vbgr"));
		usemacOSMenuBar = PreferencesData.register(new BooleanEntry("useMacOSMenuBar", true, PreferencesData.UI));
		useNativeFileChooser = PreferencesData.register(
				new BooleanEntry("useNativeFileChooser", OS.getOS() == OS.WINDOWS, PreferencesData.UI));
		expandSectionsByDefault = PreferencesData.register(
				new BooleanEntry("expandSectionsByDefault", false, PreferencesData.UI));
		use2DAcceleration = PreferencesData.register(new BooleanEntry("use2DAcceleration", false, PreferencesData.UI));
		autoReloadTabs = PreferencesData.register(new BooleanEntry("autoReloadTabs", true, PreferencesData.UI));
		remindOfUnsavedChanges = PreferencesData.register(
				new BooleanEntry("remindOfUnsavedChanges", false, PreferencesData.UI));
		discordRichPresenceEnable = PreferencesData.register(
				new BooleanEntry("discordRichPresenceEnable", true, PreferencesData.UI));

		language = PreferencesData.register(new PreferenceEntry<>("language", L10N.DEFAULT_LOCALE, PreferencesData.UI) {
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
	}

}
