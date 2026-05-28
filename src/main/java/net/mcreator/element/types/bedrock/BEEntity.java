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
import net.mcreator.element.parts.MobSpawnType;
import net.mcreator.element.types.LivingEntity;
import net.mcreator.element.types.interfaces.*;
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

	private static final String XML_BASE = """
			<xml xmlns="https://developers.google.com/blockly/xml">
			<block type="aitasks_container" deletable="false" x="40" y="40"><next>
			<block type="attack_on_collide"><field name="speed">1.2</field><field name="longmemory">FALSE</field><field name="condition">null,null</field><next>
			<block type="wander"><field name="speed">1</field><field name="condition">null,null</field><next>
			<block type="attack_action"><field name="callhelp">FALSE</field><field name="condition">null,null</field><next>
			<block type="look_around"><field name="condition">null,null</field><next>
			<block type="swim_in_water"><field name="condition">null,null</field></block></next>
			</block></next></block></next></block></next></block></next></block></xml>""";

	public String entityName;
	public String modelName;
	public String modelTexture;
	@Numeric(init=0.6, min=0, max=16, step=0.1) public double collisionBoxWidth;
	@Numeric(init=1.9, min=0, max=16, step=0.1) public double collisionBoxHeight;

	public boolean isSummonable;
	@Numeric(init=0, min=0, max=100000, step=1) public int xpAmountOnDeath;
	public MItemBlock entityDrop;
	@Numeric(init=20, min=0, max=1024, step=1) public int healthValue;
	@Numeric(init=3, min=-10000, max=10000, step=1) public int attackDamage;
	@Numeric(init=0.3, min=0, max=50, step=0.1) public double speedValue;
	public boolean canFly;
	@Numeric(init=0.3, min=0, max=50, step=0.1) public double flyingSpeedValue;
	@Numeric(init=64, min=0, max=10000, step=1) public int followRangeValue;
	public boolean isImmuneToFire;
	public boolean isPushable;
	public boolean isPushableByPiston;

	public boolean spawnNaturally;
	public MobSpawnType populationControl;
	@Numeric(init=20, min=1, max=1000, step=1) public int spawningProbability;
	@Numeric(init=4, min=1, max=128, step=1, allowMinMaxEqual = true) public int minHerdSize;
	@Numeric(init=4, min=1, max=128, step=1, allowMinMaxEqual = true) public int maxHerdSize;

	public boolean hasSpawnEgg;
	public Color spawnEggBaseColor;
	public Color spawnEggDotColor;

	@BlocklyXML(name = "aitasks", defaultXML = XML_BASE) public String aixml;

	@LimitedOptions({ "Mob", "Creature" }) public String entityBehaviourType;
	public boolean waterEntity;
	public boolean isImmuneToDrowning;
	public boolean isImmuneToFallDamage;

	public BEEntity(ModElement element) {
		super(element);
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
