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
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.parts.procedure.LogicProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringListProcedure;
import net.mcreator.element.types.interfaces.*;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.StateMap;
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

@SuppressWarnings({ "unused", "NotNullFieldNotInitialized" }) public class Tool extends GeneratableElement
		implements IItem, IItemWithModel, ITabContainedElement, ISpecialInfoHolder, IItemWithTexture {

	@Nonnull public String toolType;

	public int renderType;
	public int blockingRenderType;
	@TextureReference(TextureType.ITEM) public TextureHolder texture;
	@Nonnull public String customModelName;
	@Nonnull public String blockingModelName;

	public String name;
	public StringListProcedure specialInformation;
	@ModElementReference public List<TabEntry> creativeTabs;
	public double efficiency;
	public double attackSpeed;
	public int enchantability;
	public double damageVsEntity;
	public int usageCount;
	public LogicProcedure glowCondition;
	@ModElementReference public List<MItemBlock> repairItems;
	public boolean immuneToFire;

	public String blockDropsTier;
	public Procedure additionalDropCondition;

	@ModElementReference public List<MItemBlock> blocksAffected;

	public boolean stayInGridWhenCrafting;
	public boolean damageOnCrafting;

	public Procedure onRightClickedInAir;
	public Procedure onRightClickedOnBlock;
	public Procedure onCrafted;
	public Procedure onEntityHitWith;
	public Procedure onItemInInventoryTick;
	public Procedure onItemInUseTick;
	public Procedure onBlockDestroyedWithTool;
	public Procedure onEntitySwing;

	private Tool() {
		this(null);
	}

	public Tool(ModElement element) {
		super(element);

		this.creativeTabs = new ArrayList<>();
		this.repairItems = new ArrayList<>();

		this.attackSpeed = 2.8;

		this.blockingModelName = "Normal blocking";

		this.blockDropsTier = "WOOD";
	}

	@Override public BufferedImage generateModElementPicture() {
		return ImageUtils.resizeAndCrop(texture.getImage(TextureType.ITEM), 32);
	}

	@Override public Model getItemModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (renderType == 1)
			modelType = Model.Type.JSON;
		else if (renderType == 2)
			modelType = Model.Type.OBJ;
		return Model.getModelByParams(getModElement().getWorkspace(), customModelName, modelType);
	}

	public Model getBlockingModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (blockingRenderType == 1)
			modelType = Model.Type.JSON;
		else if (blockingRenderType == 2)
			modelType = Model.Type.OBJ;
		return Model.getModelByParams(getModElement().getWorkspace(), blockingModelName, modelType);
	}

	@Override public Map<String, TextureHolder> getTextureMap() {
		Model model = getItemModel();
		if (model instanceof TexturedModel && ((TexturedModel) model).getTextureMapping() != null)
			return ((TexturedModel) model).getTextureMapping().getTextureMap();
		return new HashMap<>();
	}

	public Map<String, TextureHolder> getBlockingTextureMap() {
		Model model = getBlockingModel();
		if (model instanceof TexturedModel && ((TexturedModel) model).getTextureMapping() != null)
			return ((TexturedModel) model).getTextureMapping().getTextureMap();
		return new HashMap<>();
	}

	public List<Item.StateEntry> getModels() {
		if (toolType.equals("Shield")) {
			Item.StateEntry model = new Item.StateEntry();
			model.setWorkspace(getModElement().getWorkspace());
			model.renderType = blockingRenderType;
			model.texture = texture;
			model.customModelName = blockingModelName;

			model.stateMap = new StateMap();
			model.stateMap.put(new PropertyData.LogicType("blocking"), true);

			return Collections.singletonList(model);
		} else {
			return Collections.emptyList();
		}
	}

	@Override public List<TabEntry> getCreativeTabs() {
		return creativeTabs;
	}

	@Override public TextureHolder getTexture() {
		return texture;
	}

	@Override public List<MCItem> providedMCItems() {
		return List.of(new MCItem.Custom(this.getModElement(), null, "item"));
	}

	@Override public List<MCItem> getCreativeTabItems() {
		return providedMCItems();
	}

	@Override public StringListProcedure getSpecialInfoProcedure() {
		return specialInformation;
	}

	public List<String> getRepairItemsAsStringList() {
		List<String> repairItems = new ArrayList<>();
		for (MItemBlock repairItem : this.repairItems)
			repairItems.add(repairItem.getUnmappedValue());
		return repairItems;
	}

	public boolean isUsingJavaModel() {
		return false; //for the moment
	}
}
