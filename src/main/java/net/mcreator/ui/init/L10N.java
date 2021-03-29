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

package net.mcreator.ui.init;

import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.util.locale.LocaleRegistration;
import net.mcreator.util.locale.UTF8Control;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class L10N {

	private static final Logger LOG = LogManager.getLogger("L10N");

	private static ResourceBundle rb;
	private static ResourceBundle rb_en;

	private static Map<Locale, LocaleRegistration> supportedLocales;

	private static boolean isTestingEnvironment = false;

	private static Locale selectedLocale = null;

	public static void initTranslations() {
		initLocalesImpl();

		rb = supportedLocales.get(getLocale()).getResourceBundle();

		LOG.info("Setting default locale to: " + getLocale());
		Locale.setDefault(getLocale());
	}

	private static void initLocalesImpl() {
		rb_en = ResourceBundle.getBundle("lang/texts", Locale.ROOT, PluginLoader.INSTANCE, new UTF8Control());

		double countAll = Collections.list(rb_en.getKeys()).size();

		Set<String> localeFiles = PluginLoader.INSTANCE.getResourcesInPackage("lang");
		supportedLocales = localeFiles.stream().map(FilenameUtils::getBaseName).filter(e -> e.contains("_"))
				.map(e -> e.split("_")).map(e -> new Locale(e[1], e[2])).collect(Collectors.toMap(key -> key, value -> {
					ResourceBundle rb = ResourceBundle
							.getBundle("lang/texts", value, PluginLoader.INSTANCE, new UTF8Control());
					return new LocaleRegistration(rb,
							(int) Math.ceil(Collections.list(rb.getKeys()).size() / countAll * 100d));
				}));

		supportedLocales.put(new Locale("en", "US"), new LocaleRegistration(rb_en, 100));
	}

	public static Set<Locale> getSupportedLocales() {
		return supportedLocales.keySet();
	}

	public static int getLocaleSupport(Locale locale) {
		LocaleRegistration localeRegistration = supportedLocales.get(locale);
		if (localeRegistration != null)
			return localeRegistration.getPercentage();

		return 0;
	}

	public static Locale getLocale() {
		if (selectedLocale == null)
			selectedLocale = PreferencesManager.PREFERENCES.ui.language;

		return selectedLocale;
	}

	public static String getLocaleString() {
		return getLocale().toString();
	}

	public static String getLangString() {
		return getLocaleString().split("_")[0].replace("iw", "he");
	}

	/**
	 * Test mode will make JVM crash with runtime exception if translation key is not found when requested
	 */
	public static void enterTestingMode() {
		isTestingEnvironment = true;
	}

	public static String t(String key, Object... parameters) {
		if (key == null)
			return null;

		if (rb.containsKey(key))
			return MessageFormat.format(rb.getString(key), parameters);
		else if (key.startsWith("blockly.") && key.endsWith(".tooltip"))
			return null;
		else if (isTestingEnvironment)
			throw new RuntimeException("Failed to load any translation for key: " + key);
		else if (key.startsWith("blockly.") || key.startsWith("trigger."))
			return null;
		else
			return key;
	}

	public static String t_en(String key, Object... parameters) {
		if (key == null)
			return null;

		if (rb_en.containsKey(key))
			return MessageFormat.format(rb_en.getString(key), parameters);
		else if (key.startsWith("blockly.") && key.endsWith(".tooltip"))
			return null;
		else if (isTestingEnvironment)
			throw new RuntimeException("Failed to load any translation for key: " + key);
		else if (key.startsWith("blockly.") || key.startsWith("trigger."))
			return null;
		else
			return key;
	}

	public static JLabel label(String key, Object... parameter) {
		return new JLabel(t(key, parameter));
	}

	public static JCheckBox checkbox(String key, Object... parameter) {
		return new JCheckBox(t(key, parameter));
	}

	public static JButton button(String key, Object... parameter) {
		return new JButton(t(key, parameter));
	}

	public static JRadioButton radiobutton(String key, Object... parameter) {
		return new JRadioButton(t(key, parameter));
	}

}
