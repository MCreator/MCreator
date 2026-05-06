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
import net.mcreator.element.types.interfaces.LimitedOptions;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;
import net.mcreator.workspace.resources.Model;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BEBlock extends GeneratableElement implements IBlock {

	@TextureReference(TextureType.BLOCK) public TextureHolder texture;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureTop;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureLeft;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureFront;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureRight;
	@TextureReference(TextureType.BLOCK) public TextureHolder textureBack;

	public int renderType;
	@Nonnull public String customModelName;

	public String name;
	public boolean enableCreativeTab;
	public String creativeTab;
	public boolean isHiddenInCommands;
	public MItemBlock customDrop;
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
	@LimitedOptions({ "uniform", "triangle" }) public String generationShape;
	public int frequencyPerChunks;
	public int oreCount;
	public int minGenerateHeight;
	public int maxGenerateHeight;
	@ModElementReference public List<MItemBlock> blocksToReplace;

	public int rotationMode;
	@LimitedOptions({ "opaque", "double_sided", "blend", "alpha_test_single_sided", "alpha_test",
			"alpha_test_to_opaque", "alpha_test_single_sided_to_opaque", "blend_to_opaque" })
	public String renderMethod;
	@LimitedOptions({ "(none)", "birch_foliage", "default_foliage", "dry_foliage", "evergreen_foliage", "grass",
			"water" }) public String tintMethod;

	@ModElementReference(acceptedTypes = { "bescript" }) public List<String> localScripts;

	private BEBlock() {
		this(null);
	}

	public BEBlock(ModElement element) {
		super(element);

		customModelName = "Normal";
		renderType = 10;

		enableCreativeTab = true;
		creativeTab = "BUILDING_BLOCKS";

		renderMethod = "opaque";
		tintMethod = "(none)";

		generationShape = "uniform";

		localScripts = new ArrayList<>();
	}

	public int renderType() {
		return renderType;
	}

	public boolean hasCustomDrop() {
		return !customDrop.isEmpty();
	}

	@Override public BufferedImage generateModElementPicture() {
		if (renderType == 10) {
			return (BufferedImage) MinecraftImageGenerator.Preview.generateBlockIcon(getTextureWithFallback(textureTop),
					getTextureWithFallback(textureLeft), getTextureWithFallback(textureFront));
		} else if (renderType == 12) {
			return (BufferedImage) MinecraftImageGenerator.Preview.generateBlockIcon(getMainTexture(), getMainTexture(),
					getMainTexture());
		} else {
			return ImageUtils.resizeAndCrop(getMainTexture(), 32);
		}
	}

	private Image getTextureWithFallback(TextureHolder texture) {
		if (texture.isEmpty())
			return getMainTexture();
		return texture.getImage(TextureType.BLOCK);
	}

	@Override public String getRenderType() {
		return "SOLID";
	}

	@Override public List<MCItem> providedMCItems() {
		return List.of(new MCItem.Custom(this.getModElement(), null, "block"));
	}

	public Model getModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (renderType == 2)
			modelType = Model.Type.BEDROCK;
		return Model.getModelByParams(getModElement().getWorkspace(), customModelName, modelType);
	}

	public boolean hasCustomModel() {
		return renderType == 2;
	}

	public boolean hasOneTexture() {
		return hasCustomModel() || renderType == 11 || renderType == 12;
	}

	private Image getMainTexture() {
		return texture.getImage(TextureType.BLOCK);
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
}
