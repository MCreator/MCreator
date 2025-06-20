/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;

import java.awt.*;

public class Slider extends SizedComponent {

	public String name;
	public double min;
	public double max;
	public double value;
	public double step;
	public final String prefix;
	public final String suffix;
	public final Procedure whenSliderMoves;

	public Slider(int x, int y, int width, int height, String name, double min, double max, double value, double step,
			String prefix, String suffix, Procedure whenSliderMoves) {
		super(x, y, width, height);
		this.name = name;
		this.min = min;
		this.max = max;
		this.value = value;
		this.step = step;
		this.prefix = prefix;
		this.suffix = suffix;
		this.whenSliderMoves = whenSliderMoves;
	}

	@Override public int getWeight() {
		return 15;
	}

	@Override public String getName() {
		return name;
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		g.drawImage(MinecraftImageGenerator.generateSliderBackground(this.width, this.height), cx, cy, width, height, wysiwygEditor);
		g.drawImage(MinecraftImageGenerator.generateButton(8, this.height), cx + this.width/2, cy, 8, height, wysiwygEditor);
		String fullText = prefix + value + suffix;
		g.setColor(new Color(0x373737));
		g.drawString(fullText,  cx + 1, cy + 13 + 1);
		g.setColor(new Color(0xdddddd));
		g.drawString(fullText, cx, cy + 13);
	}
}