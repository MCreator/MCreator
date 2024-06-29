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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import net.mcreator.io.FileIO;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.views.editor.image.color.palette.ColorPalette;
import net.mcreator.ui.views.editor.image.color.palette.ColorPalettePanel;
import net.mcreator.ui.views.editor.image.color.palettes.PaletteListPanel;
import net.mcreator.ui.views.editor.image.tool.ToolPanel;
import net.mcreator.util.ArrayListListModel;

import javax.swing.*;
import java.io.File;

public class PalettePanel extends JTabbedPane {

	private static final Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).create();

	private final File paletteFile;

	private final PaletteListPanel paletteListPanel;

	public PalettePanel(MCreator mcreator, ToolPanel toolPanel) {
		ColorPalettePanel colorPalettePanel = new ColorPalettePanel(mcreator, toolPanel);

		this.paletteListPanel = new PaletteListPanel(mcreator, this);
		paletteListPanel.setColorsPanel(colorPalettePanel);

		addTab(L10N.t("dialog.image_maker.palette.list"), null, paletteListPanel,
				L10N.t("dialog.image_maker.palette.list.description"));
		addTab(L10N.t("dialog.image_maker.palette.colors"), null, colorPalettePanel,
				L10N.t("dialog.image_maker.palette.colors.description"));

		this.paletteFile = new File(mcreator.getFolderManager().getWorkspaceCacheDir(), "colorPalettes");
	}

	public void storePalette() {
		FileIO.writeStringToFile(gson.toJson(this.paletteListPanel.getPalettes()), this.paletteFile);
	}

	public void reloadPalette() {
		ArrayListListModel<ColorPalette> palettes = this.paletteListPanel.getPalettes();

		if (this.paletteFile.isFile()) {
			palettes.clear();
			palettes.addAll(
					gson.fromJson(FileIO.readFileToString(this.paletteFile), PaletteListPanel.PaletteStorage.class));
		} else {
			//@formatter:off
			palettes.add(ColorPalette.generate("Overworld", "#3D552C", "#313E25", "#B4D0FB", "#3F3A26", "#878D80", "#705439", "#1D2D15", "#38507D", "#D5CCA4", "#57703C",
					"#212523", "#453824", "#496331", "#BBBBB9", "#494F50", "#312614", "#636436", "#776255", "#1F2F4F", "#2C4317"));
			palettes.add(ColorPalette.generate("Cave", "#8B8478", "#423C37", "#ED6A0E", "#393028", "#544A3E", "#655544", "#666049", "#1B1612"));
			palettes.add(ColorPalette.generate("Winter", "#213331", "#83BAE5", "#182427", "#DFF0EE", "#533726", "#1B1B1B", "#2C4144", "#213A33", "#73959C", "#475B64"));
			palettes.add(ColorPalette.generate("Nether", "#955E61", "#2E1C20", "#7F2C2A", "#29B3A8", "#EA732E", "#1D3A35", "#481817", "#280B0B", "#7B261D"));
			palettes.add(ColorPalette.generate("End", "#140E18", "#44363E", "#6E7455", "#555941", "#130E19", "#94A170", "#614B59", "#818762", "#9FA776"));
			//@formatter:on
		}
	}

}
