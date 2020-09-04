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

package net.mcreator.ui.views.editor.image.canvas;

import net.mcreator.ui.component.zoompane.IZoomable;
import net.mcreator.ui.component.zoompane.JZoomPane;
import net.mcreator.ui.component.zoompane.ZoomedMouseEvent;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.tool.tools.Shape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class CanvasRenderer extends JComponent implements IZoomable {
	private static final int CHECKERBOARD_TILE_SIZE = 8;
	private static final int OUTLINE_LINE_LENGTH = CHECKERBOARD_TILE_SIZE / 2;

	private Canvas canvas;
	private JZoomPane jZoomPane;

	private TexturePaint checkerboard;
	private Stroke dashed;

	public CanvasRenderer(ImageMakerView imageMakerView) {
		rebuildCheckerboardPattern();
		rebuildOutlineStroke();
		addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent e) {
				imageMakerView.mouseClicked(e);
			}

			@Override public void mousePressed(MouseEvent e) {
				imageMakerView.mousePressed(e);
			}

			@Override public void mouseReleased(MouseEvent e) {
				imageMakerView.mouseReleased(e);
			}

			@Override public void mouseEntered(MouseEvent e) {
				imageMakerView.mouseEntered(e);
			}

			@Override public void mouseExited(MouseEvent e) {
				imageMakerView.mouseExited(e);
			}
		});

		addMouseMotionListener(new MouseMotionListener() {
			@Override public void mouseDragged(MouseEvent e) {
				imageMakerView.mouseDragged(e);
			}

			@Override public void mouseMoved(MouseEvent e) {
				imageMakerView.mouseMoved(e);
			}
		});
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
		canvas.setCanvasRenderer(this);
	}

	@Override public void repaint() {
		jZoomPane.repaint();
	}

	@Override public void paintPreZoom(Graphics g, Dimension d) {
		drawCheckerboard((Graphics2D) g, d);
	}

	@Override public void paint(Graphics g) {
		Graphics2D graphics2D = (Graphics2D) g;
		graphics2D.drawImage(render(), 0, 0, null);
	}

	@Override public void paintPostZoom(Graphics g, Dimension d) {
		if (canvas.isDrawPreview() && canvas.getPreviewEvent() != null) {

			ZoomedMouseEvent mouseEvent = (ZoomedMouseEvent) canvas.getPreviewEvent();
			Double mex = mouseEvent.getRawXN();
			Double mey = mouseEvent.getRawYN();
			if (mex != null && mey != null) {
				double zoom = jZoomPane.getZoomport().getZoom();
				Shape shape = canvas.getShape();

				int size = canvas.getToolSize();
				int scaledSize = (int) Math.round(size * zoom);

				Graphics2D graphics2D = (Graphics2D) g;
				graphics2D.setColor((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));

				Stroke original = graphics2D.getStroke();
				Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 },
						0);

				graphics2D.setStroke(dashed);

				int x, y;
				if (size % 2 == 1) {
					x = (int) ((mouseEvent.getX() - (int) (size / 2.0)) * zoom);
					y = (int) ((mouseEvent.getY() - (int) (size / 2.0)) * zoom);
				} else {
					x = (int) ((int) (mouseEvent.getRawX() / zoom + 0.5 - (int) (size / 2.0)) * zoom);
					y = (int) ((int) (mouseEvent.getRawY() / zoom + 0.5 - (int) (size / 2.0)) * zoom);
				}
				switch (shape) {
				case CIRCLE:
				case RING:
					graphics2D.drawOval(x, y, scaledSize, scaledSize);
					break;
				case SQUARE:
				case FRAME:
					graphics2D.drawRect(x, y, scaledSize, scaledSize);
					break;
				}
				graphics2D.setStroke(original);
			}
		} else if (canvas.isDrawCustomPreview() && canvas.getPreviewImage() != null
				&& canvas.getPreviewEvent() != null) {
			Image image = canvas.getPreviewImage();
			ZoomedMouseEvent mouseEvent = (ZoomedMouseEvent) canvas.getPreviewEvent();
			Double mex = mouseEvent.getRawXN();
			Double mey = mouseEvent.getRawYN();
			if (mex != null && mey != null) {

				double zoom = jZoomPane.getZoomport().getZoom();

				int x, y;
				int width = image.getWidth(null), height = image.getHeight(null);

				if (width % 2.0 == 1) {
					x = (int) ((mouseEvent.getX() - width / 2.0) * zoom);
				} else {
					x = (int) ((int) (mex / zoom + 0.5 - width / 2.0) * zoom);
				}

				if (height % 2.0 == 1) {
					y = (int) ((mouseEvent.getY() - height / 2.0) * zoom);
				} else {
					y = (int) ((int) (mey / zoom + 0.5 - height / 2.0) * zoom);
				}

				g.drawImage(image, x, y, (int) Math.round(width * zoom), (int) Math.round(height * zoom), null);
			}
		}
		Layer outline = canvas.selected();
		if (outline != null) {
			double zoom = jZoomPane.getZoomport().getZoom();
			int x = (int) Math.round(outline.getX() * zoom), y = (int) Math.round(outline.getY() * zoom);
			int width = (int) Math.round(outline.getWidth() * zoom), height = (int) Math
					.round(outline.getHeight() * zoom);
			drawOutline((Graphics2D) g, x, y, width, height);
		}
	}

	@Override public void setZoomPane(JZoomPane jZoomPane) {
		this.jZoomPane = jZoomPane;
	}

	@Override public int getWidth() {
		return canvas.getWidth();
	}

	@Override public int getHeight() {
		return canvas.getHeight();
	}

	public BufferedImage render() {
		BufferedImage layerStack = new BufferedImage(canvas.getWidth(), canvas.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		if (canvas != null) {
			Graphics2D layerStackGraphics2D = layerStack.createGraphics();
			for (Layer layer : canvas)
				if (layer.isVisible()) {
					layerStackGraphics2D.drawImage(layer.mergeOverlay(false), null, layer.getX(), layer.getY());
				}
			layerStackGraphics2D.dispose();
		}
		return layerStack;
	}

	private void rebuildCheckerboardPattern() {
		BufferedImage pattern = new BufferedImage(CHECKERBOARD_TILE_SIZE * 2, CHECKERBOARD_TILE_SIZE * 2,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = pattern.createGraphics();
		g2d.setPaint(Color.GRAY);
		g2d.fillRect(0, 0, CHECKERBOARD_TILE_SIZE * 2, CHECKERBOARD_TILE_SIZE * 2);
		g2d.setPaint(Color.LIGHT_GRAY);
		g2d.fillRect(0, CHECKERBOARD_TILE_SIZE, CHECKERBOARD_TILE_SIZE, CHECKERBOARD_TILE_SIZE);
		g2d.fillRect(CHECKERBOARD_TILE_SIZE, 0, CHECKERBOARD_TILE_SIZE, CHECKERBOARD_TILE_SIZE);
		g2d.dispose();
		checkerboard = new TexturePaint(pattern, new Rectangle(0, 0, pattern.getWidth(), pattern.getHeight()));
	}

	private void rebuildOutlineStroke() {
		dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
				new float[] { OUTLINE_LINE_LENGTH }, 0);
	}

	private void drawCheckerboard(Graphics2D graphics2D, Dimension d) {
		graphics2D.setPaint(checkerboard);
		graphics2D.fillRect(0, 0, (int) d.getWidth(), (int) d.getHeight());
	}

	private void drawOutline(Graphics2D graphics2D, int x, int y, int width, int height) {
		graphics2D.setPaint((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		graphics2D.drawRect(x, y, width - 1, height - 1);
		graphics2D.setPaint((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		graphics2D.setStroke(dashed);
		graphics2D.drawRect(x, y, width - 1, height - 1);
	}

	public void recalculateBounds() {
		jZoomPane.getZoomport().recalculateBounds();
	}
}
