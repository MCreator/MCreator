/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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
import net.mcreator.element.parts.*;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringListProcedure;
import net.mcreator.element.types.interfaces.*;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.resources.TexturedModel;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused") public class Plant extends GeneratableElement
		implements IBlock, IItemWithModel, ITabContainedElement, ISpecialInfoHolder, IBlockWithBoundingBox,
		IBlockWithLootTable {

	public int renderType;
	@TextureReference(TextureType.BLOCK) public TextureHolder texture;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureBottom;
	@Nonnull public String customModelName;

	@TextureReference(TextureType.ITEM) public TextureHolder itemTexture;
	@TextureReference(TextureType.BLOCK) public TextureHolder particleTexture;

	@LimitedOptions({ "No tint", "Grass", "Foliage", "Birch foliage", "Spruce foliage", "Default foliage", "Water",
			"Sky", "Fog", "Water fog" }) public String tintType;
	public boolean isItemTinted;

	@LimitedOptions({ "normal", "double", "growapable", "sapling" }) public String plantType;

	@ModElementReference(acceptedTypes = { PotionEffect.class }) public String suspiciousStewEffect;
	@Numeric(init = 100, min = 0, max = 100000, step = 1) public int suspiciousStewDuration;

	@Numeric(init = 0.1, min = 0, max = 1, step = 0.01) public double secondaryTreeChance;
	@ModElementReference public ConfiguredFeatureEntry[] trees;
	@ModElementReference public ConfiguredFeatureEntry[] flowerTrees;
	@ModElementReference public ConfiguredFeatureEntry[] megaTrees;

	public String growapableSpawnType;
	@Numeric(init = 3, min = 1, max = 14, step = 1) public int growapableMaxHeight;

	public boolean customBoundingBox;
	public boolean disableOffset;
	public List<BoxEntry> boundingBoxes;

	public String name;
	public StringListProcedure specialInformation;
	@ModElementReference public List<TabEntry> creativeTabs;
	@Numeric(init = 0, min = -1, max = 64000, step = 0.1) public double hardness;
	@Numeric(init = 0, min = 0, max = Integer.MAX_VALUE, step = 0.5) public double resistance;
	@Numeric(init = 0, min = 0, max = 15, step = 1) public int luminance;
	public boolean unbreakable;
	public boolean isSolid;
	public boolean isWaterloggable;

	public boolean hasBlockItem;
	@Numeric(init = 64, min = 1, max = 99, step = 1) public int maxStackSize;
	@LimitedOptions({ "COMMON", "UNCOMMON", "RARE", "EPIC" }) public String rarity;
	public boolean immuneToFire;

	public boolean isCustomSoundType;
	public StepSound soundOnStep;
	public Sound breakSound;
	public Sound stepSound;
	public Sound placeSound;
	public Sound hitSound;
	public Sound fallSound;

	public boolean useLootTableForDrops;
	public MItemBlock customDrop;
	@Numeric(init = 1, min = 0, max = 200, step = 1) public int dropAmount;
	@Numeric(init = 0, min = 0, max = 1024, step = 1) public int xpAmountMin;
	@Numeric(init = 0, min = 0, max = 1024, step = 1, allowMinMaxEqual = true) public int xpAmountMax;
	public boolean forceTicking;
	public boolean emissiveRendering;

	public boolean hasTileEntity;

	public boolean isReplaceable;
	public String colorOnMap;
	public MItemBlock creativePickItem;
	@LimitedOptions({ "XZ", "XYZ", "NONE" }) public String offsetType;
	public String aiPathNodeType;
	public MItemBlock strippingResult;

	public boolean ignitedByLava;
	@Numeric(init = 100, min = 0, max = 1024, step = 1) public int flammability;
	@Numeric(init = 60, min = 0, max = 1024, step = 1) public int fireSpreadSpeed;
	@Numeric(init = 1.0, min = -1000, max = 1000, step = 0.1) public double jumpFactor;
	@Numeric(init = 1.0, min = -1000, max = 1000, step = 0.1) public double speedFactor;

	@ModElementReference public List<MItemBlock> canBePlacedOn;
	public Procedure placingCondition;

	public boolean isBonemealable;
	public Procedure isBonemealTargetCondition;
	public Procedure bonemealSuccessCondition;
	public Procedure onBonemealSuccess;

	@Numeric(init = 5, min = 0, max = 40, step = 1) public int frequencyOnChunks;
	public boolean generateFeature;
	@ModElementReference public List<BiomeEntry> restrictionBiomes;
	@LimitedOptions({ "Flower", "Grass" }) public String generationType;
	@Numeric(init = 64, min = 1, max = 1024, step = 1) public int patchSize;
	public boolean generateAtAnyHeight;

	public Procedure onBlockAdded;
	public Procedure onNeighbourBlockChanges;
	public Procedure onTickUpdate;
	public Procedure onRandomUpdateEvent;
	public Procedure onDestroyedByPlayer;
	public Procedure onDestroyedByExplosion;
	public Procedure onStartToDestroy;
	public Procedure onEntityCollides;
	public Procedure onBlockPlacedBy;
	public Procedure onRightClicked;
	public Procedure onEntityWalksOn;
	public Procedure onHitByProjectile;
	public Procedure onEntityFallsOn;

	private Plant() {
		this(null);
	}

	public Plant(ModElement element) {
		super(element);

		this.hasBlockItem = true;
		this.maxStackSize = 64;
		this.rarity = "COMMON";
		this.creativeTabs = new ArrayList<>();

		this.canBePlacedOn = new ArrayList<>();
		this.restrictionBiomes = new ArrayList<>();
		this.growapableSpawnType = "Plains";
		this.renderType = 12;
		this.customModelName = "Cross model";
		this.colorOnMap = "DEFAULT";
		this.aiPathNodeType = "DEFAULT";
		this.offsetType = "XZ";
		this.tintType = "No tint";

		this.jumpFactor = 1.0;
		this.speedFactor = 1.0;

		this.suspiciousStewEffect = "SATURATION";
		this.suspiciousStewDuration = 0;

		this.generationType = "Flower";
		this.patchSize = 64;

		this.boundingBoxes = new ArrayList<>();

		this.secondaryTreeChance = 0.1;
		this.trees = new ConfiguredFeatureEntry[2];
		this.flowerTrees = new ConfiguredFeatureEntry[2];
		this.megaTrees = new ConfiguredFeatureEntry[2];
	}

	public boolean generateLootTable() {
		return !useLootTableForDrops;
	}

	@Override public MItemBlock getDefaultDrop() {
		if (dropAmount == 0) {
			return new MItemBlock(getModElement().getWorkspace(), "Blocks.AIR");
		} else if (!customDrop.isEmpty()) {
			return customDrop;
		} else if (hasBlockItem) {
			return new MItemBlock(getModElement().getWorkspace(),
					NameMapper.MCREATOR_PREFIX + this.getModElement().getName());
		} else {
			return new MItemBlock(getModElement().getWorkspace(), "Blocks.AIR");
		}
	}

	public boolean shouldDisableOffset() {
		return disableOffset || offsetType.equals("NONE");
	}

	public boolean isWaterloggable() {
		// Disable waterlogging for sapling with mega trees due to ghost water blocks when the tree fails to grow
		if ("sapling".equals(plantType) && (megaTrees[0] != null || megaTrees[1] != null)) {
			return false;
		}
		return isWaterloggable;
	}

	@Override public Model getItemModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (renderType == 2)
			modelType = Model.Type.JSON;
		else if (renderType == 3)
			modelType = Model.Type.OBJ;
		return Model.getModelByParams(getModElement().getWorkspace(), customModelName, modelType);
	}

	@Override public Map<String, TextureHolder> getTextureMap() {
		Model model = getItemModel();
		if (model instanceof TexturedModel && ((TexturedModel) model).getTextureMapping() != null)
			return ((TexturedModel) model).getTextureMapping().getTextureMap();
		return new HashMap<>();
	}

	@Override public BufferedImage generateModElementPicture() {
		if (hasBlockItem && itemTexture != null && !itemTexture.isEmpty()) {
			return ImageUtils.resizeAndCrop(itemTexture.getImage(TextureType.ITEM), 32);
		}
		return ImageUtils.resizeAndCrop(texture.getImage(TextureType.BLOCK), 32);
	}

	@Override public List<TabEntry> getCreativeTabs() {
		return creativeTabs;
	}

	public boolean isBlockTinted() {
		return !"No tint".equals(tintType);
	}

	@Override public boolean isDoubleBlock() {
		return "double".equals(plantType);
	}

	@Override public @Nonnull List<BoxEntry> getValidBoundingBoxes() {
		return boundingBoxes.stream().filter(BoxEntry::isNotEmpty).collect(Collectors.toList());
	}

	@Override public String getRenderType() {
		return "cutout";
	}

	@Override public boolean hasCustomItemProperties() {
		return maxStackSize != 64 || !rarity.equals("COMMON") || immuneToFire;
	}

	@Override public Collection<BaseType> getBaseTypesProvided() {
		List<BaseType> baseTypes = new ArrayList<>(List.of(BaseType.BLOCK));

		if (hasBlockItem) {
			baseTypes.add(BaseType.ITEM);
		}

		if (generateFeature) {
			baseTypes.add(BaseType.CONFIGUREDFEATURE);
			if (getModElement().getGenerator().getGeneratorConfiguration().getGeneratorFlavor()
					== GeneratorFlavor.FABRIC) // Fabric needs Java code to register feature generation
				baseTypes.add(BaseType.FEATURE);
		}

		if (hasTileEntity)
			baseTypes.add(BaseType.BLOCKENTITY);

		return baseTypes;
	}

	@Override public List<MCItem> providedMCItems() {
		return List.of(new MCItem.Custom(this.getModElement(), null, hasBlockItem ? "block" : "block_without_item"));
	}

	@Override public List<MCItem> getCreativeTabItems() {
		return hasBlockItem ? providedMCItems() : Collections.emptyList();
	}

	@Override public StringListProcedure getSpecialInfoProcedure() {
		return specialInformation;
	}

	public TextureHolder textureBottom() {
		return textureBottom == null || textureBottom.isEmpty() ? texture : textureBottom;
	}

	public TextureHolder getParticleTexture() {
		return particleTexture == null || particleTexture.isEmpty() ? texture : particleTexture;
	}

}