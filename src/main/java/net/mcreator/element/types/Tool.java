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
import net.mcreator.element.parts.procedure.LogicProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringListProcedure;
import net.mcreator.element.types.interfaces.IItem;
import net.mcreator.element.types.interfaces.IItemWithModel;
import net.mcreator.element.types.interfaces.IItemWithTexture;
import net.mcreator.element.types.interfaces.ITabContainedElement;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "unused", "NotNullFieldNotInitialized" }) public class Tool extends GeneratableElement
		implements IItem, IItemWithModel, ITabContainedElement, IItemWithTexture {

	@Nonnull public String toolType;

	public int renderType;
	public int blockingRenderType;
	@TextureReference(TextureType.ITEM) public String texture;
	@Nonnull public String customModelName;
	@Nonnull public String blockingModelName;

	public String name;
	public StringListProcedure specialInformation;
	public TabEntry creativeTab;
	public int harvestLevel;
	public double efficiency;
	public double attackSpeed;
	public int enchantability;
	public double damageVsEntity;
	public int usageCount;
	@ModElementReference public List<MItemBlock> blocksAffected;
	public LogicProcedure glowCondition;
	@ModElementReference public List<MItemBlock> repairItems;
	public boolean immuneToFire;

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

		this.attackSpeed = 2.8;

		this.blockingModelName = "Normal blocking";
	}

	@Override public BufferedImage generateModElementPicture() {
		return ImageUtils.resizeAndCrop(
				getModElement().getFolderManager().getTextureImageIcon(texture, TextureType.ITEM).getImage(), 32);
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

	@Override public Map<String, String> getTextureMap() {
		Model model = getItemModel();
		if (model instanceof TexturedModel && ((TexturedModel) model).getTextureMapping() != null)
			return ((TexturedModel) model).getTextureMapping().getTextureMap();
		return new HashMap<>();
	}

	public Map<String, String> getBlockingTextureMap() {
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

	@Override public TabEntry getCreativeTab() {
		return creativeTab;
	}

	@Override public String getTexture() {
		return texture;
	}

	@Override public List<MCItem> providedMCItems() {
		return List.of(new MCItem.Custom(this.getModElement(), null, "item"));
	}

	@Override public List<MCItem> getCreativeTabItems() {
		return providedMCItems();
	}
}
