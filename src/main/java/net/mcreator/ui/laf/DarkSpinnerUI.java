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

package net.mcreator.ui.laf;

import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;

public class DarkSpinnerUI extends BasicSpinnerUI {

	public static ComponentUI createUI(JComponent c) {
		return new DarkSpinnerUI();
	}

	@Override protected Component createPreviousButton() {
		Component var1 = this.createArrowButton(5);
		var1.setName("Spinner.previousButton");
		this.installPreviousButtonListeners(var1);
		return var1;
	}

	@Override protected Component createNextButton() {
		Component var1 = this.createArrowButton(1);
		var1.setName("Spinner.nextButton");
		this.installNextButtonListeners(var1);
		return var1;
	}

	private Component createArrowButton(int var1) {
		BasicArrowButton var2 = new BasicArrowButton(var1, Theme.current().getBackgroundColor(),
				Theme.current().getBackgroundColor(), Theme.current().getForegroundColor(),
				Theme.current().getBackgroundColor());
		var2.setBackground(Theme.current().getBackgroundColor());
		var2.setForeground(Theme.current().getForegroundColor());
		var2.setBorder(BorderFactory.createLineBorder(Theme.current().getBackgroundColor(), 1));
		var2.setInheritsPopupMenu(true);
		return var2;
	}

}
