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

import net.mcreator.preferences.PreferencesSection;
import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.IntegerEntry;

public class BackupsSection extends PreferencesSection {

	public IntegerEntry workspaceAutosaveInterval;
	public IntegerEntry automatedBackupInterval;
	public IntegerEntry numberOfBackupsToStore;
	public BooleanEntry backupOnVersionSwitch;

	public BackupsSection(String preferencesIdentifier) {
		super(preferencesIdentifier);

		workspaceAutosaveInterval = addEntry(new IntegerEntry("workspaceAutosaveInterval", 30, 10, 2000));
		automatedBackupInterval = addEntry(new IntegerEntry("automatedBackupInterval", 5, 3, 120));
		numberOfBackupsToStore = addEntry(new IntegerEntry("numberOfBackupsToStore", 10, 2, 20));
		backupOnVersionSwitch = addEntry(new BooleanEntry("backupOnVersionSwitch", true));
	}

	@Override public String getSectionKey() {
		return "backups";
	}

}
