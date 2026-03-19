/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.BlocklyXML;
import net.mcreator.blockly.java.BlocklyToJava;
import net.mcreator.element.BaseType;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.interfaces.ICommonType;
import net.mcreator.element.types.interfaces.IEntityWithModel;
import net.mcreator.element.types.interfaces.IMCItemProvider;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.modgui.bedrock.BEEntityGUI;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.resources.Model;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class BEEntity extends GeneratableElement implements IEntityWithModel, ICommonType, IMCItemProvider {

	public String entityName;
	public String modelName;
	public String modelTexture;
	public double collisionBoxWidth;
	public double collisionBoxHeight;

	public List<String> typeFamily;
	public boolean isSummonable;
	public int xpAmountOnDeath;
	public MItemBlock entityDrop;
	public int healthValue;
	public int attackDamage;
	public double speedValue;
	public boolean canFly;
	public double flyingSpeedValue;
	public int followRangeValue;
	public boolean isImmuneToFire;
	public boolean isPushable;
	public boolean isPushableByPiston;

	public boolean generateEntity;
	public String populationControl;
	public int spawningProbability;
	public int minHerdSize;
	public int maxHerdSize;

	public boolean hasSpawnEgg;
	public Color spawnEggBaseColor;
	public Color spawnEggDotColor;

	@BlocklyXML("aitasks") public String aixml;

	public boolean waterEntity;
	public boolean isImmuneToDrowning;
	public boolean isImmuneToFallDamage;

	public BEEntity(ModElement element) {
		super(element);
		typeFamily = new ArrayList<>();
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateMobPreviewPicture(new ImageIcon(
				getModElement().getWorkspace().getFolderManager()
						.getTextureFile(FilenameUtilsPatched.removeExtension(modelTexture), TextureType.ENTITY)
						.getAbsolutePath()).getImage(), spawnEggBaseColor, spawnEggDotColor, hasSpawnEgg);
	}

	@Override @Nullable public Model getEntityModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (Arrays.stream(BEEntityGUI.builtinmobmodels).map(Model::getReadableName).noneMatch(modelName::equals))
			modelType = Model.Type.BEDROCK;
		return Model.getModelByParams(getModElement().getWorkspace(), modelName, modelType);
	}

	@Override public Collection<BaseType> getBaseTypesProvided() {
		if (hasSpawnEgg)
			return List.of(BaseType.ITEM, BaseType.ENTITY);
		else
			return List.of(BaseType.ENTITY);
	}

	public boolean hasDrop() {
		return !entityDrop.isEmpty();
	}

	public boolean hasType(String type) {
		return typeFamily.contains(type);
	}

	@Override public @Nullable IAdditionalTemplateDataProvider getAdditionalTemplateData() {
		return additionalData -> {
			BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(
					BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.AI_TASK).getDefinedBlocks(),
					getModElement().getGenerator().getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.AI_TASK),
					this.getModElement().getGenerator()
							.getTemplateGeneratorFromName(BlocklyEditorType.AI_TASK.registryName()),
					additionalData).setTemplateExtension(
					this.getModElement().getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage().name()
							.toLowerCase(Locale.ENGLISH));
			BlocklyToJava blocklyToJava = new BlocklyToJava(this.getModElement().getWorkspace(), this.getModElement(),
					BlocklyEditorType.AI_TASK, this.aixml, this.getModElement().getGenerator()
					.getTemplateGeneratorFromName(BlocklyEditorType.AI_TASK.registryName()),
					new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator));

			additionalData.put("aicode", blocklyToJava.getGeneratedCode());
			additionalData.put("aiblocks", blocklyToJava.getUsedBlocks());
			additionalData.put("extra_templates_code", blocklyToJava.getExtraTemplatesCode());
		};
	}

	@Override public List<MCItem> providedMCItems() {
		if (hasSpawnEgg)
			return List.of(new MCItem.Custom(this.getModElement(), "spawn_egg", "item", "Spawn egg"));
		return Collections.emptyList();
	}
}
