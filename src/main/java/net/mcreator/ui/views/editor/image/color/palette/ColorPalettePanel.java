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

package net.mcreator.ui.views.editor.image.color.palette;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.views.editor.image.color.ListEditPanel;
import net.mcreator.ui.views.editor.image.tool.ToolPanel;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class ColorPalettePanel extends ListEditPanel<Color> {
	private final ToolPanel toolPanel;
	private JDialog dialog = null;
	private ColorPalette palette;

	public ColorPalettePanel(MCreator mcreator, ToolPanel toolPanel) {
		super(mcreator);
		this.toolPanel = toolPanel;

		setPromptOnDelete(false);
	}

	@Override protected void itemSelected(Color selected) {
		if (selected != null) {
			toolPanel.getColorSelector().setForegroundColor(selected);
		}
	}

	public void setPalette(ColorPalette palette) {
		this.palette = palette;
		setList(this.palette.getColors(), new ColorCellRenderer());

		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		list.setFixedCellHeight(20);
		list.setFixedCellWidth(20);
	}

	@Override public Color createNew(Color selected) {
		JColor.colorChooser.setColor(toolPanel.getColorSelector().getForegroundColor());
		AtomicReference<Color> newColor = new AtomicReference<>();
		dialog = JColorChooser.createDialog(mcreator, L10N.t("dialog.image_maker.palette.dialog.new_color.title"), true,
				JColor.colorChooser, event -> {
					Color c = JColor.colorChooser.getColor();
					if (c != null)
						newColor.set(c);
					dialog.setVisible(false);
				}, event -> dialog.setVisible(false));
		dialog.setVisible(true);
		return newColor.get();
	}

	@Override protected void promptEdit(Color selected) {
		int colorID = selectedIndex();
		JColor.colorChooser.setColor(palette.getColors().get(colorID));
		dialog = JColorChooser.createDialog(mcreator, L10N.t("dialog.image_maker.palette.dialog.edit_color.title"),
				true, JColor.colorChooser, event -> {
					Color c = JColor.colorChooser.getColor();
					if (c != null)
						palette.getColors().set(colorID, c);
					dialog.setVisible(false);
				}, event -> dialog.setVisible(false));
		dialog.setVisible(true);
	}

	@Override protected String getItemName(Color selected) {
		return selected.toString();
	}

}
