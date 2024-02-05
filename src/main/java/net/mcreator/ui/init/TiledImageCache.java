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

package net.mcreator.ui.init;

import net.mcreator.util.image.InvalidTileSizeException;
import net.mcreator.util.image.TiledImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class TiledImageCache {

	private static final Logger LOG = LogManager.getLogger("TImage Chache");

	public static ImageIcon plantGrowingYes;
	public static ImageIcon plantGrowingNo;
	public static ImageIcon plantStaticYes;
	public static ImageIcon plantStaticNo;
	public static ImageIcon plantDoubleYes;
	public static ImageIcon plantDoubleNo;

	public static void loadTileImages() {
		try {
			TiledImageUtils plantGrowthTile = new TiledImageUtils(UIRES.get("growthtile"), 128, 215);

			plantGrowingYes = plantGrowthTile.getIcon(1, 1);
			plantGrowingNo = plantGrowthTile.getIcon(2, 1);
			plantStaticYes = plantGrowthTile.getIcon(3, 1);
			plantStaticNo = plantGrowthTile.getIcon(4, 1);
			plantDoubleYes = plantGrowthTile.getIcon(5, 1);
			plantDoubleNo = plantGrowthTile.getIcon(6, 1);
		} catch (InvalidTileSizeException e) {
			LOG.error("Failed loading some tiles into the cache" , e);
		}
	}

}
