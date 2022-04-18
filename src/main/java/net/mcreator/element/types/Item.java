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
import net.mcreator.element.parts.Procedure;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.interfaces.IItem;
import net.mcreator.element.types.interfaces.IItemWithModel;
import net.mcreator.element.types.interfaces.IItemWithTexture;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.resources.TexturedModel;

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") public class Item extends GeneratableElement
		implements IItem, IItemWithModel, ITabContainedElement, IItemWithTexture {

	public int renderType;
	public String texture;
	public String customModelName;

	public Map<String, Procedure> customProperties;
	public Map<String, ModelEntry> modelsMap;

	public String name;
	public String rarity;
	public TabEntry creativeTab;
	public int stackSize;
	public int enchantability;
	public int useDuration;
	public double toolType;
	public int damageCount;
	public MItemBlock recipeRemainder;
	public boolean destroyAnyBlock;
	public boolean immuneToFire;

	public boolean stayInGridWhenCrafting;
	public boolean damageOnCrafting;

	public boolean enableMeleeDamage;
	public double damageVsEntity;

	public List<String> specialInfo;
	public boolean hasGlow;
	public Procedure glowCondition;

	public String guiBoundTo;
	public int inventorySize;
	public int inventoryStackSize;

	public Procedure onRightClickedInAir;
	public Procedure onRightClickedOnBlock;
	public Procedure onCrafted;
	public Procedure onEntityHitWith;
	public Procedure onItemInInventoryTick;
	public Procedure onItemInUseTick;
	public Procedure onStoppedUsing;
	public Procedure onEntitySwing;
	public Procedure onDroppedByPlayer;
	public Procedure onFinishUsingItem;

	public boolean hasDispenseBehavior;
	public Procedure dispenseSuccessCondition;
	public Procedure dispenseResultItemstack;

	// Food
	public boolean isFood;
	public int nutritionalValue;
	public double saturation;
	public MItemBlock eatResultItem;
	public boolean isMeat;
	public boolean isAlwaysEdible;
	public String animation;

	public static int encodeModelType(Model.Type modelType) {
		if (modelType == Model.Type.JSON)
			return 1;
		else if (modelType == Model.Type.OBJ)
			return 2;
		else
			return 0;
	}

	public static Model.Type decodeModelType(int modelType) {
		if (modelType == 1)
			return Model.Type.JSON;
		else if (modelType == 2)
			return Model.Type.OBJ;
		else
			return Model.Type.BUILTIN;
	}

	private Item() {
		this(null);
	}

	public Item(ModElement element) {
		super(element);

		this.customProperties = new LinkedHashMap<>();
		this.modelsMap = new LinkedHashMap<>();

		this.rarity = "COMMON";
		this.inventorySize = 9;
		this.inventoryStackSize = 64;
		this.saturation = 0.3f;
		this.animation = "eat";
	}

	@Override public BufferedImage generateModElementPicture() {
		return ImageUtils.resizeAndCrop(
				getModElement().getFolderManager().getTextureImageIcon(texture, TextureType.ITEM).getImage(), 32);
	}

	@Override public Model getItemModel() {
		return Model.getModelByParams(getModElement().getWorkspace(), customModelName, decodeModelType(renderType));
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

	@Override public String getTexture() {
		return texture;
	}

	public boolean hasNormalModel() {
		return decodeModelType(renderType) == Model.Type.BUILTIN && customModelName.equals("Normal");
	}

	public boolean hasToolModel() {
		return decodeModelType(renderType) == Model.Type.BUILTIN && customModelName.equals("Tool");
	}

	public boolean hasInventory() {
		return guiBoundTo != null && !guiBoundTo.isEmpty() && !guiBoundTo.equals("<NONE>");
	}

	public boolean hasNonDefaultAnimation() {
		return isFood ? !animation.equals("eat") : !animation.equals("none");
	}

	public boolean hasEatResultItem() {
		return isFood && eatResultItem != null && !eatResultItem.isEmpty();
	}

	public static class ModelEntry {

		public int renderType;
		public String modelTexture;
		public String modelName;

		public Model getItemModel(Workspace workspace) {
			return Model.getModelByParams(workspace, modelName, decodeModelType(renderType));
		}

		public Map<String, String> getTextureMap(Workspace workspace) {
			Model model = getItemModel(workspace);
			if (model instanceof TexturedModel && ((TexturedModel) model).getTextureMapping() != null)
				return ((TexturedModel) model).getTextureMapping().getTextureMap();
			return null;
		}

		public boolean isNormalModel(Workspace workspace) {
			return decodeModelType(renderType) == Model.Type.BUILTIN && modelName.equals("Normal");
		}

		public boolean isToolModel(Workspace workspace) {
			return decodeModelType(renderType) == Model.Type.BUILTIN && modelName.equals("Tool");
		}

	}

}
