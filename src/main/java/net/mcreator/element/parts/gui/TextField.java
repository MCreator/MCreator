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
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;

import java.awt.*;

public class TextField extends SizedComponent {

	public String placeholder;

	public TextField(String name, int x, int y, int width, int height, String placeholder) {
		super(name, x, y, width, height);
		this.placeholder = placeholder;
	}

	@Override public int getWeight() {
		return 1;
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		g.drawImage(MinecraftImageGenerator.generateTextField(this.width, this.height), cx, cy,
				this.width, this.height, wysiwygEditor);
	}

}
