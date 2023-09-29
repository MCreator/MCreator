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
import net.mcreator.ui.views.editor.image.clipboard.transferables.CanvasTransferable;
import net.mcreator.ui.views.editor.image.clipboard.transferables.LayerTransferable;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.versioning.change.Modification;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

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
		if (imageMakerView.getCanvas().size() > 1)
			imageMakerView.getCanvas().remove(selected);
		else {
			selected.clear();
			imageMakerView.getVersionManager().addRevision(new Modification(imageMakerView.getCanvas(), selected));
		}
	}

	private void addCanvasToClipboard(){
		addLayerToClipboard(new CanvasTransferable(imageMakerView.getCanvas()));
	}

	private void addLayerToClipboard() {
		Layer selected = imageMakerView.getLayerPanel().selected();
		if (selected != null)
			addLayerToClipboard(new LayerTransferable(selected));
	}

	private void addLayerToClipboard(Transferable transferable) {
		// Prevent crashing on systems that lock clipboards to specific programs.
		// This still prints the stack trace due to a bug in java's clipboard implementation.
		// https://stackoverflow.com/questions/59140881/error-copying-an-image-object-to-the-clipboard
		// https://bugs.java.com/bugdatabase/view_bug?bug_id=8286481
		// This could potentially be fixed in Adoptium 17.0.9 on 17. October 2023
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
					if (clp.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						Layer layer = new Layer((String) clp.getTransferData(DataFlavor.stringFlavor), img);
						layer.setPasted(true);
						imageMakerView.getCanvas()
								.add(layer);
					} else {
						Layer layer = new Layer("Pasted layer", img);
						layer.setPasted(true);
						imageMakerView.getCanvas().add(layer);
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
