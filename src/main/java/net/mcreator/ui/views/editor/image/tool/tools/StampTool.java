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

package net.mcreator.ui.views.editor.image.tool.tools;

import net.mcreator.io.ResourcePointer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.zoompane.ZoomedMouseEvent;
import net.mcreator.ui.dialogs.TextureSelectorDialog;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.layer.LayerPanel;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.tool.component.JSlidingSpinner;
import net.mcreator.ui.views.editor.image.tool.tools.event.ToolActivationEvent;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StampTool extends AbstractModificationTool {

	private double saturation = 1;
	private double opacity = 1;

	private Point prevPoint = null;

	private final JCheckBox aliasing;
	private final JCheckBox connect;
	private final JCheckBox colorize;
	private final JCheckBox colorType;

	private final JSlidingSpinner width;
	private final JSlidingSpinner height;

	private ResourcePointer selection;

	private boolean first = true;

	public StampTool(Canvas canvas, ColorSelector colorSelector, LayerPanel layerPanel, VersionManager versionManager,
			MCreator window) {
		super(L10N.t("dialog.imageeditor.stamp_tool_name"),
				L10N.t("dialog.imageeditor.stamp_tool_description"), UIRES.get("img_editor.stamp"), canvas,
				colorSelector, versionManager);
		setLayerPanel(layerPanel);

		width = new JSlidingSpinner("Width:", 16, 0, 10000, 1);
		height = new JSlidingSpinner("Height:", 16, 0, 10000, 1);

		List<ResourcePointer> templatesSorted = new ArrayList<>(ImageMakerTexturesCache.CACHE.keySet());
		templatesSorted.sort(Comparator.comparing(resourcePointer -> resourcePointer.identifier.toString()));
		selection = templatesSorted.get(0);
		TextureSelectorDialog templateChooser = new TextureSelectorDialog(templatesSorted, window);

		JButton templateChooserButton = new JButton(
				new ImageIcon(ImageUtils.resize(ImageMakerTexturesCache.CACHE.get(selection).getImage(), 32)));
		templateChooserButton.setMargin(new Insets(0, 0, 0, 0));

		templateChooserButton.addActionListener(event -> templateChooser.setVisible(true));

		templateChooser.naprej.addActionListener(arg01 -> {
			templateChooser.setVisible(false);
			selection = templateChooser.list.getSelectedValue();
			ImageIcon icon = ImageMakerTexturesCache.CACHE.get(selection);
			templateChooserButton.setIcon(new ImageIcon(ImageUtils.resize(icon.getImage(), 32)));
			width.setValue(icon.getIconWidth());
			height.setValue(icon.getIconHeight());
		});

		JSlidingSpinner opacitySlider = new JSlidingSpinner("Opacity:");
		opacitySlider.addChangeListener(e -> opacity = opacitySlider.getValue() / 100.0);

		JSlidingSpinner saturationSlider = new JSlidingSpinner("Saturation:");
		saturationSlider.addChangeListener(e -> saturation = saturationSlider.getValue() / 100.0);

		colorize = L10N.checkbox("dialog.imageeditor.stamp_tool_colorize");
		colorType = L10N.checkbox("dialog.imageeditor.stamp_tool_lock_saturation_brightness");
		colorize.addActionListener(e -> colorType.setEnabled(colorize.isSelected()));
		colorize.setSelected(false);
		colorType.setEnabled(false);

		aliasing = L10N.checkbox("dialog.imageeditor.stamp_tool_smooth_resizing");
		connect = L10N.checkbox("dialog.imageeditor.stamp_tool_connect_points");
		connect.setSelected(true);

		settingsPanel.add(opacitySlider);
		settingsPanel.add(PanelUtils.westAndCenterElement(
				L10N.label("dialog.imageeditor.stamp_tool_base_texture"),
				PanelUtils.centerInPanel(templateChooserButton)));
		settingsPanel.add(width);
		settingsPanel.add(height);
		settingsPanel.add(saturationSlider);
		settingsPanel.add(colorize);
		settingsPanel.add(colorType);
		settingsPanel.add(aliasing);
		settingsPanel.add(connect);
	}

	@Override public boolean process(ZoomedMouseEvent e) {
		layer.setOverlayOpacity(opacity);
		if (layer.in(e.getX(), e.getY())) {
			int sx = e.getX() - layer.getX(), sy = e.getY() - layer.getY();
			Graphics2D graphics2D = layer.getOverlay().createGraphics();
			if (aliasing.isSelected())
				graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			draw(graphics2D, sx, sy, e.getRawX(), e.getRawY(), e.getZoom());
			if (((connect.isSelected() && !first) || e.isShiftDown()) && prevPoint != null) {
				int minx = Math.min(sx, prevPoint.x);
				int maxx = Math.max(sx, prevPoint.x);
				int miny = Math.min(sy, prevPoint.y);
				int maxy = Math.max(sy, prevPoint.y);

				if (sx == prevPoint.getX())
					for (int y = miny + 1; y < maxy; y++)
						draw(graphics2D, sx, y, e.getRawX(), y * e.getZoom(), e.getZoom());
				else if (sy == prevPoint.getY())
					for (int x = minx + 1; x < maxx; x++)
						draw(graphics2D, x, sy, x * e.getZoom(), e.getRawY(), e.getZoom());
				else {
					double part = 1;
					double distance = Point2D.distance(prevPoint.x, prevPoint.y, sx, sy);
					for (double t = 0; t < 1; t += 1 / (distance * part)) {
						int x = (int) Math.round((1 - t) * prevPoint.x + t * sx);
						int y = (int) Math.round((1 - t) * prevPoint.y + t * sy);
						draw(graphics2D, x, y, x * e.getZoom(), y * e.getZoom(), e.getZoom());
					}
				}
			}
			first = false;
			prevPoint = new Point(e.getX() - layer.getX(), e.getY() - layer.getY());
			graphics2D.dispose();
			canvas.getCanvasRenderer().repaint();
			return true;
		}
		return false;
	}

	@Override public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		first = true;
	}

	private void draw(Graphics2D g, int tx, int ty, double rx, double ry, double zoom) {
		int x, y;
		int width = (int) Math.round(this.width.getValue()), height = (int) Math.round(this.height.getValue());

		if (width % 2.0 == 1)
			x = tx - width / 2;
		else
			x = Math.round((int) (rx / zoom + 0.5)) - width / 2;

		if (height % 2.0 == 1)
			y = ty - height / 2;
		else
			y = Math.round((int) (ry / zoom + 0.5)) - height / 2;

		g.drawImage(getImage(), x, y, width, height, null);
	}

	@Override public void mouseEntered(MouseEvent e) {
		canvas.enableCustomPreview(true);
		super.mouseEntered(e);
	}

	@Override public void mouseExited(MouseEvent e) {
		canvas.enablePreview(false);
		canvas.getCanvasRenderer().repaint();
		super.mouseExited(e);
	}

	@Override public void toolEnabled(ToolActivationEvent e) {
		canvas.enableCustomPreview(true);
		super.toolEnabled(e);
	}

	@Override public void toolDisabled(ToolActivationEvent e) {
		canvas.enableCustomPreview(false);
		canvas.getCanvasRenderer().repaint();
		super.toolDisabled(e);
	}

	@Override public void mouseMoved(MouseEvent e) {
		canvas.updateCustomPreview(e, getImage());
	}

	public Image getImage() {
		int width = (int) Math.round(this.width.getValue()), height = (int) Math.round(this.height.getValue());
		BufferedImage image;

		if (aliasing.isSelected())
			image = ImageUtils.resizeAA(ImageMakerTexturesCache.CACHE.get(selection).getImage(), width, height);
		else
			image = ImageUtils.resize(ImageMakerTexturesCache.CACHE.get(selection).getImage(), width, height);

		ImageIcon imageIcon = ImageUtils.changeSaturation(new ImageIcon(image), (float) saturation);

		if (colorize.isSelected())
			imageIcon = ImageUtils.colorize(imageIcon, colorSelector.getForegroundColor(), !colorType.isSelected());

		return imageIcon.getImage();
	}
}
