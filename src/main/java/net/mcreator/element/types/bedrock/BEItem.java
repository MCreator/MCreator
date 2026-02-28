/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.element.types.bedrock;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.EntityEntry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.types.interfaces.IItem;
import net.mcreator.element.types.interfaces.IItemWithTexture;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BEItem extends GeneratableElement implements IItem, IItemWithTexture {

	@TextureReference(TextureType.ITEM) public TextureHolder texture;

	public String name;
	public int stackSize;
	public double useDuration;
	public int maxDurability;
	public boolean enableMeleeDamage;
	public double damageVsEntity;
	public boolean hasGlint;
	public boolean handEquipped;
	public String rarity;
	public boolean enableCreativeTab;
	public String creativeTab;
	public boolean isHiddenInCommands;
	public double movementModifier;
	public boolean allowOffHand;
	public double fuelDuration;
	public boolean shouldDespawn;
	public MItemBlock blockToPlace;
	@ModElementReference public List<MItemBlock> blockPlaceableOn;
	public EntityEntry entityToPlace;
	@ModElementReference public List<MItemBlock> entityPlaceableOn;
	@ModElementReference public List<MItemBlock> entityDispensableOn;

	// Food
	public boolean isFood;
	public int foodNutritionalValue;
	public double foodSaturation;
	public boolean foodCanAlwaysEat;
	@ModElementReference public MItemBlock usingConvertsTo;
	public String animation;

	@ModElementReference public List<String> localScripts;

	public BEItem() {
		this(null);
	}

	public BEItem(ModElement element) {
		super(element);

		rarity = "COMMON";
		movementModifier = 1.0;
		shouldDespawn = true;
		animation = "eat";
		enableCreativeTab = true;
		creativeTab = "MATERIALS";

		blockPlaceableOn = new ArrayList<>();
		entityDispensableOn = new ArrayList<>();
		entityPlaceableOn = new ArrayList<>();

		localScripts = new ArrayList<>();
	}

	@Override public BufferedImage generateModElementPicture() {
		return ImageUtils.resizeAndCrop(texture.getImage(TextureType.ITEM), 32);
	}

	@Override public TextureHolder getTexture() {
		return texture;
	}

	@Override public List<MCItem> providedMCItems() {
		return List.of(new MCItem.Custom(this.getModElement(), null, "item"));
	}

}
