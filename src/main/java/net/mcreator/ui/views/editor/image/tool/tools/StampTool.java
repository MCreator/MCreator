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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StampTool extends AbstractDrawingTool {

	private double saturation = 1;
	private final JCheckBox colorize;
	private final JCheckBox colorType;

	private final JSlidingSpinner width;
	private final JSlidingSpinner height;

	private ResourcePointer selection;

	public StampTool(Canvas canvas, ColorSelector colorSelector, LayerPanel layerPanel, VersionManager versionManager,
			MCreator window) {
		super(L10N.t("dialog.image_maker.tools.types.stamp"),
				L10N.t("dialog.image_maker.tools.types.stamp_description"), UIRES.get("img_editor.stamp"), canvas,
				colorSelector, versionManager);
		setLayerPanel(layerPanel);

		width = new JSlidingSpinner(L10N.t("dialog.imageeditor.width"), 16, 1, 10000, 1);
		height = new JSlidingSpinner(L10N.t("dialog.imageeditor.height"), 16, 1, 10000, 1);

		List<ResourcePointer> templatesSorted = new ArrayList<>(ImageMakerTexturesCache.CACHE.keySet());
		templatesSorted.sort(Comparator.comparing(resourcePointer -> resourcePointer.identifier.toString()));
		selection = templatesSorted.getFirst();
		TextureSelectorDialog templateChooser = new TextureSelectorDialog(templatesSorted, window);

		JButton templateChooserButton = new JButton(
				new ImageIcon(ImageUtils.resize(ImageMakerTexturesCache.CACHE.get(selection).getImage(), 32)));
		templateChooserButton.setMargin(new Insets(0, 0, 0, 0));

		templateChooserButton.addActionListener(event -> templateChooser.setVisible(true));

		templateChooser.naprej.addActionListener(arg01 -> {
			templateChooser.dispose();
			selection = templateChooser.list.getSelectedValue();
			ImageIcon icon = ImageMakerTexturesCache.CACHE.get(selection);
			templateChooserButton.setIcon(new ImageIcon(ImageUtils.resize(icon.getImage(), 32)));
			width.setValue(icon.getIconWidth());
			height.setValue(icon.getIconHeight());
		});

		JSlidingSpinner saturationSlider = new JSlidingSpinner(L10N.t("dialog.image_maker.tools.types.saturation"));
		saturationSlider.addChangeListener(e -> saturation = saturationSlider.getValue() / 100.0);

		colorize = L10N.checkbox("dialog.image_maker.tools.types.colorize");
		colorType = L10N.checkbox("dialog.imageeditor.stamp_tool_lock_saturation_brightness");
		colorize.addActionListener(e -> colorType.setEnabled(colorize.isSelected()));
		colorize.setSelected(false);
		colorType.setEnabled(false);

		aliasing = L10N.checkbox("dialog.imageeditor.stamp_tool_smooth_resizing");
		connect = L10N.checkbox("dialog.imageeditor.stamp_tool_connect_points");
		connect.setSelected(true);

		settingsPanel.add(PanelUtils.westAndCenterElement(L10N.label("dialog.imageeditor.stamp_tool_base_texture"),
				PanelUtils.centerInPanel(templateChooserButton)));
		settingsPanel.add(width);
		settingsPanel.add(height);
		settingsPanel.add(saturationSlider);
		settingsPanel.add(colorize);
		settingsPanel.add(colorType);
		settingsPanel.add(aliasing);
		settingsPanel.add(connect);
	}

	@Override protected void preProcess(ZoomedMouseEvent e) {
		canvas.enableCustomPreview(false);
		layer.setOverlayOpacity(colorSelector.getForegroundColor().getAlpha() / 255.0);
	}

	@Override public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		canvas.enableCustomPreview(true);
		canvas.updateCustomPreview(e, getImage());
		first = true;
	}

	@Override protected Dimension getShapeDimension() {
		return new Dimension((int) Math.round(this.width.getValue()), (int) Math.round(this.height.getValue()));
	}

	@Override protected void doDrawing(Graphics2D g, int x, int y, Dimension d) {
		g.drawImage(getImage(), x, y, d.width, d.height, null);
	}

	@Override public void mouseEntered(MouseEvent e) {
		canvas.enableCustomPreview(true);
		super.mouseEntered(e);
	}

	@Override public void mouseExited(MouseEvent e) {
		canvas.enablePreview(false);
		canvas.getImageMakerView().getCanvasRenderer().repaint();
		super.mouseExited(e);
	}

	@Override public void toolEnabled(ToolActivationEvent e) {
		canvas.enableCustomPreview(true);
		super.toolEnabled(e);
	}

	@Override public void toolDisabled(ToolActivationEvent e) {
		canvas.enableCustomPreview(false);
		canvas.getImageMakerView().getCanvasRenderer().repaint();
		super.toolDisabled(e);
	}

	@Override public void mouseMoved(MouseEvent e) {
		canvas.updateCustomPreview(e, getImage());
	}

	public Image getImage() {
		int width = (int) Math.round(this.width.getValue()), height = (int) Math.round(this.height.getValue());
		Image image;

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
