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
import net.mcreator.element.IItemWithModel;
import net.mcreator.element.ITabContainedElement;
import net.mcreator.element.parts.Fluid;
import net.mcreator.element.parts.Particle;
import net.mcreator.element.parts.Procedure;
import net.mcreator.element.parts.*;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.resources.TexturedModel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") public class Block extends GeneratableElement
		implements IItemWithModel, ITabContainedElement {

	public String texture;
	public String textureTop;
	public String textureLeft;
	public String textureFront;
	public String textureRight;
	public String textureBack;
	public int renderType;
	public String customModelName;
	public int rotationMode;
	public boolean emissiveRendering;
	public boolean displayFluidOverlay;

	public String itemTexture;
	public String particleTexture;

	public String blockBase;

	public String tintType;
	public boolean isItemTinted;
	public Color itemTint;

	public boolean hasTransparency;
	public boolean connectedSides;
	public String transparencyType;
	public double mx, my, mz, Mx, My, Mz;

	public String name;
	public List<String> specialInfo;
	public double hardness;
	public double resistance;
	public boolean hasGravity;
	public boolean isWaterloggable;
	public TabEntry creativeTab;

	public String destroyTool;
	public MItemBlock customDrop;
	public int dropAmount;
	public boolean useLootTableForDrops;

	public double enchantPowerBonus;
	public boolean plantsGrowOn;
	public boolean canProvidePower;
	public int lightOpacity;
	public Material material;

	public int tickRate;
	public boolean tickRandomly;

	public boolean isReplaceable;
	public String colorOnMap;
	public MItemBlock creativePickItem;
	public String offsetType;
	public String aiPathNodeType;
	public Color beaconColorModifier;

	public int flammability;
	public int fireSpreadSpeed;

	public boolean isLadder;
	public double slipperiness;
	public String reactionToPushing;

	public boolean isNotColidable;
	public StepSound soundOnStep;
	public int luminance;
	public boolean unbreakable;
	public int breakHarvestLevel;

	public boolean spawnParticles;
	public Particle particleToSpawn;
	public String particleSpawningShape;
	public double particleSpawningRadious;
	public int particleAmount;
	public Procedure particleCondition;

	public boolean hasInventory;
	public String guiBoundTo;
	public boolean openGUIOnRightClick;
	public int inventorySize;
	public int inventoryStackSize;
	public boolean inventoryDropWhenDestroyed;
	public boolean inventoryComparatorPower;
	public List<Integer> inventoryOutSlotIDs;
	public List<Integer> inventoryInSlotIDs;

	public boolean hasEnergyStorage;
	public int energyInitial;
	public int energyCapacity;
	public int energyMaxReceive;
	public int energyMaxExtract;

	public boolean isFluidTank;
	public int fluidCapacity;
	public List<Fluid> fluidRestrictions;

	public Procedure onRightClicked;
	public Procedure onBlockAdded;
	public Procedure onNeighbourBlockChanges;
	public Procedure onTickUpdate;
	public Procedure onRandomUpdateEvent;
	public Procedure onDestroyedByPlayer;
	public Procedure onDestroyedByExplosion;
	public Procedure onStartToDestroy;
	public Procedure onEntityCollides;
	public Procedure onEntityWalksOn;
	public Procedure onBlockPlayedBy;
	public Procedure onRedstoneOn;
	public Procedure onRedstoneOff;

	public List<String> spawnWorldTypes;
	public List<BiomeEntry> restrictionBiomes;
	public List<MItemBlock> blocksToReplace;
	public int frequencyPerChunks;
	public int frequencyOnChunk;
	public int minGenerateHeight;
	public int maxGenerateHeight;
	public Procedure generateCondition;

	private Block() {
		this(null);
	}

	public Block(ModElement element) {
		super(element);

		this.tintType = "No tint";
		this.spawnWorldTypes = new ArrayList<>();
		this.restrictionBiomes = new ArrayList<>();
		this.reactionToPushing = "NORMAL";
		this.slipperiness = 0.6;
		this.colorOnMap = "DEFAULT";
		this.aiPathNodeType = "DEFAULT";
		this.offsetType = "NONE";
		this.inventoryInSlotIDs = new ArrayList<>();
		this.inventoryOutSlotIDs = new ArrayList<>();

		this.energyCapacity = 400000;
		this.energyMaxReceive = 200;
		this.energyMaxExtract = 200;
		this.fluidCapacity = 8000;
	}

	public int renderType() {
		if (blockBase != null && !blockBase.equals(""))
			return -1;
		return renderType;
	}

	public boolean hasCustomDrop() {
		return !customDrop.isEmpty();
	}

	public boolean isGeneratedInWorld() {
		return !spawnWorldTypes.isEmpty();
	}

	@Override public BufferedImage generateModElementPicture() {
		if (renderType() == 10 && !textureTop.equals("") && !textureFront.equals("") && !textureLeft.equals("")) {
			return (BufferedImage) MinecraftImageGenerator.Preview
					.generateBlockIcon(getModElement().getFolderManager().getBlockImageIcon(textureTop).getImage(),
							getModElement().getFolderManager().getBlockImageIcon(textureLeft).getImage(),
							getModElement().getFolderManager().getBlockImageIcon(textureFront).getImage());
		} else if (renderType() == 11 && !texture.equals("")) {
			return (BufferedImage) MinecraftImageGenerator.Preview
					.generateBlockIcon(getModElement().getFolderManager().getBlockImageIcon(texture).getImage(),
							getModElement().getFolderManager().getBlockImageIcon(texture).getImage(),
							getModElement().getFolderManager().getBlockImageIcon(texture).getImage());
		} else {
			return ImageUtils
					.resizeAndCrop(getModElement().getFolderManager().getBlockImageIcon(texture).getImage(), 32);
		}
	}

	@Override public Model getItemModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (renderType == 2)
			modelType = Model.Type.JSON;
		else if (renderType == 3)
			modelType = Model.Type.OBJ;
		return Model.getModelByParams(getModElement().getWorkspace(), customModelName, modelType);
	}

	@Override public Map<String, String> getTextureMap() {
		Model model = getItemModel();
		if (model instanceof TexturedModel && ((TexturedModel) model).getTextureMapping() != null)
			return ((TexturedModel) model).getTextureMapping().getTextureMap();
		return null;
	}

	@Override public TabEntry getCreativeTab() {
		return creativeTab;
	}

}
