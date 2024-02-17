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

package net.mcreator.ui.views.editor.image.clipboard.transferables;

import net.mcreator.ui.views.editor.image.layer.Layer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

public class LayerTransferable implements Transferable {
	private final BufferedImage image;
	private final Layer layer;

	// Don't include the layer name to improve compatibility with other software
	private final DataFlavor[] flavours = new DataFlavor[] { DataFlavor.imageFlavor/*, DataFlavor.stringFlavor */ };

	public LayerTransferable(Layer layer) {
		this.layer = layer.copy();
		image = layer.getCanvas().getSelection().cropLayer(layer.copyImage(), layer.getX(), layer.getY());
	}

	public Layer getLayer() {
		return layer.copy();
	}

	@Override public DataFlavor[] getTransferDataFlavors() {
		return flavours;
	}

	@Override public boolean isDataFlavorSupported(DataFlavor flavor) {
		return Arrays.asList(flavours).contains(flavor);
	}

	@Override public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor == DataFlavor.imageFlavor)
			return image;
		//else if (flavor == DataFlavor.stringFlavor)
		//	return layer.getName();
		throw new UnsupportedFlavorException(flavor);
	}
}
