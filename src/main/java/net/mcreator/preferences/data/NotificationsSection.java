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

public class NotificationsSection {

	public BooleanEntry openWhatsNextPage;
	public BooleanEntry snapshotMessage;
	public BooleanEntry checkAndNotifyForUpdates;
	public BooleanEntry checkAndNotifyForPatches;
	public BooleanEntry checkAndNotifyForPluginUpdates;

	NotificationsSection() {
		openWhatsNextPage = PreferencesData.register(
				new BooleanEntry("openWhatsNextPage", true, PreferencesData.NOTIFICATIONS));
		snapshotMessage = PreferencesData.register(new BooleanEntry("snapshotMessage", true, PreferencesData.NOTIFICATIONS));
		checkAndNotifyForUpdates = PreferencesData.register(
				new BooleanEntry("checkAndNotifyForUpdates", true, PreferencesData.NOTIFICATIONS));
		checkAndNotifyForPatches = PreferencesData.register(
				new BooleanEntry("checkAndNotifyForPatches", true, PreferencesData.NOTIFICATIONS));
		checkAndNotifyForPluginUpdates = PreferencesData.register(
				new BooleanEntry("checkAndNotifyForPluginUpdates", false, PreferencesData.NOTIFICATIONS));
	}

}
