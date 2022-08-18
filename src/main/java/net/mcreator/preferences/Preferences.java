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
import net.mcreator.preferences.entry.NumberEntry;
import net.mcreator.preferences.entry.StringEntry;
import net.mcreator.preferences.entry.PreferenceEntry;
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
	public PreferenceEntry<String> backgroundSource;
	public PreferenceEntry<Boolean> aaText;
	public PreferenceEntry<String> textAntialiasingType;
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

	public PreferenceEntry<Double> workspaceAutosaveInterval;
	public PreferenceEntry<Double> automatedBackupInterval;
	public PreferenceEntry<Double> numberOfBackupsToStore;
	public PreferenceEntry<Boolean> backupOnVersionSwitch;

	// Blockly
	public PreferenceEntry<String> blockRenderer;
	public PreferenceEntry<Boolean> useSmartSort;
	public PreferenceEntry<Boolean> enableComments;
	public PreferenceEntry<Boolean> enableCollapse;
	public PreferenceEntry<Boolean> enableTrashcan;
	public PreferenceEntry<Double> maxScale;
	public PreferenceEntry<Double> minScale;
	public PreferenceEntry<Double> scaleSpeed;
	public PreferenceEntry<Boolean> legacyFont;
	
	// IDE
	public PreferenceEntry<String> editorTheme;
	public PreferenceEntry<Double> fontSize;
	public PreferenceEntry<Boolean> autocomplete;
	public PreferenceEntry<String> autocompleteMode;
	public PreferenceEntry<Boolean> autocompleteDocWindow;
	public PreferenceEntry<Boolean> lineNumbers;
	public PreferenceEntry<Boolean> errorInfoEnable;

	// Gradle
	public PreferenceEntry<Boolean> compileOnSave;
	public PreferenceEntry<Boolean> passLangToMinecraft;
	public PreferenceEntry<Double> xms;
	public PreferenceEntry<Double> xmx;
	public PreferenceEntry<Boolean> offline;

	// Bedrock
	public PreferenceEntry<Boolean> silentReload;

	// Hidden
	public PreferenceEntry<WorkspacePreferenceEnums.WorkspaceIconSize> workspaceModElementIconSize;
	public PreferenceEntry<Boolean> fullScreen;
	public PreferenceEntry<Double> projectTreeSplitPos;
	public PreferenceEntry<Boolean> workspaceSortAscending;
	public PreferenceEntry<WorkspacePreferenceEnums.WorkspaceSortType> workspaceSortType;
	public PreferenceEntry<File> java_home;
	public PreferenceEntry<String> uiTheme;
	public PreferenceEntry<Boolean> enableJavaPlugins;

	public Preferences() {
		// UI
		language = PreferencesManager.register(new PreferenceEntry<>("language", L10N.DEFAULT_LOCALE, UI));
		interfaceAccentColor = PreferencesManager.register(new PreferenceEntry<>("interfaceAccentColor", MCreatorTheme.MAIN_TINT_DEFAULT, UI));
		backgroundSource = PreferencesManager.register(new StringEntry("backgroundSource", "All", UI,
				"All", "Current theme", "Custom", "None"));
		aaText = PreferencesManager.register(new PreferenceEntry<>("aaText", true, UI));
		textAntialiasingType = PreferencesManager.register(new StringEntry("textAntialiasingType", "All", UI,
				"on", "off", "gasp", "lcd", "lcd_hbgr", "lcd_vrgb", "lcd_vbgr"));
		usemacOSMenuBar = PreferencesManager.register(new PreferenceEntry<>("useMacOSMenuBar", true, UI));
		useNativeFileChooser = PreferencesManager.register(new PreferenceEntry<>("useNativeFileChooser", OS.getOS() == OS.WINDOWS, UI));
		expandSectionsByDefault = PreferencesManager.register(new PreferenceEntry<>("expandSectionsByDefault", false, UI));
		use2DAcceleration = PreferencesManager.register(new PreferenceEntry<>("use2DAcceleration", false, UI));
		autoReloadTabs = PreferencesManager.register(new PreferenceEntry<>("autoReloadTabs", true, UI));
		remindOfUnsavedChanges = PreferencesManager.register(new PreferenceEntry<>("remindOfUnsavedChanges", false, UI));
		discordRichPresenceEnable = PreferencesManager.register(new PreferenceEntry<>("discordRichPresenceEnable", true, UI));

		// Notifications
		openWhatsNextPage = PreferencesManager.register(new PreferenceEntry<>("openWhatsNextPage", true, NOTIFICATIONS));
		snapshotMessage = PreferencesManager.register(new PreferenceEntry<>("snapshotMessage", true, NOTIFICATIONS));
		checkAndNotifyForUpdates = PreferencesManager.register(new PreferenceEntry<>("checkAndNotifyForUpdates", true, NOTIFICATIONS));
		checkAndNotifyForPatches = PreferencesManager.register(new PreferenceEntry<>("checkAndNotifyForPatches", true, NOTIFICATIONS));
		checkAndNotifyForPluginUpdates = PreferencesManager.register(new PreferenceEntry<>("checkAndNotifyForPluginUpdates", false, NOTIFICATIONS));

		// Backups
		workspaceAutosaveInterval = PreferencesManager.register(new NumberEntry("workspaceAutosaveInterval", 30, BACKUPS, 10, 1800));
		automatedBackupInterval = PreferencesManager.register(new NumberEntry("automatedBackupInterval", 5, BACKUPS, 3, 120));
		numberOfBackupsToStore = PreferencesManager.register(new NumberEntry("numberOfBackupsToStore", 10, BACKUPS, 2, 20));
		backupOnVersionSwitch = PreferencesManager.register(new PreferenceEntry<>("backupOnVersionSwitch", true, BACKUPS));

		// Blockly
		blockRenderer = PreferencesManager.register(new StringEntry("blockRenderer", "Thrasos", BLOCKLY, "Geras", "Thrasos"));
		useSmartSort = PreferencesManager.register(new PreferenceEntry<>("useSmartSort", true, BLOCKLY));
		enableComments = PreferencesManager.register(new PreferenceEntry<>("enableComments", true, BLOCKLY));
		enableCollapse = PreferencesManager.register(new PreferenceEntry<>("enableCollapse", true, BLOCKLY));
		enableTrashcan = PreferencesManager.register(new PreferenceEntry<>("enableTrashcan", true, BLOCKLY));
		maxScale = PreferencesManager.register(new NumberEntry("maxScale", 100, BLOCKLY, 95, 200));
		minScale = PreferencesManager.register(new NumberEntry("minScale", 40, BLOCKLY, 20, 95));
		scaleSpeed = PreferencesManager.register(new NumberEntry("scaleSpeed", 105, BLOCKLY, 0, 200));
		legacyFont = PreferencesManager.register(new PreferenceEntry<>("legacyFont", false, BLOCKLY));

		// IDE
		editorTheme = PreferencesManager.register(new StringEntry("editorTheme", "Thrasos", IDE, "Geras", "Thrasos"));
		fontSize = PreferencesManager.register(new NumberEntry("fontSize", 12, IDE, 5, 48));
		autocomplete = PreferencesManager.register(new PreferenceEntry<>("autocomplete", true, IDE));
		autocompleteMode = PreferencesManager.register(new StringEntry("autocompleteMode", "Smart", IDE, "Manual", "Trigger on dot", "Smart"));
		autocompleteDocWindow = PreferencesManager.register(new PreferenceEntry<>("autocompleteDocWindow", true, IDE));
		lineNumbers = PreferencesManager.register(new PreferenceEntry<>("lineNumbers", true, IDE));
		errorInfoEnable = PreferencesManager.register(new PreferenceEntry<>("errorInfoEnable", true, IDE));

		// Gradle
		compileOnSave = PreferencesManager.register(new PreferenceEntry<>("compileOnSave", true, GRADLE));
		passLangToMinecraft = PreferencesManager.register(new PreferenceEntry<>("passLangToMinecraft", true, GRADLE));
		xms = PreferencesManager.register(new NumberEntry("xms", OS.getBundledJVMBits() == OS.BIT64 ? 625 : 512, GRADLE, 128, NumberEntry.MAX_RAM));
		xmx = PreferencesManager.register(new NumberEntry("xmx", OS.getBundledJVMBits() == OS.BIT64 ? 2048 : 1500, GRADLE, 128, NumberEntry.MAX_RAM));
		offline = PreferencesManager.register(new PreferenceEntry<>("offline", false, GRADLE));

		// Bedrock
		silentReload = PreferencesManager.register(new PreferenceEntry<>("silentReload", false, BEDROCK));

		// Hidden
		workspaceModElementIconSize = PreferencesManager.register(new PreferenceEntry<>("workspaceModElementIconSize", WorkspacePreferenceEnums.WorkspaceIconSize.TILES, HIDDEN));
		fullScreen = PreferencesManager.register(new PreferenceEntry<>("fullScreen", false, HIDDEN));
		projectTreeSplitPos = PreferencesManager.register(new NumberEntry("projectTreeSplitPos", 0, HIDDEN));
		workspaceSortAscending = PreferencesManager.register(new PreferenceEntry<>("workspaceSortAscending", false, HIDDEN));
		workspaceSortType = PreferencesManager.register(new PreferenceEntry<>("workspaceSortType", WorkspacePreferenceEnums.WorkspaceSortType.CREATED, HIDDEN));
		java_home = PreferencesManager.register(new PreferenceEntry<>("java_home", null, HIDDEN));
		uiTheme = PreferencesManager.register(new PreferenceEntry<>("uiTheme", "default_dark", HIDDEN));
		enableJavaPlugins = PreferencesManager.register(new PreferenceEntry<>("fullScreen", false, HIDDEN));

	}
}
