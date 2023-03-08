/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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
import net.mcreator.element.parts.procedure.StringProcedure;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableTypeLoader;

import java.awt.*;

public class Tooltip extends SizedComponent {

	public String name;

	public StringProcedure text;
	public Procedure displayCondition;

	public Tooltip(String name, int x, int y, int width, int height, StringProcedure text, Procedure displayCondition) {
		super(x, y, width, height);
		this.text = text;
		this.displayCondition = displayCondition;
		this.name = name;
	}

	@Override public String getName() {
		return name;
	}

	public String getRenderText() {
		if (text.getName() == null)
			return text.getFixedValue();
		else
			return text.getName();
	}

	@Override
	public boolean changesHeight() {
		return true;
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		g.draw3DRect(x, y, width, height,false);
		g.drawString(text.getName() == null ? text.getFixedValue() : text.getName(), x, y - 3);

		if (text.getName() != null) { // we have a procedure-based text
			g.setColor(VariableTypeLoader.BuiltInTypes.STRING.getBlocklyColor());
			g.drawLine(x, y - 1, (int) (x + WYSIWYG.fontMC.getStringBounds(text.getName(), WYSIWYG.frc).getWidth()), y - 1);
		}
	}

	@Override public int getWeight() {
		return -15;
	}
}
