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

import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeRegistry;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.util.image.InvalidTileSizeException;
import net.mcreator.util.image.TiledImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class TiledImageCache {

	private static final Logger LOG = LogManager.getLogger("TImage Chache");

	private static TiledImageUtils modTypes;

	public static ImageIcon plantGrowingYes;
	public static ImageIcon plantGrowingNo;
	public static ImageIcon plantStaticYes;
	public static ImageIcon plantStaticNo;
	public static ImageIcon plantDoubleYes;
	public static ImageIcon plantDoubleNo;

	public static ImageIcon modTabRed;
	public static ImageIcon modTabGreen;
	public static ImageIcon modTabBlue;
	public static ImageIcon modTabPurple;

	public static ImageIcon workspaceAdd;
	public static ImageIcon workspaceCode;
	public static ImageIcon workspaceDelete;
	public static ImageIcon workspaceDeleteAll;
	public static ImageIcon workspaceEdit;
	public static ImageIcon workspaceDuplicate;
	public static ImageIcon workspaceToggle;
	public static ImageIcon workspaceModElementIDs;

	public static ImageIcon bucket;
	public static ImageIcon bucketMask;

	public static void loadAndTileImages() {
		try {
			TiledImageUtils plantGrowthTile = new TiledImageUtils(UIRES.get("growthtile"), 128, 215);
			TiledImageUtils modTabTile = new TiledImageUtils(UIRES.get("taboverlaytile"), 64, 64);
			TiledImageUtils workspaceIcons = new TiledImageUtils(UIRES.get("wrktile"), 45, 45);
			TiledImageUtils bucketIcons = new TiledImageUtils(UIRES.get("fluidbucket"), 32, 32);
			modTypes = new TiledImageUtils(UIRES.get("modtypes"), 64, 64);

			plantGrowingYes = plantGrowthTile.getIcon(1, 1);
			plantGrowingNo = plantGrowthTile.getIcon(2, 1);
			plantStaticYes = plantGrowthTile.getIcon(3, 1);
			plantStaticNo = plantGrowthTile.getIcon(4, 1);
			plantDoubleYes = plantGrowthTile.getIcon(5, 1);
			plantDoubleNo = plantGrowthTile.getIcon(6, 1);

			modTabRed = modTabTile.getIcon(1, 1);
			modTabGreen = modTabTile.getIcon(2, 1);
			modTabBlue = modTabTile.getIcon(3, 1);
			modTabPurple = modTabTile.getIcon(4, 1);

			workspaceAdd = ImageUtils
					.colorize(workspaceIcons.getIcon(1, 1), (Color) UIManager.get("MCreatorLAF.MAIN_TINT"), false);
			workspaceCode = workspaceIcons.getIcon(2, 1);
			workspaceDelete = workspaceIcons.getIcon(3, 1);
			workspaceDeleteAll = workspaceIcons.getIcon(4, 1);
			workspaceEdit = workspaceIcons.getIcon(5, 1);
			workspaceDuplicate = workspaceIcons.getIcon(7, 1);
			workspaceToggle = workspaceIcons.getIcon(8, 1);
			workspaceModElementIDs = workspaceIcons.getIcon(6, 1);

			bucket = bucketIcons.getIcon(1, 1);
			bucketMask = bucketIcons.getIcon(2, 1);

		} catch (InvalidTileSizeException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static ImageIcon getModTypeIcon(ModElementType modType) {
		if (modType == null)
			return modTypes.getIcon(1, 1);

		ModElementTypeRegistry.ModTypeRegistration<?> modRegistration = ModElementTypeRegistry.REGISTRY.get(modType);
		if (modRegistration != null) {
			return modTypes.getIcon(modRegistration.getIconID(), 1);
		}

		return modTypes.getIcon(1, 1);
	}

}
