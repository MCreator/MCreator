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
import net.mcreator.ui.views.editor.image.versioning.change.*;
import net.mcreator.util.ArrayListListModel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class Canvas extends ArrayListListModel<Layer> {
	private int width;
	private int height;
	private final LayerPanel layerPanel;
	private final VersionManager versionManager;
	private final Selection selection;
	private CanvasRenderer canvasRenderer;
	private boolean drawPreview;
	private MouseEvent previewEvent;
	private Shape shape;
	private int size;

	private Image previewImage;
	private boolean drawCustomPreview;
	private Layer floatingLayer;

	public Canvas(int width, int height, LayerPanel layerPanel, VersionManager versionManager) {
		this.width = width;
		this.height = height;
		this.layerPanel = layerPanel;
		this.versionManager = versionManager;
		this.selection = new Selection(this);
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
		floatingCheck(layer);
		return success;
	}

	public boolean add(Layer layer, int index, UUID group) {
		super.add(index, layer);
		layer.setCanvas(this);
		layerPanel.select(indexOf(layer));
		Addition addition = new Addition(this, layer);
		addition.setUUID(group);
		versionManager.addRevision(addition);
		floatingCheck(layer);
		return true;
	}

	@Override public boolean add(Layer layer) {
		super.add(0, layer);
		layer.setCanvas(this);
		layerPanel.select(indexOf(layer));
		versionManager.addRevision(new Addition(this, layer));
		floatingCheck(layer);
		return true;
	}

	public boolean addOnTop(Layer layer, UUID group) {
		super.add(layerPanel.selectedID(), layer);
		layer.setCanvas(this);
		layerPanel.select(indexOf(layer));
		Addition addition = new Addition(this, layer);
		addition.setUUID(group);
		versionManager.addRevision(addition);
		floatingCheck(layer);
		return true;
	}

	public boolean addOnTop(Layer layer) {
		return addOnTop(layer, null);
	}

	@Override public void add(int index, Layer element) {
		super.add(index, element);
		element.setCanvas(this);
		layerPanel.select(indexOf(element));
		floatingCheck(element);
	}

	/**
	 * Checks if the layer is floating and updates the floating layer.
	 *
	 * @param layer the layer to check
	 */
	public void floatingCheck(Layer layer) {
		if (layer == null || layer.isPasted()) {
			floatingLayer = layer;
			layerPanel.updateFloatingLayer();
		}
	}

	public Layer getFloatingLayer() {
		return floatingLayer;
	}

	/**
	 * Returns the selection.
	 *
	 * @return the selection
	 */
	public Selection getSelection() {
		return selection;
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
		floatingCheck(null);
		versionManager.refreshPreview();
		return removed;
	}

	public Layer remove(int index, UUID group, int toSelect) {
		Removal removal = new Removal(this, get(index), layerPanel, toSelect);
		Layer removed = super.remove(index);
		layerPanel.select(toSelect);
		removal.setUUID(group);
		versionManager.addRevision(removal);
		floatingCheck(null);
		versionManager.refreshPreview();
		return removed;
	}

	@Override public Layer remove(int index) {
		versionManager.addRevision(new Removal(this, get(index)));
		Layer removed = super.remove(index);
		layerPanel.select(Math.max(index - 1, 0));
		floatingCheck(null);
		versionManager.refreshPreview();
		return removed;
	}

	@Override public boolean remove(Object o) {
		int index = indexOf(o);
		versionManager.addRevision(new Removal(this, (Layer) o));
		boolean removed = super.remove(o);
		layerPanel.select(Math.max(index - 1, 0));
		floatingCheck(null);
		versionManager.refreshPreview();
		return removed;
	}

	public Layer removeNR(int index) {
		Layer removed = super.remove(index);
		layerPanel.select(index - 1);
		floatingCheck(null);
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

	public boolean mergeDown(int selectedID) {
		UUID uuid = UUID.randomUUID();
		return mergeDown(selectedID, uuid);
	}

	public boolean mergeDown(int selectedID, UUID uuid) {
		get(selectedID + 1).mergeOnTop(get(selectedID));

		Modification adt = new Modification(this, get(selectedID + 1));
		adt.setUUID(uuid);
		versionManager.addRevision(adt);

		boolean success = remove(selectedID, uuid, selectedID) != null;
		layerPanel.select(selectedID);
		return success;
	}

	public boolean consolidateFloating() {
		Consolidation consolidation = new Consolidation(this, floatingLayer);
		floatingLayer.setPasted(false);
		consolidation.setAfter(floatingLayer);
		versionManager.addRevision(consolidation);
		return true;
	}

	public boolean mergeSelectedDown() {
		return mergeDown(layerPanel.selectedID());
	}

	public void update(Layer layer) {
		update(indexOf(layer));
	}

	public void update(int index) {
		fireContentsChanged(this, index, index);
		canvasRenderer.repaint();
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

	public LayerPanel getLayerPanel() {
		return layerPanel;
	}

	public VersionManager getVersionManager() {
		return versionManager;
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