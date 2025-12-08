/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.laf;

import net.mcreator.ui.init.L10N;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FlafIcons {

	private static final Map<String, BufferedImage> FLAG_CACHE = new HashMap<>();

	public static BufferedImage getFlag(String id) {
		return FLAG_CACHE.computeIfAbsent(
				"/flags/" + L10N.getLocale().toString().split("_")[1].toUpperCase(Locale.ENGLISH) + ".png",
				flagpath -> {
					try {
						InputStream stream = FlafIcons.class.getResourceAsStream(flagpath);
						BufferedImage retval = ImageIO.read(Objects.requireNonNull(stream));
						stream.close();
						return retval;
					} catch (IOException e) {
						return ImageUtils.toBufferedImage(new EmptyIcon.ImageIcon(16, 16).getImage());
					}
				});
	}

}
