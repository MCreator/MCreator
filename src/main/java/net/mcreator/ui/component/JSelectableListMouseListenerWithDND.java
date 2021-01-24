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

import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.ui.init.UIRES;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class JSelectableListMouseListenerWithDND<T> extends MousePressListener {

	@Nullable private Point srcPoint = new Point(); // when null, we are in dnd

	int[] selection = null;
	int[] finalDNDselection = null;

	private final Cursor DRAG_FOLDER = Toolkit.getDefaultToolkit()
			.createCustomCursor(UIRES.get("folder").getImage(), new Point(0, 0), "Drag cursor");

	private final Cursor DRAG_ELEMENTS = Toolkit.getDefaultToolkit()
			.createCustomCursor(UIRES.get("mods").getImage(), new Point(0, 0), "Drag elements cursor");

	private final JSelectableList<T> list;

	JSelectableListMouseListenerWithDND(JSelectableList<T> list) {
		this.list = list;
	}

	public void stopDNDAction() {
		list.setCursor(Cursor.getDefaultCursor());
		finalDNDselection = null;
	}

	@Override public void mouseDragged(MouseEvent e) {
		if (srcPoint == null) { // we are in DND dragging
			list.getRubberBand().reset();
			list.setFocusable(true);
			list.repaint();

			if (finalDNDselection == null) {
				if (selection != null && Arrays.stream(selection)
						.anyMatch(selectedItem -> selectedItem == list.locationToIndex(e.getPoint())))
					list.setSelectedIndices(selection);

				if (list.getSelectedIndices().length > 1) {
					list.setCursor(DRAG_ELEMENTS);
				} else {
					Object element = list.getSelectedValue();
					if (element instanceof FolderElement) {
						list.setCursor(DRAG_FOLDER);
					} else if (element instanceof ModElement) {
						ImageIcon icon = ((ModElement) element).getElementIcon();
						if (icon == null || icon.getImage() == null || icon.getIconWidth() <= 0
								|| icon.getIconHeight() <= 0) {
							icon = TiledImageCache.getModTypeIcon(((ModElement) element).getType());
						}

						if (icon != null)
							list.setCursor(Toolkit.getDefaultToolkit()
									.createCustomCursor(icon.getImage(), new Point(0, 0),
											((ModElement) element).getName()));
						else
							list.setCursor(new Cursor(Cursor.MOVE_CURSOR));
					}
				}

				finalDNDselection = list.getSelectedIndices();
			}
		} else { // we are in selection dragging
			list.setFocusable(true);
			Point destPoint = e.getPoint();
			Path2D rubberBand = list.getRubberBand();
			rubberBand.reset();
			rubberBand.moveTo(srcPoint.x, srcPoint.y);
			rubberBand.lineTo(destPoint.x, srcPoint.y);
			rubberBand.lineTo(destPoint.x, destPoint.y);
			rubberBand.lineTo(srcPoint.x, destPoint.y);
			rubberBand.closePath();
			int[] selNew = IntStream.range(0, list.getModel().getSize())
					.filter(i -> rubberBand.intersects(list.getCellBounds(i, i))).toArray();
			int[] curr = list.getSelectedIndices();
			int[] indices = new int[selNew.length + curr.length];
			System.arraycopy(selNew, 0, indices, 0, selNew.length);
			System.arraycopy(curr, 0, indices, selNew.length, curr.length);
			list.setSelectedIndices(indices);
			list.repaint();

			selection = indices;
		}
	}

	@Override public void mouseReleased(MouseEvent e) {
		list.getRubberBand().reset();
		list.setFocusable(true);
		list.repaint();

		if (list.dndCustom) {
			if (finalDNDselection != null)
				list.listener.dndComplete(list.getModel().getElementAt(list.locationToIndex(e.getPoint())),
						Arrays.stream(finalDNDselection).mapToObj(i -> list.getModel().getElementAt(i))
								.collect(Collectors.toList()));

			stopDNDAction();
		}
	}

	@Override public void pressFiltered(MouseEvent e, int clicks) {
		if (clicks == 2 && list.dndCustom) {
			srcPoint = null; // Initiate DND action
		} else if (clicks == 1) {
			selection = null; // If only one click, reset the DND selection candidates
		} else if (clicks == 0) {
			int index = list.locationToIndex(e.getPoint());
			Rectangle rect = list.getCellBounds(index, index);
			if (rect != null && rect.contains(e.getPoint())) {
				list.setFocusable(true);
			} else if ((e.getModifiers() & MouseEvent.CTRL_MASK) == MouseEvent.CTRL_MASK) {
				list.clearSelection();
				list.getSelectionModel().setAnchorSelectionIndex(-1);
				list.getSelectionModel().setLeadSelectionIndex(-1);
				list.setFocusable(false);
			} else {
				list.setFocusable(false);
			}
			srcPoint = new Point();
			srcPoint.setLocation(e.getPoint());
			list.repaint();
		}
	}

}
