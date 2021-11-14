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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public class ToolboxCategory {
	private static final Logger LOG = LogManager.getLogger("Toolbox category");

	String id, name, description;

	String color;
	boolean api;

	public String getName() {
		String l10nname = L10N.t("blockly.category." + id);
		if (l10nname != null)
			return l10nname;

		return name;
	}

	/**
	 * Returns the color of this toolbox category. If the field is a valid hex color code, it's returned as-is.
	 * If it's a valid integer, it's treated as a hue to get the color with the correct saturation and value.
	 *
	 * @return The color of this toolbox category, or black if it's badly formatted.
	 */
	public Color getColor() {
		try {
			if (!color.startsWith("#"))
				return BlocklyBlockUtil.getBlockColorFromHUE(Integer.parseInt(color));
			else
				return Color.decode(color);
		} catch (Exception e) {
			LOG.warn("The color for toolbox category " + getName()
					+ " isn't formatted correctly. Using color black for it");
			return Color.BLACK;
		}
	}
}
