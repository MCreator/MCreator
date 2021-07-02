/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

import java.io.File;

public class PreferencesData {

	@PreferencesSection public BlocklySettings blockly = new BlocklySettings();
	@PreferencesSection public IDESettings ide = new IDESettings();
	@PreferencesSection public BedrockSettings bedrock = new BedrockSettings();

	public HiddenPreferences hidden = new HiddenPreferences();

	public static class BlocklySettings {

		@PreferencesEntry(arrayData = { "Geras", "Thrasos" }) public String blockRenderer = "Thrasos";
		@PreferencesEntry public boolean useSmartSort = true;
		@PreferencesEntry public boolean enableComments = true;
		@PreferencesEntry public boolean enableCollapse = true;
		@PreferencesEntry public boolean enableTrashcan = true;
		@PreferencesEntry(min = 95, max = 200) public int maxScale = 100;
		@PreferencesEntry(min = 20, max = 95) public int minScale = 40;
		@PreferencesEntry(min = 0, max = 200) public int scaleSpeed = 105;
		@PreferencesEntry public boolean legacyFont = false;

	}

	public static class IDESettings {

		@PreferencesEntry(arrayData = { "MCreator", "Default", "Default-Alt", "Dark", "Eclipse", "Idea", "Monokai",
				"VS" }) public String editorTheme = "MCreator";

		@PreferencesEntry(min = 5, max = 48) public int fontSize = 12;
		@PreferencesEntry public boolean autocomplete = true;
		@PreferencesEntry(arrayData = { "Manual", "Trigger on dot", "Smart" }) public String autocompleteMode = "Smart";
		@PreferencesEntry public boolean autocompleteDocWindow = true;
		@PreferencesEntry public boolean lineNumbers = true;
		@PreferencesEntry public boolean errorInfoEnable = true;

	}

	public static class BedrockSettings {

		@PreferencesEntry public boolean silentReload = false;

	}

	public static class HiddenPreferences {
		public WorkspaceIconSize workspaceModElementIconSize = WorkspaceIconSize.TILES;
		public boolean fullScreen = false;
		public int projectTreeSplitPos = 0;
		public boolean workspaceSortAscending = true;
		public WorkspaceSortType workspaceSortType = WorkspaceSortType.CREATED;
		public File java_home = null;
		public String uiTheme = "default_dark";
	}

	public enum WorkspaceSortType {
		NAME, CREATED, TYPE, LOADORDER
	}

	public enum WorkspaceIconSize {
		TILES, LARGE, MEDIUM, SMALL, LIST, DETAILS
	}

}
