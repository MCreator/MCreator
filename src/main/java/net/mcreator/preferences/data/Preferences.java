/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.PreferenceEntry;

public class Preferences {
	// Sections
	public static final String UI = "ui";
	public static final String BACKUPS = "backups";
	public static final String BLOCKLY = "blockly";
	public static final String IDE = "ide";
	public static final String GRADLE = "gradle";
	public static final String BEDROCK = "bedrock";
	public static final String NOTIFICATIONS = "notifications";
	public static final String HIDDEN = "hidden";

	public UI ui;
	public Notifications notifications;
	public Backups backups;
	public Blockly blockly;
	public IDE ide;
	public Gradle gradle;
	public Bedrock bedrock;
	public Hidden hidden;

	public Preferences() {
		ui = new UI();
		notifications = new Notifications();
		backups = new Backups();
		ide = new IDE();
		gradle = new Gradle();
		bedrock = new Bedrock();
		hidden = new Hidden();
	}

	static <T, S extends PreferenceEntry<T>> S register(S entry) {
		PreferencesManager.register("mcreator", entry);
		return entry;
	}

	public static class Bedrock {
		public BooleanEntry silentReload;

		public Bedrock() {
			silentReload = register(new BooleanEntry("silentReload", false, BEDROCK));
		}
	}

}
