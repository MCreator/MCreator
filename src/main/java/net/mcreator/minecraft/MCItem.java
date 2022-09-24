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

import net.mcreator.element.types.Armor;
import net.mcreator.io.ResourcePointer;
import net.mcreator.ui.init.BlockItemIcons;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Objects;

public class MCItem extends DataListEntry {

	public static final ImageIcon DEFAULT_ICON = UIRES.get("mod");
	public static final ImageIcon TAG_ICON = UIRES.get("tag");

	public ImageIcon icon;
	boolean hasSubtypes;

	private MCItem(String name) {
		super(name);
	}

	public MCItem(DataListEntry entry) {
		super(entry.getName());
		setReadableName(entry.getReadableName());
		setDescription(entry.getDescription());
		setType(entry.getType());
		setTexture(entry.getTexture());
		setRequiredAPIs(entry.getRequiredAPIs());
		setIcon(BlockItemIcons.getIconForItem(entry.getTexture()));
	}

	public boolean hasNoSubtypes() {
		return !hasSubtypes;
	}

	public void setSubtypes(boolean hasSubtypes) {
		this.hasSubtypes = hasSubtypes;
	}

	protected void setIcon(ImageIcon icon) {
		if (icon == null || icon.getImage() == null) {
			this.icon = DEFAULT_ICON;
		} else
			this.icon = icon;
	}

	public static ImageIcon getBlockIconBasedOnName(Workspace workspace, String name) {
		if (name == null || name.trim().equals(""))
			return new EmptyIcon.ImageIcon(32, 32);

		ImageIcon retval = null;
		try {
			if (name.startsWith("CUSTOM:")) {
				if (new File(workspace.getFolderManager().getModElementPicturesCacheDir(),
						name.replace("CUSTOM:", "") + ".png").isFile()) {
					retval = new ImageIcon(
							workspace.getFolderManager().getModElementPicturesCacheDir().getAbsolutePath() + "/"
									+ name.replace("CUSTOM:", "") + ".png");
				} else if (name.endsWith(".helmet")) {
					retval = workspace.getFolderManager().getTextureImageIcon(((Armor) Objects.requireNonNull(
							workspace.getModElementByName(name.replace("CUSTOM:", "").replace(".helmet", ""))
									.getGeneratableElement())).textureHelmet, TextureType.ITEM);
				} else if (name.endsWith(".body")) {
					retval = workspace.getFolderManager().getTextureImageIcon(((Armor) Objects.requireNonNull(
							workspace.getModElementByName(name.replace("CUSTOM:", "").replace(".body", ""))
									.getGeneratableElement())).textureBody, TextureType.ITEM);
				} else if (name.endsWith(".legs")) {
					retval = workspace.getFolderManager().getTextureImageIcon(((Armor) Objects.requireNonNull(
							workspace.getModElementByName(name.replace("CUSTOM:", "").replace(".legs", ""))
									.getGeneratableElement())).textureLeggings, TextureType.ITEM);
				} else if (name.endsWith(".boots")) {
					retval = workspace.getFolderManager().getTextureImageIcon(((Armor) Objects.requireNonNull(
							workspace.getModElementByName(name.replace("CUSTOM:", "").replace(".boots", ""))
									.getGeneratableElement())).textureBoots, TextureType.ITEM);
				} else if (name.endsWith(".bucket")) {
					if (new File(workspace.getFolderManager().getModElementPicturesCacheDir(),
							name.replace("CUSTOM:", "").replace(".bucket", "") + ".png").isFile()) {
						retval = MinecraftImageGenerator.generateFluidBucketIcon(new ImageIcon(
								workspace.getFolderManager().getModElementPicturesCacheDir().getAbsolutePath() + "/"
										+ name.replace("CUSTOM:", "").replace(".bucket", "") + ".png"));
					} else {
						retval = TiledImageCache.bucket;
					}
				}
			} else if (name.startsWith("TAG:")) {
				return TAG_ICON;
			} else if (name.startsWith("POTION:")) {
				String potion = name.replace("POTION:", "");
				if (potion.startsWith("CUSTOM:")) {
					if (new File(workspace.getFolderManager().getModElementPicturesCacheDir(),
							potion.replace("CUSTOM:", "") + ".png").isFile()) {
						retval = new ImageIcon(
								workspace.getFolderManager().getModElementPicturesCacheDir().getAbsolutePath() + "/"
										+ potion.replace("CUSTOM:", "") + ".png");
					} else {
						retval = ImageMakerTexturesCache.CACHE.get(
								new ResourcePointer("templates/textures/texturemaker/potion_bottle_overlay.png"));
					}
				} else if (DataListLoader.loadDataMap("potions").containsKey(potion)) {
					int color = Integer.parseInt(DataListLoader.loadDataMap("potions").get(potion).getTexture());
					retval = new ImageIcon(MinecraftImageGenerator.Preview.generatePotionIcon(new Color(color)));
				} else {
					retval = new ImageIcon(MinecraftImageGenerator.Preview.generatePotionIcon(Color.BLACK));
				}
			} else {
				retval = BlockItemIcons.getIconForItem(
						DataListLoader.loadDataMap("blocksitems").get(name).getTexture());
			}

			if (retval != null && retval.getImage() != null) {
				if (retval.getImage().getWidth(null) > -1 && retval.getImage().getHeight(null) > -1) {
					return retval;
				}
			}

		} catch (Exception ignored) {
		}

		return DEFAULT_ICON;
	}

	public static final class Custom extends MCItem {

		public Custom(ModElement element, String fieldName) {
			super("CUSTOM:" + element.getName() + (fieldName == null ? "" : ("." + fieldName)));
			setReadableName(element.getName() + " - " + element.getType().getReadableName());
			setIcon(getBlockIconBasedOnName(element.getWorkspace(), getName()));
			setType("mcreator");
			setDescription(element.getType().getDescription());
		}

		@Override public boolean isSupportedInWorkspace(Workspace workspace) {
			return true;
		}
	}

	public static final class Tag extends MCItem {

		public Tag(@Nonnull Workspace workspace, String name) {
			super("TAG:" + name);
			setType("tag");
			icon = MCItem.getBlockIconBasedOnName(workspace, "TAG:" + name);
		}

		@Override public boolean isSupportedInWorkspace(Workspace workspace) {
			return true;
		}

	}

	public static final class Potion extends MCItem {
		public Potion(@Nonnull Workspace workspace, DataListEntry potion) {
			super("POTION:" + potion.getName());
			setType("potion");
			icon = MCItem.getBlockIconBasedOnName(workspace, "POTION:" + potion.getName());
		}

		@Override public boolean isSupportedInWorkspace(Workspace workspace) {
			return true;
		}
	}

	public interface ListProvider {
		List<MCItem> provide(Workspace workspace);
	}

}
