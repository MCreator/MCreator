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

package net.mcreator.blockly.data;

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.ui.init.L10N;

import java.awt.*;

@SuppressWarnings("unused") public class ToolboxCategory {
	String id, name, description;
	int color;
	boolean api;

	public String getName() {
		String l10nname = L10N.t("blockly.category." + id);
		if (l10nname != null)
			return l10nname;

		return name;
	}

	public Color getColor() {
		return BlocklyBlockUtil.getBlockColorFromHUE(color);
	}
}
