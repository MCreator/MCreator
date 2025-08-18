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

package net.mcreator.ui.component;

import javax.swing.*;
import java.awt.*;

public class ScrollablePanel extends JPanel implements Scrollable {

	@Override public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 65;
	}

	@Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 65;
	}

	@Override public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override public boolean getScrollableTracksViewportHeight() {
		return false;
	}
}
