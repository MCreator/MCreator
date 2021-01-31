<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2020 Pylo and contributors
 # 
 # This program is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 3 of the License, or
 # (at your option) any later version.
 # 
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 # GNU General Public License for more details.
 # 
 # You should have received a copy of the GNU General Public License
 # along with this program.  If not, see <https://www.gnu.org/licenses/>.
 # 
 # Additional permission for code generator templates (*.ftl files)
 # 
 # As a special exception, you may create a larger work that contains part or 
 # all of the MCreator code generator templates (*.ftl files) and distribute 
 # that work under terms of your choice, so long as that work isn't itself a 
 # template for code generation. Alternatively, if you modify or redistribute 
 # the template itself, you may (at your option) remove this special exception, 
 # which will cause the template and the resulting code generator output files 
 # to be licensed under the GNU General Public License without this special 
 # exception.
-->

<#-- @formatter:off -->
<#include "mcitems.ftl">
<#include "procedures.java.ftl">
<#include "particles.java.ftl">

package ${package}.entity;

import net.minecraft.block.material.Material;

@${JavaModName}Elements.ModElement.Tag public class ${name}Entity extends ${JavaModName}Elements.ModElement {

	public static EntityType entity = null;

	<#if data.ranged && data.rangedItemType == "Default item">
	@ObjectHolder("${modid}:entitybullet${registryname}")
	public static final EntityType arrow = null;
	</#if>

	public ${name}Entity (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});

		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@Override public void initElements() {
		entity = (EntityType.Builder.<CustomEntity>create(CustomEntity::new, ${generator.map(data.mobSpawningType, "mobspawntypes")})
					.setShouldReceiveVelocityUpdates(true).setTrackingRange(${data.trackingRange}).setUpdateInterval(3).setCustomClientFactory(CustomEntity::new)
					<#if data.immuneToFire>.immuneToFire()</#if>.size(${data.modelWidth}f, ${data.modelHeight}f))
					.build("${registryname}").setRegistryName("${registryname}");

		elements.entities.add(() -> entity);

		<#if data.hasSpawnEgg>
		elements.items.add(() -> new SpawnEggItem(entity, ${data.spawnEggBaseColor.getRGB()}, ${data.spawnEggDotColor.getRGB()},
				new Item.Properties()<#if data.creativeTab??>.group(${data.creativeTab})<#else>.group(ItemGroup.MISC)</#if>)
				.setRegistryName("${registryname}_spawn_egg"));
		</#if>

		<#if data.ranged && data.rangedItemType == "Default item">
		elements.entities.add(() -> (EntityType.Builder.<ArrowCustomEntity>create(ArrowCustomEntity::new, EntityClassification.MISC)
					.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1)
					.setCustomClientFactory(ArrowCustomEntity::new).size(0.5f, 0.5f)).build("entitybullet${registryname}").setRegistryName("entitybullet${registryname}"));
		</#if>
	}

	<#if data.spawnThisMob>
	@Override public void init(FMLCommonSetupEvent event) {
		for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
			<#if data.restrictionBiomes?has_content>
				boolean biomeCriteria = false;
				<#list data.restrictionBiomes as restrictionBiome>
					<#if restrictionBiome.canProperlyMap()>
					if (ForgeRegistries.BIOMES.getKey(biome).equals(new ResourceLocation("${restrictionBiome}")))
						biomeCriteria = true;
					</#if>
				</#list>
				if (!biomeCriteria)
					continue;
			</#if>

			biome.getSpawns(${generator.map(data.mobSpawningType, "mobspawntypes")}).add(new Biome.SpawnListEntry(entity, ${data.spawningProbability},
							${data.minNumberOfMobsPerGroup}, ${data.maxNumberOfMobsPerGroup}));
		}

		<#if data.mobSpawningType == "creature">
		EntitySpawnPlacementRegistry.register(entity, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
				<#if hasCondition(data.spawningCondition)>
				(entityType, world, reason, pos, random) -> {
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();
					return <@procedureOBJToConditionCode data.spawningCondition/>;
				}
				<#else>
				(entityType, world, reason, pos, random) -> (world.getBlockState(pos.down()).getMaterial() == Material.ORGANIC && world.getLightSubtracted(pos, 0) > 8)
				</#if>
		);
		<#elseif data.mobSpawningType == "ambient">
		EntitySpawnPlacementRegistry.register(entity, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
				<#if hasCondition(data.spawningCondition)>
				(entityType, world, reason, pos, random) -> {
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();
					return <@procedureOBJToConditionCode data.spawningCondition/>;
				}
				<#else>
				MobEntity::canSpawnOn
				</#if>
		);
		<#elseif data.mobSpawningType == "waterCreature">
		EntitySpawnPlacementRegistry.register(entity, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
				<#if hasCondition(data.spawningCondition)>
				(entityType, world, reason, pos, random) -> {
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();
					return <@procedureOBJToConditionCode data.spawningCondition/>;
				}
				<#else>
				SquidEntity::func_223365_b
				</#if>
		);
		<#else>
		EntitySpawnPlacementRegistry.register(entity, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
				<#if hasCondition(data.spawningCondition)>
				(entityType, world, reason, pos, random) -> {
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();
					return <@procedureOBJToConditionCode data.spawningCondition/>;
				}
				<#else>
				MonsterEntity::canMonsterSpawn
				</#if>
		);
		</#if>

		<#if data.spawnInDungeons>
		DungeonHooks.addDungeonMob(entity, 180);
		</#if>
	}
	</#if>

	@SubscribeEvent @OnlyIn(Dist.CLIENT) public void registerModels(ModelRegistryEvent event) {
		<#if data.mobModelName == "Chicken">
			RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> new MobRenderer(renderManager, new ChickenModel(), ${data.modelShadowSize}f) {
					<#if data.mobModelGlowTexture?has_content>{ this.addLayer(new GlowingLayer<>(this)); }</#if>
					@Override public ResourceLocation getEntityTexture(Entity entity) { 
				        <@entityTexture/>
					}
				});
		<#elseif data.mobModelName == "Cow">
			RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> new MobRenderer(renderManager, new CowModel(), ${data.modelShadowSize}f) {
					<#if data.mobModelGlowTexture?has_content>{ this.addLayer(new GlowingLayer<>(this)); }</#if>
					@Override public ResourceLocation getEntityTexture(Entity entity) { 
				        <@entityTexture/>
					}
				});
		<#elseif data.mobModelName == "Creeper">
			RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> new MobRenderer(renderManager, new CreeperModel(), ${data.modelShadowSize}f) {
					<#if data.mobModelGlowTexture?has_content>{ this.addLayer(new GlowingLayer<>(this)); }</#if>
					@Override public ResourceLocation getEntityTexture(Entity entity) { 
				        <@entityTexture/>
					}
				});
		<#elseif data.mobModelName == "Ghast">
			RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> new MobRenderer(renderManager, new GhastModel(), ${data.modelShadowSize}f) {
					<#if data.mobModelGlowTexture?has_content>{ this.addLayer(new GlowingLayer<>(this)); }</#if>
					@Override public ResourceLocation getEntityTexture(Entity entity) {
				        <@entityTexture/>
					}
				});
		<#elseif data.mobModelName == "Pig">
			RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> new MobRenderer(renderManager, new PigModel(), ${data.modelShadowSize}f) {
					<#if data.mobModelGlowTexture?has_content>{ this.addLayer(new GlowingLayer<>(this)); }</#if>
					@Override public ResourceLocation getEntityTexture(Entity entity) { 
				        <@entityTexture/>
					}
				});
		<#elseif data.mobModelName == "Slime">
			RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> new MobRenderer(renderManager, new SlimeModel(0), ${data.modelShadowSize}f) {
					<#if data.mobModelGlowTexture?has_content>{ this.addLayer(new GlowingLayer<>(this)); }</#if>
					@Override public ResourceLocation getEntityTexture(Entity entity) { 
				        <@entityTexture/>
					}
				});
		<#elseif data.mobModelName == "Spider">
			RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> new MobRenderer(renderManager, new SpiderModel(), ${data.modelShadowSize}f) {
					<#if data.mobModelGlowTexture?has_content>{ this.addLayer(new GlowingLayer<>(this)); }</#if>
					@Override public ResourceLocation getEntityTexture(Entity entity) { 
				        <@entityTexture/>
					}
				});
		<#elseif data.mobModelName == "Villager">
			RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> new MobRenderer(renderManager, new VillagerModel(0), ${data.modelShadowSize}f) {
				<#if data.mobModelGlowTexture?has_content>{ this.addLayer(new GlowingLayer<>(this)); }</#if>
				@Override public ResourceLocation getEntityTexture(Entity entity) { 
				        <@entityTexture/>
				});
		<#elseif data.mobModelName == "Silverfish">
			RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> new MobRenderer(renderManager, new SilverfishModel(), ${data.modelShadowSize}f) {
				<#if data.mobModelGlowTexture?has_content>{ this.addLayer(new GlowingLayer<>(this)); }</#if>
				@Override public ResourceLocation getEntityTexture(Entity entity) { 
				        <@entityTexture/>
					}
				});
		<#elseif !data.isBuiltInModel()>
			RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> {
				return new MobRenderer(renderManager, new ${data.mobModelName}(), ${data.modelShadowSize}f) {
					<#if data.mobModelGlowTexture?has_content>{ this.addLayer(new GlowingLayer<>(this)); }</#if>
					@Override public ResourceLocation getEntityTexture(Entity entity) { 
				        <@entityTexture/>
					}
				});
		<#else>
			RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> {
				BipedRenderer customRender = new BipedRenderer(renderManager, new BipedModel(0), ${data.modelShadowSize}f) {
					@Override public ResourceLocation getEntityTexture(Entity entity) { 
				        <@entityTexture/>
					}
				};
				customRender.addLayer(new BipedArmorLayer(customRender, new BipedModel(0.5f), new BipedModel(1)));
				<#if data.mobModelGlowTexture?has_content>customRender.addLayer(new GlowingLayer<>(customRender));</#if>
				return customRender;
			});
		</#if>


		<#if data.ranged && data.rangedItemType == "Default item">
		RenderingRegistry.registerEntityRenderingHandler(arrow, renderManager -> new SpriteRenderer(renderManager, Minecraft.getInstance().getItemRenderer()));
		</#if>
	}

	<#assign extendsClass = "Creature">
	<#if data.aiBase != "(none)" >
	    <#assign extendsClass = data.aiBase>
	<#else>
	    <#assign extendsClass = data.mobBehaviourType.replace("Mob", "Monster")>
	</#if>

	<#if data.breedable>
	    <#assign extendsClass = "Animal">
	</#if>

	<#if data.tameable>
		<#assign extendsClass = "Tameable">
	</#if>

	public static class CustomEntity extends ${extendsClass}Entity<#if data.ranged > implements IRangedAttackMob</#if> {
		
		public CustomEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        	this(entity, world);
    	}

		public CustomEntity(EntityType<CustomEntity> type, World world) {
      		super(type, world);
			experienceValue = ${data.xpAmount};
			setNoAI(${(!data.hasAI)});

			<#if data.mobLabel?has_content >
            	setCustomName(new StringTextComponent("${data.mobLabel}"));
            	setCustomNameVisible(true);
            </#if>

			<#if !data.doesDespawnWhenIdle>
				enablePersistence();
            </#if>

			<#if !data.equipmentMainHand.isEmpty()>
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ${mappedMCItemToItemStackCode(data.equipmentMainHand, 1)});
            </#if>
            <#if !data.equipmentOffHand.isEmpty()>
            this.setItemStackToSlot(EquipmentSlotType.OFFHAND, ${mappedMCItemToItemStackCode(data.equipmentOffHand, 1)});
            </#if>
            <#if !data.equipmentHelmet.isEmpty()>
            this.setItemStackToSlot(EquipmentSlotType.HEAD, ${mappedMCItemToItemStackCode(data.equipmentHelmet, 1)});
            </#if>
            <#if !data.equipmentBody.isEmpty()>
            this.setItemStackToSlot(EquipmentSlotType.CHEST, ${mappedMCItemToItemStackCode(data.equipmentBody, 1)});
            </#if>
            <#if !data.equipmentLeggings.isEmpty()>
            this.setItemStackToSlot(
					EquipmentSlotType.LEGS, ${mappedMCItemToItemStackCode(data.equipmentLeggings, 1)});
            </#if>
            <#if !data.equipmentBoots.isEmpty()>
            this.setItemStackToSlot(EquipmentSlotType.FEET, ${mappedMCItemToItemStackCode(data.equipmentBoots, 1)});
            </#if>

			<#if data.flyingMob>
			this.moveController = new FlyingMovementController(this, 10, true);
			this.navigator = new FlyingPathNavigator(this, this.world);
			<#elseif data.waterMob>
			this.moveController = new MovementController(this) {
				@Override public void tick() {
					if (CustomEntity.this.areEyesInFluid(FluidTags.WATER))
						CustomEntity.this.setMotion(CustomEntity.this.getMotion().add(0, 0.005, 0));

					if (this.action == MovementController.Action.MOVE_TO && !CustomEntity.this.getNavigator().noPath()) {
						double dx = this.posX - CustomEntity.this.getPosX();
						double dy = this.posY - CustomEntity.this.getPosY();
						double dz = this.posZ - CustomEntity.this.getPosZ();
						dy = dy / (double)MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
						CustomEntity.this.rotationYaw = this.limitAngle(CustomEntity.this.rotationYaw,
								(float)(MathHelper.atan2(dz, dx) * (double) (180 / (float) Math.PI)) - 90, 90);
						CustomEntity.this.renderYawOffset = CustomEntity.this.rotationYaw;
						CustomEntity.this.setAIMoveSpeed(MathHelper.lerp(0.125f, CustomEntity.this.getAIMoveSpeed(),
								(float)(this.speed * CustomEntity.this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue())));
						CustomEntity.this.setMotion(CustomEntity.this.getMotion().add(0, CustomEntity.this.getAIMoveSpeed() * dy * 0.1, 0));
					} else {
						CustomEntity.this.setAIMoveSpeed(0);
					}
				}
			};
			this.navigator = new SwimmerPathNavigator(this, this.world);
			</#if>
		}

		@Override public IPacket<?> createSpawnPacket() {
			return NetworkHooks.getEntitySpawningPacket(this);
		}
		
		<#if data.hasAI>
		@Override protected void registerGoals() {
			super.registerGoals();

			<#if aicode??>
                ${aicode}
            </#if>

            <#if data.ranged>
                this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 20, 10) {
					@Override public boolean shouldContinueExecuting() {
						return this.shouldExecute();
					}
				});
            </#if>
		}
		</#if>

		@Override public CreatureAttribute getCreatureAttribute() {
			return CreatureAttribute.${data.mobCreatureType};
		}

		<#if !data.doesDespawnWhenIdle>
		@Override public boolean canDespawn(double distanceToClosestPlayer) {
			return false;
		}
        </#if>

		<#if data.mountedYOffset != 0>
		@Override public double getMountedYOffset() {
			return super.getMountedYOffset() + ${data.mountedYOffset};
		}
		</#if>

		<#if !data.mobDrop.isEmpty()>
		protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
   		   super.dropSpecialItems(source, looting, recentlyHitIn);
		   	this.entityDropItem(${mappedMCItemToItemStackCode(data.mobDrop, 1)});
   		}
		</#if>

   		<#if data.livingSound.getMappedValue()?has_content>
		@Override public net.minecraft.util.SoundEvent getAmbientSound() {
			return (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.livingSound}"));
		}
		</#if>

   		<#if data.stepSound?has_content && data.stepSound.getMappedValue()?has_content>
		@Override public void playStepSound(BlockPos pos, BlockState blockIn) {
			this.playSound((net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.stepSound}")), 0.15f, 1);
		}
		</#if>

		@Override public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.hurtSound}"));
		}

		@Override public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.deathSound}"));
		}

		<#if hasProcedure(data.onStruckByLightning)>
		@Override public void onStruckByLightning(LightningBoltEntity entityLightningBolt) {
			super.onStruckByLightning(entityLightningBolt);
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Entity entity = this;
			<@procedureOBJToCode data.onStruckByLightning/>
		}
        </#if>

		<#if hasProcedure(data.whenMobFalls) || data.flyingMob>
		@Override public boolean onLivingFall(float l, float d) {
			<#if hasProcedure(data.whenMobFalls) >
				double x = this.getPosX();
				double y = this.getPosY();
				double z = this.getPosZ();
				Entity entity = this;
				<@procedureOBJToCode data.whenMobFalls/>
			</#if>

			<#if data.flyingMob >
				return false;
			<#else>
				return super.onLivingFall(l, d);
			</#if>
		}
        </#if>

		<#if hasProcedure(data.whenMobIsHurt) || data.immuneToArrows || data.immuneToFallDamage
			|| data.immuneToCactus || data.immuneToDrowning || data.immuneToLightning || data.immuneToPotions
			|| data.immuneToPlayer || data.immuneToExplosion || data.immuneToTrident || data.immuneToAnvil
			|| data.immuneToDragonBreath || data.immuneToWither>
		@Override public boolean attackEntityFrom(DamageSource source, float amount) {
			<#if hasProcedure(data.whenMobIsHurt)>
				double x = this.getPosX();
				double y = this.getPosY();
				double z = this.getPosZ();
				Entity entity = this;
				Entity sourceentity = source.getTrueSource();
				<@procedureOBJToCode data.whenMobIsHurt/>
			</#if>
			<#if data.immuneToArrows>
				if (source.getImmediateSource() instanceof ArrowEntity)
					return false;
			</#if>
			<#if data.immuneToPlayer>
				if (source.getImmediateSource() instanceof PlayerEntity)
					return false;
			</#if>
			<#if data.immuneToPotions>
				if (source.getImmediateSource() instanceof PotionEntity)
					return false;
			</#if>
			<#if data.immuneToFallDamage>
				if (source == DamageSource.FALL)
					return false;
			</#if>
			<#if data.immuneToCactus>
				if (source == DamageSource.CACTUS)
					return false;
			</#if>
			<#if data.immuneToDrowning>
				if (source == DamageSource.DROWN)
					return false;
			</#if>
			<#if data.immuneToLightning>
				if (source == DamageSource.LIGHTNING_BOLT)
					return false;
			</#if>
			<#if data.immuneToExplosion>
				if (source.isExplosion())
					return false;
			</#if>
			<#if data.immuneToTrident>
				if (source.getDamageType().equals("trident"))
					return false;
			</#if>
			<#if data.immuneToAnvil>
				if (source == DamageSource.ANVIL)
					return false;
			</#if>
			<#if data.immuneToDragonBreath>
				if (source == DamageSource.DRAGON_BREATH)
					return false;
			</#if>
			<#if data.immuneToWither>
				if (source == DamageSource.WITHER)
					return false;
				if (source.getDamageType().equals("witherSkull"))
					return false;
			</#if>
			return super.attackEntityFrom(source, amount);
		}
        </#if>

		<#if hasProcedure(data.whenMobDies)>
		@Override public void onDeath(DamageSource source) {
			super.onDeath(source);
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Entity sourceentity = source.getTrueSource();
			Entity entity = this;
			<@procedureOBJToCode data.whenMobDies/>
		}
        </#if>

		<#if hasProcedure(data.onInitialSpawn)>
		@Override public ILivingEntityData onInitialSpawn(IWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData livingdata, CompoundNBT tag) {
			ILivingEntityData retval = super.onInitialSpawn(world, difficulty, reason, livingdata, tag);
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Entity entity = this;
			<@procedureOBJToCode data.onInitialSpawn/>
			return retval;
		}
        </#if>

		<#if data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>">
		private final ItemStackHandler inventory = new ItemStackHandler(${data.inventorySize}) {
			@Override public int getSlotLimit(int slot) {
				return ${data.inventoryStackSize};
			}
		};

		private final CombinedInvWrapper combined = new CombinedInvWrapper(inventory, new EntityHandsInvWrapper(this), new EntityArmorInvWrapper(this));

		@Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
			if (this.isAlive() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side == null)
				return LazyOptional.of(() -> combined).cast();

			return super.getCapability(capability, side);
		}

   		@Override protected void dropInventory() {
			super.dropInventory();
			for(int i = 0; i < inventory.getSlots(); ++i) {
				ItemStack itemstack = inventory.getStackInSlot(i);
				if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
					this.entityDropItem(itemstack);
				}
			}
		}

		@Override public void writeAdditional(CompoundNBT compound) {
      		super.writeAdditional(compound);
			compound.put("InventoryCustom", inventory.serializeNBT());
		}

		@Override public void readAdditional(CompoundNBT compound) {
      		super.readAdditional(compound);
			INBT inventoryCustom = compound.get("InventoryCustom");
			if(inventoryCustom instanceof CompoundNBT)
				inventory.deserializeNBT((CompoundNBT) inventoryCustom);
      	}
        </#if>

		<#if hasProcedure(data.onRightClickedOn) || data.ridable || data.tameable || (data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>")>
		@Override public boolean processInteract(PlayerEntity sourceentity, Hand hand) {
			ItemStack itemstack = sourceentity.getHeldItem(hand);
			boolean retval = true;

			<#if data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>">
				<#if data.ridable>
					if (sourceentity.isSecondaryUseActive()) {
				</#if>
					if(sourceentity instanceof ServerPlayerEntity) {
						NetworkHooks.openGui((ServerPlayerEntity) sourceentity, new INamedContainerProvider() {

							@Override public ITextComponent getDisplayName() {
								return new StringTextComponent("${data.mobName}");
							}

							@Override public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
								PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
								packetBuffer.writeBlockPos(new BlockPos(sourceentity));
								packetBuffer.writeByte(0);
								packetBuffer.writeVarInt(CustomEntity.this.getEntityId());
								return new ${(data.guiBoundTo)}Gui.GuiContainerMod(id, inventory, packetBuffer);
							}

						}, buf -> {
							buf.writeBlockPos(new BlockPos(sourceentity));
							buf.writeByte(0);
							buf.writeVarInt(this.getEntityId());
						});
					}
				<#if data.ridable>
						return true;
					}
				</#if>
			</#if>

			<#if data.tameable>
				Item item = itemstack.getItem();
				if (itemstack.getItem() instanceof SpawnEggItem) {
					retval = super.processInteract(sourceentity, hand);
				} else if (this.world.isRemote) {
					retval = this.isTamed() && this.isOwner(sourceentity) || this.isBreedingItem(itemstack);
				} else {
					if (this.isTamed()) {
						if (this.isOwner(sourceentity)) {
							if (item.isFood() && this.isBreedingItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
								this.consumeItemFromStack(sourceentity, itemstack);
								this.heal((float)item.getFood().getHealing());
								retval = true;
							} else if (this.isBreedingItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
								this.consumeItemFromStack(sourceentity, itemstack);
								this.heal(4);
								retval = true;
							} else {
								retval = super.processInteract(sourceentity, hand);
							}
						}
					} else if (this.isBreedingItem(itemstack)) {
						this.consumeItemFromStack(sourceentity, itemstack);
						if (this.rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, sourceentity)) {
							this.setTamedBy(sourceentity);
							this.world.setEntityState(this, (byte)7);
						} else {
							this.world.setEntityState(this, (byte)6);
						}

						this.enablePersistence();
						retval = true;
					} else {
						retval = super.processInteract(sourceentity, hand);
						if (retval)
							this.enablePersistence();
					}
				}
			<#else>
				super.processInteract(sourceentity, hand);
			</#if>

			<#if data.ridable>
            sourceentity.startRiding(this);
            </#if>

			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Entity entity = this;
			<@procedureOBJToCode data.onRightClickedOn/>
			return retval;
		}
        </#if>

		<#if hasProcedure(data.whenThisMobKillsAnother)>
		@Override public void onKillEntity(LivingEntity entity) {
			super.onKillEntity(entity);
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Entity sourceentity = this;
			<@procedureOBJToCode data.whenThisMobKillsAnother/>
		}
        </#if>

		<#if hasProcedure(data.onMobTickUpdate)>
		@Override public void baseTick() {
			super.baseTick();
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Entity entity = this;
			<@procedureOBJToCode data.onMobTickUpdate/>
		}
        </#if>

		<#if hasProcedure(data.onPlayerCollidesWith)>
		@Override public void onCollideWithPlayer(PlayerEntity sourceentity) {
			super.onCollideWithPlayer(sourceentity);
			Entity entity = this;
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			<@procedureOBJToCode data.onPlayerCollidesWith/>
		}
        </#if>

        @Override protected void registerAttributes() {
			super.registerAttributes();

			if (this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null)
				this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(${data.movementSpeed});

			if (this.getAttribute(SharedMonsterAttributes.MAX_HEALTH) != null)
				this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(${data.health});

			if (this.getAttribute(SharedMonsterAttributes.ARMOR) != null)
				this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(${data.armorBaseValue});

			if (this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) == null)
				this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(${data.attackStrength});

			<#if (data.knockbackResistance > 0)>
			if (this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE) == null)
				this.getAttributes().registerAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
			this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(${data.knockbackResistance}D);
			</#if>

			<#if (data.attackKnockback > 0)>
			if (this.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK) == null)
				this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK);
			this.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).setBaseValue(${data.attackKnockback}D);
			</#if>

			<#if data.flyingMob>
			if (this.getAttribute(SharedMonsterAttributes.FLYING_SPEED) == null)
				this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
			this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(${data.movementSpeed});
			</#if>
		}

        <#if data.ranged>
		    public void attackEntityWithRangedAttack(LivingEntity target, float flval) {
				<#if data.rangedItemType == "Default item">
					ArrowCustomEntity entityarrow = new ArrowCustomEntity(arrow, this, this.world);
					double d0 = target.getPosY() + (double) target.getEyeHeight() - 1.1;
					double d1 = target.getPosX() - this.getPosX();
					double d3 = target.getPosZ() - this.getPosZ();
					entityarrow.shoot(d1, d0 - entityarrow.getPosY() + (double) MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F, d3, 1.6F, 12.0F);
					world.addEntity(entityarrow);
				<#else>
					${data.rangedItemType}Item.shoot(this, target);
				</#if>
			}
        </#if>

		<#if data.breedable>
            @Override public AgeableEntity createChild(AgeableEntity ageable) {
				CustomEntity retval = (CustomEntity) entity.create(this.world);
				retval.onInitialSpawn(this.world, this.world.getDifficultyForLocation(new BlockPos(retval)), SpawnReason.BREEDING, (ILivingEntityData)null, (CompoundNBT)null);
				return retval;
			}

			@Override public boolean isBreedingItem(ItemStack stack) {
				if (stack == null)
					return false;

            	<#list data.breedTriggerItems as breedTriggerItem>
					if (${mappedMCItemToItemStackCode(breedTriggerItem,1)}.getItem() == stack.getItem())
						return true;
                </#list>

				return false;
			}
        </#if>

		<#if data.waterMob>
		@Override public boolean canBreatheUnderwater() {
        	return true;
    	}

    	@Override public boolean isNotColliding(IWorldReader worldreader) {
        	return worldreader.checkNoEntityCollision(this, VoxelShapes.create(this.getBoundingBox()));
    	}

    	@Override public boolean isPushedByWater() {
			return false;
    	}
		</#if>

		<#if data.disableCollisions>
		@Override public boolean canBeCollidedWith() {
        	return false;
		}
		</#if>

		<#if data.isBoss>
		   @Override public boolean isNonBoss() {
				return false;
			}

		   private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(),
					BossInfo.Color.${data.bossBarColor}, BossInfo.Overlay.${data.bossBarType});

		   @Override public void addTrackingPlayer(ServerPlayerEntity player) {
				super.addTrackingPlayer(player);
				this.bossInfo.addPlayer(player);
			}

		   @Override public void removeTrackingPlayer(ServerPlayerEntity player) {
				super.removeTrackingPlayer(player);
				this.bossInfo.removePlayer(player);
			}

		   @Override public void updateAITasks() {
				super.updateAITasks();
				this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
			}
		</#if>

        <#if data.ridable && (data.canControlForward || data.canControlStrafe)>
            @Override public void travel(Vec3d dir) {
            	<#if data.canControlForward || data.canControlStrafe>
				Entity entity = this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
				if (this.isBeingRidden()) {
					this.rotationYaw = entity.rotationYaw;
					this.prevRotationYaw = this.rotationYaw;
					this.rotationPitch = entity.rotationPitch * 0.5F;
					this.setRotation(this.rotationYaw, this.rotationPitch);
					this.jumpMovementFactor = this.getAIMoveSpeed() * 0.15F;
					this.renderYawOffset = entity.rotationYaw;
					this.rotationYawHead = entity.rotationYaw;
					this.stepHeight = 1.0F;

					if (entity instanceof LivingEntity) {
						this.setAIMoveSpeed((float) this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());

						<#if data.canControlForward>
							float forward = ((LivingEntity) entity).moveForward;
						<#else>
							float forward = 0;
						</#if>

						<#if data.canControlStrafe>
							float strafe = ((LivingEntity) entity).moveStrafing;
						<#else>
							float strafe = 0;
						</#if>

						super.travel(new Vec3d(strafe, 0, forward));
					}

					this.prevLimbSwingAmount = this.limbSwingAmount;
					double d1 = this.getPosX() - this.prevPosX;
					double d0 = this.getPosZ() - this.prevPosZ;
					float f1 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
					if (f1 > 1.0F) f1 = 1.0F;
					this.limbSwingAmount += (f1 - this.limbSwingAmount) * 0.4F;
					this.limbSwing += this.limbSwingAmount;
					return;
				}
				this.stepHeight = 0.5F;
				this.jumpMovementFactor = 0.02F;
				</#if>

				super.travel(dir);
			}
        </#if>

		<#if data.flyingMob>
		@Override protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   		}

   		@Override public void setNoGravity(boolean ignored) {
			super.setNoGravity(true);
		}
        </#if>

        <#if data.spawnParticles || data.flyingMob>
        public void livingTick() {
			super.livingTick();

			<#if data.flyingMob>
			this.setNoGravity(true);
			</#if>

			<#if data.spawnParticles>
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			Random random = this.rand;
			Entity entity = this;
            <@particles data.particleSpawningShape data.particleToSpawn data.particleSpawningRadious data.particleAmount data.particleCondition/>
			</#if>
		}
        </#if>

	}

	<#if data.mobModelGlowTexture?has_content>
	@OnlyIn(Dist.CLIENT) private static class GlowingLayer<T extends Entity, M extends EntityModel<T>> extends LayerRenderer<T, M> {

		public GlowingLayer(IEntityRenderer<T, M> er) {
			super(er);
		}

		public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing,
				float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEyes(new ResourceLocation("${modid}:textures/${data.mobModelGlowTexture}")));
			this.getEntityModel().render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		}

	}
	</#if>

	<#if data.ranged && data.rangedItemType == "Default item">
   	@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class) private static class ArrowCustomEntity extends AbstractArrowEntity implements IRendersAsItem {

		public ArrowCustomEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        	super(arrow, world);
    	}

		public ArrowCustomEntity(EntityType<? extends ArrowCustomEntity> type, World world) {
			super(type, world);
		}

		public ArrowCustomEntity(EntityType<? extends ArrowCustomEntity> type, double x, double y, double z, World world) {
			super(type, x, y, z, world);
		}

		public ArrowCustomEntity(EntityType<? extends ArrowCustomEntity> type, LivingEntity entity, World world) {
			super(type, entity, world);
		}

		@Override public IPacket<?> createSpawnPacket() {
        	return NetworkHooks.getEntitySpawningPacket(this);
    	}

		@Override @OnlyIn(Dist.CLIENT) public ItemStack getItem() {
			return ${mappedMCItemToItemStackCode(data.rangedAttackItem, 1)};
		}

		@Override protected ItemStack getArrowStack() {
			return ${mappedMCItemToItemStackCode(data.rangedAttackItem, 1)};
		}
	}
	</#if>

	<#if data.getModelCode()?? && !data.isBuiltInModel() >
	${data.getModelCode().toString()
		.replace("extends ModelBase", "extends EntityModel<Entity>")
		.replace("GlStateManager.translate", "GlStateManager.translated")
		.replace("RendererModel ", "ModelRenderer ")
		.replace("RendererModel(", "ModelRenderer(")
		.replace("GlStateManager.scale", "GlStateManager.scaled")
		.replaceAll("(.*?)\\.cubeList\\.add\\(new\\sModelBox\\(", "addBoxHelper(")
		.replaceAll(",[\n\r\t\\s]+true\\)\\);", ", true);")
		.replaceAll(",[\n\r\t\\s]+false\\)\\);", ", false);")
		.replaceAll("setRotationAngles\\(float[\n\r\t\\s]+f,[\n\r\t\\s]+float[\n\r\t\\s]+f1,[\n\r\t\\s]+float[\n\r\t\\s]+f2,[\n\r\t\\s]+float[\n\r\t\\s]+f3,[\n\r\t\\s]+float[\n\r\t\\s]+f4,[\n\r\t\\s]+float[\n\r\t\\s]+f5,[\n\r\t\\s]+Entity[\n\r\t\\s]+e\\)",
		"setRotationAngles(Entity e, float f, float f1, float f2, float f3, float f4)")
		.replaceAll("setRotationAngles\\(float[\n\r\t\\s]+f,[\n\r\t\\s]+float[\n\r\t\\s]+f1,[\n\r\t\\s]+float[\n\r\t\\s]+f2,[\n\r\t\\s]+float[\n\r\t\\s]+f3,[\n\r\t\\s]+float[\n\r\t\\s]+f4,[\n\r\t\\s]+float[\n\r\t\\s]+f5,[\n\r\t\\s]+Entity[\n\r\t\\s]+entity\\)",
		"setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4)")

		.replaceAll("((super\\.)?)setRotationAngles\\(f,[\n\r\t\\s]+f1,[\n\r\t\\s]+f2,[\n\r\t\\s]+f3,[\n\r\t\\s]+f4,[\n\r\t\\s]+f5,[\n\r\t\\s]+e\\);",
		"")
		.replaceAll("((super\\.)?)setRotationAngles\\(f,[\n\r\t\\s]+f1,[\n\r\t\\s]+f2,[\n\r\t\\s]+f3,[\n\r\t\\s]+f4,[\n\r\t\\s]+f5,[\n\r\t\\s]+entity\\);",
		"")

		.replaceAll("render\\(Entity[\n\r\t\\s]+entity,[\n\r\t\\s]+float[\n\r\t\\s]+f,[\n\r\t\\s]+float[\n\r\t\\s]+f1,[\n\r\t\\s]+float[\n\r\t\\s]+f2,[\n\r\t\\s]+float[\n\r\t\\s]+f3,[\n\r\t\\s]+float[\n\r\t\\s]+f4,[\n\r\t\\s]+float[\n\r\t\\s]+f5\\)",
		"render(MatrixStack ms, IVertexBuilder vb, int i1, int i2, float f1, float f2, float f3, float f4)")
		.replaceAll("super\\.render\\(entity,[\n\r\t\\s]+f,[\n\r\t\\s]+f1,[\n\r\t\\s]+f2,[\n\r\t\\s]+f3,[\n\r\t\\s]+f4,[\n\r\t\\s]+f5\\);", "")
		.replace(".render(f5);", ".render(ms, vb, i1, i2, f1, f2, f3, f4);")
	}

		<#if data.getModelCode().contains(".cubeList.add(new")> <#-- if the model is pre 1.15.2 -->
		@OnlyIn(Dist.CLIENT) public static void addBoxHelper(ModelRenderer renderer, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta) {
			addBoxHelper(renderer, texU, texV, x, y, z, dx, dy, dz, delta, renderer.mirror);
		}

		@OnlyIn(Dist.CLIENT) public static void addBoxHelper(ModelRenderer renderer, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta, boolean mirror) {
			renderer.mirror = mirror;
			renderer.addBox("", x, y, z, dx, dy, dz, delta, texU, texV);
		}
		</#if>

	</#if>

}
<#macro entityTexture>
<#if hasCondition(data.entityTextureSelector)>
    double x = entity.getPosX();
    double y = entity.getPosY();
    double z = entity.getPosZ();
    World world = entity.world;
    return new ResourceLocation("${modid}", "textures/" + <@procedureOBJToConditionCode data.entityTextureSelector/>);
    <#else>return new ResourceLocation("${modid}:textures/${data.mobModelTexture}");
    </#if>
</#macro>
<#-- @formatter:on -->
