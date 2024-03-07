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

package net.mcreator.ui.views.editor.image.color.palettes;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.views.editor.image.color.ListEditPanel;
import net.mcreator.ui.views.editor.image.color.PalettePanel;
import net.mcreator.ui.views.editor.image.color.palette.ColorPalettePanel;
import net.mcreator.ui.views.editor.image.color.palette.ColorPalette;
import net.mcreator.util.ArrayListListModel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class PaletteListPanel extends ListEditPanel<ColorPalette> {
	private final ArrayListListModel<ColorPalette> palettes = new ArrayListListModel<>();
	private final PalettePanel palettePanel;
	private ColorPalettePanel colorPalettePanel;

	public PaletteListPanel(MCreator mcreator, PalettePanel palettePanel) {
		super(mcreator);
		this.palettePanel = palettePanel;
		setList(palettes, new PaletteCellRenderer());

		//list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		//list.setVisibleRowCount(-1);
		//list.setFixedCellHeight(20);
		//list.setFixedCellWidth(20);

		palettes.add(new ColorPalette("Test 1"));
		palettes.add(new ColorPalette("Test 2"));
		palettes.add(new ColorPalette("Long name palette super long let'sgooooo"));

		palettes.get(0).addAll(Arrays.asList(Color.red, Color.green, Color.blue, Color.yellow, Color.orange, Color.pink,
				Color.cyan, Color.magenta, Color.black, Color.white));
		for (int i = 0; i < 6; i++) {
			float saturation = (float) Math.random() * 0.5f + 0.5f;
			for (int j = 0; j < 6; j++) {
				float brightness = (float) Math.random() * 0.5f + 0.5f;
				for (int k = 0; k < 10; k++) {
					palettes.get(0).add(Color.getHSBColor((float) Math.random(), saturation, brightness));
				}
			}
		}

		palettes.get(1).addAll(Arrays.asList(Color.red, Color.green, Color.blue, Color.yellow, Color.orange, Color.cyan,
				Color.magenta, Color.black, Color.white));

		for (int i = 0; i < 8; i++) {
			float saturation = (float) Math.random() * 0.5f + 0.5f;
			for (int j = 0; j < 8; j++) {
				float brightness = (float) Math.random() * 0.5f + 0.5f;
				for (int k = 0; k < 16; k++) {
					palettes.get(2).add(Color.getHSBColor((float) Math.random(), saturation, brightness));
				}
			}
		}
	}

	@Override protected void itemSelected(ColorPalette selected) {
		if (colorPalettePanel != null) {
			colorPalettePanel.setPalette(selected);
		}
	}

	@Override protected void itemDoubleClicked(ColorPalette selected) {
		if (colorPalettePanel != null) {
			palettePanel.setSelectedComponent(colorPalettePanel);
		}
	}

	@Override public ColorPalette createNew(ColorPalette selected) {
		return new ColorPalette("New Palette");
	}

	@Override public void promptEdit(ColorPalette selected) {
		String newName = (String) JOptionPane.showInputDialog(mcreator,
				L10N.t("dialog.image_maker.palette.dialog.edit_palette.message"),
				L10N.t("dialog.image_maker.palette.dialog.edit_palette.title"), JOptionPane.PLAIN_MESSAGE, null, null,
				selected.getName());
		if (newName != null) {
			selected.setName(newName);
			repaintAll();
		}
	}

	@Override public String getItemName(ColorPalette selected) {
		return selected.getName();
	}

	public void setColorsPanel(ColorPalettePanel colorPalettePanel) {
		this.colorPalettePanel = colorPalettePanel;
	}
}