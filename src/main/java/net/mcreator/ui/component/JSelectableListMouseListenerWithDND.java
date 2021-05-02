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

import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.workspace.breadcrumb.FolderElementCrumb;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.ModElementManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class JSelectableListMouseListenerWithDND<T> extends MousePressListener {

	@Nullable private Point srcPoint = new Point(); // when null, we are in dnd

	private int[] selection = null;

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
		// reset cursors
		list.setCursor(null);
		if (list.additionalDNDComponent != null)
			list.additionalDNDComponent.setCursor(null);
		finalDNDselection = null;
	}

	@Override public void mouseDragged(MouseEvent e) {
		if (srcPoint == null) { // we are in DND dragging
			list.getRubberBand().reset();
			list.setFocusable(true);
			list.repaint();

			JComponent co = getAdditionalTargetFor(e);
			if (co instanceof FolderElementCrumb) { // highlight crumbs
				co.setOpaque(true);
				co.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			}

			if (finalDNDselection == null) {
				if (selection != null && Arrays.stream(selection)
						.anyMatch(selectedItem -> selectedItem == list.locationToIndex(e.getPoint())))
					list.setSelectedIndices(selection);

				if (list.getSelectedIndices().length > 1) {
					list.setCursor(DRAG_ELEMENTS);
					if (list.additionalDNDComponent != null)
						list.additionalDNDComponent.setCursor(DRAG_ELEMENTS);
				} else {
					Object element = list.getSelectedValue();
					if (element instanceof FolderElement) {
						list.setCursor(DRAG_FOLDER);
						if (list.additionalDNDComponent != null)
							list.additionalDNDComponent.setCursor(DRAG_FOLDER);
					} else if (element instanceof ModElement) {
						list.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
								ModElementManager.getModElementIcon((ModElement) element).getImage(), new Point(0, 0),
								((ModElement) element).getName()));
						if (list.additionalDNDComponent != null)
							list.additionalDNDComponent.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
									ModElementManager.getModElementIcon((ModElement) element).getImage(),
									new Point(0, 0), ((ModElement) element).getName()));
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
			if (finalDNDselection != null && list.listener != null) {
				JComponent co = getAdditionalTargetFor(e);
				if (co instanceof FolderElementCrumb) { // check if point was released over additional target
					list.listener.dndComplete(((FolderElementCrumb) co).getFolder(),
							Arrays.stream(finalDNDselection).mapToObj(i -> list.getModel().getElementAt(i))
									.collect(Collectors.toList()));
				} else if (list.findComponentAt(e.getPoint()) == list) { // check if point was released over the list
					list.listener.dndComplete(list.getModel().getElementAt(list.locationToIndex(e.getPoint())),
							Arrays.stream(finalDNDselection).mapToObj(i -> list.getModel().getElementAt(i))
									.collect(Collectors.toList()));
				}
			}

			stopDNDAction();
		}
	}

	@Override public void pressFiltered(MouseEvent e, int clicks) {
		if (clicks == 2 && list.dndCustom) {
			srcPoint = null; // Initiate DND action
		} else if (clicks == 1 && list.dndCustom) {
			if ((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0 || (e.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
				selection = list.getSelectedIndices();
			} else {
				selection = null; // If only one click, reset the DND selection candidates
			}
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

	@Nullable private JComponent getAdditionalTargetFor(MouseEvent e) {
		if (list.additionalDNDComponent != null) {
			Point pointOnADND = subtract(e.getLocationOnScreen(), list.additionalDNDComponent.getLocationOnScreen());
			Component co = list.additionalDNDComponent.getComponentAt(pointOnADND);
			if (co instanceof JComponent) {
				if (co == list.additionalDNDComponent || co.getParent() == list.additionalDNDComponent) {
					return (JComponent) co;
				}
			}
		}
		return null;
	}

	private static Point subtract(Point p1, Point p2) {
		return new Point((int) (p1.getX() - p2.getX()), (int) (p1.getY() - p2.getY()));
	}

}
