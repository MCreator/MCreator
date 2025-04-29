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

import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.tool.tools.Shape;
import net.mcreator.ui.views.editor.image.versioning.change.*;
import net.mcreator.util.ArrayListListModel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class Canvas extends ArrayListListModel<Layer> {

	/*
	 * Transient references and fields
	 */
	// ImageMakerView reference (needs to be set right after creation)
	private transient ImageMakerView imageMakerView;

	// Preview fields
	private transient boolean drawPreview = false;
	private transient Shape shape = Shape.SQUARE;
	private transient int size = 1;

	// Preview image (if custom image is provided for preview)
	private transient Image previewImage;
	private transient boolean drawCustomPreview;

	// Preview changed event
	private transient MouseEvent previewEvent;

	// Pasted layer reference
	private transient Layer floatingLayer = null;

	/*
	 * Serialized references and fields
	 */
	// Canvas size
	private int width;
	private int height;

	// Selection object
	private final Selection selection = new Selection(this);

	public Canvas(ImageMakerView imageMakerView, int width, int height) {
		this.width = width;
		this.height = height;
		initReferences(imageMakerView);
	}

	private void initReferences(ImageMakerView imageMakerView) {
		this.imageMakerView = imageMakerView;
		imageMakerView.getLayerPanel().setCanvas(this);
		for (Layer layer : this) {
			layer.setCanvas(this);
			floatingCheck(layer);
		}
		selection.setCanvas(this);
		imageMakerView.getCanvasRenderer().setCanvas(this);
		imageMakerView.getToolPanel().setCanvas(this);
		imageMakerView.getLayerPanel().updateSelection();
	}

	public boolean add(Layer layer, UUID group) {
		boolean success = super.add(layer);
		layer.setCanvas(this);
		imageMakerView.getLayerPanel().select(indexOf(layer));
		Addition addition = new Addition(this, layer);
		addition.setUUID(group);
		imageMakerView.getVersionManager().addRevision(addition);
		floatingCheck(layer);
		return success;
	}

	public boolean add(Layer layer, int index, UUID group) {
		super.add(index, layer);
		layer.setCanvas(this);
		imageMakerView.getLayerPanel().select(indexOf(layer));
		Addition addition = new Addition(this, layer);
		addition.setUUID(group);
		imageMakerView.getVersionManager().addRevision(addition);
		floatingCheck(layer);
		return true;
	}

	@Override public boolean add(Layer layer) {
		super.add(0, layer);
		layer.setCanvas(this);
		imageMakerView.getLayerPanel().select(indexOf(layer));
		imageMakerView.getVersionManager().addRevision(new Addition(this, layer));
		floatingCheck(layer);
		return true;
	}

	public boolean addOnTop(Layer layer, UUID group) {
		super.add(imageMakerView.getLayerPanel().selectedID(), layer);
		layer.setCanvas(this);
		imageMakerView.getLayerPanel().select(indexOf(layer));
		Addition addition = new Addition(this, layer);
		addition.setUUID(group);
		imageMakerView.getVersionManager().addRevision(addition);
		floatingCheck(layer);
		return true;
	}

	public boolean addOnTop(Layer layer) {
		return addOnTop(layer, null);
	}

	@Override public void add(int index, Layer element) {
		super.add(index, element);
		element.setCanvas(this);
		imageMakerView.getLayerPanel().select(indexOf(element));
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
			imageMakerView.getLayerPanel().updateFloatingLayer();
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
		imageMakerView.getLayerPanel().select(indexOf(layer));
		return inserted;
	}

	public Layer remove(int index, UUID group) {
		Removal removal = new Removal(this, get(index));
		Layer removed = super.remove(index);
		imageMakerView.getLayerPanel().select(index - 1);
		removal.setUUID(group);
		imageMakerView.getVersionManager().addRevision(removal);
		floatingCheck(null);
		imageMakerView.getVersionManager().refreshPreview();
		return removed;
	}

	public Layer remove(int index, UUID group, int toSelect) {
		Removal removal = new Removal(this, get(index), imageMakerView.getLayerPanel(), toSelect);
		Layer removed = super.remove(index);
		imageMakerView.getLayerPanel().select(toSelect);
		removal.setUUID(group);
		imageMakerView.getVersionManager().addRevision(removal);
		floatingCheck(null);
		imageMakerView.getVersionManager().refreshPreview();
		return removed;
	}

	@Override public Layer remove(int index) {
		imageMakerView.getVersionManager().addRevision(new Removal(this, get(index)));
		Layer removed = super.remove(index);
		imageMakerView.getLayerPanel().select(Math.max(index - 1, 0));
		floatingCheck(null);
		imageMakerView.getVersionManager().refreshPreview();
		return removed;
	}

	@Override public boolean remove(Object o) {
		int index = indexOf(o);
		imageMakerView.getVersionManager().addRevision(new Removal(this, (Layer) o));
		boolean removed = super.remove(o);
		imageMakerView.getLayerPanel().select(Math.max(index - 1, 0));
		floatingCheck(null);
		imageMakerView.getVersionManager().refreshPreview();
		return removed;
	}

	/**
	 * Removes the layer at the specified index without adding a revision (hence NR - No Revision).
	 *
	 * @param index the index of the layer to remove
	 * @return the removed layer
	 */
	public Layer removeNR(int index) {
		Layer removed = super.remove(index);
		imageMakerView.getLayerPanel().select(index - 1);
		floatingCheck(null);
		return removed;
	}

	@Override public boolean moveUp(int index) {
		boolean mu = super.moveUp(index);
		if (mu)
			imageMakerView.getLayerPanel().select(index - 1);
		return mu;
	}

	@Override public boolean moveDown(int index) {
		boolean md = super.moveDown(index);
		if (md)
			imageMakerView.getLayerPanel().select(index + 1);
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
		imageMakerView.getVersionManager().addRevision(adt);

		boolean success = remove(selectedID, uuid, selectedID) != null;
		imageMakerView.getLayerPanel().select(selectedID);
		return success;
	}

	public boolean consolidateFloating() {
		if (floatingLayer != null) {
			Consolidation consolidation = new Consolidation(this, floatingLayer);
			floatingLayer.setPasted(false);
			consolidation.setAfter(floatingLayer);
			imageMakerView.getVersionManager().addRevision(consolidation);
			return true;
		}
		return false;
	}

	public boolean mergeSelectedDown() {
		return mergeDown(imageMakerView.getLayerPanel().selectedID());
	}

	public void update(Layer layer) {
		update(indexOf(layer));
	}

	public void update(int index) {
		fireContentsChanged(this, index, index);
		imageMakerView.getCanvasRenderer().repaint();
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

	public void updateCustomPreview(MouseEvent event, Shape shape, int size) {
		previewEvent = event;
		this.shape = shape;
		this.size = size;
	}

	public void enablePreview(boolean drawPreview) {
		this.drawPreview = drawPreview;
	}

	public void updateCustomPreview(MouseEvent event, Image image) {
		previewEvent = event;
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

	public void setSize(int width, int height, UUID group) {
		CanvasResize canvasResize = new CanvasResize(this, imageMakerView.getLayerPanel().selected(), width, height);
		canvasResize.setUUID(group);
		imageMakerView.getVersionManager().addRevision(canvasResize);
		this.width = width;
		this.height = height;
		imageMakerView.getCanvasRenderer().recalculateBounds();
		imageMakerView.getCanvasRenderer().repaint();
	}

	public void setSize(int width, int height) {
		CanvasResize canvasResize = new CanvasResize(this, imageMakerView.getLayerPanel().selected(), width, height);
		imageMakerView.getVersionManager().addRevision(canvasResize);
		this.width = width;
		this.height = height;
		imageMakerView.getCanvasRenderer().recalculateBounds();
		imageMakerView.getCanvasRenderer().repaint();
	}

	public ImageMakerView getImageMakerView() {
		return imageMakerView;
	}

}