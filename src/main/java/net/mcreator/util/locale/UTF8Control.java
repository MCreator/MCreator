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

package net.mcreator.util.locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UTF8Control extends ResourceBundle.Control {

	private static final Logger LOG = LogManager.getLogger(UTF8Control.class);

	@Override public List<Locale> getCandidateLocales(String baseName, Locale locale) {
		if (locale.getLanguage().equals("zh"))
			return Arrays.asList(locale, Locale.ROOT);

		return super.getCandidateLocales(baseName, locale);
	}

	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
			throws IOException {
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
