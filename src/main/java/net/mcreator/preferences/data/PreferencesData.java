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
import net.mcreator.preferences.entries.PreferenceEntry;

public class PreferencesData {
	// Sections
	public static final String UI = "ui";
	public static final String BACKUPS = "backups";
	public static final String BLOCKLY = "blockly";
	public static final String IDE = "ide";
	public static final String GRADLE = "gradle";
	public static final String BEDROCK = "bedrock";
	public static final String NOTIFICATIONS = "notifications";
	public static final String HIDDEN = "hidden";

	public UISection ui;
	public NotificationsSection notifications;
	public BackupsSection backups;
	public BlocklySection blockly;
	public IDESection ide;
	public GradleSection gradle;
	public BedrockSection bedrock;
	public HiddenSection hidden;

	public PreferencesData() {
		ui = new UISection();
		notifications = new NotificationsSection();
		backups = new BackupsSection();
		blockly = new BlocklySection();
		ide = new IDESection();
		gradle = new GradleSection();
		bedrock = new BedrockSection();
		hidden = new HiddenSection();
	}

	static <T, S extends PreferenceEntry<T>> S register(S entry) {
		PreferencesManager.register("mcreator", entry);
		return entry;
	}

}
