/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.MouseWheelEvent;

public class ScrollWheelPassLayer extends LayerUI<JScrollPane> {

	@Override public void installUI(JComponent c) {
		super.installUI(c);
		if (c instanceof JLayer<?> layer)
			layer.setLayerEventMask(AWTEvent.MOUSE_WHEEL_EVENT_MASK);
	}

	@Override public void uninstallUI(JComponent c) {
		if (c instanceof JLayer<?> layer)
			layer.setLayerEventMask(0);
		super.uninstallUI(c);
	}

	@Override protected void processMouseWheelEvent(MouseWheelEvent e, JLayer<? extends JScrollPane> l) {
		Component c = e.getComponent();
		int dir = e.getWheelRotation();
		JScrollPane main = l.getView();
		if (c instanceof JScrollPane child && !c.equals(main)) {
			BoundedRangeModel m = child.getVerticalScrollBar().getModel();
			int extent = m.getExtent();
			int minimum = m.getMinimum();
			int maximum = m.getMaximum();
			int value = m.getValue();
			if (value + extent >= maximum && dir > 0 || value <= minimum && dir < 0) {
				main.dispatchEvent(SwingUtilities.convertMouseEvent(c, e, main));
			}
		}
	}
}
