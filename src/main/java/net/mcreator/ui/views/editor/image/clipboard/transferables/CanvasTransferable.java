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

import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.util.image.ImageUtils;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

public class CanvasTransferable implements Transferable {
	private final BufferedImage image;
	private final DataFlavor[] flavours = new DataFlavor[] { DataFlavor.imageFlavor };
	private final Canvas canvas;

	public CanvasTransferable(Canvas canvas) {
		this.canvas = canvas;
		image = canvas.getSelection().cropCanvas(canvas.getCanvasRenderer().render());
	}

	public BufferedImage getRender() {
		return ImageUtils.deepCopy(image);
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
		throw new UnsupportedFlavorException(flavor);
	}
}
