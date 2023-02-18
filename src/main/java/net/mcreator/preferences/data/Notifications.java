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

public class Notifications {
	public BooleanEntry openWhatsNextPage;
	public BooleanEntry snapshotMessage;
	public BooleanEntry checkAndNotifyForUpdates;
	public BooleanEntry checkAndNotifyForPatches;
	public BooleanEntry checkAndNotifyForPluginUpdates;

	public Notifications() {
		openWhatsNextPage = Preferences.register(
				new BooleanEntry("openWhatsNextPage", true, Preferences.NOTIFICATIONS));
		snapshotMessage = Preferences.register(new BooleanEntry("snapshotMessage", true, Preferences.NOTIFICATIONS));
		checkAndNotifyForUpdates = Preferences.register(
				new BooleanEntry("checkAndNotifyForUpdates", true, Preferences.NOTIFICATIONS));
		checkAndNotifyForPatches = Preferences.register(
				new BooleanEntry("checkAndNotifyForPatches", true, Preferences.NOTIFICATIONS));
		checkAndNotifyForPluginUpdates = Preferences.register(
				new BooleanEntry("checkAndNotifyForPluginUpdates", false, Preferences.NOTIFICATIONS));
	}
}
