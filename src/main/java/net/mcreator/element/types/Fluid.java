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
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.Particle;
import net.mcreator.element.parts.Sound;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringProcedure;
import net.mcreator.element.types.interfaces.IBlock;
import net.mcreator.element.types.interfaces.ISpecialInformationHolder;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused") public class Fluid extends GeneratableElement
		implements IBlock, ITabContainedElement, ISpecialInformationHolder {

	public String name;
	public String bucketName;

	public String textureStill;
	public String textureFlowing;

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
	public String type;

	public boolean generateBucket;
	public String textureBucket;
	public TabEntry creativeTab;
	public Sound emptySound;
	public String rarity;
	public StringProcedure specialInformation;

	public double resistance;
	public int luminance;
	public int lightOpacity;
	public boolean emissiveRendering;
	public int tickRate;
	public int flammability;
	public int fireSpreadSpeed;
	public String colorOnMap;

	public int frequencyOnChunks;
	public List<String> spawnWorldTypes;
	public List<BiomeEntry> restrictionBiomes;
	public Procedure generateCondition;

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

		this.spawnWorldTypes = new ArrayList<>();
		this.frequencyOnChunks = 5;
		this.restrictionBiomes = new ArrayList<>();
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

	public boolean extendsForgeFlowingFluid() {
		return spawnParticles || flowStrength != 1 || flowCondition != null || beforeReplacingBlock != null;
	}

	public boolean doesGenerateInWorld() {
		return spawnWorldTypes.size() > 0;
	}

	@Override public String getRenderType() {
		return "translucent";
	}

	@Override public Collection<BaseType> getBaseTypesProvided() {
		List<BaseType> baseTypes = new ArrayList<>(List.of(BaseType.BLOCK));

		if (generateBucket)
			baseTypes.add(BaseType.ITEM);

		if (doesGenerateInWorld())
			baseTypes.add(BaseType.FEATURE);

		return baseTypes;
	}

	@Override public StringProcedure getSpecialInformation() {
		return specialInformation;
	}

	@Override public void setSpecialInformation(String name, String fixedValue) {
		specialInformation = new StringProcedure(name, fixedValue);
	}

}
