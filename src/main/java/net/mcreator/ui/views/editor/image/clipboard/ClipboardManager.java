/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.views.editor.image.clipboard;

import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.clipboard.transferables.CanvasTransferable;
import net.mcreator.ui.views.editor.image.clipboard.transferables.LayerTransferable;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.versioning.change.Modification;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.UUID;

public class ClipboardManager implements ClipboardOwner {
	private final Clipboard clipboard;
	private final ImageMakerView imageMakerView;

	public ClipboardManager(ImageMakerView imageMakerView) {
		this.imageMakerView = imageMakerView;
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	public void copy() {
		addLayerToClipboard();
	}

	public void copyAll() {
		addCanvasToClipboard();
	}

	public void cut() {
		addLayerToClipboard();
		Layer selected = imageMakerView.getLayerPanel().selected();
		selected.clearSelection();
		imageMakerView.getVersionManager().addRevision(new Modification(imageMakerView.getCanvas(), selected));
	}

	private void addCanvasToClipboard() {
		addLayerToClipboard(new CanvasTransferable(imageMakerView.getCanvas()));
	}

	private void addLayerToClipboard() {
		Layer selected = imageMakerView.getLayerPanel().selected();
		if (selected != null)
			addLayerToClipboard(new LayerTransferable(selected));
	}

	private void addLayerToClipboard(Transferable transferable) {
		try {
			clipboard.setContents(transferable, this);
		} catch (IllegalStateException ignored) {
		}
	}

	public void paste() {
		if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
			Transferable clp = clipboard.getContents(this);
			if (clp.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				try {
					Image img = (Image) clp.getTransferData(DataFlavor.imageFlavor);

					Layer layer;
					if (clp.isDataFlavorSupported(DataFlavor.stringFlavor))
						layer = new Layer((String) clp.getTransferData(DataFlavor.stringFlavor), img);
					else
						layer = new Layer("Pasted layer", img);

					layer.setX(imageMakerView.getCanvas().getWidth() / 2 - layer.getWidth() / 2);
					layer.setY(imageMakerView.getCanvas().getHeight() / 2 - layer.getHeight() / 2);
					layer.setPasted(!imageMakerView.getCanvas().isEmpty());

					addFloatingLayer(layer);
				} catch (UnsupportedFlavorException | IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void addFloatingLayer(Layer layer) {
		if (imageMakerView.getLayerPanel().isFloating()) {
			Canvas canvas = imageMakerView.getCanvas();
			UUID uuid = UUID.randomUUID();
			canvas.mergeDown(canvas.indexOf(canvas.getFloatingLayer()), uuid);
			canvas.addOnTop(layer, uuid);
		} else {
			imageMakerView.getCanvas().addOnTop(layer);
		}
	}

	@Override public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// Oh no, anyway?
	}
}
