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

public class PreferencesData {

	public static final String CORE_PREFERENCES_KEY = "core";

	public final UISection ui;
	public final NotificationsSection notifications;
	public final BackupsSection backups;
	public final BlocklySection blockly;
	public final IDESection ide;
	public final GradleSection gradle;
	public final BedrockSection bedrock;
	public final HiddenSection hidden;

	public PreferencesData() {
		ui = new UISection(CORE_PREFERENCES_KEY);
		notifications = new NotificationsSection(CORE_PREFERENCES_KEY);
		backups = new BackupsSection(CORE_PREFERENCES_KEY);
		blockly = new BlocklySection(CORE_PREFERENCES_KEY);
		ide = new IDESection(CORE_PREFERENCES_KEY);
		gradle = new GradleSection(CORE_PREFERENCES_KEY);
		bedrock = new BedrockSection(CORE_PREFERENCES_KEY);
		hidden = new HiddenSection(CORE_PREFERENCES_KEY);
	}

}
