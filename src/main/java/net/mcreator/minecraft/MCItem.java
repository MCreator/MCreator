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

import net.mcreator.element.types.interfaces.IMCItemProvider;
import net.mcreator.generator.GeneratorWrapper;
import net.mcreator.io.ResourcePointer;
import net.mcreator.ui.init.BlockItemIcons;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MCItem extends DataListEntry {

	private static final Logger LOG = LogManager.getLogger(MCItem.class);

	public static final ImageIcon EMPTY_ICON = new EmptyIcon.ImageIcon(32, 32);
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
		setOther(entry.getOther());
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

	public boolean isPOI() {
		if (getOther() instanceof Map<?, ?> otherMap) {
			return otherMap.containsKey("poi") && Boolean.parseBoolean(otherMap.get("poi").toString());
		}

		return false;
	}

	public static ImageIcon getBlockIconBasedOnName(Workspace workspace, String name) {
		if (name == null || name.isBlank())
			return EMPTY_ICON;

		if (name.startsWith("TAG:"))
			return TAG_ICON;

		ImageIcon retval = null;
		try {
			if (name.startsWith("CUSTOM:")) {
				String elementName = GeneratorWrapper.getElementPlainName(name);
				String suffix = StringUtils.substringAfterLast(name, ".");
				boolean hasGeneratableIcon = false;

				ModElement modElement = workspace.getModElementByName(elementName);

				// if the element is not found, use the default icon
				if (modElement == null) {
					retval = DEFAULT_ICON;
					hasGeneratableIcon = true;
				}
				// try to get the icon from the generatable element
				else if (modElement.getGeneratableElement() instanceof IMCItemProvider provider) {
					ImageIcon providedIcon = provider.getIconForMCItem(workspace, suffix);
					if (providedIcon != null) {
						retval = providedIcon;
						hasGeneratableIcon = true;
					}
				}

				// Otherwise, try using the mod element icon
				if (!hasGeneratableIcon && new File(workspace.getFolderManager().getModElementPicturesCacheDir(),
						elementName + ".png").isFile()) {
					retval = new ImageIcon(
							workspace.getFolderManager().getModElementPicturesCacheDir().getAbsolutePath() + "/"
									+ elementName + ".png");
				}
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

			if (retval != null && retval.getImage() != null && retval.getImage().getWidth(null) > -1
					&& retval.getImage().getHeight(null) > -1) {
				// The image is cropped to fix an issue with long animated textures
				return new ImageIcon(ImageUtils.resizeAndCrop(retval.getImage(), 32));
			}

		} catch (Exception e) {
			LOG.warn("Failed to load icon for item: " + name, e);
		}

		return DEFAULT_ICON;
	}

	public static final class Custom extends MCItem {

		public Custom(ModElement element, String fieldName, String type) {
			this(element, fieldName, type, null);
		}

		public Custom(ModElement element, String fieldName, String type, @Nullable String descriptor) {
			super("CUSTOM:" + element.getName() + (fieldName == null ? "" : ("." + fieldName)));

			if (descriptor != null) {
				setReadableName(
						element.getName() + " - " + element.getType().getReadableName() + " " + descriptor.toLowerCase(
								Locale.ENGLISH));
			} else {
				setReadableName(element.getName() + " - " + element.getType().getReadableName());
			}

			setIcon(getBlockIconBasedOnName(element.getWorkspace(), getName()));
			setType(type);
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
