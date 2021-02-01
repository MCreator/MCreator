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

package net.mcreator.element.parts.gui;

import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.awt.Image;

public class Checkbox extends GUIComponent{
	public String text;
	public boolean isChecked;

	public Checkbox(String name, int x, int y, String text, boolean isChecked){
		super(name, x, y);
		this.text = text;
		this.isChecked = isChecked;
	}


	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		g.setFont(g.getFont().deriveFont(5f));
		if(isChecked)
			g.drawImage(UIRES.get("32px.checkbox").getImage(), cx, cy, 20, 20, wysiwygEditor);
		else
			g.drawImage(new ImageIcon(MinecraftImageGenerator.generateCheckbox()).getImage(), cx, cy, 20, 20, wysiwygEditor);
	}

	@Override public int getWidth(Workspace workspace) {
		return 20;
	}

	@Override public int getHeight(Workspace workspace) {
		return 20;
	}

	@Override public int getWeight() {
		return 5;
	}
}
