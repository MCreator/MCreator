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

	private static final int MAX_HEAP_SIZE = 100 * 1024 * 1024; // 100 MB

	private final LinkedList<Change> changes = new LinkedList<>();
	private int index = -1;
	private final ImageMakerView imageMakerView;
	private LayerPanel layerPanel;
	private RevisionListener revisionListener;

	public VersionManager(ImageMakerView imageMakerView) {
		this.imageMakerView = imageMakerView;
	}

	public void setLayerPanel(LayerPanel layerPanel) {
		this.layerPanel = layerPanel;
	}

	public void setRevisionListener(RevisionListener listener) {
		this.revisionListener = listener;
	}

	public void addRevision(Change change) {
		while (((sizeOf() + change.sizeOf()) > MAX_HEAP_SIZE) && (!changes.isEmpty())) {
			changes.removeFirst();
			index--;
		}

		index++;

		if (index < changes.size())
			changes.subList(index, changes.size()).clear();

		linkChanges(change);

		changes.add(change);

		refreshPreview();

		revisionListener.revisionChanged();
	}

	public void refreshPreview() {
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

	/**
	 * Determines if the currently active revision is the initial full revision of the image
	 * that should not be undone.
	 * This method handles two different cases:
	 * 1. Elementary changes (without a group UUID): Returns true if this is the very first change (index = 0)
	 * 2. Complex changes (with a group UUID): Returns true if the first element of the group change is the first
	 *    overall change, making the group the initial full version.
	 *
	 * @return true if the current revision is the first atomic revision that cannot be undone, false otherwise
	 */
	public boolean firstRevision() {
		// Quick check: if index is 0, this is always the first atomic revision
		if (index == 0) {
			return true;
		}

		Change currentChange  = changes.get(index);
		UUID currentGroupId = currentChange.getGroup();

		// If this is an elementary change (no group ID), it's not the first atomic revision
		// since we already checked index != 0
		if (currentGroupId == null) {
			return false;
		}

		int startIndex = index;

		// Iterates to the beginning of the change group
		while (startIndex > 0 && changes.get(startIndex - 1).getGroup() == currentGroupId)
			startIndex--;

		// Check if the beginning of the group is at index 0
		return startIndex == 0;
	}

	public boolean lastRevision() {
		return index == changes.size() - 1;
	}

	public void undo() {
		if (!firstRevision()) {
			Change c = changes.get(index);
			c.revert();
			index--;
			if (c.getGroup() != null)
				undo(c.getGroup());
		}
		imageMakerView.refreshTab();
		if (layerPanel != null)
			layerPanel.repaintList();
		revisionListener.revisionChanged();
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
		revisionListener.revisionChanged();
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
		LOG.debug("{}, current heap size: {}B/{}KB/{}MB", message, byteSize, byteSize / 1000, byteSize / 1000000);
	}

	public interface RevisionListener {
		void revisionChanged();
	}
}
