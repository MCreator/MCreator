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

import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.OceanTheme;

public class MCreatorTheme extends OceanTheme {

	private final Theme theme;

	public MCreatorTheme(Theme theme) {
		this.theme = theme;
	}

	@Override public void addCustomEntriesToTable(UIDefaults table) {
		super.addCustomEntriesToTable(table);
		theme.applyUIDefaultsOverrides(table);
	}

	@Override public String getName() {
		return "MCreator";
	}

	@Override protected ColorUIResource getPrimary1() {
		return new ColorUIResource(theme.getBackgroundColor());
	}

	@Override protected ColorUIResource getPrimary2() {
		return new ColorUIResource(theme.getInterfaceAccentColor());
	}

	@Override protected ColorUIResource getPrimary3() {
		return new ColorUIResource(theme.getInterfaceAccentColor());
	}

	@Override protected ColorUIResource getSecondary1() {
		return new ColorUIResource(theme.getAltBackgroundColor());
	}

	@Override protected ColorUIResource getSecondary2() {
		return new ColorUIResource(theme.getAltBackgroundColor());
	}

	@Override public ColorUIResource getControl() {
		return new ColorUIResource(theme.getAltBackgroundColor());
	}

	@Override public ColorUIResource getControlHighlight() {
		return new ColorUIResource(theme.getAltBackgroundColor());
	}

	@Override public ColorUIResource getPrimaryControlHighlight() {
		return new ColorUIResource(theme.getAltForegroundColor());
	}

	@Override public FontUIResource getControlTextFont() {
		return new FontUIResource(theme.getFont());
	}

	@Override public FontUIResource getSystemTextFont() {
		return new FontUIResource(theme.getFont());
	}

	@Override public FontUIResource getUserTextFont() {
		return new FontUIResource(theme.getFont());
	}

	@Override public FontUIResource getMenuTextFont() {
		return new FontUIResource(theme.getFont());
	}

	@Override public FontUIResource getWindowTitleFont() {
		return new FontUIResource(theme.getFont());
	}

	@Override public FontUIResource getSubTextFont() {
		return new FontUIResource(theme.getFont());
	}

}
