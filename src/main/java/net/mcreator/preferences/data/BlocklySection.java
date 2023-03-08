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
import net.mcreator.preferences.entries.IntegerEntry;
import net.mcreator.preferences.entries.StringEntry;

public class BlocklySection {

	public StringEntry blockRenderer;
	public BooleanEntry useSmartSort;
	public BooleanEntry enableComments;
	public BooleanEntry enableCollapse;
	public BooleanEntry enableTrashcan;
	public IntegerEntry maxScale;
	public IntegerEntry minScale;
	public IntegerEntry scaleSpeed;
	public BooleanEntry legacyFont;

	BlocklySection() {
		blockRenderer = PreferencesData.register(
				new StringEntry("blockRenderer", "Thrasos", PreferencesData.BLOCKLY, "Geras", "Thrasos"));
		useSmartSort = PreferencesData.register(new BooleanEntry("useSmartSort", true, PreferencesData.BLOCKLY));
		enableComments = PreferencesData.register(new BooleanEntry("enableComments", true, PreferencesData.BLOCKLY));
		enableCollapse = PreferencesData.register(new BooleanEntry("enableCollapse", true, PreferencesData.BLOCKLY));
		enableTrashcan = PreferencesData.register(new BooleanEntry("enableTrashcan", true, PreferencesData.BLOCKLY));
		maxScale = PreferencesData.register(new IntegerEntry("maxScale", 100, PreferencesData.BLOCKLY, 95, 200));
		minScale = PreferencesData.register(new IntegerEntry("minScale", 40, PreferencesData.BLOCKLY, 20, 95));
		scaleSpeed = PreferencesData.register(new IntegerEntry("scaleSpeed", 105, PreferencesData.BLOCKLY, 0, 200));
		legacyFont = PreferencesData.register(new BooleanEntry("legacyFont", false, PreferencesData.BLOCKLY));
	}

}
