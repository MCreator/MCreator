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
import net.mcreator.element.parts.procedure.StringProcedure;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.VariableTypeLoader;

import java.awt.*;

public class Label extends GUIComponent {

	public String name;

	public StringProcedure text;
	public Color color;

	public Procedure displayCondition;

	public Label(String name, int x, int y, StringProcedure text, Color color, Procedure displayCondition) {
		super(x, y);
		this.text = text;
		this.color = color;
		this.displayCondition = displayCondition;
		this.name = name;
	}

	public Label(String name, int x, int y, StringProcedure text, Color color, Procedure displayCondition,
			AnchorPoint anchorPoint) {
		this(name, x, y, text, color, displayCondition);
		this.anchorPoint = anchorPoint;
	}

	@Override public String getName() {
		return name;
	}

	@Override public final int getWidth(Workspace workspace) {
		return (int) (WYSIWYG.fontMC.getStringBounds(this.getRenderText(), WYSIWYG.frc).getWidth());
	}

	@Override public final int getHeight(Workspace workspace) {
		return (int) (WYSIWYG.fontMC.getStringBounds(this.getRenderText(), WYSIWYG.frc).getHeight()) + 1;
	}

	@Override public int getWeight() {
		return 10;
	}

	@Override public boolean isSizeKnown() {
		return false; // one could be using tokens in the label
	}

	public String getRenderText() {
		if (text.getName() == null)
			return text.getFixedValue();
		else
			return text.getName();
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		int textheight = (int) (WYSIWYG.fontMC.getStringBounds(this.getRenderText(), WYSIWYG.frc).getHeight()) - 1;
		g.setColor(this.color);
		g.drawString(this.getRenderText(), cx, cy + textheight);

		if (text.getName() != null) { // we have a procedure-based text
			g.setColor(VariableTypeLoader.BuiltInTypes.STRING.getBlocklyColor());
			g.drawLine(cx, cy + getHeight(null), cx + this.getWidth(null), cy + getHeight(null));
		}
	}

}
