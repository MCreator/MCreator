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
import net.mcreator.ui.views.editor.image.clipboard.transferables.LayerTransferable;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.versioning.change.Modification;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ClipboardManager implements ClipboardOwner {
	private final Clipboard clipboard;
	private ImageMakerView imageMakerView;

	public ClipboardManager(ImageMakerView imageMakerView) {
		this.imageMakerView = imageMakerView;
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	public void copy() {
		addToClipboard();
	}

	public void copyAll() {
		// TODO: Add support for copying all layers as a single image.
	}

	public void cut() {
		addToClipboard();
		Layer selected = imageMakerView.getLayerPanel().selected();
		if (imageMakerView.getCanvas().size() > 1)
			imageMakerView.getCanvas().remove(selected);
		else {
			selected.clear();
			imageMakerView.getVersionManager().addRevision(new Modification(imageMakerView.getCanvas(), selected));
		}
	}

	private void addToClipboard() {
		// Prevent crashing on systems that lock clipboards to specific programs. This still crashes due to a bug in java's clipboard implementation (https://stackoverflow.com/questions/59140881/error-copying-an-image-object-to-the-clipboard).
		try {
			clipboard.setContents(new LayerTransferable(imageMakerView.getLayerPanel().selected()), this);
		} catch (IllegalStateException ignored) {
		}
	}

	public void paste() {
		if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
			Transferable clp = clipboard.getContents(this);
			if (clp.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				try {
					Image img = (Image) clp.getTransferData(DataFlavor.imageFlavor);
					if (clp.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						imageMakerView.getCanvas()
								.add(new Layer((String) clp.getTransferData(DataFlavor.stringFlavor), img));
					} else {
						imageMakerView.getCanvas().add(new Layer("Pasted layer", img));
					}

				} catch (UnsupportedFlavorException | IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// Oh no, anyway?
	}
}
