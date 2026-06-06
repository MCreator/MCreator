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

public class ProxyUtils {
	private static final Logger LOGGER = LogManager.getLogger("Proxy utils");

	public static void applyProxies() {
		var proxySection = PreferencesManager.PREFERENCES.proxy;
		String type = proxySection.proxyType.get();

		if (proxySection.useSystemProxy.get()) {
			LOGGER.debug("Current proxy: System proxy settings");
			System.setProperty("java.net.useSystemProxies", "true");
		} else {
			LOGGER.debug("Current proxy: {}:{}:{}", type, proxySection.proxyHost.get(), proxySection.proxyPort.get());
			if (isHttpTypeProxy(type)) {
				System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
				System.setProperty(type + ".proxyHost", proxySection.proxyHost.get());
				System.setProperty(type + ".proxyPort", String.valueOf(proxySection.proxyPort));
			} else if (type.equals("socks")) {
				System.setProperty("socksProxyHost", proxySection.proxyHost.get());
				System.setProperty("socksProxyPort", proxySection.proxyPort.get().toString());
			}
		}

		setPasswordAndUser(type, proxySection);
	}

	public static boolean isHttpTypeProxy(String proxyType) {
		return proxyType.startsWith("http");
	}

	public static void setPasswordAndUser(String type, ProxySection proxySection) {
		if (!proxySection.proxyUser.get().isEmpty()) {
			if (type.equals("socks")) {
				System.setProperty("java.net.socks.username", proxySection.proxyUser.get());
				System.setProperty("java.net.socks.password", proxySection.proxyPassword.get());
			} else if (isHttpTypeProxy(type)) {
				System.setProperty(type + ".proxyUser", proxySection.proxyUser.get());
				System.setProperty(type + ".proxyPassword", proxySection.proxyPassword.get());
			}
		}
	}
}
