/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.preferences;

import net.mcreator.io.OS;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.MCreatorTheme;

import java.awt.*;
import java.util.Locale;

public class GlobalPreferencesData {

	@GlobalPreferencesSection public BackupsSettings backups = new BackupsSettings();
	@GlobalPreferencesSection public UISettings ui = new UISettings();
	@GlobalPreferencesSection public NotificationSettings notifications = new NotificationSettings();
	@GlobalPreferencesSection public GradleSettings gradle = new GradleSettings();


	public static class UISettings {

		@GlobalPreferencesEntry public Color interfaceAccentColor = MCreatorTheme.MAIN_TINT_DEFAULT;

		@GlobalPreferencesEntry public Locale language = L10N.DEFAULT_LOCALE;

		@GlobalPreferencesEntry public boolean aatext = true;

		@GlobalPreferencesEntry(arrayData = { "on", "off", "gasp", "lcd", "lcd_hbgr", "lcd_vrgb", "lcd_vbgr" })
		public String textAntialiasingType = "on";

		@GlobalPreferencesEntry public boolean expandSectionsByDefault = false;
		@GlobalPreferencesEntry public boolean use2DAcceleration = false;
		@GlobalPreferencesEntry public boolean autoreloadTabs = true;
		@GlobalPreferencesEntry public boolean discordRichPresenceEnable = true;

	}

	public static class NotificationSettings {

		@GlobalPreferencesEntry public boolean openWhatsNextPage = true;
		@GlobalPreferencesEntry public boolean checkAndNotifyForUpdates = true;
		@GlobalPreferencesEntry public boolean checkAndNotifyForPatches = true;
		@GlobalPreferencesEntry public boolean checkAndNotifyForPluginUpdates = false;

	}

	public static class BackupsSettings {

		@GlobalPreferencesEntry(min = 10, max = 1800) public int workspaceAutosaveInterval = 30;
		@GlobalPreferencesEntry(min = 3, max = 120) public int automatedBackupInterval = 5;
		@GlobalPreferencesEntry(min = 2, max = 20) public int numberOfBackupsToStore = 10;
		@GlobalPreferencesEntry public boolean backupOnVersionSwitch = true;

	}

	public static class GradleSettings {

		@GlobalPreferencesEntry public boolean compileOnSave = true;
		@GlobalPreferencesEntry public boolean passLangToMinecraft = true;

		@GlobalPreferencesEntry(min = 128, meta = "max:maxram") public int xms =
				OS.getBundledJVMBits() == OS.BIT64 ? 625 : 512;

		@GlobalPreferencesEntry(min = 128, meta = "max:maxram") public int xmx =
				OS.getBundledJVMBits() == OS.BIT64 ? 2048 : 1500;

		@GlobalPreferencesEntry public boolean offline = false;
	}

}
