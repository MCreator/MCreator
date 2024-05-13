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
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.VariableTypeLoader;

import java.awt.*;

public class Tooltip extends SizedComponent {

	public final String name;

	public final StringProcedure text;
	public final Procedure displayCondition;

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

	@Override public boolean canChangeHeight() {
		return true;
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		String renderText = getRenderText();
		if (text.getName() == null)
			renderText = StringUtils.abbreviateString(renderText, 10);

		g.setColor(new Color(0, 0, 0, 70));
		g.fillRect(cx, cy, width, height);

		g.setColor(new Color(230, 230, 230));
		g.setFont(g.getFont().deriveFont(5f));

		int textHeight = (int) (g.getFont().getStringBounds(renderText, WYSIWYG.frc).getHeight());
		int textWidth = (int) (g.getFont().getStringBounds(renderText, WYSIWYG.frc).getWidth());
		g.drawString(renderText, cx + 2, cy + textHeight + 1);

		if (text.getName() != null) { // we have a procedure-based text
			g.setColor(VariableTypeLoader.BuiltInTypes.STRING.getBlocklyColor());
			g.drawLine(cx + 2, cy + textHeight + 3, cx + 2 + textWidth, cy + textHeight + 3);
		}
	}

	@Override public int getWeight() {
		return -15;
	}

}
