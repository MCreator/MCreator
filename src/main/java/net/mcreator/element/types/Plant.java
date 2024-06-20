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
import net.mcreator.element.types.interfaces.IBlock;
import net.mcreator.element.types.interfaces.IBlockWithBoundingBox;
import net.mcreator.element.types.interfaces.IItemWithModel;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.resources.Texture;
import net.mcreator.workspace.resources.TexturedModel;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused") public class Plant extends GeneratableElement
		implements IBlock, IItemWithModel, ITabContainedElement, IBlockWithBoundingBox {

	public int renderType;
	@TextureReference(TextureType.BLOCK) public String texture;
	@TextureReference(TextureType.BLOCK) public String textureBottom;
	@Nonnull public String customModelName;

	@TextureReference(TextureType.ITEM) public String itemTexture;
	@TextureReference(TextureType.BLOCK) public String particleTexture;

	public String tintType;
	public boolean isItemTinted;

	public String plantType;

	@ModElementReference public String suspiciousStewEffect;
	public int suspiciousStewDuration;

	public String growapableSpawnType;
	public int growapableMaxHeight;

	public boolean customBoundingBox;
	public boolean disableOffset;
	public List<BoxEntry> boundingBoxes;

	public String name;
	public StringListProcedure specialInformation;
	public TabEntry creativeTab;
	public double hardness;
	public double resistance;
	public int luminance;
	public boolean unbreakable;
	public boolean isSolid;

	public boolean isCustomSoundType;
	public StepSound soundOnStep;
	public Sound breakSound;
	public Sound stepSound;
	public Sound placeSound;
	public Sound hitSound;
	public Sound fallSound;

	public boolean useLootTableForDrops;
	public MItemBlock customDrop;
	public int dropAmount;
	public boolean forceTicking;
	public boolean emissiveRendering;

	public boolean hasTileEntity;

	public boolean isReplaceable;
	public String colorOnMap;
	public MItemBlock creativePickItem;
	public String offsetType;
	public String aiPathNodeType;

	public int flammability;
	public int fireSpreadSpeed;
	public double jumpFactor;
	public double speedFactor;

	@ModElementReference public List<MItemBlock> canBePlacedOn;
	public Procedure placingCondition;

	public boolean isBonemealable;
	public Procedure isBonemealTargetCondition;
	public Procedure bonemealSuccessCondition;
	public Procedure onBonemealSuccess;

	public int frequencyOnChunks;
	public boolean generateFeature;
	@ModElementReference public List<BiomeEntry> restrictionBiomes;
	public String generationType;
	public int patchSize;
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

	private Plant() {
		this(null);
	}

	public Plant(ModElement element) {
		super(element);

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
	}

	public boolean generateLootTable() {
		return !useLootTableForDrops;
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
		return new HashMap<>();
	}

	@Override public BufferedImage generateModElementPicture() {
		return ImageUtils.resizeAndCrop(Texture.getImage(getModElement().getWorkspace(), TextureType.BLOCK, texture),
				32);
	}

	@Override public TabEntry getCreativeTab() {
		return creativeTab;
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

	@Override public Collection<BaseType> getBaseTypesProvided() {
		List<BaseType> baseTypes = new ArrayList<>(List.of(BaseType.BLOCK, BaseType.ITEM));

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
		return List.of(new MCItem.Custom(this.getModElement(), null, "block"));
	}

	@Override public List<MCItem> getCreativeTabItems() {
		return providedMCItems();
	}

}