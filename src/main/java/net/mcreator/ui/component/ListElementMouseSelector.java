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

package net.mcreator.ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.stream.IntStream;

class ListElementMouseSelector extends MouseAdapter {

	private final Point srcPoint = new Point();

	@Override public void mouseDragged(MouseEvent e) {
		JSelectableList l = (JSelectableList) e.getComponent();
		l.setFocusable(true);
		Point destPoint = e.getPoint();
		Path2D rubberBand = l.getRubberBand();
		rubberBand.reset();
		rubberBand.moveTo(srcPoint.x, srcPoint.y);
		rubberBand.lineTo(destPoint.x, srcPoint.y);
		rubberBand.lineTo(destPoint.x, destPoint.y);
		rubberBand.lineTo(srcPoint.x, destPoint.y);
		rubberBand.closePath();
		int[] selNew = IntStream.range(0, l.getModel().getSize())
				.filter(i -> rubberBand.intersects(l.getCellBounds(i, i))).toArray();
		int[] curr = l.getSelectedIndices();
		int[] indices = new int[selNew.length + curr.length];
		System.arraycopy(selNew, 0, indices, 0, selNew.length);
		System.arraycopy(curr, 0, indices, selNew.length, curr.length);
		l.setSelectedIndices(indices);
		l.repaint();
	}

	@Override public void mouseReleased(MouseEvent e) {
		JSelectableList l = (JSelectableList) e.getComponent();
		l.getRubberBand().reset();
		Component c = e.getComponent();
		c.setFocusable(true);
		c.repaint();
	}

	@Override public void mousePressed(MouseEvent e) {
		JList l = (JList) e.getComponent();
		int index = l.locationToIndex(e.getPoint());
		Rectangle rect = l.getCellBounds(index, index);
		if (rect != null && rect.contains(e.getPoint())) {
			l.setFocusable(true);
		} else if ((e.getModifiers() & MouseEvent.CTRL_MASK) == MouseEvent.CTRL_MASK) {
			l.clearSelection();
			l.getSelectionModel().setAnchorSelectionIndex(-1);
			l.getSelectionModel().setLeadSelectionIndex(-1);
			l.setFocusable(false);
		} else {
			l.setFocusable(false);
		}
		srcPoint.setLocation(e.getPoint());
		l.repaint();
	}

}
