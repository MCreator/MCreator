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
import net.mcreator.element.IItemWithTexture;
import net.mcreator.element.ITabContainedElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.Procedure;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.resources.TexturedModel;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") public class Tool extends GeneratableElement
		implements IItemWithModel, ITabContainedElement, IItemWithTexture {

	public int renderType;
	public String texture;
	public String customModelName;

	public String name;
	public List<String> specialInfo;
	public boolean onShiftOnly;
	public List<String> onShiftInfo;
	public boolean onCommandOnly;
	public List<String> onCommandInfo;
	public TabEntry creativeTab;
	public String toolType;
	public int harvestLevel;
	public double efficiency;
	public double attackSpeed;
	public int enchantability;
	public double damageVsEntity;
	public int usageCount;
	public List<MItemBlock> blocksAffected;
	public boolean hasGlow;
	public Procedure glowCondition;
	public List<MItemBlock> repairItems;

	public boolean stayInGridWhenCrafting;
	public boolean damageOnCrafting;

	public Procedure onRightClickedInAir;
	public Procedure onRightClickedOnBlock;
	public Procedure onCrafted;
	public Procedure onEntityHitWith;
	public Procedure onItemInInventoryTick;
	public Procedure onItemInUseTick;
	public Procedure onStoppedUsing;
	public Procedure onBlockDestroyedWithTool;
	public Procedure onEntitySwing;

	private Tool() {
		this(null);
	}

	public Tool(ModElement element) {
		super(element);

		this.attackSpeed = 2.8;

		this.specialInfo = new ArrayList<>();
	}

	@Override public BufferedImage generateModElementPicture() {
		return ImageUtils
				.resizeAndCrop(getModElement().getWorkspace().getFolderManager().getItemImageIcon(texture).getImage(),
						32);
	}

	@Override public Model getItemModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (renderType == 1)
			modelType = Model.Type.JSON;
		else if (renderType == 2)
			modelType = Model.Type.OBJ;
		return Model.getModelByParams(getModElement().getWorkspace(), customModelName, modelType);
	}

	@Override public Map<String, String> getTextureMap() {
		Model model = getItemModel();
		if (model instanceof TexturedModel && ((TexturedModel) model).getTextureMapping() != null)
			return ((TexturedModel) model).getTextureMapping().getTextureMap();
		return null;
	}

	public boolean toolInfoOnly() {
		return !specialInfo.isEmpty();
	}

	public boolean toolShiftOnly() {
		return onShiftOnly;
	}

	public boolean toolCommandOnly() {
		return onCommandOnly;
	}

	@Override public TabEntry getCreativeTab() {
		return creativeTab;
	}

	@Override public String getTexture() {
		return texture;
	}

}
