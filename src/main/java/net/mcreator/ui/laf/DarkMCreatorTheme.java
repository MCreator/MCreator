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

import javax.annotation.Nonnull;
import java.awt.*;

public class DarkMCreatorTheme extends AbstractMCreatorTheme {

	private final Color BLACK_ACCENT = new Color(30, 30, 30);
	private final Color DARK_ACCENT = new Color(50, 50, 50);
	private final Color LIGHT_ACCENT = new Color(80, 80, 80);
	private final Color GRAY_COLOR = new Color(194, 194, 194);
	private final Color BRIGHT_COLOR = new Color(245, 245, 245);

	@Override protected @Nonnull Color getBlackAccent() {
		return BLACK_ACCENT;
	}

	@Override protected @Nonnull Color getDarkAccent() {
		return DARK_ACCENT;
	}

	@Override protected @Nonnull Color getLightAccent() {
		return LIGHT_ACCENT;
	}

	@Override protected @Nonnull Color getGrayColor() {
		return GRAY_COLOR;
	}

	@Override protected @Nonnull Color getBrightColor() {
		return BRIGHT_COLOR;
	}

	@Override protected @Nonnull String getBlocklyCSSName() {
		return "mcreator_blockly_dark.css";
	}

	@Override protected @Nonnull String getCodeEditorXML() {
		return "/codeeditor_dark.xml";
	}
}
