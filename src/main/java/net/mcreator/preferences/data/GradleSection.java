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

import java.lang.management.ManagementFactory;

public class GradleSection extends PreferencesSection {

	public static final int MAX_RAM =
			(int) (((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize()
					/ 1048576) - 1024;

	public BooleanEntry buildOnSave;
	public BooleanEntry passLangToMinecraft;
	public IntegerEntry xms;
	public IntegerEntry xmx;
	public BooleanEntry offline;

	GradleSection(String preferencesIdentifier) {
		super(preferencesIdentifier);

		buildOnSave = addEntry(new BooleanEntry("buildOnSave", false));
		passLangToMinecraft = addEntry(new BooleanEntry("passLangToMinecraft", true));
		xms = addEntry(new IntegerEntry("Xms", Math.min(1024, MAX_RAM), 128, MAX_RAM));
		xmx = addEntry(new IntegerEntry("Xmx", Math.min(3072, MAX_RAM), 128, MAX_RAM));
		offline = addEntry(new BooleanEntry("offline", false));
	}

	@Override public String getSectionKey() {
		return "gradle";
	}

}
