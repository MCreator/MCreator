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

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.BlocklyXML;
import net.mcreator.blockly.java.BlocklyToJava;
import net.mcreator.element.BaseType;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.*;
import net.mcreator.element.parts.procedure.LogicProcedure;
import net.mcreator.element.parts.procedure.NumberProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.interfaces.*;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.IAdditionalTemplateDataProvider;
import net.mcreator.io.FileIO;
import net.mcreator.io.ResourcePointer;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;
import net.mcreator.ui.modgui.LivingEntityGUI;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.ResourceReference;
import net.mcreator.workspace.references.TextureReference;
import net.mcreator.workspace.resources.Model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.module.ModuleDescriptor;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({ "unused", "NotNullFieldNotInitialized" }) public class LivingEntity extends GeneratableElement
		implements IEntityWithModel, ITabContainedElement, ICommonType, IMCItemProvider {

	private static final String XML_BASE = """
			<xml xmlns="https://developers.google.com/blockly/xml">
			<block type="aitasks_container" deletable="false" x="40" y="40"><next>
			<block type="attack_on_collide"><field name="speed">1.2</field><field name="longmemory">FALSE</field><field name="condition">null,null</field><next>
			<block type="wander"><field name="speed">1</field><field name="condition">null,null</field><next>
			<block type="attack_action"><field name="callhelp">FALSE</field><field name="condition">null,null</field><next>
			<block type="look_around"><field name="condition">null,null</field><next>
			<block type="swim_in_water"><field name="condition">null,null</field></block></next>
			</block></next></block></next></block></next></block></next></block></xml>""";

	public String mobName;
	public String mobLabel;

	@Nonnull public String mobModelName;
	@TextureReference(TextureType.ENTITY) public String mobModelTexture;
	public LogicProcedure transparentModelCondition;
	public LogicProcedure isShakingCondition;
	public LogicProcedure solidBoundingBox;
	public NumberProcedure visualScale;
	public NumberProcedure boundingBoxScale;

	@ModElementReference @TextureReference(TextureType.ENTITY) @ResourceReference("model")
	public List<ModelLayerEntry> modelLayers;

	@ModElementReference @ResourceReference("animation") public List<AnimationEntry> animations;

	@Numeric(init = 0.6, min = 0, max = 1024, step = 0.1) public double modelWidth;
	@Numeric(init = 1.8, min = 0, max = 1024, step = 0.1) public double modelHeight;
	@Numeric(init = 0.5, min = 0, max = 20, step = 0.1) public double modelShadowSize;
	@Numeric(init = 0, min = -1024, max = 1024, step = 0.1) public double mountedYOffset;

	public boolean hasSpawnEgg;
	public Color spawnEggBaseColor;
	public Color spawnEggDotColor;
	@TextureReference(TextureType.ITEM) public TextureHolder spawnEggTexture;
	@ModElementReference public List<TabEntry> creativeTabs;

	public boolean isBoss;
	public String bossBarColor;
	public String bossBarType;

	public MItemBlock equipmentMainHand;
	public MItemBlock equipmentOffHand;
	public MItemBlock equipmentHelmet;
	public MItemBlock equipmentBody;
	public MItemBlock equipmentLeggings;
	public MItemBlock equipmentBoots;

	public String mobBehaviourType;
	public String mobCreatureType;
	@Numeric(init = 3, min = 0, max = 10000, step = 1) public int attackStrength;
	@Numeric(init = 0, min = 0, max = 1000, step = 0.1) public double attackKnockback;
	@Numeric(init = 0, min = 0, max = 1000, step = 0.1) public double knockbackResistance;
	@Numeric(init = 0.3, min = 0, max = 50, step = 0.1) public double movementSpeed;
	@Numeric(init = 0.6, min = 0, max = 255, step = 0.1) public double stepHeight;
	@Numeric(init = 0, min = 0, max = 100, step = 0.1) public double armorBaseValue;
	@Numeric(init = 64, min = 0, max = 10000, step = 1) public int trackingRange;
	@Numeric(init = 16, min = 0, max = 2048, step = 1) public int followRange;
	@Numeric(init = 10, min = 0, max = 1024, step = 1) public int health;
	@Numeric(init = 0, min = 0, max = 100000, step = 1) public int xpAmount;
	public boolean waterMob;
	public LogicProcedure breatheUnderwater;
	public LogicProcedure pushedByFluids;
	public boolean flyingMob;

	@ModElementReference(acceptedTypes = { GUI.class }) @Nullable public String guiBoundTo;
	@Numeric(init = 9, min = 0, max = 256, step = 1) public int inventorySize;
	@Numeric(init = 99, min = 1, max = 1024, step = 1) public int inventoryStackSize;

	public boolean disableCollisions;

	public boolean ridable;
	public boolean canControlForward;
	public boolean canControlStrafe;

	public boolean immuneToFire;
	public boolean immuneToArrows;
	public boolean immuneToFallDamage;
	public boolean immuneToCactus;
	public boolean immuneToDrowning;
	public boolean immuneToLightning;
	public boolean immuneToPotions;
	public boolean immuneToPlayer;
	public boolean immuneToExplosion;
	public boolean immuneToTrident;
	public boolean immuneToAnvil;
	public boolean immuneToWither;
	public boolean immuneToDragonBreath;

	public MItemBlock mobDrop;

	public Sound livingSound;
	public Sound hurtSound;
	public Sound deathSound;
	public Sound stepSound;
	public Sound raidCelebrationSound;

	public List<PropertyDataWithValue<?>> entityDataEntries;

	public Procedure onStruckByLightning;
	public Procedure whenMobFalls;
	public Procedure whenMobDies;
	public Procedure whenMobIsHurt;
	public Procedure onRightClickedOn;
	public Procedure whenThisMobKillsAnother;
	public Procedure onMobTickUpdate;
	public Procedure onPlayerCollidesWith;
	public Procedure onInitialSpawn;

	public boolean hasAI;
	public String aiBase;
	@BlocklyXML(name = "aitasks", defaultXML = LivingEntity.XML_BASE) public String aixml;

	public boolean breedable;
	public boolean tameable;
	@ModElementReference public List<MItemBlock> breedTriggerItems;

	public boolean ranged;
	public MItemBlock rangedAttackItem;
	@ModElementReference(defaultValues = "Default item", acceptedTypes = Projectile.class) public String rangedItemType;
	@Numeric(init = 20, min = 0, max = 1024, step = 1) public int rangedAttackInterval;
	@Numeric(init = 10, min = 0, max = 1024, step = 0.1) public double rangedAttackRadius;

	public boolean spawnThisMob;
	public boolean doesDespawnWhenIdle;
	public Procedure spawningCondition;
	@Numeric(init = 20, min = 1, max = 1000, step = 1) public int spawningProbability;
	public String mobSpawningType;
	@Numeric(init = 4, min = 1, max = 1000, step = 1, allowMinMaxEqual = true) public int minNumberOfMobsPerGroup;
	@Numeric(init = 4, min = 1, max = 1000, step = 1, allowMinMaxEqual = true) public int maxNumberOfMobsPerGroup;
	@ModElementReference public List<BiomeEntry> restrictionBiomes;
	public boolean spawnInDungeons;
	public int[] raidSpawnsCount;

	public boolean sensitiveToVibration;
	public List<GameEventEntry> vibrationalEvents;
	public NumberProcedure vibrationSensitivityRadius;
	public Procedure canReceiveVibrationCondition;
	public Procedure onReceivedVibration;

	private LivingEntity() {
		this(null);
	}

	public LivingEntity(ModElement element) {
		super(element);

		this.creativeTabs = new ArrayList<>();

		this.modelShadowSize = 0.5;
		this.mobCreatureType = "UNDEFINED";
		this.trackingRange = 64;
		this.rangedItemType = "Default item";
		this.rangedAttackInterval = 20;
		this.rangedAttackRadius = 10;
		this.stepHeight = 0.6;
		this.followRange = 16;

		this.inventorySize = 9;
		this.inventoryStackSize = 99;

		this.entityDataEntries = new ArrayList<>();
		this.modelLayers = new ArrayList<>();

		this.raidSpawnsCount = new int[] { 4, 3, 3, 4, 4, 4, 2 };

		this.animations = new ArrayList<>();

		this.vibrationalEvents = new ArrayList<>();
	}

	@Override @Nullable public Model getEntityModel() {
		Model.Type modelType = Model.Type.BUILTIN;
		if (Arrays.stream(LivingEntityGUI.builtinmobmodels).map(Model::getReadableName).noneMatch(mobModelName::equals)
				&& !mobModelName.equals("Zombie")) // legacy check as zombie was supported in the past
			modelType = Model.Type.JAVA;
		return Model.getModelByParams(getModElement().getWorkspace(), mobModelName, modelType);
	}

	@Override public Collection<BaseType> getBaseTypesProvided() {
		if (hasSpawnEgg)
			return List.of(BaseType.ITEM, BaseType.ENTITY);
		else
			return List.of(BaseType.ENTITY);
	}

	@Override public List<TabEntry> getCreativeTabs() {
		return creativeTabs;
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateMobPreviewPicture(new ImageIcon(
				getModElement().getWorkspace().getFolderManager()
						.getTextureFile(FilenameUtilsPatched.removeExtension(mobModelTexture), TextureType.ENTITY)
						.getAbsolutePath()).getImage(), spawnEggBaseColor, spawnEggDotColor, hasSpawnEgg);
	}

	public boolean hasDrop() {
		return !mobDrop.isEmpty();
	}

	public boolean hasCustomProjectile() {
		return ranged && "Default item".equals(rangedItemType) && !rangedAttackItem.isEmpty();
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

			List<?> unmodifiableAIBases = (List<?>) getModElement().getWorkspace().getGenerator()
					.getGeneratorConfiguration().getDefinitionsProvider()
					.getModElementDefinition(ModElementType.LIVINGENTITY).get("unmodifiable_ai_bases");
			additionalData.put("aicode", unmodifiableAIBases != null && !unmodifiableAIBases.contains(aiBase) ?
					blocklyToJava.getGeneratedCode() :
					"");
			additionalData.put("aiblocks", blocklyToJava.getUsedBlocks());
			additionalData.put("extra_templates_code", blocklyToJava.getExtraTemplatesCode());
		};
	}

	@Override public List<MCItem> providedMCItems() {
		if (hasSpawnEgg)
			return List.of(new MCItem.Custom(this.getModElement(), "spawn_egg", "item", "Spawn egg"));
		return Collections.emptyList();
	}

	@Override public List<MCItem> getCreativeTabItems() {
		return providedMCItems();
	}

	@Override public ImageIcon getIconForMCItem(Workspace workspace, String suffix) {
		if ("spawn_egg".equals(suffix)) {
			return MinecraftImageGenerator.generateSpawnEggIcon(spawnEggBaseColor, spawnEggDotColor);
		}

		return null;
	}

	public Set<String> getVibrationalEvents() {
		return vibrationalEvents.stream().map(e -> e.getMappedValue(1)).collect(Collectors.toSet());
	}

	public static class ModelLayerEntry implements IWorkspaceDependent {

		public String model;
		@TextureReference(TextureType.ENTITY) public String texture;
		public boolean disableHurtOverlay;
		public boolean glow;
		public Procedure condition;

		@Nullable transient Workspace workspace;

		// This method is primarily here for the usages system to detect the model usage
		public Model getLayerModel() {
			return model.equals("Default") ? null : Model.getModelByParams(workspace, model, Model.Type.JAVA);
		}

		@Override public void setWorkspace(@Nullable Workspace workspace) {
			this.workspace = workspace;
		}

		@Override public @Nullable Workspace getWorkspace() {
			return workspace;
		}
	}

	public static class AnimationEntry {

		public Animation animation;
		public double speed;

		public Procedure condition;

		// Walking animation only
		public boolean walking;
		public double amplitude;

	}

	@Override public void finalizeModElementGeneration() {
		// if spawn egg is enabled and texture is not specified and the Minecraft version is 1.21.5 Java Edition or higher,
		// where one can't use built-in spawn egg generation, a texture needs to be generated manually as fallback
		if (hasSpawnEgg && (spawnEggTexture == null || spawnEggTexture.isEmpty()) && ModuleDescriptor.Version.parse(
						getModElement().getGeneratorConfiguration().getGeneratorMinecraftVersion())
				.compareTo(ModuleDescriptor.Version.parse("1.21.5")) >= 0 && (
				getModElement().getGeneratorConfiguration().getGeneratorFlavor().getGamePlatform()
						== GeneratorFlavor.GamePlatform.JAVAEDITION)) {
			File spawnEggTextureFile = getModElement().getFolderManager()
					.getTextureFile(getModElement().getRegistryName() + "_spawn_egg_generated", TextureType.ITEM);
			ImageIcon spawnEgg = ImageUtils.drawOver(ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(
							new ResourcePointer("templates/textures/texturemaker/egg_base.png")), spawnEggBaseColor, true),
					ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(
									new ResourcePointer("templates/textures/texturemaker/egg_accent.png")), spawnEggDotColor,
							true));
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(spawnEgg.getImage()), spawnEggTextureFile);
		}
	}

}
