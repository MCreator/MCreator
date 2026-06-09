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

		switch (type) {
		case "systemproxy" -> {
			LOG.debug("Using system proxy");
			properties.setProperty("java.net.useSystemProxies", "true");
			return properties;
		}
		case "http" -> {
			// this must be empty, or the http proxy user and http proxy password will be ignored
			properties.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
			properties.setProperty("http.proxyHost", proxySection.proxyHost.get());
			properties.setProperty("http.proxyPort", String.valueOf(proxySection.proxyPort.get()));
			properties.setProperty("https.proxyHost", proxySection.proxyHost.get());
			properties.setProperty("https.proxyPort", String.valueOf(proxySection.proxyPort.get()));
			// note that these properties is not supported by JDK itself, but is used quite broadly in libraries
			if (!proxySection.proxyUser.get().isEmpty() && !proxySection.proxyPassword.get().isEmpty()) {
				properties.setProperty("http.proxyUser", proxySection.proxyUser.get());
				properties.setProperty("http.proxyPassword", proxySection.proxyPassword.get());
				properties.setProperty("https.proxyUser", proxySection.proxyUser.get());
				properties.setProperty("https.proxyPassword", proxySection.proxyPassword.get());
			}
			if (!proxySection.nonProxyHosts.get().isEmpty()) {
				properties.setProperty("http.nonProxyHosts", proxySection.nonProxyHosts.get());
			}
		}
		case "socks5" -> {
			properties.setProperty("socksProxyHost", proxySection.proxyHost.get());
			properties.setProperty("socksProxyPort", String.valueOf(proxySection.proxyPort.get()));
			// note that these properties is not supported by JDK itself, but is used quite broadly in libraries
			// these properties can't effect gradle download task, but ensure that some libraries can get.
			if (!proxySection.proxyUser.get().isEmpty() && !proxySection.proxyPassword.get().isEmpty()) {
				properties.setProperty("java.net.socks.username", proxySection.proxyUser.get());
				properties.setProperty("java.net.socks.password", proxySection.proxyPassword.get());
			}
		}
		case "socks4" -> {
			properties.setProperty("socksProxyVersion", "4");
			properties.setProperty("socksProxyHost", proxySection.proxyHost.get());
			properties.setProperty("socksProxyPort", String.valueOf(proxySection.proxyPort.get()));
			// note that these properties is not supported by JDK itself, but is used quite broadly in libraries
			// these properties can't effect gradle download task, but ensure that some libraries can get.
			if (!proxySection.proxyUser.get().isEmpty() && !proxySection.proxyPassword.get().isEmpty()) {
				properties.setProperty("java.net.socks.username", proxySection.proxyUser.get());
				properties.setProperty("java.net.socks.password", proxySection.proxyPassword.get());
			}
		}
		}

		LOG.debug("Using proxy: {}:{}:{}", type, proxySection.proxyHost.get(), proxySection.proxyPort.get());

		return properties;
	}
}
