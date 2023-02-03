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
import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.NumberEntry;
import net.mcreator.preferences.entries.StringEntry;
import net.mcreator.preferences.entries.PreferenceEntry;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.MCreatorTheme;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

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

	public UI ui;

	// Notifications
	public BooleanEntry openWhatsNextPage;
	public BooleanEntry snapshotMessage;
	public BooleanEntry checkAndNotifyForUpdates;
	public BooleanEntry checkAndNotifyForPatches;
	public BooleanEntry checkAndNotifyForPluginUpdates;

	// Backups

	public NumberEntry workspaceAutosaveInterval;
	public NumberEntry automatedBackupInterval;
	public NumberEntry numberOfBackupsToStore;
	public BooleanEntry backupOnVersionSwitch;

	// Blockly
	public StringEntry blockRenderer;
	public BooleanEntry useSmartSort;
	public BooleanEntry enableComments;
	public BooleanEntry enableCollapse;
	public BooleanEntry enableTrashcan;
	public NumberEntry maxScale;
	public NumberEntry minScale;
	public NumberEntry scaleSpeed;
	public BooleanEntry legacyFont;

	// IDE
	public StringEntry editorTheme;
	public NumberEntry fontSize;
	public BooleanEntry autocomplete;
	public StringEntry autocompleteMode;
	public BooleanEntry autocompleteDocWindow;
	public BooleanEntry lineNumbers;
	public BooleanEntry errorInfoEnable;

	// Gradle
	public BooleanEntry compileOnSave;
	public BooleanEntry passLangToMinecraft;
	public NumberEntry xms;
	public NumberEntry xmx;
	public BooleanEntry offline;

	// Bedrock
	public BooleanEntry silentReload;

	// Hidden
	public PreferenceEntry<WorkspacePreferenceEnums.IconSize> workspaceModElementIconSize;
	public BooleanEntry fullScreen;
	public NumberEntry projectTreeSplitPos;
	public BooleanEntry workspaceSortAscending;
	public PreferenceEntry<WorkspacePreferenceEnums.SortType> workspaceSortType;
	public PreferenceEntry<File> java_home;
	public PreferenceEntry<String> uiTheme;
	public BooleanEntry enableJavaPlugins;

	public Preferences() {
		ui = new UI();
		ui.language = register(new PreferenceEntry<>("language", L10N.DEFAULT_LOCALE, UI) {
			@Override public JComponent getComponent(Window parent, Consumer<EventObject> fct) {
				List<Locale> locales = new ArrayList<>(L10N.getSupportedLocales());
				locales.sort((a, b) -> {
					int sa = L10N.getUITextsLocaleSupport(a) + L10N.getHelpTipsSupport(a);
					int sb = L10N.getUITextsLocaleSupport(b) + L10N.getHelpTipsSupport(b);
					if (sa == sb)
						return a.getDisplayName().compareTo(b.getDisplayName());

					return sb - sa;
				});
				JComboBox<Locale> box = new JComboBox<>(locales.toArray(new Locale[0]));
				box.setRenderer(new PreferencesDialog.LocaleListRenderer());
				box.setSelectedItem(this.value);
				box.addActionListener(fct::accept);
				return box;
			}
		});
		ui.interfaceAccentColor = register(new PreferenceEntry<>("interfaceAccentColor", MCreatorTheme.MAIN_TINT_DEFAULT, UI));
		ui.backgroundSource = register(new StringEntry("backgroundSource", "All", UI,
				"All", "Current theme", "Custom", "None"));
		ui.aaText = register(new BooleanEntry("aaText", true, UI));
		ui.textAntialiasingType = register(new StringEntry("textAntialiasingType", "All", UI,
				"on", "off", "gasp", "lcd", "lcd_hbgr", "lcd_vrgb", "lcd_vbgr"));
		ui.usemacOSMenuBar = register(new BooleanEntry("useMacOSMenuBar", true, UI));
		ui.useNativeFileChooser = register(new BooleanEntry("useNativeFileChooser", OS.getOS() == OS.WINDOWS, UI));
		ui.expandSectionsByDefault = register(new BooleanEntry("expandSectionsByDefault", false, UI));
		ui.use2DAcceleration = register(new BooleanEntry("use2DAcceleration", false, UI));
		ui.autoReloadTabs = register(new BooleanEntry("autoReloadTabs", true, UI));
		ui.remindOfUnsavedChanges = register(new BooleanEntry("remindOfUnsavedChanges", false, UI));
		ui.discordRichPresenceEnable = register(new BooleanEntry("discordRichPresenceEnable", true, UI));

		// Notifications
		openWhatsNextPage = register(new BooleanEntry("openWhatsNextPage", true, NOTIFICATIONS));
		snapshotMessage = register(new BooleanEntry("snapshotMessage", true, NOTIFICATIONS));
		checkAndNotifyForUpdates = register(new BooleanEntry("checkAndNotifyForUpdates", true, NOTIFICATIONS));
		checkAndNotifyForPatches = register(new BooleanEntry("checkAndNotifyForPatches", true, NOTIFICATIONS));
		checkAndNotifyForPluginUpdates = register(new BooleanEntry("checkAndNotifyForPluginUpdates", false, NOTIFICATIONS));

		// Backups
		workspaceAutosaveInterval = register(new NumberEntry("workspaceAutosaveInterval", 30, BACKUPS, 10, 2000));
		automatedBackupInterval = register(new NumberEntry("automatedBackupInterval", 5, BACKUPS, 3, 120));
		numberOfBackupsToStore = register(new NumberEntry("numberOfBackupsToStore", 10, BACKUPS, 2, 20));
		backupOnVersionSwitch = register(new BooleanEntry("backupOnVersionSwitch", true, BACKUPS));

		// Blockly
		blockRenderer = register(new StringEntry("blockRenderer", "Thrasos", BLOCKLY, "Geras", "Thrasos"));
		useSmartSort = register(new BooleanEntry("useSmartSort", true, BLOCKLY));
		enableComments = register(new BooleanEntry("enableComments", true, BLOCKLY));
		enableCollapse = register(new BooleanEntry("enableCollapse", true, BLOCKLY));
		enableTrashcan = register(new BooleanEntry("enableTrashcan", true, BLOCKLY));
		maxScale = register(new NumberEntry("maxScale", 100, BLOCKLY, 95, 200));
		minScale = register(new NumberEntry("minScale", 40, BLOCKLY, 20, 95));
		scaleSpeed = register(new NumberEntry("scaleSpeed", 105, BLOCKLY, 0, 200));
		legacyFont = register(new BooleanEntry("legacyFont", false, BLOCKLY));

		// IDE
		editorTheme = register(new StringEntry("editorTheme", "MCreator", IDE, "MCreator", "Default", "Default-Alt",
				"Dark", "Eclipse", "Idea", "Monokai", "VS"));
		fontSize = register(new NumberEntry("fontSize", 12, IDE, 5, 48));
		autocomplete = register(new BooleanEntry("autocomplete", true, IDE));
		autocompleteMode = register(new StringEntry("autocompleteMode", "Smart", IDE, "Manual", "Trigger on dot", "Smart"));
		autocompleteDocWindow = register(new BooleanEntry("autocompleteDocWindow", true, IDE));
		lineNumbers = register(new BooleanEntry("lineNumbers", true, IDE));
		errorInfoEnable = register(new BooleanEntry("errorInfoEnable", true, IDE));

		// Gradle
		compileOnSave = register(new BooleanEntry("compileOnSave", true, GRADLE));
		passLangToMinecraft = register(new BooleanEntry("passLangToMinecraft", true, GRADLE));
		xms = register(new NumberEntry("xms", OS.getBundledJVMBits() == OS.BIT64 ? 625 : 512, GRADLE, 128, NumberEntry.MAX_RAM));
		xmx = register(new NumberEntry("xmx", OS.getBundledJVMBits() == OS.BIT64 ? 2048 : 1500, GRADLE, 128, NumberEntry.MAX_RAM));
		offline = register(new BooleanEntry("offline", false, GRADLE));

		// Bedrock
		silentReload = register(new BooleanEntry("silentReload", false, BEDROCK));

		// Hidden
		workspaceModElementIconSize = register(new PreferenceEntry<>("workspaceModElementIconSize", WorkspacePreferenceEnums.IconSize.TILES, HIDDEN));
		fullScreen = register(new BooleanEntry("fullScreen", false, HIDDEN));
		projectTreeSplitPos = register(new NumberEntry("projectTreeSplitPos", 0, HIDDEN));
		workspaceSortAscending = register(new BooleanEntry("workspaceSortAscending", false, HIDDEN));
		workspaceSortType = register(new PreferenceEntry<>("workspaceSortType", WorkspacePreferenceEnums.SortType.CREATED, HIDDEN));
		java_home = register(new PreferenceEntry<>("java_home", null, HIDDEN));
		uiTheme = register(new PreferenceEntry<>("uiTheme", "default_dark", HIDDEN));
		enableJavaPlugins = register(new BooleanEntry("fullScreen", false, HIDDEN));
	}

	private static <T, S extends PreferenceEntry<T>> S register(S entry) {
		PreferencesManager.register("mcreator", entry);
		return entry;
	}

	public static class UI {
		public PreferenceEntry<Locale> language;
		public PreferenceEntry<Color> interfaceAccentColor;
		public StringEntry backgroundSource;
		public BooleanEntry aaText;
		public StringEntry textAntialiasingType;
		public BooleanEntry usemacOSMenuBar;
		public BooleanEntry useNativeFileChooser;
		public BooleanEntry expandSectionsByDefault;
		public BooleanEntry use2DAcceleration;
		public BooleanEntry autoReloadTabs;
		public BooleanEntry remindOfUnsavedChanges;
		public BooleanEntry discordRichPresenceEnable;
	}
}
