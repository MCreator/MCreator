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

import com.sun.management.OperatingSystemMXBean;
import net.mcreator.preferences.PreferencesSection;
import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.IntegerEntry;
import net.mcreator.preferences.entries.StringEntry;

import java.lang.management.ManagementFactory;

public class GradleSection extends PreferencesSection {

	public static final int MAX_RAM =
			(int) (((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize()
					/ 1048576) - 1024;

	public final BooleanEntry buildOnSave;
	public final BooleanEntry passLangToMinecraft;
	public final BooleanEntry enablePerformanceMonitor;
	public final IntegerEntry xmx;
	public final BooleanEntry offline;
	public final StringEntry proxyType;
	public final StringEntry proxyHost;
	public final IntegerEntry proxyPort;
	public final StringEntry proxyUser;
	public final StringEntry proxyPassword;
	public final BooleanEntry useSystemProxy;

	GradleSection(String preferencesIdentifier) {
		super(preferencesIdentifier);

		buildOnSave = addEntry(new BooleanEntry("buildOnSave", false));
		passLangToMinecraft = addEntry(new BooleanEntry("passLangToMinecraft", true));
		enablePerformanceMonitor = addEntry(new BooleanEntry("enablePerformanceMonitor", true));
		xmx = addEntry(new IntegerEntry("Xmx", Math.min(3072, MAX_RAM), 128, MAX_RAM));
		offline = addEntry(new BooleanEntry("offline", false));
		proxyType = addEntry(new StringEntry("proxyType", "none", "none", "http", "https", "socks"));
		proxyHost = addEntry(new StringEntry("proxyHost", "", true));
		proxyPort = addEntry(new IntegerEntry("proxyPort", 0));
		proxyUser = addEntry(new StringEntry("proxyUser", "", true));
		proxyPassword = addEntry(new StringEntry("proxyPassword", "", true));
		useSystemProxy = addEntry(new BooleanEntry("useSystemProxy", false));
	}

	@Override public String getSectionKey() {
		return "gradle";
	}

}
