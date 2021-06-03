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

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.Procedure;
import net.mcreator.element.parts.Sound;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") public class Fluid extends GeneratableElement implements ITabContainedElement {

	public String name;
	public String bucketName;

	public String textureStill;
	public String textureFlowing;

	public int luminosity;
	public int density;
	public int viscosity;
	public boolean isGas;
	public String type;

	public boolean generateBucket;
	public String textureBucket;
	public TabEntry creativeTab;
	public Sound emptySound;
	public String rarity;
	public List<String> specialInfo;

	public double resistance;
	public int luminance;
	public boolean emissiveRendering;
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

	private Fluid() {
		this(null);
	}

	public Fluid(ModElement element) {
		super(element);

		this.rarity = "COMMON";
		this.specialInfo = new ArrayList<>();

		this.resistance = 100;
		this.colorOnMap = "DEFAULT";

		this.spawnWorldTypes = new ArrayList<>();
		this.frequencyOnChunks = 5;
		this.restrictionBiomes = new ArrayList<>();
	}

	@Override public BufferedImage generateModElementPicture() {
		return ImageUtils
				.resizeAndCrop(getModElement().getFolderManager().getBlockImageIcon(textureStill).getImage(), 32);
	}

	@Override public TabEntry getCreativeTab() {
		return creativeTab;
	}
}
