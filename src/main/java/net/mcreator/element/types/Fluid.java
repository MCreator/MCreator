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

package net.mcreator.element.types;

import net.mcreator.element.BaseType;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.Particle;
import net.mcreator.element.parts.Sound;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringListProcedure;
import net.mcreator.element.types.interfaces.IBlock;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.TextureReference;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({ "unused", "NotNullFieldNotInitialized" }) public class Fluid extends GeneratableElement
		implements IBlock, ITabContainedElement {

	public String name;
	public String bucketName;

	@TextureReference(TextureType.BLOCK) public String textureStill;
	@TextureReference(TextureType.BLOCK) public String textureFlowing;

	public String tintType;

	public boolean canMultiply;
	public int flowRate;
	public int levelDecrease;
	public int slopeFindDistance;
	public boolean spawnParticles;
	public Particle dripParticle;
	public double flowStrength;

	public int luminosity;
	public int density;
	public int viscosity;
	public int temperature;
	@Nonnull public String type;

	public boolean generateBucket;
	@TextureReference(TextureType.ITEM) public String textureBucket;
	public TabEntry creativeTab;
	public Sound emptySound;
	public String rarity;
	public StringListProcedure specialInformation;

	public double resistance;
	public int luminance;
	public int lightOpacity;
	public boolean emissiveRendering;
	public int tickRate;
	public int flammability;
	public int fireSpreadSpeed;
	public String colorOnMap;

	public Procedure onBlockAdded;
	public Procedure onNeighbourChanges;
	public Procedure onTickUpdate;
	public Procedure onEntityCollides;
	public Procedure onRandomUpdateEvent;
	public Procedure onDestroyedByExplosion;
	public Procedure flowCondition;
	public Procedure beforeReplacingBlock;

	private Fluid() {
		this(null);
	}

	public Fluid(ModElement element) {
		super(element);

		this.tintType = "No tint";

		this.rarity = "COMMON";

		this.flowRate = 5;
		this.slopeFindDistance = 4;
		this.levelDecrease = 1;
		this.flowStrength = 1;

		this.lightOpacity = 1;
		this.tickRate = 10;

		this.temperature = 300;

		this.resistance = 100;
		this.colorOnMap = "DEFAULT";
	}

	@Override public BufferedImage generateModElementPicture() {
		return ImageUtils.resizeAndCrop(
				getModElement().getFolderManager().getTextureImageIcon(textureStill, TextureType.BLOCK).getImage(), 32);
	}

	@Override public TabEntry getCreativeTab() {
		return creativeTab;
	}

	public boolean isFluidTinted() {
		return !"No tint".equals(tintType);
	}

	public boolean extendsFluidAttributes() {
		return isFluidTinted();
	}

	@Override public String getRenderType() {
		return "translucent";
	}

	@Override public Collection<BaseType> getBaseTypesProvided() {
		List<BaseType> baseTypes = new ArrayList<>(List.of(BaseType.BLOCK));

		if (generateBucket)
			baseTypes.add(BaseType.ITEM);

		return baseTypes;
	}

	@Override public List<MCItem> providedMCItems() {
		ArrayList<MCItem> retval = new ArrayList<>();
		retval.add(new MCItem.Custom(this.getModElement(), null, "block", "Block"));
		if (this.generateBucket)
			retval.add(new MCItem.Custom(this.getModElement(), "bucket", "item", "Bucket"));
		return retval;
	}

	@Override public List<MCItem> getCreativeTabItems() {
		ArrayList<MCItem> retval = new ArrayList<>();
		if (this.generateBucket)
			retval.add(new MCItem.Custom(this.getModElement(), "bucket", "item", "Bucket"));
		return retval;
	}

	@Override public ImageIcon getIconForMCItem(Workspace workspace, String suffix) {
		if ("bucket".equals(suffix)) {
			// Use the custom bucket texture if present
			if (textureBucket != null && !textureBucket.isEmpty()) {
				return workspace.getFolderManager().getTextureImageIcon(textureBucket, TextureType.ITEM);
			}
			// Otherwise, fallback to the generated fluid bucket icon
			return MinecraftImageGenerator.generateFluidBucketIcon(
					workspace.getFolderManager().getTextureImageIcon(textureStill, TextureType.BLOCK));
		}
		return null;
	}
}
