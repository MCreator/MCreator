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

package net.mcreator.ui.views.editor.image.versioning;

import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.views.editor.image.layer.LayerPanel;
import net.mcreator.ui.views.editor.image.versioning.change.Change;
import net.mcreator.ui.views.editor.image.versioning.change.IVisualChange;
import net.mcreator.ui.views.editor.image.versioning.change.Modification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

public class VersionManager {
	private static final Logger LOG = LogManager.getLogger("Version Manager");

	private static final int MAX_HEAP_SIZE = 100000000;
	private final LinkedList<Change> changes = new LinkedList<>();
	private int index = -1;
	private final ImageMakerView imageMakerView;
	private LayerPanel layerPanel;

	public VersionManager(ImageMakerView imageMakerView) {
		this.imageMakerView = imageMakerView;
	}

	public void setLayerPanel(LayerPanel layerPanel) {
		this.layerPanel = layerPanel;
	}

	public void addRevision(Change change) {
		while (((sizeOf() + change.sizeOf()) > MAX_HEAP_SIZE) && (changes.size() > 0)) {
			changes.removeFirst();
			index--;
		}

		index++;

		if (index < changes.size())
			changes.subList(index, changes.size()).clear();

		linkChanges(change);

		changes.add(change);
		imageMakerView.refreshTab();
		if (layerPanel != null)
			layerPanel.repaintList();
	}

	private void linkChanges(Change change) {
		if (change instanceof Modification) {
			Iterator<Change> iter = changes.descendingIterator();
			while (iter.hasNext()) {
				Change prev = iter.next();
				if (linkLayer(change, prev))
					break;
			}
		}
	}

	private boolean linkLayer(Change change, Change link) {
		if (link instanceof IVisualChange && change.getLayer() == link.getLayer()) {
			((Modification) change).setBefore(((IVisualChange) link).getImage());
			return true;
		}
		return false;
	}

	public void undo() {
		if (index > 0) {
			Change c = changes.get(index);
			c.revert();
			index--;
			if (c.getGroup() != null)
				undo(c.getGroup());
		}
		imageMakerView.refreshTab();
		if (layerPanel != null)
			layerPanel.repaintList();
	}

	private void undo(UUID group) {
		while (index > 0) {
			Change c = changes.get(index);
			if (group.equals(c.getGroup())) {
				c.revert();
				index--;
			} else
				break;
		}
	}

	public void redo() {
		if (index < changes.size() - 1) {
			index++;
			Change c = changes.get(index);
			c.apply();
			if (c.getGroup() != null)
				redo(c.getGroup());
		}
		imageMakerView.refreshTab();
		if (layerPanel != null)
			layerPanel.repaintList();
	}

	private void redo(UUID group) {
		while (index < changes.size() - 1) {
			Change c = changes.get(index + 1);
			if (group.equals(c.getGroup())) {
				c.apply();
				index++;
			} else
				break;
		}
	}

	private int sizeOf() {
		int size = 0;
		for (Change change : changes)
			size += change.sizeOf();
		return size;
	}

	private void printHeapSize(String message) {
		int byteSize = sizeOf();
		LOG.debug(message + ", current heap size: " + byteSize + "B/" + byteSize / 1000 + "KB/" + byteSize / 1000000
				+ "MB");
	}
}
