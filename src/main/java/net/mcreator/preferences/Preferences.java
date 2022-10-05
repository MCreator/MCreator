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

package net.mcreator.preferences;

import net.mcreator.io.OS;
import net.mcreator.preferences.entries.NumberEntry;
import net.mcreator.preferences.entries.StringEntry;
import net.mcreator.preferences.entries.PreferenceEntry;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.MCreatorTheme;

import java.awt.*;
import java.io.File;
import java.util.Locale;

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

	// UI
	public PreferenceEntry<Locale> language;
	public PreferenceEntry<Color> interfaceAccentColor;
	public StringEntry backgroundSource;
	public PreferenceEntry<Boolean> aaText;
	public StringEntry textAntialiasingType;
	public PreferenceEntry<Boolean> usemacOSMenuBar;
	public PreferenceEntry<Boolean> useNativeFileChooser;
	public PreferenceEntry<Boolean> expandSectionsByDefault;
	public PreferenceEntry<Boolean> use2DAcceleration;
	public PreferenceEntry<Boolean> autoReloadTabs;
	public PreferenceEntry<Boolean> remindOfUnsavedChanges;
	public PreferenceEntry<Boolean> discordRichPresenceEnable;

	// Notifications
	public PreferenceEntry<Boolean> openWhatsNextPage;
	public PreferenceEntry<Boolean> snapshotMessage;
	public PreferenceEntry<Boolean> checkAndNotifyForUpdates;
	public PreferenceEntry<Boolean> checkAndNotifyForPatches;
	public PreferenceEntry<Boolean> checkAndNotifyForPluginUpdates;

	// Backups

	public NumberEntry workspaceAutosaveInterval;
	public NumberEntry automatedBackupInterval;
	public NumberEntry numberOfBackupsToStore;
	public PreferenceEntry<Boolean> backupOnVersionSwitch;

	// Blockly
	public StringEntry blockRenderer;
	public PreferenceEntry<Boolean> useSmartSort;
	public PreferenceEntry<Boolean> enableComments;
	public PreferenceEntry<Boolean> enableCollapse;
	public PreferenceEntry<Boolean> enableTrashcan;
	public NumberEntry maxScale;
	public NumberEntry minScale;
	public NumberEntry scaleSpeed;
	public PreferenceEntry<Boolean> legacyFont;

	// IDE
	public StringEntry editorTheme;
	public NumberEntry fontSize;
	public PreferenceEntry<Boolean> autocomplete;
	public StringEntry autocompleteMode;
	public PreferenceEntry<Boolean> autocompleteDocWindow;
	public PreferenceEntry<Boolean> lineNumbers;
	public PreferenceEntry<Boolean> errorInfoEnable;

	// Gradle
	public PreferenceEntry<Boolean> compileOnSave;
	public PreferenceEntry<Boolean> passLangToMinecraft;
	public NumberEntry xms;
	public NumberEntry xmx;
	public PreferenceEntry<Boolean> offline;

	// Bedrock
	public PreferenceEntry<Boolean> silentReload;

	// Hidden
	public PreferenceEntry<WorkspacePreferenceEnums.WorkspaceIconSize> workspaceModElementIconSize;
	public PreferenceEntry<Boolean> fullScreen;
	public NumberEntry projectTreeSplitPos;
	public PreferenceEntry<Boolean> workspaceSortAscending;
	public PreferenceEntry<WorkspacePreferenceEnums.WorkspaceSortType> workspaceSortType;
	public PreferenceEntry<File> java_home;
	public PreferenceEntry<String> uiTheme;
	public PreferenceEntry<Boolean> enableJavaPlugins;

	public Preferences() {
		// UI
		language = register(new PreferenceEntry<>("language", L10N.DEFAULT_LOCALE, UI));
		interfaceAccentColor = register(new PreferenceEntry<>("interfaceAccentColor", MCreatorTheme.MAIN_TINT_DEFAULT, UI));
		backgroundSource = register(new StringEntry("backgroundSource", "All", UI,
				"All", "Current theme", "Custom", "None"));
		aaText = register(new PreferenceEntry<>("aaText", true, UI));
		textAntialiasingType = register(new StringEntry("textAntialiasingType", "All", UI,
				"on", "off", "gasp", "lcd", "lcd_hbgr", "lcd_vrgb", "lcd_vbgr"));
		usemacOSMenuBar = register(new PreferenceEntry<>("useMacOSMenuBar", true, UI));
		useNativeFileChooser = register(new PreferenceEntry<>("useNativeFileChooser", OS.getOS() == OS.WINDOWS, UI));
		expandSectionsByDefault = register(new PreferenceEntry<>("expandSectionsByDefault", false, UI));
		use2DAcceleration = register(new PreferenceEntry<>("use2DAcceleration", false, UI));
		autoReloadTabs = register(new PreferenceEntry<>("autoReloadTabs", true, UI));
		remindOfUnsavedChanges = register(new PreferenceEntry<>("remindOfUnsavedChanges", false, UI));
		discordRichPresenceEnable = register(new PreferenceEntry<>("discordRichPresenceEnable", true, UI));

		// Notifications
		openWhatsNextPage = register(new PreferenceEntry<>("openWhatsNextPage", true, NOTIFICATIONS));
		snapshotMessage = register(new PreferenceEntry<>("snapshotMessage", true, NOTIFICATIONS));
		checkAndNotifyForUpdates = register(new PreferenceEntry<>("checkAndNotifyForUpdates", true, NOTIFICATIONS));
		checkAndNotifyForPatches = register(new PreferenceEntry<>("checkAndNotifyForPatches", true, NOTIFICATIONS));
		checkAndNotifyForPluginUpdates = register(new PreferenceEntry<>("checkAndNotifyForPluginUpdates", false, NOTIFICATIONS));

		// Backups
		workspaceAutosaveInterval = register(new NumberEntry("workspaceAutosaveInterval", 30, BACKUPS, 10, 1800));
		automatedBackupInterval = register(new NumberEntry("automatedBackupInterval", 5, BACKUPS, 3, 120));
		numberOfBackupsToStore = register(new NumberEntry("numberOfBackupsToStore", 10, BACKUPS, 2, 20));
		backupOnVersionSwitch = register(new PreferenceEntry<>("backupOnVersionSwitch", true, BACKUPS));

		// Blockly
		blockRenderer = register(new StringEntry("blockRenderer", "Thrasos", BLOCKLY, "Geras", "Thrasos"));
		useSmartSort = register(new PreferenceEntry<>("useSmartSort", true, BLOCKLY));
		enableComments = register(new PreferenceEntry<>("enableComments", true, BLOCKLY));
		enableCollapse = register(new PreferenceEntry<>("enableCollapse", true, BLOCKLY));
		enableTrashcan = register(new PreferenceEntry<>("enableTrashcan", true, BLOCKLY));
		maxScale = register(new NumberEntry("maxScale", 100, BLOCKLY, 95, 200));
		minScale = register(new NumberEntry("minScale", 40, BLOCKLY, 20, 95));
		scaleSpeed = register(new NumberEntry("scaleSpeed", 105, BLOCKLY, 0, 200));
		legacyFont = register(new PreferenceEntry<>("legacyFont", false, BLOCKLY));

		// IDE
		editorTheme = register(new StringEntry("editorTheme", "MCreator", IDE, "MCreator", "Default", "Default-Alt",
				"Dark", "Eclipse", "Idea", "Monokai", "VS"));
		fontSize = register(new NumberEntry("fontSize", 12, IDE, 5, 48));
		autocomplete = register(new PreferenceEntry<>("autocomplete", true, IDE));
		autocompleteMode = register(new StringEntry("autocompleteMode", "Smart", IDE, "Manual", "Trigger on dot", "Smart"));
		autocompleteDocWindow = register(new PreferenceEntry<>("autocompleteDocWindow", true, IDE));
		lineNumbers = register(new PreferenceEntry<>("lineNumbers", true, IDE));
		errorInfoEnable = register(new PreferenceEntry<>("errorInfoEnable", true, IDE));

		// Gradle
		compileOnSave = register(new PreferenceEntry<>("compileOnSave", true, GRADLE));
		passLangToMinecraft = register(new PreferenceEntry<>("passLangToMinecraft", true, GRADLE));
		xms = register(new NumberEntry("xms", OS.getBundledJVMBits() == OS.BIT64 ? 625 : 512, GRADLE, 128, NumberEntry.MAX_RAM));
		xmx = register(new NumberEntry("xmx", OS.getBundledJVMBits() == OS.BIT64 ? 2048 : 1500, GRADLE, 128, NumberEntry.MAX_RAM));
		offline = register(new PreferenceEntry<>("offline", false, GRADLE));

		// Bedrock
		silentReload = register(new PreferenceEntry<>("silentReload", false, BEDROCK));

		// Hidden
		workspaceModElementIconSize = register(new PreferenceEntry<>("workspaceModElementIconSize", WorkspacePreferenceEnums.WorkspaceIconSize.TILES, HIDDEN));
		fullScreen = register(new PreferenceEntry<>("fullScreen", false, HIDDEN));
		projectTreeSplitPos = register(new NumberEntry("projectTreeSplitPos", 0, HIDDEN));
		workspaceSortAscending = register(new PreferenceEntry<>("workspaceSortAscending", false, HIDDEN));
		workspaceSortType = register(new PreferenceEntry<>("workspaceSortType", WorkspacePreferenceEnums.WorkspaceSortType.CREATED, HIDDEN));
		java_home = register(new PreferenceEntry<>("java_home", null, HIDDEN));
		uiTheme = register(new PreferenceEntry<>("uiTheme", "default_dark", HIDDEN));
		enableJavaPlugins = register(new PreferenceEntry<>("fullScreen", false, HIDDEN));
	}

	private static <T, S extends PreferenceEntry<T>> S register(S entry) {
		PreferencesManager.register("mcreator", entry);
		return entry;
	}
}
