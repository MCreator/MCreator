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
import net.mcreator.element.parts.*;
import net.mcreator.element.parts.Fluid;
import net.mcreator.element.parts.procedure.NumberProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringListProcedure;
import net.mcreator.element.types.interfaces.*;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;
import net.mcreator.ui.minecraft.states.StateMap;
import net.mcreator.ui.minecraft.states.block.BlockStatePropertyUtils;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.ResourceReference;
import net.mcreator.workspace.references.TextureReference;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.resources.TexturedModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({ "unused", "NotNullFieldNotInitialized" }) public class Block extends GeneratableElement
		implements IBlock, IItemWithModel, ITabContainedElement, ISpecialInfoHolder, IBlockWithBoundingBox {

	@TextureReference(TextureType.BLOCK) public TextureHolder texture;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureTop;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureLeft;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureFront;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureRight;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureBack;
	public int renderType;
	@Nonnull public String customModelName;
	public int rotationMode;
	public boolean enablePitch;
	public boolean emissiveRendering;
	public boolean displayFluidOverlay;

	@TextureReference(TextureType.BLOCK) @ResourceReference("model") public List<Block.StateEntry> states;

	@ModElementReference @ResourceReference("animation") public List<AnimationEntry> animations;

	@TextureReference(TextureType.ITEM) public TextureHolder itemTexture;
	@TextureReference(TextureType.BLOCK) public TextureHolder particleTexture;

	public String blockBase;
	public String blockSetType;
	public MItemBlock pottedPlant;

	public String tintType;
	public boolean isItemTinted;

	public boolean hasTransparency;
	public boolean connectedSides;
	public String transparencyType;

	public boolean disableOffset;
	public List<BoxEntry> boundingBoxes;

	public List<PropertyDataWithValue<?>> customProperties;

	public String name;
	public StringListProcedure specialInformation;
	public double hardness;
	public double resistance;
	public boolean hasGravity;
	public boolean isWaterloggable;

	public boolean hasBlockItem;
	public int maxStackSize;
	public String rarity;
	public boolean immuneToFire;
	@ModElementReference public List<TabEntry> creativeTabs;

	@Nonnull public String destroyTool;
	public MItemBlock customDrop;
	public int dropAmount;
	public int xpAmountMin;
	public int xpAmountMax;
	public boolean useLootTableForDrops;
	public boolean requiresCorrectTool;

	public double enchantPowerBonus;
	public boolean plantsGrowOn;
	public boolean canRedstoneConnect;
	public int lightOpacity;

	public int tickRate;
	public boolean tickRandomly;

	public boolean isReplaceable;
	public boolean canProvidePower;
	public NumberProcedure emittedRedstonePower;
	public String colorOnMap;
	public String noteBlockInstrument;
	public MItemBlock creativePickItem;
	public String offsetType;
	public String aiPathNodeType;
	public Color beaconColorModifier;
	public MItemBlock strippingResult;

	public boolean ignitedByLava;
	public int flammability;
	public int fireSpreadSpeed;

	public boolean isLadder;
	public double slipperiness;
	public double speedFactor;
	public double jumpFactor;
	public String reactionToPushing;

	public boolean isNotColidable;

	public boolean isCustomSoundType;
	public StepSound soundOnStep;
	public Sound breakSound;
	public Sound fallSound;
	public Sound hitSound;
	public Sound placeSound;
	public Sound stepSound;

	public int luminance;
	public boolean unbreakable;
	public String vanillaToolTier;
	public Procedure additionalHarvestCondition;

	public Procedure placingCondition;

	public boolean isBonemealable;
	public Procedure isBonemealTargetCondition;
	public Procedure bonemealSuccessCondition;
	public Procedure onBonemealSuccess;

	public boolean sensitiveToVibration;
	public List<GameEventEntry> vibrationalEvents;
	public NumberProcedure vibrationSensitivityRadius;
	public Procedure canReceiveVibrationCondition;
	public Procedure onReceivedVibration;

	public boolean hasInventory;
	@ModElementReference @Nullable public String guiBoundTo;
	public boolean openGUIOnRightClick;
	public int inventorySize;
	public int inventoryStackSize;
	public boolean inventoryDropWhenDestroyed;
	public boolean inventoryComparatorPower;
	public List<Integer> inventoryOutSlotIDs;
	public List<Integer> inventoryInSlotIDs;
	public Procedure inventoryAutomationTakeCondition;
	public Procedure inventoryAutomationPlaceCondition;

	public boolean hasEnergyStorage;
	public int energyInitial;
	public int energyCapacity;
	public int energyMaxReceive;
	public int energyMaxExtract;

	public boolean isFluidTank;
	public int fluidCapacity;
	@ModElementReference public List<Fluid> fluidRestrictions;

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
	public Procedure onHitByProjectile;
	public Procedure onEntityFallsOn;

	public boolean generateFeature;
	@ModElementReference public List<BiomeEntry> restrictionBiomes;
	@ModElementReference public List<MItemBlock> blocksToReplace;
	public String generationShape;
	public int frequencyPerChunks;
	public int frequencyOnChunk;
	public int minGenerateHeight;
	public int maxGenerateHeight;

	private Block() {
		this(null);
	}

	public Block(ModElement element) {
		super(element);

		this.hasBlockItem = true;
		this.maxStackSize = 64;
		this.rarity = "COMMON";
		this.creativeTabs = new ArrayList<>();

		this.customProperties = new ArrayList<>();

		this.blockSetType = "OAK";
		this.tintType = "No tint";
		this.boundingBoxes = new ArrayList<>();
		this.restrictionBiomes = new ArrayList<>();
		this.reactionToPushing = "NORMAL";
		this.slipperiness = 0.6;
		this.speedFactor = 1.0;
		this.jumpFactor = 1.0;
		this.colorOnMap = "DEFAULT";
		this.noteBlockInstrument = "harp";
		this.aiPathNodeType = "DEFAULT";
		this.offsetType = "NONE";
		this.generationShape = "UNIFORM";
		this.destroyTool = "Not specified";
		this.vanillaToolTier = "NONE";
		this.inventoryInSlotIDs = new ArrayList<>();
		this.inventoryOutSlotIDs = new ArrayList<>();

		this.energyCapacity = 400000;
		this.energyMaxReceive = 200;
		this.energyMaxExtract = 200;
		this.fluidCapacity = 8000;

		this.vibrationalEvents = new ArrayList<>();

		this.animations = new ArrayList<>();

		this.states = new ArrayList<>();
	}

	public int renderType() {
		if (blockBase != null && !blockBase.isEmpty() && !blockBase.equals("FlowerPot"))
			return -1;
		return renderType;
	}

	public boolean hasCustomDrop() {
		return !customDrop.isEmpty();
	}

	public boolean generateLootTable() {
		return !useLootTableForDrops;
	}

	public boolean isBlockTinted() {
		return !"No tint".equals(tintType);
	}

	@Override public boolean isDoubleBlock() {
		return "Door".equals(blockBase);
	}

	public boolean shouldOpenGUIOnRightClick() {
		return guiBoundTo != null && openGUIOnRightClick;
	}

	public boolean shouldScheduleTick() {
		return tickRate > 0 && !tickRandomly;
	}

	public boolean shouldDisableOffset() {
		return disableOffset || offsetType.equals("NONE");
	}

	public boolean hasDrops() {
		return dropAmount > 0 && (hasBlockItem || hasCustomDrop() || "FlowerPot".equals(blockBase));
	}

	public boolean supportsBlockStates() {
		if (getItemModel().getType() == Model.Type.JAVA)
			return false;

		return blockBase == null || blockBase.isEmpty();
	}

	@Override public boolean isFullCube() {
		if ("Stairs".equals(blockBase) || "Slab".equals(blockBase) || "Fence".equals(blockBase) || "Wall".equals(
				blockBase) || "TrapDoor".equals(blockBase) || "Door".equals(blockBase) || "FenceGate".equals(blockBase)
				|| "EndRod".equals(blockBase) || "PressurePlate".equals(blockBase) || "Button".equals(blockBase)
				|| "FlowerPot".equals(blockBase))
			return false;

		return IBlockWithBoundingBox.super.isFullCube();
	}

	@Override public Model getItemModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (renderType == 2)
			modelType = Model.Type.JSON;
		else if (renderType == 3)
			modelType = Model.Type.OBJ;
		else if (renderType == 4)
			modelType = Model.Type.JAVA;
		return Model.getModelByParams(getModElement().getWorkspace(), customModelName, modelType);
	}

	@Override public Map<String, TextureHolder> getTextureMap() {
		Model model = getItemModel();
		if (model instanceof TexturedModel && ((TexturedModel) model).getTextureMapping() != null)
			return ((TexturedModel) model).getTextureMapping().getTextureMap();
		return new HashMap<>();
	}

	@Override public List<TabEntry> getCreativeTabs() {
		return creativeTabs;
	}

	@Override public @Nonnull List<BoxEntry> getValidBoundingBoxes() {
		return boundingBoxes.stream().filter(BoxEntry::isNotEmpty).collect(Collectors.toList());
	}

	@Override public BufferedImage generateModElementPicture() {
		if ((hasBlockItem && itemTexture != null && !itemTexture.isEmpty()) || (renderType() == 4)) {
			return ImageUtils.resizeAndCrop(itemTexture.getImage(TextureType.ITEM), 32);
		} else if (renderType() == 10) {
			return (BufferedImage) MinecraftImageGenerator.Preview.generateBlockIcon(getTextureWithFallback(textureTop),
					getTextureWithFallback(textureLeft), getTextureWithFallback(textureFront));
		} else if (renderType() == 11 || renderType() == 110 || "Leaves".equals(blockBase)) {
			return (BufferedImage) MinecraftImageGenerator.Preview.generateBlockIcon(getMainTexture(), getMainTexture(),
					getMainTexture());
		} else if ("Slab".equals(blockBase)) {
			return (BufferedImage) MinecraftImageGenerator.Preview.generateSlabIcon(getTextureWithFallback(textureTop),
					getTextureWithFallback(textureFront));
		} else if ("TrapDoor".equals(blockBase)) {
			return (BufferedImage) MinecraftImageGenerator.Preview.generateTrapdoorIcon(getMainTexture());
		} else if ("Stairs".equals(blockBase)) {
			return (BufferedImage) MinecraftImageGenerator.Preview.generateStairsIcon(
					getTextureWithFallback(textureTop), getTextureWithFallback(textureFront));
		} else if ("Wall".equals(blockBase)) {
			return (BufferedImage) MinecraftImageGenerator.Preview.generateWallIcon(getMainTexture());
		} else if ("Fence".equals(blockBase)) {
			return (BufferedImage) MinecraftImageGenerator.Preview.generateFenceIcon(getMainTexture());
		} else if ("FenceGate".equals(blockBase)) {
			return (BufferedImage) MinecraftImageGenerator.Preview.generateFenceGateIcon(getMainTexture());
		} else if ("EndRod".equals(blockBase)) {
			return (BufferedImage) MinecraftImageGenerator.Preview.generateEndRodIcon(getMainTexture());
		} else if ("PressurePlate".equals(blockBase)) {
			return (BufferedImage) MinecraftImageGenerator.Preview.generatePressurePlateIcon(getMainTexture());
		} else if ("Button".equals(blockBase)) {
			return (BufferedImage) MinecraftImageGenerator.Preview.generateButtonIcon(getMainTexture());
		} else if (renderType() == 14) {
			Image side = ImageUtils.drawOver(new ImageIcon(getTextureWithFallback(textureFront)),
					new ImageIcon(getTextureWithFallback(textureLeft))).getImage();
			return (BufferedImage) MinecraftImageGenerator.Preview.generateBlockIcon(getTextureWithFallback(textureTop),
					side, side);
		} else {
			return ImageUtils.resizeAndCrop(getMainTexture(), 32);
		}
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

	private Image getMainTexture() {
		return texture.getImage(TextureType.BLOCK);
	}

	private Image getTextureWithFallback(TextureHolder texture) {
		if (texture.isEmpty())
			return getMainTexture();
		return texture.getImage(TextureType.BLOCK);
	}

	@Override public String getRenderType() {
		if (hasTransparency && transparencyType.equals(
				"solid")) // if hasTransparency is enabled but transparencyType is left solid, we assume cutout
			return "cutout";

		return transparencyType.toLowerCase(Locale.ENGLISH);
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

		if (hasInventory)
			baseTypes.add(BaseType.BLOCKENTITY);

		return baseTypes;
	}

	public Set<String> getVibrationalEvents() {
		return vibrationalEvents.stream().map(e -> e.getMappedValue(1)).collect(Collectors.toSet());
	}

	public TextureHolder textureTop() {
		return textureTop == null || textureTop.isEmpty() ? texture : textureTop;
	}

	public TextureHolder textureLeft() {
		return textureLeft == null || textureLeft.isEmpty() ? texture : textureLeft;
	}

	public TextureHolder textureFront() {
		return textureFront == null || textureFront.isEmpty() ? texture : textureFront;
	}

	public TextureHolder textureRight() {
		return textureRight == null || textureRight.isEmpty() ? texture : textureRight;
	}

	public TextureHolder textureBack() {
		return textureBack == null || textureBack.isEmpty() ? texture : textureBack;
	}

	public TextureHolder getParticleTexture() {
		return particleTexture == null || particleTexture.isEmpty() ? texture : particleTexture;
	}

	/**
	 * @return List of user-defined states or empty list if none defined or of the current block does not support custom block states
	 */
	public List<StateEntry> getDefinedStates() {
		if (!supportsBlockStates() || states.isEmpty())
			return List.of();
		return new ArrayList<>(states);
	}

	/**
	 * @return List of {@link #states} with missing state combinations autofilled
	 */
	public List<StateEntry> getStateCombinations() {
		if (!supportsBlockStates() || states.isEmpty())
			return List.of();

		// add user-defined states
		ArrayList<StateEntry> retval = new ArrayList<>(states);

		// autofill missing state condition combinations below

		// collect all used properties (all state maps use all properties as UI validation ensures this)
		Set<PropertyData<?>> usedProperties = states.getFirst().stateMap.keySet();

		// generate a list of all possible state condition combinations
		Map<PropertyData<?>, List<Object>> valueMap = new LinkedHashMap<>();
		for (PropertyData<?> prop : usedProperties) {
			valueMap.put(prop, BlockStatePropertyUtils.getPossiblePropertyValues(prop));
		}

		Set<StateMap> possibleStateCombinations = new HashSet<>();
		for (Map<PropertyData<?>, Object> combo : ListUtils.cartesianProduct(valueMap)) {
			StateMap stateCombination = new StateMap();
			stateCombination.putAll(combo);
			possibleStateCombinations.add(stateCombination);
		}

		// remove combinations that are already handled
		possibleStateCombinations.removeAll(states.stream().map(s -> s.stateMap).collect(Collectors.toSet()));

		// add combinations that are missing
		for (StateMap possibleStateCombination : possibleStateCombinations) {
			var stateEntry = new StateEntry();
			stateEntry.renderType = -1; // use the default model
			stateEntry.stateMap = possibleStateCombination;
			retval.add(stateEntry);
		}

		return retval;
	}

	public List<StateEntry> getDefinedStatesWithCustomShape() {
		List<StateEntry> retval = new ArrayList<>();
		for (StateEntry stateEntry : getDefinedStates()) {
			if (stateEntry.hasCustomBoundingBox && !stateEntry.isFullCube()) {
				retval.add(stateEntry);
			}
		}
		return retval;
	}

	public List<String> getPropertiesUsedInStates() {
		if (!supportsBlockStates() || states.isEmpty())
			return List.of();
		return states.getFirst().stateMap.keySet().stream().map(PropertyData::getName).collect(Collectors.toList());
	}

	public static class StateEntry implements IWorkspaceDependent, IItemWithModel, IBlockWithBoundingBox {

		@TextureReference(TextureType.BLOCK) public TextureHolder texture;
		@TextureReference(TextureType.BLOCK) public TextureHolder textureTop;
		@TextureReference(TextureType.BLOCK) public TextureHolder textureLeft;
		@TextureReference(TextureType.BLOCK) public TextureHolder textureFront;
		@TextureReference(TextureType.BLOCK) public TextureHolder textureRight;
		@TextureReference(TextureType.BLOCK) public TextureHolder textureBack;
		public int renderType;
		@Nonnull public String customModelName;

		@TextureReference(TextureType.BLOCK) public TextureHolder particleTexture;

		public boolean hasCustomBoundingBox;
		public List<BoxEntry> boundingBoxes;

		public StateMap stateMap;

		@Nullable transient Workspace workspace;

		@Override public void setWorkspace(@Nullable Workspace workspace) {
			this.workspace = workspace;
		}

		@Nullable @Override public Workspace getWorkspace() {
			return workspace;
		}

		// Helper methods so the same templates as for the main model can be used

		public TextureHolder textureTop() {
			return textureTop == null || textureTop.isEmpty() ? texture : textureTop;
		}

		public TextureHolder textureLeft() {
			return textureLeft == null || textureLeft.isEmpty() ? texture : textureLeft;
		}

		public TextureHolder textureFront() {
			return textureFront == null || textureFront.isEmpty() ? texture : textureFront;
		}

		public TextureHolder textureRight() {
			return textureRight == null || textureRight.isEmpty() ? texture : textureRight;
		}

		public TextureHolder textureBack() {
			return textureBack == null || textureBack.isEmpty() ? texture : textureBack;
		}

		public TextureHolder getParticleTexture(TextureHolder fallback) {
			return particleTexture == null || particleTexture.isEmpty() ? fallback : particleTexture;
		}

		@Override public Model getItemModel() {
			Model.Type modelType = Model.Type.BUILTIN;
			if (renderType == 2)
				modelType = Model.Type.JSON;
			else if (renderType == 3)
				modelType = Model.Type.OBJ;
			return Model.getModelByParams(workspace, customModelName, modelType);
		}

		@Override public Map<String, TextureHolder> getTextureMap() {
			Model model = getItemModel();
			if (model instanceof TexturedModel && ((TexturedModel) model).getTextureMapping() != null)
				return ((TexturedModel) model).getTextureMapping().getTextureMap();
			return new HashMap<>();
		}

		@Override public @Nonnull List<BoxEntry> getValidBoundingBoxes() {
			return boundingBoxes.stream().filter(BoxEntry::isNotEmpty).collect(Collectors.toList());
		}

	}

	public static class AnimationEntry {

		public Animation animation;
		public double speed;

		public Procedure condition;

	}

}
