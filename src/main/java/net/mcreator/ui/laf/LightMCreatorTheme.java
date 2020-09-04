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

package net.mcreator.ui.laf;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class LightMCreatorTheme extends AbstractMCreatorTheme {

	private final Color BLACK_ACCENT = new Color(245, 245, 245);
	private final Color DARK_ACCENT = new Color(225, 225, 225);
	private final Color LIGHT_ACCENT = new Color(200, 200, 200);
	private final Color GRAY_COLOR = new Color(57, 57, 57);
	private final Color BRIGHT_COLOR = new Color(24, 24, 24);

	@Override protected @NotNull Color getBlackAccent() {
		return BLACK_ACCENT;
	}

	@Override protected @NotNull Color getDarkAccent() {
		return DARK_ACCENT;
	}

	@Override protected @NotNull Color getLightAccent() {
		return LIGHT_ACCENT;
	}

	@Override protected @NotNull Color getGrayColor() {
		return GRAY_COLOR;
	}

	@Override protected @NotNull Color getBrightColor() {
		return BRIGHT_COLOR;
	}
}
