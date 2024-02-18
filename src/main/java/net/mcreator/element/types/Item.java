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
import net.mcreator.element.parts.IWorkspaceDependent;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.ProjectileEntry;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.procedure.LogicProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringListProcedure;
import net.mcreator.element.types.interfaces.IItem;
import net.mcreator.element.types.interfaces.IItemWithModel;
import net.mcreator.element.types.interfaces.IItemWithTexture;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.minecraft.states.StateMap;
import net.mcreator.ui.workspace.resources.TextureType;
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
import java.awt.image.BufferedImage;
import java.util.*;

@SuppressWarnings({ "unused", "NotNullFieldNotInitialized" }) public class Item extends GeneratableElement
		implements IItem, IItemWithModel, ITabContainedElement, IItemWithTexture {

	public int renderType;
	@TextureReference(TextureType.ITEM) public String texture;
	@Nonnull public String customModelName;

	@ModElementReference public Map<String, Procedure> customProperties;
	@TextureReference(TextureType.ITEM) @ResourceReference("model") public List<StateEntry> states;

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

	public StringListProcedure specialInformation;
	public LogicProcedure glowCondition;

	@Nullable @ModElementReference(defaultValues = "<NONE>") public String guiBoundTo;
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

	// Ranged properties
	public boolean enableRanged;
	public boolean shootConstantly;
	public ProjectileEntry projectile;
	public Procedure onRangedItemUsed;
	public Procedure rangedUseCondition;

	// Food
	public boolean isFood;
	public int nutritionalValue;
	public double saturation;
	public MItemBlock eatResultItem;
	public boolean isMeat;
	public boolean isAlwaysEdible;
	public String animation;

	private Item() {
		this(null);
	}

	public Item(ModElement element) {
		super(element);

		this.customProperties = new LinkedHashMap<>();
		this.states = new ArrayList<>();

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
		if (getItemModel() instanceof TexturedModel textured && textured.getTextureMapping() != null)
			return textured.getTextureMapping().getTextureMap();
		return new HashMap<>();
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

	public boolean hasNormalModel() {
		return decodeModelType(renderType) == Model.Type.BUILTIN && customModelName.equals("Normal");
	}

	public boolean hasToolModel() {
		return decodeModelType(renderType) == Model.Type.BUILTIN && customModelName.equals("Tool");
	}

	public boolean hasRangedItemModel() {
		return decodeModelType(renderType) == Model.Type.BUILTIN && customModelName.equals("Ranged item");
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

	/**
	 * Returns a copy of {@link #states} referencing only properties supported in the current workspace.
	 * Should only be used by generators to filter invalid data.
	 * <p>
	 * Also populates models with Workspace reference for the use in templates
	 *
	 * @return Models with contents matching current generator.
	 */
	public List<StateEntry> getModels() {
		List<StateEntry> models = new ArrayList<>();
		List<String> builtinProperties = DataListLoader.loadDataList("itemproperties").stream()
				.filter(e -> e.isSupportedInWorkspace(getModElement().getWorkspace())).map(DataListEntry::getName)
				.toList();

		states.forEach(state -> {
			StateEntry model = new StateEntry();
			model.setWorkspace(getModElement().getWorkspace());
			model.renderType = state.renderType;
			model.texture = state.texture;
			model.customModelName = state.customModelName;

			model.stateMap = new StateMap();
			state.stateMap.forEach((prop, value) -> {
				if (customProperties.containsKey(prop.getName().replace("CUSTOM:", "")) || builtinProperties.contains(
						prop.getName()))
					model.stateMap.put(prop, value);
			});

			// only add this state if at least one supported property is present
			if (!model.stateMap.isEmpty())
				models.add(model);
		});
		return models;
	}

	public static class StateEntry implements IWorkspaceDependent {

		public int renderType;
		@TextureReference(TextureType.ITEM) public String texture;
		public String customModelName;

		public StateMap stateMap;

		@Nullable transient Workspace workspace;

		@Override public void setWorkspace(@Nullable Workspace workspace) {
			this.workspace = workspace;
		}

		@Nullable @Override public Workspace getWorkspace() {
			return workspace;
		}

		public Model getItemModel() {
			return Model.getModelByParams(workspace, customModelName, decodeModelType(renderType));
		}

		public Map<String, String> getTextureMap() {
			if (getItemModel() instanceof TexturedModel textured && textured.getTextureMapping() != null)
				return textured.getTextureMapping().getTextureMap();
			return null;
		}

		public boolean hasNormalModel() {
			return decodeModelType(renderType) == Model.Type.BUILTIN && customModelName.equals("Normal");
		}

		public boolean hasToolModel() {
			return decodeModelType(renderType) == Model.Type.BUILTIN && customModelName.equals("Tool");
		}

		public boolean hasRangedItemModel() {
			return decodeModelType(renderType) == Model.Type.BUILTIN && customModelName.equals("Ranged item");
		}
	}

	public static int encodeModelType(Model.Type modelType) {
		return switch (modelType) {
			case JSON -> 1;
			case OBJ -> 2;
			default -> 0;
		};
	}

	public static Model.Type decodeModelType(int modelType) {
		return switch (modelType) {
			case 1 -> Model.Type.JSON;
			case 2 -> Model.Type.OBJ;
			default -> Model.Type.BUILTIN;
		};
	}

}
