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

import net.mcreator.element.parts.Procedure;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.Workspace;

import java.awt.*;

public class Label extends GUIComponent {

	public String text;
	public Color color;

	public Procedure displayCondition;

	public Label(String name, int x, int y, String text, Color color, Procedure displayCondition) {
		super(name, x, y);
		this.text = text;
		this.color = color;
		this.displayCondition = displayCondition;
	}

	@Override public final int getWidth(Workspace workspace) {
		return (int) (WYSIWYG.fontMC.getStringBounds(this.text, WYSIWYG.frc).getWidth());
	}

	@Override public final int getHeight(Workspace workspace) {
		return  (int) (WYSIWYG.fontMC.getStringBounds(this.text, WYSIWYG.frc).getHeight()) + 1;
	}

	@Override public int getWeight() {
		return 0;
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		int textheight = (int) (WYSIWYG.fontMC.getStringBounds(this.text, WYSIWYG.frc).getHeight()) - 1;
		g.setColor(this.color);
		g.drawString(this.text, cx, cy + textheight);
	}

}
