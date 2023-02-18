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
import net.mcreator.preferences.entries.NumberEntry;
import net.mcreator.preferences.entries.StringEntry;

public class Blockly {
	public StringEntry blockRenderer;
	public BooleanEntry useSmartSort;
	public BooleanEntry enableComments;
	public BooleanEntry enableCollapse;
	public BooleanEntry enableTrashcan;
	public NumberEntry maxScale;
	public NumberEntry minScale;
	public NumberEntry scaleSpeed;
	public BooleanEntry legacyFont;

	public Blockly() {
		blockRenderer = Preferences.register(
				new StringEntry("blockRenderer", "Thrasos", Preferences.BLOCKLY, "Geras", "Thrasos"));
		useSmartSort = Preferences.register(new BooleanEntry("useSmartSort", true, Preferences.BLOCKLY));
		enableComments = Preferences.register(new BooleanEntry("enableComments", true, Preferences.BLOCKLY));
		enableCollapse = Preferences.register(new BooleanEntry("enableCollapse", true, Preferences.BLOCKLY));
		enableTrashcan = Preferences.register(new BooleanEntry("enableTrashcan", true, Preferences.BLOCKLY));
		maxScale = Preferences.register(new NumberEntry("maxScale", 100, Preferences.BLOCKLY, 95, 200));
		minScale = Preferences.register(new NumberEntry("minScale", 40, Preferences.BLOCKLY, 20, 95));
		scaleSpeed = Preferences.register(new NumberEntry("scaleSpeed", 105, Preferences.BLOCKLY, 0, 200));
		legacyFont = Preferences.register(new BooleanEntry("legacyFont", false, Preferences.BLOCKLY));
	}
}
