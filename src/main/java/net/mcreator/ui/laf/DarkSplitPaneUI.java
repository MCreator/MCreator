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

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.metal.MetalSplitPaneUI;
import java.awt.*;

public class DarkSplitPaneUI extends MetalSplitPaneUI {

	public static ComponentUI createUI(JComponent var0) {
		return new DarkSplitPaneUI();
	}

	public BasicSplitPaneDivider createDefaultDivider() {
		BasicSplitPaneDivider divider = super.createDefaultDivider();
		divider.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		return divider;
	}

}
