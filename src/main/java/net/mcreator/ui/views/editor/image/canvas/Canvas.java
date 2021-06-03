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

import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.layer.LayerPanel;
import net.mcreator.ui.views.editor.image.tool.tools.Shape;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;
import net.mcreator.ui.views.editor.image.versioning.change.Addition;
import net.mcreator.ui.views.editor.image.versioning.change.CanvasResize;
import net.mcreator.ui.views.editor.image.versioning.change.Removal;
import net.mcreator.util.ArrayListListModel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class Canvas extends ArrayListListModel<Layer> {
	private int width;
	private int height;
	private final LayerPanel layerPanel;
	private final VersionManager versionManager;
	private CanvasRenderer canvasRenderer;

	private boolean drawPreview;
	private MouseEvent previewEvent;
	private Shape shape;
	private int size;

	private Image previewImage;
	private boolean drawCustomPreview;

	public Canvas(int width, int height, LayerPanel layerPanel, VersionManager versionManager) {
		this.width = width;
		this.height = height;
		this.layerPanel = layerPanel;
		this.versionManager = versionManager;
		layerPanel.setCanvas(this);
		layerPanel.updateSelection();
	}

	public boolean add(Layer layer, UUID group) {
		boolean success = super.add(layer);
		layer.setCanvas(this);
		layerPanel.select(indexOf(layer));
		Addition addition = new Addition(this, layer);
		addition.setUUID(group);
		versionManager.addRevision(addition);
		return success;
	}

	@Override public boolean add(Layer layer) {
		boolean success = super.add(layer);
		layer.setCanvas(this);
		layerPanel.select(indexOf(layer));
		versionManager.addRevision(new Addition(this, layer));
		return success;
	}

	@Override public void add(int index, Layer element) {
		super.add(index, element);
		element.setCanvas(this);
		layerPanel.select(indexOf(element));
	}

	@Override public Layer set(int index, Layer layer) {
		Layer inserted = super.set(index, layer);
		layer.setCanvas(this);
		layerPanel.select(indexOf(layer));
		return inserted;
	}

	public Layer remove(int index, UUID group) {
		Removal removal = new Removal(this, get(index));
		Layer removed = super.remove(index);
		layerPanel.select(index - 1);
		removal.setUUID(group);
		versionManager.addRevision(removal);
		return removed;
	}

	@Override public Layer remove(int index) {
		versionManager.addRevision(new Removal(this, get(index)));
		Layer removed = super.remove(index);
		layerPanel.select(index - 1);
		return removed;
	}

	public Layer removeNR(int index) {
		Layer removed = super.remove(index);
		layerPanel.select(index - 1);
		return removed;
	}

	@Override public boolean moveUp(int index) {
		boolean mu = super.moveUp(index);
		if (mu)
			layerPanel.select(index - 1);
		return mu;
	}

	@Override public boolean moveDown(int index) {
		boolean md = super.moveDown(index);
		if (md)
			layerPanel.select(index + 1);
		return md;
	}

	public void update(Layer layer) {
		update(indexOf(layer));
	}

	public void update(int index) {
		fireContentsChanged(this, index, index);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public CanvasRenderer getCanvasRenderer() {
		return canvasRenderer;
	}

	void setCanvasRenderer(CanvasRenderer canvasRenderer) {
		this.canvasRenderer = canvasRenderer;
	}

	public void updateCustomPreview(MouseEvent event, Shape shape, int size) {
		previewEvent = event;
		this.shape = shape;
		this.size = size;
	}

	public void enablePreview(boolean drawPreview) {
		this.drawPreview = drawPreview;
	}

	public void updateCustomPreview(MouseEvent e, Image image) {
		previewEvent = e;
		this.previewImage = image;
	}

	public void enableCustomPreview(boolean drawPreview) {
		this.drawCustomPreview = drawPreview;
	}

	public boolean isDrawPreview() {
		return drawPreview;
	}

	public MouseEvent getPreviewEvent() {
		return previewEvent;
	}

	public Shape getShape() {
		return shape;
	}

	public int getToolSize() {
		return size;
	}

	public Image getPreviewImage() {
		return previewImage;
	}

	public boolean isDrawCustomPreview() {
		return drawCustomPreview;
	}

	public Layer selected() {
		return layerPanel.selected();
	}

	public void setSize(int width, int height, UUID group) {
		CanvasResize canvasResize = new CanvasResize(this, layerPanel.selected(), width, height);
		canvasResize.setUUID(group);
		versionManager.addRevision(canvasResize);
		this.width = width;
		this.height = height;
		canvasRenderer.recalculateBounds();
		canvasRenderer.repaint();
	}

	public void setSize(int width, int height) {
		CanvasResize canvasResize = new CanvasResize(this, layerPanel.selected(), width, height);
		versionManager.addRevision(canvasResize);
		this.width = width;
		this.height = height;
		canvasRenderer.recalculateBounds();
		canvasRenderer.repaint();
	}
}