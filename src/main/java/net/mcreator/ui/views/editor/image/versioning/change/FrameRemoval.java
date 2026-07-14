/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.views.editor.image.versioning.change;

import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.layer.Layer;

import java.awt.image.BufferedImage;

public class FrameRemoval extends FrameAddition implements IVisualChange {
	private final ImageMakerView imv;
	private final int frameIndex;
	private final boolean addEmptyFrame;
	private final Canvas emptyCanvas;

	public FrameRemoval(Canvas canvas, Layer layer, int frameIndex, boolean addEmptyFrame) {
		super(canvas, layer, frameIndex);
		this.frameIndex = frameIndex;
		this.addEmptyFrame = addEmptyFrame;

		this.imv = canvas.getImageMakerView();

		Layer emptyLayer = new Layer(canvas.getWidth(), canvas.getHeight(), 0, 0, "Layer");
		emptyCanvas = new Canvas(imv, canvas.getWidth(), canvas.getHeight());
		emptyCanvas.add(emptyLayer);
	}

	@Override public void apply() {
		super.revert();
		if (addEmptyFrame)
			imv.getAnimationTimeline().insertFrameToTimelineNR(emptyCanvas, frameIndex);
	}

	@Override public void revert() {
		if (addEmptyFrame)
			imv.getAnimationTimeline().removeFrameFromTimelineNR(frameIndex);
		super.apply();
	}

	@Override public BufferedImage getImage() {
		return canvas.render();
	}
}
