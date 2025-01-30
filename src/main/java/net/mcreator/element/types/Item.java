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
import net.mcreator.element.parts.*;
import net.mcreator.element.parts.procedure.LogicProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringListProcedure;
import net.mcreator.element.types.interfaces.*;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.minecraft.states.PropertyData;
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
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({ "unused", "NotNullFieldNotInitialized" }) public class Item extends GeneratableElement
		implements IItem, IItemWithModel, ITabContainedElement, ISpecialInfoHolder, IItemWithTexture {

	public int renderType;
	@TextureReference(TextureType.ITEM) public TextureHolder texture;
	@Nonnull public String customModelName;

	@ModElementReference public Map<String, Procedure> customProperties;
	@TextureReference(TextureType.ITEM) @ResourceReference("model") public List<StateEntry> states;

	public String name;
	public String rarity;
	@ModElementReference public List<TabEntry> creativeTabs;
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

	@Nullable @ModElementReference public String guiBoundTo;
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
	public boolean rangedItemChargesPower;
	public ProjectileEntry projectile;
	public boolean projectileDisableAmmoCheck;
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

	// Music disc
	public boolean isMusicDisc;
	public Sound musicDiscMusic;
	public String musicDiscDescription;
	public int musicDiscLengthInTicks;
	public int musicDiscAnalogOutput;

	private Item() {
		this(null);
	}

	public Item(ModElement element) {
		super(element);

		this.creativeTabs = new ArrayList<>();

		this.customProperties = new LinkedHashMap<>();
		this.states = new ArrayList<>();

		this.rarity = "COMMON";
		this.inventorySize = 9;
		this.inventoryStackSize = 64;
		this.saturation = 0.3f;
		this.animation = "eat";
	}

	@Override public BufferedImage generateModElementPicture() {
		return ImageUtils.resizeAndCrop(texture.getImage(TextureType.ITEM), 32);
	}

	@Override public Model getItemModel() {
		return Model.getModelByParams(getModElement().getWorkspace(), customModelName, decodeModelType(renderType));
	}

	@Override public Map<String, TextureHolder> getTextureMap() {
		if (getItemModel() instanceof TexturedModel textured && textured.getTextureMapping() != null)
			return textured.getTextureMapping().getTextureMap();
		return new HashMap<>();
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
		return guiBoundTo != null && !guiBoundTo.isEmpty();
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
		AtomicInteger index = new AtomicInteger();

		states.stream().sorted(Comparator.comparing(state -> -state.stateMap.size())).forEach(state -> {
			StateEntry model = new StateEntry();
			model.setWorkspace(getModElement().getWorkspace());
			model.renderType = state.renderType;
			model.texture = state.texture;
			model.customModelName = state.customModelName;

			model.stateMap = new StateMap();
			state.stateMap.forEach((prop, value) -> {
				if (customProperties.containsKey(prop.getName().replace(NameMapper.MCREATOR_PREFIX, ""))
						|| builtinProperties.contains(prop.getName()))
					model.stateMap.put(prop, value);
			});

			// only add this state if not duplicate and at least one supported property is present
			if (!models.contains(model) && !model.stateMap.isEmpty()) {
				model.listIndex = index.getAndIncrement();
				models.add(model);
			}
		});
		return models;
	}

	public StateEntry getBaseState() {
		StateEntry baseState = new StateEntry();
		baseState.renderType = renderType;
		baseState.texture = texture;
		baseState.customModelName = customModelName;
		return baseState;
	}

	/**
	 * Returns a tree-like states representation referencing only properties supported in the current workspace.
	 * For use by 1.21.4+ generators.
	 *
	 * @return Models tree with contents matching current generator.
	 */
	public StateTreeNode getModelsTree() {
		StateEntry baseState = getBaseState();

		// initially set to item's default state
		// if no model overrides are defined, this will return tree of depth 1
		StateTreeNode root = baseState;
		for (StateEntry state : getModels()) {
			if (state.stateMap.isEmpty())
				continue; // should never happen because states with empty StateMap are removed by getModels()

			StateBranch current = new StateBranch(null, root);
			Map.Entry<PropertyData<?>, Object> cachedPair = null;
			for (Map.Entry<PropertyData<?>, Object> pair : state.stateMap.entrySet()) {
				if (cachedPair != null) {
					StateTreeNode node = current.values.get(cachedPair.getValue());
					if (node instanceof StateBranch branch) {
						current = branch; // switch to found branch to handle it later
					} else { // otherwise create a new branch for current property
						current.values.put(cachedPair.getValue(),
								new StateBranch(pair.getKey(), Objects.requireNonNullElse(node, baseState)));
						current = (StateBranch) current.values.get(cachedPair.getValue());
					}
				}

				// these two will only be called if we are at a wrong branch
				while (!pair.getKey().equals(current.property) && current.fallback instanceof StateBranch branch)
					current = branch; // keep going to fallback node until we reach branch for given property
				if (!pair.getKey().equals(current.property)) { // or create it if not found
					current.fallback = new StateBranch(pair.getKey(), current.fallback);
					current = (StateBranch) current.fallback;
					if (root == baseState) // update reference to root node to be returned
						root = current; // this will not happen more than once (when the first branch is created)
				}

				cachedPair = pair;
			}

			StateTreeNode node = current.values.get(cachedPair.getValue());
			if (node instanceof StateBranch branch) {
				// find the deepest fallback node that is a branch
				current = branch;
				while (current.fallback instanceof StateBranch branch2)
					current = branch2;
				if (current.fallback == null || current.fallback == baseState)
					current.fallback = state; // only reassign if currently unset or set to default state
			} else if (node == null || node == baseState) {
				current.values.put(cachedPair.getValue(), state);
			}
		}

		return root;
	}

	public sealed interface StateTreeNode permits StateBranch, StateEntry {}

	public static final class StateBranch implements StateTreeNode {

		public PropertyData<?> property;
		public LinkedHashMap<Object, StateTreeNode> values;
		public StateTreeNode fallback;

		public StateBranch(PropertyData<?> property, StateTreeNode fallback) {
			this.property = property;
			this.values = new LinkedHashMap<>();
			this.fallback = fallback;
		}

	}

	public static final class StateEntry implements StateTreeNode, IWorkspaceDependent {

		public int listIndex = -1;

		public int renderType;
		@TextureReference(TextureType.ITEM) public TextureHolder texture;
		public String customModelName;

		public StateMap stateMap;

		@Nullable transient Workspace workspace;

		@Override public void setWorkspace(@Nullable Workspace workspace) {
			this.workspace = workspace;
		}

		@Nullable @Override public Workspace getWorkspace() {
			return workspace;
		}

		public String indexString() {
			return listIndex >= 0 ? "_" + listIndex : "";
		}

		public Model getItemModel() {
			return Model.getModelByParams(workspace, customModelName, decodeModelType(renderType));
		}

		public Map<String, TextureHolder> getTextureMap() {
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

		@Override public boolean equals(Object o) {
			return this == o || o instanceof StateEntry that && Objects.equals(stateMap, that.stateMap);
		}

		@Override public int hashCode() {
			return Objects.hashCode(stateMap);
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
