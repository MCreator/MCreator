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
import net.mcreator.ui.component.TechnicalButton;
import net.mcreator.ui.dialogs.workspace.GeneratorSelector;
import net.mcreator.ui.help.HelpLoader;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.locale.LocaleRegistration;
import net.mcreator.util.locale.UTF8Control;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class L10N {

	private static final Logger LOG = LogManager.getLogger("L10N");

	public static final Locale DEFAULT_LOCALE = Locale.of("en", "US");

	private static final Pattern NEWLINES = Pattern.compile("[\r\n]+");

	private static ResourceBundle rb;
	private static ResourceBundle rb_en;

	private static Map<Locale, LocaleRegistration> supportedLocales;

	private static Locale osLocale = Locale.getDefault();

	private static Locale selectedLocale = null;

	public static void initTranslations() {
		initLocalesImpl();

		// Clear selectedLocale cache
		selectedLocale = null;

		if (supportedLocales.containsKey(getLocale())) {
			rb = supportedLocales.get(getLocale()).resourceBundle();
		} else {
			LOG.warn("Locale {} is not supported. Falling back to default locale.", getLocale());

			rb = supportedLocales.get(DEFAULT_LOCALE).resourceBundle();
		}

		osLocale = Locale.getDefault();
		LOG.info("Setting default locale to: {}; OS locale: {}", getLocale(), osLocale);
		Locale.setDefault(getLocale());
		JComponent.setDefaultLocale(getLocale());

		// UIDefaults tables cache the default locale at construction time, and LaF is installed before
		// this method is called, so we need to update their locale for UIManager.getString(...) lookups
		UIManager.getDefaults().setDefaultLocale(getLocale());
		UIManager.getLookAndFeelDefaults().setDefaultLocale(getLocale());
	}

	private static void initLocalesImpl() {
		if (rb_en != null) // check if locales are already loaded
			return;

		rb_en = ResourceBundle.getBundle("lang/texts", Locale.ROOT, PluginLoader.INSTANCE, new UTF8Control());

		double countAll = Collections.list(rb_en.getKeys()).size();

		Set<String> localeFiles = PluginLoader.INSTANCE.getResourcesInPackage("lang");
		supportedLocales = localeFiles.stream().map(FilenameUtilsPatched::getBaseName).filter(e -> e.contains("_"))
				.map(e -> e.split("_")).map(e -> Locale.of(e[1], e[2])).collect(Collectors.toMap(key -> key, value -> {
					ResourceBundle rb = ResourceBundle.getBundle("lang/texts", value, PluginLoader.INSTANCE,
							new UTF8Control());
					return new LocaleRegistration(rb,
							(int) Math.ceil(Collections.list(rb.getKeys()).size() / countAll * 100d),
							HelpLoader.getCoverageForLocale(value));
				}));

		supportedLocales.put(DEFAULT_LOCALE, new LocaleRegistration(rb_en, 100, 100));
	}

	public static Set<Locale> getSupportedLocales() {
		return supportedLocales.keySet();
	}

	public static int getUITextsLocaleSupport(Locale locale) {
		LocaleRegistration localeRegistration = supportedLocales.get(locale);
		if (localeRegistration != null)
			return localeRegistration.uiTextsPercentage();

		return 0;
	}

	public static int getHelpTipsSupport(Locale locale) {
		LocaleRegistration localeRegistration = supportedLocales.get(locale);
		if (localeRegistration != null)
			return localeRegistration.helpTipsPercentage();

		return 0;
	}

	public static Locale getLocale() {
		if (selectedLocale == null)
			selectedLocale = PreferencesManager.PREFERENCES.ui.language.get();

		return selectedLocale;
	}

	public static Locale getOSLocale() {
		return osLocale;
	}

	public static String getLocaleString() {
		return getLocale().toString();
	}

	public static String getBlocklyLangName() {
		Locale locale = getLocale();

		if (Locale.of("zh", "TW").equals(locale)) // Chinese Traditional
			return "zh-hant";
		else if (Locale.of("zh", "CN").equals(locale)) // Chinese Simplified
			return "zh-hans";

		return getLocaleString().split("_")[0].replace("iw", "he").replace("no", "nb");
	}

	public static String t(String key, Object... parameters) {
		return t_impl(rb, key, parameters);
	}

	public static String t_en(String key, Object... parameters) {
		return t_impl(rb_en, key, parameters);
	}

	private static String t_impl(ResourceBundle resourceBundle, String key, Object... parameters) {
		if (key == null)
			return null;

		if (resourceBundle.containsKey(key)) {
			String value = hardenHTMLString(resourceBundle.getString(key), rb_en.containsKey(key) ? rb_en.getString(key) : null);
			return MessageFormat.format(value, parameters);
		} else if (key.startsWith("blockly.") && (key.endsWith(".tooltip") || key.endsWith(".tip") || key.endsWith(
				".description"))) {
			return null;
		} else if (key.startsWith("blockly.") || key.startsWith("trigger.") || key.startsWith(
				GeneratorSelector.covpfx)) {
			LOG.warn("Missing translation for key: {} in locale: {}", key, getLocale());
			return null;
		} else {
			return key;
		}
	}

	/**
	 * <p>Hardens localized strings against common translation mistakes that break HTML rendering in Swing components:
	 * leading whitespace before the opening html tag, html tag present in the source string but missing in the
	 * localized one, and literal newline characters inside HTML strings. Newlines carry no meaning in rendered HTML,
	 * but cause components such as JOptionPane to split the message into multiple parts, in which case only the
	 * first part is rendered as HTML.</p>
	 *
	 * @param value       The localized string to harden
	 * @param sourceValue The source (English) string for the same key, or null if not known
	 * @return Localized string safe to use in Swing components
	 */
	private static String hardenHTMLString(String value, @Nullable String sourceValue) {
		String stripped = value.stripLeading();
		boolean isHTML = stripped.regionMatches(true, 0, "<html>", 0, 6);

		if (!isHTML && sourceValue != null && sourceValue.regionMatches(true, 0, "<html>", 0, 6)) {
			// Source string is HTML, so the localized string must be HTML too, otherwise
			// HTML tags contained in it would render as plain text
			stripped = "<html>" + stripped;
			isHTML = true;
		}

		return isHTML ? NEWLINES.matcher(stripped).replaceAll(" ") : value;
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

	public static TechnicalButton technicalbutton(String key, Object... parameter) {
		return new TechnicalButton(t(key, parameter));
	}

	public static JRadioButton radiobutton(String key, Object... parameter) {
		return new JRadioButton(t(key, parameter));
	}

	public static JToggleButton togglebutton(String key, Object... parameter) {
		return new JToggleButton(t(key, parameter));
	}

	public static JMenu menu(String key, Object... parameter) {
		return new JMenu(t(key, parameter));
	}

}
