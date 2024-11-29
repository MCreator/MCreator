/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.ColorUtils;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class SimpleLineChart extends JPanel {

	private final List<Double> xPoints;
	private final List<Double> yPoints;

	private int maxPoints = -1;

	private double[] xLimits = null;
	private double[] yLimits = null;

	private Color chartColor = Color.BLUE;

	private Point mousePoint = null;

	private final List<SimpleLineChart> linkedCharts = new ArrayList<>();

	private Function<double[], String[]> labelFormatter = null;

	public SimpleLineChart() {
		this.xPoints = Collections.synchronizedList(new LinkedList<>());
		this.yPoints = Collections.synchronizedList(new LinkedList<>());
		setOpaque(false);

		addMouseListener(new MouseAdapter() {
			@Override public void mouseExited(MouseEvent e) {
				setMousePoint(null);
				linkedCharts.forEach(c -> c.setMousePoint(null));
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override public void mouseMoved(MouseEvent e) {
				setMousePoint(e.getPoint());
				linkedCharts.forEach(c -> c.setMousePoint(e.getPoint()));
			}
		});
	}

	public void addLinkedChart(SimpleLineChart chart) {
		linkedCharts.add(chart);
	}

	public void setLabelFormatter(Function<double[], String[]> labelFormatter) {
		this.labelFormatter = labelFormatter;
	}

	private void setMousePoint(Point mousePoint) {
		this.mousePoint = mousePoint;
		repaint();
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}

	public void setChartColor(Color color) {
		this.chartColor = color;
	}

	public void addPoint(double x, double y) {
		synchronized (this) {
			if (maxPoints > 0 && xPoints.size() >= maxPoints) {
				xPoints.removeFirst();
				yPoints.removeFirst();
			}

			xPoints.add(x);
			yPoints.add(y);
		}
		repaint();
	}

	public void clear() {
		xPoints.clear();
		yPoints.clear();
		repaint();
	}

	public void setXLimits(double min, double max) {
		xLimits = new double[2];
		xLimits[0] = min;
		xLimits[1] = max;
		repaint();
	}

	public void setYLimits(double min, double max) {
		yLimits = new double[2];
		yLimits[0] = min;
		yLimits[1] = max;
	}

	@Override protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (xPoints.isEmpty() || yPoints.isEmpty()) {
			return;
		}

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = getWidth();
		int height = getHeight();

		// Find the min and max for scaling
		double xMin = (xLimits != null && maxPoints <= 0) ?
				xLimits[0] :
				xPoints.stream().min(Double::compare).orElse(0.0);
		double xMax = (xLimits != null && maxPoints <= 0) ?
				xLimits[1] :
				xPoints.stream().max(Double::compare).orElse(1.0);
		double yMin = yLimits != null ? yLimits[0] : yPoints.stream().min(Double::compare).orElse(0.0);
		double yMax = yLimits != null ? yLimits[1] : yPoints.stream().max(Double::compare).orElse(1.0);

		// User rolling min and max if in rolling mode
		if (maxPoints > 0) {
			xMin = xPoints.stream().min(Double::compare).orElse(0.0);
			xMax = xPoints.stream().max(Double::compare).orElse(1.0);
		}

		// Transform data points to pixel coordinates
		List<Point> points = transformToScreenCoordinates(xMin, xMax, yMin, yMax, width, height);

		// Create a Path2D to represent the filled area
		Path2D path = getPath2D(points, height);

		// Fill the area under the line
		g2d.setColor(ColorUtils.applyAlpha(chartColor, 100));
		g2d.fill(path);

		// Draw the line chart
		g2d.setColor(ColorUtils.applyAlpha(chartColor, 200));
		for (int i = 0; i < points.size() - 1; i++) {
			Point p1 = points.get(i);
			Point p2 = points.get(i + 1);
			g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
		}

		if (mousePoint != null) {
			int mousePointIdx = mouseToChartPoint(mousePoint, xMin, xMax, width);
			if (mousePointIdx >= 0 && mousePointIdx < points.size()) {
				g2d.setColor(Theme.current().getAltBackgroundColor());
				g2d.drawLine(points.get(mousePointIdx).x, 15, points.get(mousePointIdx).x, height);
				g2d.setColor(ColorUtils.applyAlpha(chartColor, 200));
				g2d.fillOval(points.get(mousePointIdx).x - 3, points.get(mousePointIdx).y - 3, 6, 6);
				g2d.setColor(ColorUtils.applyAlpha(chartColor, 255));
				g2d.drawOval(points.get(mousePointIdx).x - 3, points.get(mousePointIdx).y - 3, 6, 6);

				if (labelFormatter != null) {
					g2d.setColor(Theme.current().getForegroundColor());

					String[] pointLabels = labelFormatter.apply(
							new double[] { xPoints.get(mousePointIdx), yPoints.get(mousePointIdx) });
					String xLabel = pointLabels[0];
					String yLabel = pointLabels[1];

					Rectangle2D xLabelBounds = g2d.getFontMetrics().getStringBounds(xLabel, g2d);
					g2d.drawString(xLabel, points.get(mousePointIdx).x - (int) xLabelBounds.getWidth() / 2, 10);

					int yLabelOffset = -8;
					if (points.get(mousePointIdx).y < height * 0.45) {
						yLabelOffset = 8;
					}
					Rectangle2D yLabelBounds = g2d.getFontMetrics().getStringBounds(yLabel, g2d);
					g2d.drawString(yLabel, points.get(mousePointIdx).x - (int) yLabelBounds.getWidth() / 2,
							points.get(mousePointIdx).y + yLabelOffset);
				}
			}
		}
	}

	@Nonnull private static Path2D getPath2D(List<Point> points, int height) {
		Path2D path = new Path2D.Double();
		if (!points.isEmpty()) {
			Point firstPoint = points.getFirst();
			path.moveTo(firstPoint.x, height);
			path.lineTo(firstPoint.x, firstPoint.y);
			for (Point point : points)
				path.lineTo(point.x, point.y);
			Point lastPoint = points.getLast();
			path.lineTo(lastPoint.x, height);
			path.closePath();
		}
		return path;
	}

	private int mouseToChartPoint(Point mousePoint, double xMin, double xMax, double width) {
		int mouseX = mousePoint.x;
		double xNormalized = (double) mouseX / width;

		double xNormalizedOffset = getNormalizedXOffset();
		xNormalized = (xNormalized - xNormalizedOffset) / (1 - xNormalizedOffset);

		double xData = xNormalized * (xMax - xMin) + xMin;

		int closestIndex = -1;
		double minDistance = Double.MAX_VALUE;
		for (int i = 0; i < xPoints.size(); i++) {
			double x = xPoints.get(i);
			double distance = Math.abs(x - xData);
			if (distance < minDistance) {
				minDistance = distance;
				closestIndex = i;
			}
		}

		return closestIndex;
	}

	private List<Point> transformToScreenCoordinates(double xMin, double xMax, double yMin, double yMax, int width,
			int height) {
		List<Point> transformedPoints = new ArrayList<>();

		double xRange = xMax - xMin;
		double yRange = yMax - yMin;
		if (xRange == 0 || yRange == 0)
			return transformedPoints;

		double xNormalizedOffset = getNormalizedXOffset();

		for (int i = 0; i < xPoints.size(); i++) {
			double x = xPoints.get(i);
			double y = yPoints.get(i);

			if (x < xMin || x > xMax)
				continue;

			double xNormalized = (x - xMin) / xRange;
			xNormalized = xNormalizedOffset + xNormalized * (1 - xNormalizedOffset);

			double yNormalized = (y - yMin) / yRange;

			int xScreen = (int) (xNormalized * width);
			int yScreen = (int) (height - yNormalized * height);
			transformedPoints.add(new Point(xScreen, yScreen));
		}

		return transformedPoints;
	}

	private double getNormalizedXOffset() {
		double xNormalizedOffset = 0;
		if (maxPoints > 0 && xPoints.size() < maxPoints)
			xNormalizedOffset = (maxPoints - xPoints.size()) / (double) maxPoints;
		return xNormalizedOffset;
	}

}