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
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class L10N {

	private static final Logger LOG = LogManager.getLogger("L10N");

	private static ResourceBundle rb;
	private static ResourceBundle rb_en;

	private static Set<Locale> supportedLocales = null;

	private static boolean isTestingEnvironment = false;

	public static Set<Locale> getSupportedLocales() {
		if (supportedLocales == null) { // lazy-load supported locales
			Set<String> localeFiles = PluginLoader.INSTANCE.getResourcesInPackage("lang");
			supportedLocales = localeFiles.stream().map(FilenameUtils::getBaseName).filter(e -> e.contains("_"))
					.map(e -> e.split("_")).map(e -> new Locale(e[1], e[2])).collect(Collectors.toSet());
			supportedLocales.add(new Locale("en", "US"));
		}

		return supportedLocales;
	}

	public static Locale getLocale() {
		return PreferencesManager.PREFERENCES.ui.language;
	}

	public static String getLocaleString() {
		return getLocale().toString();
	}

	public static String getLangString() {
		return getLocaleString().split("_")[0];
	}

	public static void initTranslations() {
		rb = ResourceBundle.getBundle("lang/texts", getLocale(), PluginLoader.INSTANCE, new UTF8Control());
		rb_en = ResourceBundle
				.getBundle("lang/texts", new Locale("en", "US"), PluginLoader.INSTANCE, new UTF8Control());
		Locale.setDefault(getLocale());
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

	private static class UTF8Control extends ResourceBundle.Control {

		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
				boolean reload) throws IOException {
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, "properties");

			List<ResourceBundle> resourceBundles = new ArrayList<>();

			Collections.list(loader.getResources(resourceName)).forEach(url -> {
				try {
					URLConnection connection = url.openConnection();
					connection.setUseCaches(!reload);
					try (InputStream stream = connection.getInputStream()) {
						resourceBundles
								.add(new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8)));
					}
				} catch (IOException e) {
					LOG.warn("Failed to load localization", e);
				}
			});

			return new MultiResourceBundle(resourceBundles);
		}

	}

	private static class MultiResourceBundle extends ResourceBundle {

		private final List<ResourceBundle> delegates;

		public MultiResourceBundle(List<ResourceBundle> resourceBundles) {
			this.delegates = resourceBundles == null ? new ArrayList<>() : resourceBundles;
		}

		@Override protected Object handleGetObject(@NotNull String key) {
			Optional<Object> firstPropertyValue = this.delegates.stream()
					.filter(delegate -> delegate != null && delegate.containsKey(key))
					.map(delegate -> delegate.getObject(key)).findFirst();

			return firstPropertyValue.orElse(null);
		}

		@Override @NotNull public Enumeration<String> getKeys() {
			List<String> keys = this.delegates.stream().filter(Objects::nonNull)
					.flatMap(delegate -> Collections.list(delegate.getKeys()).stream()).collect(Collectors.toList());

			return Collections.enumeration(keys);
		}
	}

}
