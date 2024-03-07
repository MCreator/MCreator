/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.views.editor.image.color;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.views.editor.image.color.palette.ColorPalettePanel;
import net.mcreator.ui.views.editor.image.color.palettes.PaletteListPanel;
import net.mcreator.ui.views.editor.image.tool.ToolPanel;

import javax.swing.*;

public class PalettePanel extends JTabbedPane {
	private final PaletteListPanel paletteListPanel;
	private final ColorPalettePanel colorPalettePanel;

	public PalettePanel(MCreator mcreator, ToolPanel toolPanel) {

		paletteListPanel = new PaletteListPanel(mcreator, this);
		colorPalettePanel = new ColorPalettePanel(mcreator, toolPanel);

		paletteListPanel.setColorsPanel(colorPalettePanel);

		addTab(L10N.t("dialog.image_maker.palette.list"), null, paletteListPanel, L10N.t("dialog.image_maker.palette.list.description"));
		addTab(L10N.t("dialog.image_maker.palette.colors"), null, colorPalettePanel, L10N.t("dialog.image_maker.palette.colors.description"));
	}
}
