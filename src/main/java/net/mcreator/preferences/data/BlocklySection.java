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
import net.mcreator.preferences.entries.StringEntry;

public class BlocklySection extends PreferencesSection {

	public StringEntry blockRenderer;
	public IntegerEntry colorSaturation;
	public IntegerEntry colorValue;
	public BooleanEntry useSmartSort;
	public StringEntry expandCategories;
	public BooleanEntry enableComments;
	public BooleanEntry enableCollapse;
	public BooleanEntry enableTrashcan;
	public IntegerEntry maxScale;
	public IntegerEntry minScale;
	public IntegerEntry scaleSpeed;
	public BooleanEntry legacyFont;
	public BooleanEntry transparentBackground;

	BlocklySection(String preferencesIdentifier) {
		super(preferencesIdentifier);

		blockRenderer = addEntry(new StringEntry("blockRenderer", "Thrasos", "Geras", "Thrasos"));
		colorSaturation = addEntry(new IntegerEntry("colorSaturation", 45, 30, 100));
		colorValue = addEntry(new IntegerEntry("colorValue", 65, 30, 100));
		useSmartSort = addEntry(new BooleanEntry("useSmartSort", true));
		expandCategories = addEntry(new StringEntry("expandCategories", "Default", "Default", "Always", "Never"));
		enableComments = addEntry(new BooleanEntry("enableComments", true));
		enableCollapse = addEntry(new BooleanEntry("enableCollapse", true));
		enableTrashcan = addEntry(new BooleanEntry("enableTrashcan", true));
		maxScale = addEntry(new IntegerEntry("maxScale", 100, 95, 200));
		minScale = addEntry(new IntegerEntry("minScale", 40, 20, 95));
		scaleSpeed = addEntry(new IntegerEntry("scaleSpeed", 105, 0, 200));
		legacyFont = addEntry(new BooleanEntry("legacyFont", false));
		transparentBackground = addEntry(new BooleanEntry("transparentBackground", false));
	}

	@Override public String getSectionKey() {
		return "blockly";
	}

}
