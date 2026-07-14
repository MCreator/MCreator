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

public class FrameAddition extends Change implements IVisualChange {

	private final int frameIndex;
	private final ImageMakerView imv;

	public FrameAddition(Canvas canvas, Layer layer, int frameIndex) {
		super(canvas, layer);
		this.frameIndex = frameIndex;
		this.imv = canvas.getImageMakerView();
	}

	@Override public void apply() {
		canvas.getImageMakerView().getAnimationTimeline().insertFrameToTimelineNR(canvas, frameIndex);
	}

	@Override public void revert() {
		if (frameIndex > 0) {// We don't revert the original frame to avoid getting an empty timeline
			imv.getAnimationTimeline().getTimelineModel().remove(frameIndex);
			try {
				imv.setDisplayedCanvas(imv.getAnimationTimeline().getTimelineModel().get(frameIndex));
			} catch (ArrayIndexOutOfBoundsException e) {
				imv.setDisplayedCanvas(imv.getAnimationTimeline().getTimelineModel().lastElement());
			}
		}
	}

	@Override public BufferedImage getImage() {
		return canvas.render();
	}
}
