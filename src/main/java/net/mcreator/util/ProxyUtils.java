/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.util;

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.preferences.data.ProxySection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Properties;

public class ProxyUtils {

	private static final Logger LOG = LogManager.getLogger(ProxyUtils.class);

	@Nullable private static Properties cachedProxyProperties = null;

	public static void installProxyIfEnabled() {
		Properties proxyProperties = getProxyProperties();
		if (proxyProperties != null) {
			System.getProperties().putAll(proxyProperties);
		}
	}

	@Nullable public static Properties getProxyProperties() {
		if (cachedProxyProperties == null)
			cachedProxyProperties = buildProxyProperties();
		return cachedProxyProperties;
	}

	@Nullable private static Properties buildProxyProperties() {
		ProxySection proxySection = PreferencesManager.PREFERENCES.proxy;
		String type = proxySection.proxyType.get();

		if ("none".equals(type)) {
			return null;
		}

		Properties properties = new Properties();

		if (type.equals("systemproxy")) {
			LOG.debug("Using system proxy");
			properties.setProperty("java.net.useSystemProxies", "true");
			return properties;
		}

		if (isHttpTypeProxy(type)) {
			properties.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
			properties.setProperty(type + ".proxyHost", proxySection.proxyHost.get());
			properties.setProperty(type + ".proxyPort", String.valueOf(proxySection.proxyPort.get()));
		} else if (type.equals("socks")) {
			properties.setProperty("socksProxyHost", proxySection.proxyHost.get());
			properties.setProperty("socksProxyPort", String.valueOf(proxySection.proxyPort.get()));
		}

		LOG.debug("Using proxy: {}:{}:{}", type, proxySection.proxyHost.get(), proxySection.proxyPort.get());

		configurePasswordAndUserProperties(properties, type, proxySection.proxyUser.get(),
				proxySection.proxyPassword.get());

		return properties;
	}

	private static void configurePasswordAndUserProperties(Properties properties, String type, String proxyUser,
			String proxyPassword) {
		if (!proxyUser.isEmpty()) {
			if (type.equals("socks")) {
				properties.setProperty("java.net.socks.username", proxyUser);
				properties.setProperty("java.net.socks.password", proxyPassword);
			} else if (isHttpTypeProxy(type)) {
				properties.setProperty(type + ".proxyUser", proxyUser);
				properties.setProperty(type + ".proxyPassword", proxyPassword);
			}
		}
	}

	private static boolean isHttpTypeProxy(String proxyType) {
		return proxyType.startsWith("http");
	}

}
