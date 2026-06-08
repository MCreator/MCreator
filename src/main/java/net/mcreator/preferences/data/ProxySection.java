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

package net.mcreator.preferences.data;

import net.mcreator.preferences.PreferencesSection;
import net.mcreator.preferences.entries.IntegerEntry;
import net.mcreator.preferences.entries.StringEntry;

public class ProxySection extends PreferencesSection {

	public final StringEntry proxyType;
	public final StringEntry proxyHost;
	public final IntegerEntry proxyPort;
	public final StringEntry proxyUser;
	public final StringEntry proxyPassword;

	ProxySection(String preferencesIdentifier) {
		super(preferencesIdentifier);

		proxyType = addEntry(new StringEntry("proxyType", "none", "none", "http", "https", "socks", "systemproxy"));
		proxyHost = addEntry(new StringEntry("proxyHost", "", true));
		proxyPort = addEntry(new IntegerEntry("proxyPort", 0, 0, 65536));
		proxyUser = addEntry(new StringEntry("proxyUser", "", true));
		proxyPassword = addEntry(new StringEntry("proxyPassword", "", true));
	}

	@Override public String getSectionKey() {
		return "proxy";
	}
}
