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
import net.mcreator.ui.laf.themes.Theme;
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

	private static final Color FLOATING_LAYER_OUTLINE = new Color(0x8D7DD2);

	private Canvas canvas;
	private JZoomPane jZoomPane;

	private TexturePaint checkerboard;
	private Stroke dashed, dashed_animated, shadow;
	private float stroke_phase = 0;
	private Color shadowColor;

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
				graphics2D.setColor(Theme.current().getAltForegroundColor());

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
				int centerX = width / 2, centerY = height / 2;

				if (width % 2.0 == 1) {
					x = (int) ((mouseEvent.getX() - centerX) * zoom);
				} else {
					x = (int) Math.round(Math.floor((mex / zoom + 0.5 - centerX)) * zoom);
				}

				if (height % 2.0 == 1) {
					y = (int) ((mouseEvent.getY() - centerY) * zoom);
				} else {
					y = (int) Math.round(Math.floor((mey / zoom + 0.5 - centerY)) * zoom);
				}

				g.drawImage(image, x, y, (int) Math.round(width * zoom), (int) Math.round(height * zoom), null);
			}
		}
		double zoom = jZoomPane.getZoomport().getZoom();
		Layer outline = canvas.selected();
		if (outline != null) {
			int x = (int) Math.round(outline.getX() * zoom);
			int y = (int) Math.round(outline.getY() * zoom);

			int width = (int) Math.round(outline.getWidth() * zoom);
			int height = (int) Math.round(outline.getHeight() * zoom);

			drawOutline((Graphics2D) g, x, y, width, height, outline.isPasted(), false);
		}

		Selection selection = canvas.getSelection();
		if (selection.getEditing() != SelectedBorder.NONE) {
			selection.drawHandles((Graphics2D) g);

			int x = (int) Math.round(selection.getLeft() * zoom);
			int y = (int) Math.round(selection.getTop() * zoom);

			int width = (int) Math.round((selection.getWidth()) * zoom);
			int height = (int) Math.round((selection.getHeight()) * zoom);

			drawOutline((Graphics2D) g, x, y, width, height, true, true);
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

	public double getZoom() {
		return jZoomPane.getZoomport().getZoom();
	}

	public BufferedImage render() {
		BufferedImage layerStack = new BufferedImage(canvas.getWidth(), canvas.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		if (canvas != null) {
			Graphics2D layerStackGraphics2D = layerStack.createGraphics();
			for (int i = canvas.size() - 1; i >= 0; i--) {
				Layer layer = canvas.get(i);
				if (layer.isVisible()) {
					layerStackGraphics2D.drawImage(layer.mergeOverlay(false), null, layer.getX(), layer.getY());
				}
			}
			layerStackGraphics2D.dispose();
		}
		return layerStack;
	}

	private void rebuildCheckerboardPattern() {
		checkerboard = buildCheckerboardPattern();
	}

	public static TexturePaint buildCheckerboardPattern() {
		BufferedImage pattern = new BufferedImage(CHECKERBOARD_TILE_SIZE * 2, CHECKERBOARD_TILE_SIZE * 2,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = pattern.createGraphics();
		g2d.setPaint(Color.GRAY);
		g2d.fillRect(0, 0, CHECKERBOARD_TILE_SIZE * 2, CHECKERBOARD_TILE_SIZE * 2);
		g2d.setPaint(Color.LIGHT_GRAY);
		g2d.fillRect(0, CHECKERBOARD_TILE_SIZE, CHECKERBOARD_TILE_SIZE, CHECKERBOARD_TILE_SIZE);
		g2d.fillRect(CHECKERBOARD_TILE_SIZE, 0, CHECKERBOARD_TILE_SIZE, CHECKERBOARD_TILE_SIZE);
		g2d.dispose();
		return new TexturePaint(pattern, new Rectangle(0, 0, pattern.getWidth(), pattern.getHeight()));
	}

	private void rebuildOutlineStroke() {
		// Stroke building
		dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
				new float[] { OUTLINE_LINE_LENGTH, OUTLINE_LINE_LENGTH }, 0);
		dashed_animated = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
				new float[] { OUTLINE_LINE_LENGTH, OUTLINE_LINE_LENGTH }, stroke_phase);
		shadow = new BasicStroke(3);

		// Builds the semi-transparent color for the outline shadow
		Color baseColor = Theme.current().getSecondAltBackgroundColor();
		shadowColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 120);
	}

	public void addPhaseToOutline(float phase) {
		float sp = (stroke_phase - phase / ((float) Math.PI) * OUTLINE_LINE_LENGTH) % (OUTLINE_LINE_LENGTH * 2);
		if (sp < 0)
			stroke_phase = OUTLINE_LINE_LENGTH * 2 + sp;
		else
			stroke_phase = sp;
		dashed_animated = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
				new float[] { OUTLINE_LINE_LENGTH, OUTLINE_LINE_LENGTH }, stroke_phase);
	}

	private void drawCheckerboard(Graphics2D graphics2D, Dimension d) {
		graphics2D.setPaint(checkerboard);
		graphics2D.fillRect(0, 0, (int) d.getWidth(), (int) d.getHeight());
	}

	private void drawOutline(Graphics2D graphics2D, int x, int y, int width, int height, boolean pasted,
			boolean animated) {
		// Save the previous stroke to avoid unwanted changes
		Stroke prevStroke = graphics2D.getStroke();

		// The base for the outline
		graphics2D.setPaint(Theme.current().getSecondAltBackgroundColor());
		graphics2D.drawRect(x, y, width - 1, height - 1);

		// Shadow after the black stroke to avoid shifting strokes
		// Shouldn't be visible since it's black on black
		graphics2D.setStroke(shadow);
		graphics2D.setPaint(shadowColor);
		graphics2D.drawRect(x, y, width - 1, height - 1);

		// If pasted, the outline is brighter
		if (pasted && animated)
			graphics2D.setPaint(Theme.current().getAltForegroundColor());
		else if (pasted)
			graphics2D.setPaint(FLOATING_LAYER_OUTLINE);
		else
			graphics2D.setPaint(Theme.current().getInterfaceAccentColor());

		// Use the animated stroke if the flag is set
		if (animated)
			graphics2D.setStroke(dashed_animated);
		else
			graphics2D.setStroke(dashed);

		// Draw the outline
		graphics2D.drawRect(x, y, width - 1, height - 1);

		// Return the previous stroke to avoid unwanted changes
		graphics2D.setStroke(prevStroke);
	}

	public void recalculateBounds() {
		jZoomPane.getZoomport().recalculateBounds();
	}
}
