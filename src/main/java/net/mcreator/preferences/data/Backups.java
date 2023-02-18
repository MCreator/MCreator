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

import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.NumberEntry;

public class Backups {
	public NumberEntry workspaceAutosaveInterval;
	public NumberEntry automatedBackupInterval;
	public NumberEntry numberOfBackupsToStore;
	public BooleanEntry backupOnVersionSwitch;

	public Backups() {
		workspaceAutosaveInterval = Preferences.register(
				new NumberEntry("workspaceAutosaveInterval", 30, Preferences.BACKUPS, 10, 2000));
		automatedBackupInterval = Preferences.register(
				new NumberEntry("automatedBackupInterval", 5, Preferences.BACKUPS, 3, 120));
		numberOfBackupsToStore = Preferences.register(
				new NumberEntry("numberOfBackupsToStore", 10, Preferences.BACKUPS, 2, 20));
		backupOnVersionSwitch = Preferences.register(
				new BooleanEntry("backupOnVersionSwitch", true, Preferences.BACKUPS));
	}
}
