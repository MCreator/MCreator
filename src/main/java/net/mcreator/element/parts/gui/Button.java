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

import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;

import java.awt.*;

public class Button extends SizedComponent {

	public String name;
	public String text;
	public boolean isUndecorated;
	public Procedure onClick;
	public Procedure displayCondition;

	public Button(String name, int x, int y, String text, int width, int height, boolean isUndecorated,
			Procedure onClick, Procedure displayCondition) {
		super(x, y, width, height);
		this.text = text;
		this.onClick = onClick;
		this.displayCondition = displayCondition;
		this.name = name;
		this.isUndecorated = isUndecorated;
	}

	@Override public int getWeight() {
		return 30;
	}

	@Override public String getName() {
		return name;
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		int textwidth = (int) (WYSIWYG.fontMC.getStringBounds(this.text, WYSIWYG.frc).getWidth());
		int textheight = (int) (WYSIWYG.fontMC.getStringBounds(this.text, WYSIWYG.frc).getHeight()) - 4;

		if (isUndecorated) {
			g.drawString(this.text, cx, cy + textheight + 4);
		} else {
			g.drawImage(MinecraftImageGenerator.generateButton(this.width, this.height), cx, cy, this.width,
					this.height, wysiwygEditor);
			g.drawString(this.text, cx + (this.width / 2) - (textwidth / 2),
					cy + textheight + (this.height / 2) - (textheight / 2));
		}
	}

}