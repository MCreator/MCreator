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
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.StepSound;
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.types.interfaces.IBlock;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BEBlock extends GeneratableElement implements IBlock {

	@TextureReference(TextureType.BLOCK) public TextureHolder texture;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureTop;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureLeft;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureFront;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureRight;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureBack;

	public String name;
	public MItemBlock loot;
	public int dropAmount;
	public double friction;
	public StepSound soundOnStep;
	public double hardness;
	public double resistance;
	public int lightEmission;
	public int flammability;
	public int flammableDestroyChance;
	public String colorOnMap;

	public boolean generateFeature;
	public int frequencyPerChunks;
	public int oreCount;
	public int minGenerateHeight;
	public int maxGenerateHeight;
	@ModElementReference public List<MItemBlock> blocksToReplace;

	private BEBlock() {
		this(null);
	}

	public BEBlock(ModElement element) {
		super(element);
	}

	public boolean hasCustomDrop() {
		return !loot.isEmpty();
	}

	@Override public BufferedImage generateModElementPicture() {
		return ImageUtils.resizeAndCrop(getMainTexture(), 32);
	}

	@Override public String getRenderType() {
		return "SOLID";
	}

	@Override public List<MCItem> providedMCItems() {
		return List.of(new MCItem.Custom(this.getModElement(), null, "block" ));
	}

	private Image getMainTexture() {
		return texture.getImage(TextureType.BLOCK);
	}
}
