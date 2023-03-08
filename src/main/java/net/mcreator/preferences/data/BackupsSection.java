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
import net.mcreator.preferences.entries.IntegerEntry;

public class BackupsSection {

	public IntegerEntry workspaceAutosaveInterval;
	public IntegerEntry automatedBackupInterval;
	public IntegerEntry numberOfBackupsToStore;
	public BooleanEntry backupOnVersionSwitch;

	BackupsSection() {
		workspaceAutosaveInterval = PreferencesData.register(
				new IntegerEntry("workspaceAutosaveInterval", 30, PreferencesData.BACKUPS, 10, 2000));
		automatedBackupInterval = PreferencesData.register(
				new IntegerEntry("automatedBackupInterval", 5, PreferencesData.BACKUPS, 3, 120));
		numberOfBackupsToStore = PreferencesData.register(
				new IntegerEntry("numberOfBackupsToStore", 10, PreferencesData.BACKUPS, 2, 20));
		backupOnVersionSwitch = PreferencesData.register(
				new BooleanEntry("backupOnVersionSwitch", true, PreferencesData.BACKUPS));
	}

}
