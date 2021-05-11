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

import javax.annotation.Nonnull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Arrays;

public class JZoomport extends JComponent {
	private final double SCROLL_SPEED = 0.05;
	private double minZoom = .391, maxZoom = 1200, zoomFactor = 1.4, zoom = 1;
	private double[] zoomPresets = { .1, .5, 1, 2, 4, 10, 100, 1000, 2500 };
	private int width, height, canvasX, canvasY, paneWidth, paneHeight, maxScrollX, maxScrollY;
	private double viewPosX, viewPosY;
	private boolean updateScrollbarX;
	private boolean updateScrollbarY;

	private final JZoomPane zoomPane;

	private final JComponent toZoom;

	private Point prevDragLocation = new Point(0, 0);

	private Cursor lastCursor = Cursor.getDefaultCursor();
	boolean first = true;

	public JZoomport(JComponent toZoom, @Nonnull JZoomPane zoomPane) {
		this.zoomPane = zoomPane;
		this.toZoom = toZoom;

		setOpaque(false);

		registerChildListeners();

		addMouseWheelListener(e -> {
			int sign = e.getWheelRotation();
			if (((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK)) {
				if (getMousePosition() != null) {
					Point2D mouseLoc = getMousePosition();
					Point2D zoomportMouseLocation = new Point2D.Double(mouseLoc.getX() + viewPosX,
							mouseLoc.getY() + viewPosY);

					Point2D mouseLocOnCanvas = new Point2D.Double(zoomportMouseLocation.getX() - paneWidth / 2.0,
							zoomportMouseLocation.getY() - paneHeight / 2.0);

					double realZoomFactor;
					double zoom = this.zoom, originalZoom = zoom;
					realZoomFactor = Math.pow(zoomFactor, -sign);

					zoom *= realZoomFactor;

					if (zoom < minZoom)
						zoom = minZoom;
					if (zoom > maxZoom)
						zoom = maxZoom;

					realZoomFactor = zoom / originalZoom;

					setZoom(zoom);

					Point2D scaledLoc = new Point2D.Double(mouseLocOnCanvas.getX() * realZoomFactor,
							mouseLocOnCanvas.getY() * realZoomFactor);

					Point2D mouseLocOnPanel = new Point2D.Double(scaledLoc.getX() + paneWidth / 2.0,
							scaledLoc.getY() + paneHeight / 2.0);

					setViewPosition(new Point((int) (mouseLocOnPanel.getX() - mouseLoc.getX()),
							(int) (mouseLocOnPanel.getY() - mouseLoc.getY())));
					repaint();
				}
			} else if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
				setViewPosX(viewPosX + (int) (maxScrollX * SCROLL_SPEED * sign));
				repaint();
			} else {
				setViewPosY(viewPosY + (int) (maxScrollY * SCROLL_SPEED * sign));
				repaint();
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && SwingUtilities.isMiddleMouseButton(e)) {
					setZoom(1);
					recenter();
				}
			}

			@Override public void mousePressed(MouseEvent e) {
				updateCursor(e);
				prevDragLocation = e.getLocationOnScreen();
			}

			@Override public void mouseReleased(MouseEvent e) {
				resetCursor();
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override public void mouseDragged(MouseEvent e) {
				updateCursor(e);
				Point currentLocation = e.getLocationOnScreen();

				if (SwingUtilities.isMiddleMouseButton(e)) {
					setViewPosX(viewPosX + prevDragLocation.x - currentLocation.x);
					setViewPosY((viewPosY + prevDragLocation.y - currentLocation.y));
					repaint();
				}

				prevDragLocation = e.getLocationOnScreen();
			}
		});

		addComponentListener(new ComponentAdapter() {
			private int counter = 0;

			@Override public void componentResized(ComponentEvent e) {
				recalculateBounds();
				if (counter < 2) {
					recenter();
					counter++;
					repaint();
				}
			}
		});
	}

	private boolean isEventValid(MouseEvent e, JZoomport zoomport) {
		return e != null && zoomport.getToZoom() != null;
	}

	private void registerChildListeners() {
		addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent e) {
				Arrays.stream(toZoom.getMouseListeners()).forEach(listener -> {
					if (isEventValid(e, JZoomport.this))
						listener.mouseClicked(new ZoomedMouseEvent(e, JZoomport.this));
				});
				repaint();
			}

			@Override public void mousePressed(MouseEvent e) {
				Arrays.stream(toZoom.getMouseListeners()).forEach(listener -> {
					if (isEventValid(e, JZoomport.this))
						listener.mousePressed(new ZoomedMouseEvent(e, JZoomport.this));
				});
				repaint();
			}

			@Override public void mouseReleased(MouseEvent e) {
				Arrays.stream(toZoom.getMouseListeners()).forEach(listener -> {
					if (isEventValid(e, JZoomport.this))
						listener.mouseReleased(new ZoomedMouseEvent(e, JZoomport.this));
				});
				repaint();
			}

			@Override public void mouseEntered(MouseEvent e) {
				Arrays.stream(toZoom.getMouseListeners()).forEach(listener -> {
					if (isEventValid(e, JZoomport.this))
						listener.mouseEntered(new ZoomedMouseEvent(e, JZoomport.this));
				});
				repaint();
			}

			@Override public void mouseExited(MouseEvent e) {
				Arrays.stream(toZoom.getMouseListeners()).forEach(listener -> {
					if (isEventValid(e, JZoomport.this))
						listener.mouseExited(new ZoomedMouseEvent(e, JZoomport.this));
				});
				repaint();
			}
		});

		addMouseWheelListener(e -> {
			Arrays.stream(toZoom.getMouseWheelListeners()).forEach(listener -> {
				if (isEventValid(e, JZoomport.this))
					listener.mouseWheelMoved(new ZoomedMouseWheelEvent(e, JZoomport.this));
			});
			repaint();
		});

		addMouseMotionListener(new MouseMotionListener() {
			@Override public void mouseDragged(MouseEvent e) {
				Arrays.stream(toZoom.getMouseMotionListeners()).forEach(listener -> {
					if (isEventValid(e, JZoomport.this))
						listener.mouseDragged(new ZoomedMouseEvent(e, JZoomport.this));
				});
				repaint();
			}

			@Override public void mouseMoved(MouseEvent e) {
				Arrays.stream(toZoom.getMouseMotionListeners()).forEach(listener -> {
					if (isEventValid(e, JZoomport.this))
						listener.mouseMoved(new ZoomedMouseEvent(e, JZoomport.this));
				});
				repaint();
			}
		});

		addKeyListener(new KeyListener() {
			@Override public void keyTyped(KeyEvent e) {
				Arrays.stream(toZoom.getKeyListeners()).forEach(listener -> listener.keyTyped(e));
				repaint();
			}

			@Override public void keyPressed(KeyEvent e) {
				Arrays.stream(toZoom.getKeyListeners()).forEach(listener -> listener.keyPressed(e));
				repaint();
			}

			@Override public void keyReleased(KeyEvent e) {
				Arrays.stream(toZoom.getKeyListeners()).forEach(listener -> listener.keyReleased(e));
				repaint();
			}
		});

		addFocusListener(new FocusListener() {
			@Override public void focusGained(FocusEvent e) {
				Arrays.stream(toZoom.getFocusListeners()).forEach(listener -> listener.focusGained(e));
				repaint();
			}

			@Override public void focusLost(FocusEvent e) {
				Arrays.stream(toZoom.getFocusListeners()).forEach(listener -> listener.focusLost(e));
				repaint();
			}
		});

		addHierarchyListener(e -> {
			Arrays.stream(toZoom.getHierarchyListeners()).forEach(listener -> listener.hierarchyChanged(e));
			repaint();
		});

		addHierarchyBoundsListener(new HierarchyBoundsListener() {
			@Override public void ancestorMoved(HierarchyEvent e) {
				Arrays.stream(toZoom.getHierarchyBoundsListeners()).forEach(listener -> listener.ancestorMoved(e));
				repaint();
			}

			@Override public void ancestorResized(HierarchyEvent e) {
				Arrays.stream(toZoom.getHierarchyBoundsListeners()).forEach(listener -> listener.ancestorResized(e));
				repaint();
			}
		});
	}

	/**
	 * Returns current zoom factor
	 *
	 * @return double zoom factor
	 */
	public final double getZoom() {
		return zoom;
	}

	/**
	 * Sets zoom value
	 */
	public final void setZoom(double zoom) {
		this.zoom = zoom;
		recalculateBounds();

		zoomPane.updateZoomDisplay(zoom);
	}

	/**
	 * Sets zoom value to fit canvas and center
	 */
	public final void fitZoom() {
		fitZoom(20, 20);
	}

	/**
	 * Sets zoom value to fit canvas and center. Also adds some padding
	 */
	public final void fitZoom(int hpadding, int vpadding) {
		if (1.0 * toZoom.getWidth() / toZoom.getHeight() < 1.0 * (getWidth() - hpadding * 2) / (getHeight()
				- vpadding * 2))
			setZoom(1.0 * (getHeight() - vpadding * 2) / toZoom.getHeight());
		else
			setZoom(1.0 * (getWidth() - hpadding * 2) / toZoom.getWidth());
		recenter();
		recalculateBounds();
	}

	/**
	 * Sets zoom value keeping the center still
	 */
	public final void setZoomAroundCenter(double zoom) {
		Point2D mouseLocOnCanvas = new Point2D.Double(getWidth() / 2.0 + viewPosX - paneWidth / 2.0,
				getHeight() / 2.0 + viewPosY - paneHeight / 2.0);

		double realZoomFactor = zoom / this.zoom;
		setZoom(zoom);

		setViewPosition(new Point((int) (mouseLocOnCanvas.getX() * realZoomFactor + paneWidth / 2.0 - getWidth() / 2.0),
				(int) (mouseLocOnCanvas.getY() * realZoomFactor + paneHeight / 2.0 - getHeight() / 2.0)));
		repaint();
	}

	public Point2D.Double getLocationOnCanvas(Point2D panelLocation) {
		return new Point2D.Double(panelLocation.getX() - canvasX, panelLocation.getY() - canvasY);
	}

	public Point2D.Double getLocationOnPanel(Point2D canvasLocation) {
		return new Point2D.Double(canvasLocation.getX() + canvasX, canvasLocation.getY() + canvasY);
	}

	/**
	 * Returns location on canvas
	 */
	Point2D.Double getLocationOnCanvasFromMiddle(Point2D panelLocation) {
		Point2D p = getLocationOnCanvas(panelLocation);
		return new Point2D.Double(p.getX() - width / 2.0, p.getY() - height / 2.0);
	}

	/**
	 * Returns location on panel
	 */
	Point2D.Double getLocationOnPanelFromMiddle(Point2D canvasLocation) {
		Point2D p = getLocationOnPanel(canvasLocation);
		return new Point2D.Double(p.getX() + width / 2.0, p.getY() + height / 2.0);
	}

	/**
	 * If the zoomable element changes size when zoom or viewport size changes this has to be implemented for proper operation.
	 */
	public void recalculateBounds() {
		width = (int) (toZoom.getWidth() * zoom);
		height = (int) (toZoom.getHeight() * zoom);

		if (zoomPane != null) {
			if (width >= getWidth())
				paneWidth = width + getWidth();
			else
				paneWidth = getWidth() * 2;
			if (height >= getWidth())
				paneHeight = height + getHeight();
			else
				paneHeight = getHeight() * 2;
		} else {
			paneWidth = width;
			paneHeight = height;
		}

		canvasX = (paneWidth - width) / 2;
		canvasY = (paneHeight - height) / 2;

		maxScrollX = paneWidth - getWidth();
		maxScrollY = paneHeight - getHeight();

		zoomPane.getHorizontalScrollBar().setMaximum(paneWidth);
		zoomPane.getVerticalScrollBar().setMaximum(paneHeight);
		zoomPane.getHorizontalScrollBar().setVisibleAmount(getWidth());
		zoomPane.getVerticalScrollBar().setVisibleAmount(getHeight());

		revalidate();
		toZoom.revalidate();
	}

	@Override public void paint(Graphics g) {
		Graphics2D graphics2D = (Graphics2D) g;
		graphics2D.translate(canvasX - viewPosX, canvasY - viewPosY);
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

		if (toZoom instanceof IZoomable)
			((IZoomable) toZoom).paintPreZoom(g, new Dimension(width, height));

		AffineTransform preZoomTransform = graphics2D.getTransform();
		graphics2D.scale(zoom, zoom);
		toZoom.paint(g);

		if (toZoom instanceof IZoomable) {
			graphics2D.setTransform(preZoomTransform);
			((IZoomable) toZoom).paintPostZoom(g, new Dimension(width, height));
		}

		graphics2D.dispose();
	}

	public void setViewPosition(Point point) {
		setViewPosX(point.x);
		setViewPosY(point.y);
	}

	public void setViewPosX(double viewPosX) {
		if (viewPosX < 0)
			this.viewPosX = 0;
		else if (viewPosX > maxScrollX)
			this.viewPosX = maxScrollX;
		else
			this.viewPosX = viewPosX;
		setUpdateScrollbarX(false);
		zoomPane.getHorizontalScrollBar().setValue((int) this.viewPosX);
	}

	public void setViewPosY(double viewPosY) {
		if (viewPosY < 0)
			this.viewPosY = 0;
		else if (viewPosY > maxScrollY)
			this.viewPosY = maxScrollY;
		else
			this.viewPosY = viewPosY;
		setUpdateScrollbarY(false);
		zoomPane.getVerticalScrollBar().setValue((int) this.viewPosY);
	}

	public double getViewPositionX() {
		return viewPosX;
	}

	public double getViewPositionY() {
		return viewPosY;
	}

	public Point2D getViewPositionPoint() {
		return new Point2D.Double(viewPosX, viewPosY);
	}

	public int getCanvasX() {
		return canvasX;
	}

	public int getCanvasY() {
		return canvasY;
	}

	public Point getCanvasPoint() {
		return new Point(canvasX, canvasY);
	}

	public int getPaneWidth() {
		return paneWidth;
	}

	public int getPaneHeight() {
		return paneHeight;
	}

	public double getMinZoom() {
		return minZoom;
	}

	public void setMinZoom(double minZoom) {
		this.minZoom = minZoom;
	}

	public double getMaxZoom() {
		return maxZoom;
	}

	public void setMaxZoom(double maxZoom) {
		this.maxZoom = maxZoom;
	}

	public double[] getZoomPresets() {
		return zoomPresets;
	}

	public void setZoomPresets(double[] zoomPresets) {
		this.zoomPresets = zoomPresets;
	}

	public double getZoomFactor() {
		return zoomFactor;
	}

	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
	}

	private void updateCursor(MouseEvent event) {
		if (first) {
			lastCursor = getCursor();
			first = false;
		}
		if (SwingUtilities.isMiddleMouseButton(event))
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}

	private void resetCursor() {
		setCursor(lastCursor);
		first = true;
	}

	public void recenter() {
		setViewPosition(new Point((maxScrollX) / 2, (maxScrollY) / 2));
		repaint();
	}

	public boolean isUpdateScrollbarX() {
		return updateScrollbarX;
	}

	public void setUpdateScrollbarX(boolean updateScrollbarX) {
		this.updateScrollbarX = updateScrollbarX;
	}

	public boolean isUpdateScrollbarY() {
		return updateScrollbarY;
	}

	public void setUpdateScrollbarY(boolean updateScrollbarY) {
		this.updateScrollbarY = updateScrollbarY;
	}

	public JComponent getToZoom() {
		return toZoom;
	}
}
