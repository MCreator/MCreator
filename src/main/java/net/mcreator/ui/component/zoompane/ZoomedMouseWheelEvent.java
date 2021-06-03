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

package net.mcreator.ui.component.zoompane;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

public class ZoomedMouseWheelEvent extends MouseWheelEvent {
	private final Point mousePosition, canvasPosition;
	private final Point2D viewPosition;

	@SuppressWarnings("deprecation") public ZoomedMouseWheelEvent(MouseWheelEvent event, JZoomport zoomport)
			throws NullPointerException {
		super(zoomport.getToZoom(), event.getID(), event.getWhen(), event.getModifiers(), (int) Math
						.floor((event.getX() - zoomport.getCanvasX() + zoomport.getViewPositionX()) / zoomport.getZoom()),
				(int) Math.floor((event.getY() - zoomport.getCanvasY() + zoomport.getViewPositionY()) / zoomport
						.getZoom()), event.getXOnScreen(), event.getYOnScreen(), event.getClickCount(),
				event.isPopupTrigger(), event.getScrollType(), event.getScrollAmount(), event.getWheelRotation(),
				event.getPreciseWheelRotation());
		mousePosition = event.getPoint();
		canvasPosition = zoomport.getCanvasPoint();
		viewPosition = zoomport.getViewPositionPoint();
	}

	public double getRawX() {
		if (mousePosition != null)
			return mousePosition.getX() - canvasPosition.getX() + viewPosition.getX();
		else
			return 0;
	}

	public double getRawY() {
		if (mousePosition != null)
			return mousePosition.getY() - canvasPosition.getY() + viewPosition.getY();
		else
			return 0;
	}

	public Point2D.Double getRawPoint() {
		if (mousePosition != null)
			return new Point2D.Double(mousePosition.getX() - canvasPosition.getX() + viewPosition.getX(),
					mousePosition.getY() - canvasPosition.getY() + viewPosition.getY());
		else
			return new Point2D.Double();
	}
}
