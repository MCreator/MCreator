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

package net.mcreator.minecraft;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Converts sign textures in the legacy 64x32 entity texture layout (box unwraps of the sign model parts) to the
 * texture layouts used since Minecraft 26.3, where signs are rendered as block models:
 * - 32x32 block texture referenced by the sign block model templates
 * - 24x26 GUI texture used by the standing sign edit screen
 * <p>
 * Textures with higher resolution than 64x32 are supported as long as they keep the 2:1 aspect ratio.
 */
public final class SignTextureConverter {

	private static final int ENTITY_TEXTURE_WIDTH = 64;
	private static final int ENTITY_TEXTURE_HEIGHT = 32;

	private record Region(int sx, int sy, int w, int h, int dx, int dy, boolean flipX, boolean flipY) {

		Region(int sx, int sy, int w, int h, int dx, int dy) {
			this(sx, sy, w, h, dx, dy, false, false);
		}

	}

	private static final List<Region> SIGN_BLOCK_TEXTURE = List.of(
			// Board (24x12x2 at texture offset 0,0)
			new Region(2, 0, 24, 2, 0, 0), // up
			new Region(2, 2, 24, 12, 0, 2), // front
			new Region(28, 2, 24, 12, 0, 16), // back
			new Region(26, 0, 24, 2, 0, 28, false, true), // down
			new Region(26, 2, 2, 12, 24, 2), // east
			new Region(0, 2, 2, 12, 24, 16), // west
			// Post (2x14x2 at texture offset 0,14)
			new Region(2, 16, 2, 14, 28, 0), // front
			new Region(6, 16, 2, 14, 28, 16), // back
			new Region(4, 14, 2, 2, 28, 30, false, true), // down
			new Region(4, 16, 2, 14, 30, 0), // east
			new Region(0, 16, 2, 14, 30, 16) // west
	);

	private static final List<Region> HANGING_SIGN_BLOCK_TEXTURE = List.of(
			// Plank of the wall hanging sign (16x2x4 at texture offset 0,0)
			new Region(4, 0, 16, 4, 0, 0), // up
			new Region(4, 4, 16, 2, 0, 4), // front
			new Region(24, 4, 16, 2, 0, 7), // back
			new Region(20, 0, 16, 4, 0, 9, true, true), // down
			new Region(20, 4, 4, 2, 16, 4), // east
			new Region(0, 4, 4, 2, 16, 7), // west
			// Board (14x10x2 at texture offset 0,12)
			new Region(2, 12, 14, 2, 2, 14), // up
			new Region(2, 14, 14, 10, 2, 16), // front
			new Region(18, 14, 14, 10, 18, 16), // back
			new Region(16, 12, 14, 2, 2, 26, false, true), // down
			new Region(16, 14, 2, 10, 16, 16), // east
			new Region(0, 14, 2, 10, 0, 16), // west
			// Chains
			new Region(14, 6, 12, 6, 20, 0), // attached (horizontal) chains
			new Region(0, 6, 3, 6, 22, 7), // chain link 1
			new Region(6, 7, 3, 4, 28, 8) // chain link 2
	);

	private static final List<Region> SIGN_GUI_TEXTURE = List.of(new Region(2, 2, 24, 12, 0, 0), // board front
			new Region(2, 16, 2, 14, 11, 12) // post front
	);

	/**
	 * @param entityTexture Standing sign texture in the 64x32 entity texture layout
	 * @return 32x32 block texture for the sign block models
	 */
	public static BufferedImage toSignBlockTexture(BufferedImage entityTexture) {
		return repack(entityTexture, 32, 32, SIGN_BLOCK_TEXTURE);
	}

	/**
	 * @param entityTexture Hanging sign texture in the 64x32 entity texture layout
	 * @return 32x32 block texture for the hanging sign block models
	 */
	public static BufferedImage toHangingSignBlockTexture(BufferedImage entityTexture) {
		return repack(entityTexture, 32, 32, HANGING_SIGN_BLOCK_TEXTURE);
	}

	/**
	 * @param entityTexture Standing sign texture in the 64x32 entity texture layout
	 * @return 24x26 texture shown in the standing sign edit screen
	 */
	public static BufferedImage toSignGUITexture(BufferedImage entityTexture) {
		return repack(entityTexture, 24, 26, SIGN_GUI_TEXTURE);
	}

	private static BufferedImage repack(BufferedImage source, int width, int height, List<Region> regions) {
		int scale = Math.max(1, Math.round(source.getWidth() / (float) ENTITY_TEXTURE_WIDTH));
		if (source.getWidth() != ENTITY_TEXTURE_WIDTH * scale || source.getHeight() != ENTITY_TEXTURE_HEIGHT * scale)
			source = resize(source, ENTITY_TEXTURE_WIDTH * scale, ENTITY_TEXTURE_HEIGHT * scale);

		BufferedImage target = new BufferedImage(width * scale, height * scale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = target.createGraphics();
		for (Region region : regions) {
			int sx1 = region.sx() * scale, sy1 = region.sy() * scale;
			int sx2 = sx1 + region.w() * scale, sy2 = sy1 + region.h() * scale;
			int dx1 = region.dx() * scale, dy1 = region.dy() * scale;
			int dx2 = dx1 + region.w() * scale, dy2 = dy1 + region.h() * scale;
			if (region.flipX()) {
				int tmp = dx1;
				dx1 = dx2;
				dx2 = tmp;
			}
			if (region.flipY()) {
				int tmp = dy1;
				dy1 = dy2;
				dy2 = tmp;
			}
			g2d.drawImage(source, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
		}
		g2d.dispose();
		return target;
	}

	private static BufferedImage resize(BufferedImage source, int width, int height) {
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2d.drawImage(source, 0, 0, width, height, null);
		g2d.dispose();
		return resized;
	}

}
