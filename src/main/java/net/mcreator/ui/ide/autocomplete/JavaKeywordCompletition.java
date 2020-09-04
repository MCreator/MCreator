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

package net.mcreator.ui.ide.autocomplete;

import org.fife.rsta.ac.java.JavaTemplateCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import javax.swing.*;
import java.awt.*;

public class JavaKeywordCompletition extends JavaTemplateCompletion {

	public JavaKeywordCompletition(CompletionProvider provider, String keyWord) {
		super(provider, keyWord, keyWord, keyWord + "${cursor}", "Java keyword",
				"Inserts " + keyWord + " Java keyword");
	}

	@Override public Icon getIcon() {
		return null;
	}

	@Override public void rendererText(Graphics g, int x, int y, boolean selected) {
		Color orig = g.getColor();
		g.setColor((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));

		g.drawString(getInputText(), x, y);
		if (getShortDescription() != null) {
			x += g.getFontMetrics().stringWidth(getInputText());
			g.setColor(orig);

			String temp = " - ";
			g.drawString(temp, x, y);
			x += g.getFontMetrics().stringWidth(temp);
			g.setColor(Color.GRAY);

			g.drawString(getShortDescription(), x, y);
		}
	}

	@Override public int getRelevance() {
		return 5;
	}

}
