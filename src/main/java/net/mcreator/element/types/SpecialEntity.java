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

package net.mcreator.element.types;

import net.mcreator.element.BaseType;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.types.interfaces.ICommonType;
import net.mcreator.element.types.interfaces.IMCItemProvider;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.module.ModuleDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused") public class SpecialEntity extends GeneratableElement
		implements ICommonType, ITabContainedElement, IMCItemProvider {

	private static final Logger LOG = LogManager.getLogger(SpecialEntity.class);

	public String entityType;
	public String name;
	@ModElementReference public List<TabEntry> creativeTabs;

	@TextureReference(TextureType.ENTITY) public TextureHolder entityTexture;
	@TextureReference(TextureType.ITEM) public TextureHolder itemTexture;

	private SpecialEntity() {
		this(null);
	}

	public SpecialEntity(ModElement element) {
		super(element);
	}

	@Override public void finalizeModElementGeneration() {
		try {
			File entityTextureLocation = new File(
					getModElement().getFolderManager().getTexturesFolder(TextureType.OTHER),
					"entity/" + ("Boat".equals(entityType) ? "boat/" : "chest_boat/")
							+ getModElement().getRegistryName() + ".png");
			FileIO.copyFile(entityTexture.toFile(TextureType.ENTITY), entityTextureLocation);
		} catch (Exception e) {
			LOG.error("Failed to copy special entity texture", e);
		}
	}

	@Override public BufferedImage generateModElementPicture() {
		return ImageUtils.resizeAndCrop(itemTexture.getImage(TextureType.ITEM), 32);
	}

	@Override public Collection<BaseType> getBaseTypesProvided() {
		List<BaseType> baseTypes = new ArrayList<>();
		baseTypes.add(BaseType.ITEM);

		// Since 1.21.2 custom boats are stand-alone entity types, not variants of the vanilla boat entity
		if (ModuleDescriptor.Version.parse(getModElement().getGenerator().getGeneratorMinecraftVersion())
				.compareTo(ModuleDescriptor.Version.parse("1.21.2")) >= 0)
			baseTypes.add(BaseType.ENTITY);

		return baseTypes;
	}

	@Override public ImageIcon getIconForMCItem(Workspace workspace, String suffix) {
		return itemTexture.getImageIcon(TextureType.ITEM);
	}

	@Override public List<MCItem> providedMCItems() {
		ArrayList<MCItem> retval = new ArrayList<>();
		retval.add(new MCItem.Custom(this.getModElement(), null, "item"));
		return retval;
	}

	@Override public List<TabEntry> getCreativeTabs() {
		return creativeTabs;
	}

	@Override public List<MCItem> getCreativeTabItems() {
		return List.of(new MCItem.Custom(this.getModElement(), null, "item"));
	}
}
