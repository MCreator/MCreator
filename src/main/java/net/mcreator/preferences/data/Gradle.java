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

import net.mcreator.io.OS;
import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.NumberEntry;

public class Gradle {
	public BooleanEntry compileOnSave;
	public BooleanEntry passLangToMinecraft;
	public NumberEntry xms;
	public NumberEntry xmx;
	public BooleanEntry offline;

	public Gradle() {
		compileOnSave = Preferences.register(new BooleanEntry("compileOnSave", true, Preferences.GRADLE));
		passLangToMinecraft = Preferences.register(new BooleanEntry("passLangToMinecraft", true, Preferences.GRADLE));
		xms = Preferences.register(
				new NumberEntry("xms", OS.getBundledJVMBits() == OS.BIT64 ? 625 : 512, Preferences.GRADLE, 128,
						NumberEntry.MAX_RAM));
		xmx = Preferences.register(
				new NumberEntry("xmx", OS.getBundledJVMBits() == OS.BIT64 ? 2048 : 1500, Preferences.GRADLE, 128,
						NumberEntry.MAX_RAM));
		offline = Preferences.register(new BooleanEntry("offline", false, Preferences.GRADLE));
	}
}
