<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2022, Pylo, opensource contributors
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
<#include "../mcitems.ftl">
<#include "../procedures.java.ftl">

package ${package}.entity;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.material.Material;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;

import javax.annotation.Nullable;

<#assign extendsClass = "PathfinderMob">

<#if data.aiBase != "(none)" >
	<#assign extendsClass = data.aiBase?replace("Enderman", "EnderMan")>
<#else>
	<#assign extendsClass = data.mobBehaviourType?replace("Mob", "Monster")?replace("Creature", "PathfinderMob")>
</#if>

<#if data.breedable>
	<#assign extendsClass = "Animal">
</#if>

<#if (data.tameable && data.breedable)>
	<#assign extendsClass = "TamableAnimal">
</#if>

public class ${name}Entity extends ${extendsClass} <#if data.ranged>implements RangedAttackMob</#if> {

	<#if data.isBoss>
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(),
		ServerBossEvent.BossBarColor.${data.bossBarColor}, ServerBossEvent.BossBarOverlay.${data.bossBarType});
	</#if>

	public ${name}Entity(PlayMessages.SpawnEntity packet, Level world) {
    	this(${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}.get(), world);
    }

	public ${name}Entity(EntityType<${name}Entity> type, Level world) {
    	super(type, world);
		xpReward = ${data.xpAmount};
		setNoAi(${(!data.hasAI)});

		<#if data.mobLabel?has_content >
        	setCustomName(Component.literal("${data.mobLabel}"));
        	setCustomNameVisible(true);
        </#if>

		<#if !data.doesDespawnWhenIdle>
			setPersistenceRequired();
        </#if>

		<#if !data.equipmentMainHand.isEmpty()>
        this.setItemSlot(EquipmentSlot.MAINHAND, ${mappedMCItemToItemStackCode(data.equipmentMainHand, 1)});
        </#if>
        <#if !data.equipmentOffHand.isEmpty()>
        this.setItemSlot(EquipmentSlot.OFFHAND, ${mappedMCItemToItemStackCode(data.equipmentOffHand, 1)});
        </#if>
        <#if !data.equipmentHelmet.isEmpty()>
        this.setItemSlot(EquipmentSlot.HEAD, ${mappedMCItemToItemStackCode(data.equipmentHelmet, 1)});
        </#if>
        <#if !data.equipmentBody.isEmpty()>
        this.setItemSlot(EquipmentSlot.CHEST, ${mappedMCItemToItemStackCode(data.equipmentBody, 1)});
        </#if>
        <#if !data.equipmentLeggings.isEmpty()>
        this.setItemSlot(EquipmentSlot.LEGS, ${mappedMCItemToItemStackCode(data.equipmentLeggings, 1)});
        </#if>
        <#if !data.equipmentBoots.isEmpty()>
        this.setItemSlot(EquipmentSlot.FEET, ${mappedMCItemToItemStackCode(data.equipmentBoots, 1)});
        </#if>

		<#if data.flyingMob>
		this.moveControl = new FlyingMoveControl(this, 10, true);
		<#elseif data.waterMob>
		this.setPathfindingMalus(BlockPathTypes.WATER, 0);
		this.moveControl = new MoveControl(this) {
			@Override public void tick() {
			    if (${name}Entity.this.isInWater())
                    ${name}Entity.this.setDeltaMovement(${name}Entity.this.getDeltaMovement().add(0, 0.005, 0));

				if (this.operation == MoveControl.Operation.MOVE_TO && !${name}Entity.this.getNavigation().isDone()) {
					double dx = this.wantedX - ${name}Entity.this.getX();
					double dy = this.wantedY - ${name}Entity.this.getY();
					double dz = this.wantedZ - ${name}Entity.this.getZ();

					float f = (float) (Mth.atan2(dz, dx) * (double) (180 / Math.PI)) - 90;
					float f1 = (float) (this.speedModifier * ${name}Entity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());

					${name}Entity.this.setYRot(this.rotlerp(${name}Entity.this.getYRot(), f, 10));
					${name}Entity.this.yBodyRot = ${name}Entity.this.getYRot();
					${name}Entity.this.yHeadRot = ${name}Entity.this.getYRot();

					if (${name}Entity.this.isInWater()) {
						${name}Entity.this.setSpeed((float) ${name}Entity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());

						float f2 = - (float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * (180 / Math.PI));
						f2 = Mth.clamp(Mth.wrapDegrees(f2), -85, 85);
						${name}Entity.this.setXRot(this.rotlerp(${name}Entity.this.getXRot(), f2, 5));
						float f3 = Mth.cos(${name}Entity.this.getXRot() * (float) (Math.PI / 180.0));

						${name}Entity.this.setZza(f3 * f1);
						${name}Entity.this.setYya((float) (f1 * dy));
					} else {
						${name}Entity.this.setSpeed(f1 * 0.05F);
					}
				} else {
					${name}Entity.this.setSpeed(0);
					${name}Entity.this.setYya(0);
					${name}Entity.this.setZza(0);
				}
			}
		};
		</#if>
	}

	@Override public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	<#if data.flyingMob>
	@Override protected PathNavigation createNavigation(Level world) {
		return new FlyingPathNavigation(this, world);
	}
	<#elseif data.waterMob>
	@Override protected PathNavigation createNavigation(Level world) {
		return new WaterBoundPathNavigation(this, world);
	}
	</#if>

	<#if data.hasAI>
	@Override protected void registerGoals() {
		super.registerGoals();

		<#if aicode??>
            ${aicode}
        </#if>

        <#if data.ranged>
            this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, ${data.rangedAttackInterval}, ${data.rangedAttackRadius}f) {
				@Override public boolean canContinueToUse() {
					return this.canUse();
				}
			});
        </#if>
	}
	</#if>

	@Override public MobType getMobType() {
		return MobType.${data.mobCreatureType};
	}

	<#if !data.doesDespawnWhenIdle>
	@Override public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}
    </#if>

	<#if data.mountedYOffset != 0>
	@Override public double getPassengersRidingOffset() {
		return super.getPassengersRidingOffset() + ${data.mountedYOffset};
	}
	</#if>

	<#if !data.mobDrop.isEmpty()>
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropCustomDeathLoot(source, looting, recentlyHitIn);
        this.spawnAtLocation(${mappedMCItemToItemStackCode(data.mobDrop, 1)});
   	}
	</#if>

   	<#if data.livingSound.getMappedValue()?has_content>
	@Override public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.livingSound}"));
	}
	</#if>

   	<#if data.stepSound?has_content && data.stepSound.getMappedValue()?has_content>
	@Override public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.stepSound}")), 0.15f, 1);
	}
	</#if>

	@Override public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.hurtSound}"));
	}

	@Override public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.deathSound}"));
	}

	<#if hasProcedure(data.onStruckByLightning)>
	@Override public void thunderHit(ServerLevel serverWorld, LightningBolt lightningBolt) {
		super.thunderHit(serverWorld, lightningBolt);
		<@procedureCode data.onStruckByLightning, {
			"x": "this.getX()",
			"y": "this.getY()",
			"z": "this.getZ()",
			"entity": "this",
			"world": "this.level"
		}/>
	}
    </#if>

	<#if hasProcedure(data.whenMobFalls) || data.flyingMob>
	@Override public boolean causeFallDamage(float l, float d, DamageSource source) {
		<#if hasProcedure(data.whenMobFalls)>
			<@procedureCode data.whenMobFalls, {
				"x": "this.getX()",
				"y": "this.getY()",
				"z": "this.getZ()",
				"entity": "this",
				"world": "this.level"
			}/>
		</#if>

		<#if data.flyingMob >
			return false;
		<#else>
			return super.causeFallDamage(l, d, source);
		</#if>
	}
    </#if>

	<#if hasProcedure(data.whenMobIsHurt) || data.immuneToFire || data.immuneToArrows || data.immuneToFallDamage
		|| data.immuneToCactus || data.immuneToDrowning || data.immuneToLightning || data.immuneToPotions
		|| data.immuneToPlayer || data.immuneToExplosion || data.immuneToTrident || data.immuneToAnvil
		|| data.immuneToDragonBreath || data.immuneToWither>
	@Override public boolean hurt(DamageSource source, float amount) {
		<#if hasProcedure(data.whenMobIsHurt)>
			<@procedureCode data.whenMobIsHurt, {
				"x": "this.getX()",
				"y": "this.getY()",
				"z": "this.getZ()",
				"entity": "this",
				"world": "this.level",
				"sourceentity": "source.getEntity()"
			}/>
		</#if>
		<#if data.immuneToFire>
			if (source.is(DamageTypes.IN_FIRE))
				return false;
		</#if>
		<#if data.immuneToArrows>
			if (source.getDirectEntity() instanceof AbstractArrow)
				return false;
		</#if>
		<#if data.immuneToPlayer>
			if (source.getDirectEntity() instanceof Player)
				return false;
		</#if>
		<#if data.immuneToPotions>
			if (source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud)
				return false;
		</#if>
		<#if data.immuneToFallDamage>
			if (source.is(DamageTypes.FALL))
				return false;
		</#if>
		<#if data.immuneToCactus>
			if (source.is(DamageTypes.CACTUS))
				return false;
		</#if>
		<#if data.immuneToDrowning>
			if (source.is(DamageTypes.DROWN))
				return false;
		</#if>
		<#if data.immuneToLightning>
			if (source.is(DamageTypes.LIGHTNING_BOLT))
				return false;
		</#if>
		<#if data.immuneToExplosion>
			if (source.is(DamageTypes.EXPLOSION))
				return false;
		</#if>
		<#if data.immuneToTrident>
			if (source.is(DamageTypes.TRIDENT))
				return false;
		</#if>
		<#if data.immuneToAnvil>
			if (source.is(DamageTypes.FALLING_ANVIL))
				return false;
		</#if>
		<#if data.immuneToDragonBreath>
			if (source.is(DamageTypes.DRAGON_BREATH))
				return false;
		</#if>
		<#if data.immuneToWither>
			if (source.is(DamageTypes.WITHER))
				return false;
			if (source.is(DamageTypes.WITHER_SKULL))
				return false;
		</#if>
		return super.hurt(source, amount);
	}
    </#if>

	<#if hasProcedure(data.whenMobDies)>
	@Override public void die(DamageSource source) {
		super.die(source);
		<@procedureCode data.whenMobDies, {
			"x": "this.getX()",
			"y": "this.getY()",
			"z": "this.getZ()",
			"sourceentity": "source.getEntity()",
			"entity": "this",
			"world": "this.level"
		}/>
	}
    </#if>

	<#if hasProcedure(data.onInitialSpawn)>
	@Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty,
			MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		<@procedureCode data.onInitialSpawn, {
			"x": "this.getX()",
			"y": "this.getY()",
			"z": "this.getZ()",
			"world": "world",
			"entity": "this"
		}/>
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
		if (this.isAlive() && capability == ForgeCapabilities.ITEM_HANDLER && side == null)
			return LazyOptional.of(() -> combined).cast();

		return super.getCapability(capability, side);
	}

   	@Override protected void dropEquipment() {
		super.dropEquipment();
		for(int i = 0; i < inventory.getSlots(); ++i) {
			ItemStack itemstack = inventory.getStackInSlot(i);
			if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
				this.spawnAtLocation(itemstack);
			}
		}
	}

	@Override public void addAdditionalSaveData(CompoundTag compound) {
    	super.addAdditionalSaveData(compound);
		compound.put("InventoryCustom", inventory.serializeNBT());
	}

	@Override public void readAdditionalSaveData(CompoundTag compound) {
    	super.readAdditionalSaveData(compound);
		Tag inventoryCustom = compound.get("InventoryCustom");
		if(inventoryCustom instanceof CompoundTag inventoryTag)
			inventory.deserializeNBT(inventoryTag);
    }
    </#if>

	<#if hasProcedure(data.onRightClickedOn) || data.ridable || (data.tameable && data.breedable) || (data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>")>
	@Override public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		InteractionResult retval = InteractionResult.sidedSuccess(this.level.isClientSide());

		<#if data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>">
			<#if data.ridable>
				if (sourceentity.isSecondaryUseActive()) {
			</#if>
				if(sourceentity instanceof ServerPlayer serverPlayer) {
					NetworkHooks.openScreen(serverPlayer, new MenuProvider() {

						@Override public Component getDisplayName() {
							return Component.literal("${data.mobName}");
						}

						@Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
							FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
							packetBuffer.writeBlockPos(sourceentity.blockPosition());
							packetBuffer.writeByte(0);
							packetBuffer.writeVarInt(${name}Entity.this.getId());
							return new ${data.guiBoundTo}Menu(id, inventory, packetBuffer);
						}

					}, buf -> {
						buf.writeBlockPos(sourceentity.blockPosition());
						buf.writeByte(0);
						buf.writeVarInt(this.getId());
					});
				}
			<#if data.ridable>
					return InteractionResult.sidedSuccess(this.level.isClientSide());
				}
			</#if>
		</#if>

		<#if (data.tameable && data.breedable)>
			Item item = itemstack.getItem();
			if (itemstack.getItem() instanceof SpawnEggItem) {
				retval = super.mobInteract(sourceentity, hand);
			} else if (this.level.isClientSide()) {
				retval = (this.isTame() && this.isOwnedBy(sourceentity) || this.isFood(itemstack))
						? InteractionResult.sidedSuccess(this.level.isClientSide()) : InteractionResult.PASS;
			} else {
				if (this.isTame()) {
					if (this.isOwnedBy(sourceentity)) {
						if (item.isEdible() && this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
							this.usePlayerItem(sourceentity, hand, itemstack);
							this.heal((float)item.getFoodProperties().getNutrition());
							retval = InteractionResult.sidedSuccess(this.level.isClientSide());
						} else if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
							this.usePlayerItem(sourceentity, hand, itemstack);
							this.heal(4);
							retval = InteractionResult.sidedSuccess(this.level.isClientSide());
						} else {
							retval = super.mobInteract(sourceentity, hand);
						}
					}
				} else if (this.isFood(itemstack)) {
					this.usePlayerItem(sourceentity, hand, itemstack);
					if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, sourceentity)) {
						this.tame(sourceentity);
						this.level.broadcastEntityEvent(this, (byte) 7);
					} else {
						this.level.broadcastEntityEvent(this, (byte) 6);
					}

					this.setPersistenceRequired();
					retval = InteractionResult.sidedSuccess(this.level.isClientSide());
				} else {
					retval = super.mobInteract(sourceentity, hand);
					if (retval == InteractionResult.SUCCESS || retval == InteractionResult.CONSUME)
						this.setPersistenceRequired();
				}
			}
		<#else>
			super.mobInteract(sourceentity, hand);
		</#if>

		<#if data.ridable>
		sourceentity.startRiding(this);
	    </#if>

		<#if hasProcedure(data.onRightClickedOn)>
			double x = this.getX();
			double y = this.getY();
			double z = this.getZ();
			Entity entity = this;
			Level world = this.level;
			<#if hasReturnValueOf(data.onRightClickedOn, "actionresulttype")>
				return <@procedureOBJToInteractionResultCode data.onRightClickedOn/>;
			<#else>
				<@procedureOBJToCode data.onRightClickedOn/>
				return retval;
			</#if>
		<#else>
			return retval;
		</#if>
	}
    </#if>

	<#if hasProcedure(data.whenThisMobKillsAnother)>
	@Override public void awardKillScore(Entity entity, int score, DamageSource damageSource) {
		super.awardKillScore(entity, score, damageSource);
		<@procedureCode data.whenThisMobKillsAnother, {
			"x": "this.getX()",
			"y": "this.getY()",
			"z": "this.getZ()",
			"entity": "entity",
			"sourceentity": "this",
			"world": "this.level"
		}/>
	}
    </#if>

	<#if hasProcedure(data.onMobTickUpdate)>
	@Override public void baseTick() {
		super.baseTick();
		<@procedureCode data.onMobTickUpdate, {
			"x": "this.getX()",
			"y": "this.getY()",
			"z": "this.getZ()",
			"entity": "this",
			"world": "this.level"
		}/>
	}
    </#if>

	<#if hasProcedure(data.onPlayerCollidesWith)>
	@Override public void playerTouch(Player sourceentity) {
		super.playerTouch(sourceentity);
		<@procedureCode data.onPlayerCollidesWith, {
			"x": "this.getX()",
			"y": "this.getY()",
			"z": "this.getZ()",
			"entity": "this",
			"sourceentity": "sourceentity",
			"world": "this.level"
		}/>
	}
    </#if>

    <#if data.ranged>
	    @Override public void performRangedAttack(LivingEntity target, float flval) {
			<#if data.rangedItemType == "Default item">
				<#if !data.rangedAttackItem.isEmpty()>
				${name}EntityProjectile entityarrow = new ${name}EntityProjectile(${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}_PROJECTILE.get(), this, this.level);
				<#else>
				Arrow entityarrow = new Arrow(this.level, this);
				</#if>
				double d0 = target.getY() + target.getEyeHeight() - 1.1;
				double d1 = target.getX() - this.getX();
				double d3 = target.getZ() - this.getZ();
				entityarrow.shoot(d1, d0 - entityarrow.getY() + Math.sqrt(d1 * d1 + d3 * d3) * 0.2F, d3, 1.6F, 12.0F);
				level.addFreshEntity(entityarrow);
			<#else>
				${data.rangedItemType}Entity.shoot(this, target);
			</#if>
		}
    </#if>

	<#if data.breedable>
        @Override public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
			${name}Entity retval = ${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}.get().create(serverWorld);
			retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
			return retval;
		}

		@Override public boolean isFood(ItemStack stack) {
			return List.of(<#list data.breedTriggerItems as breedTriggerItem>${mappedMCItemToItem(breedTriggerItem)}<#if breedTriggerItem?has_next>,</#if></#list>).contains(stack.getItem());
		}
    </#if>

	<#if data.waterMob>
	@Override public boolean canBreatheUnderwater() {
    	return true;
    }

    @Override public boolean checkSpawnObstruction(LevelReader world) {
		return world.isUnobstructed(this);
	}

    @Override public boolean isPushedByFluid() {
		return false;
    }
	</#if>

	<#if data.disableCollisions>
	@Override public boolean isPushable() {
		return false;
	}

   	@Override protected void doPush(Entity entityIn) {
   	}

   	@Override protected void pushEntities() {
   	}
	</#if>

	<#if data.solidBoundingBox?? && (hasProcedure(data.solidBoundingBox) || data.solidBoundingBox.getFixedValue())>
	@Override
	public boolean canCollideWith(Entity entity) {
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		<#if hasProcedure(data.solidBoundingBox)>
		Entity entity = this;
		Level world = entity.level;
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		</#if>
		return <@procedureOBJToConditionCode data.solidBoundingBox true false/>;
	}
	</#if>

	<#if data.isBoss>
	@Override public boolean canChangeDimensions() {
		return false;
	}

	@Override public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Override public void customServerAiStep() {
		super.customServerAiStep();
		this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
	}
	</#if>

    <#if data.ridable && (data.canControlForward || data.canControlStrafe)>
        @Override public void travel(Vec3 dir) {
        	<#if data.canControlForward || data.canControlStrafe>
			Entity entity = this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
			if (this.isVehicle()) {
				this.setYRot(entity.getYRot());
				this.yRotO = this.getYRot();
				this.setXRot(entity.getXRot() * 0.5F);
				this.setRot(this.getYRot(), this.getXRot());
				this.yBodyRot = entity.getYRot();
				this.yHeadRot = entity.getYRot();
				this.maxUpStep = 1.0F;

				if (entity instanceof LivingEntity passenger) {
					this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));

					<#if data.canControlForward>
						float forward = passenger.zza;
					<#else>
						float forward = 0;
					</#if>

					<#if data.canControlStrafe>
						float strafe = passenger.xxa;
					<#else>
						float strafe = 0;
					</#if>

					super.travel(new Vec3(strafe, 0, forward));
				}
				
				return;
			}
			this.maxUpStep = 0.5F;
			</#if>

			super.travel(dir);
		}
    </#if>

	<#if data.flyingMob>
	@Override protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   	}

   	@Override public void setNoGravity(boolean ignored) {
		super.setNoGravity(true);
	}
    </#if>

    <#if data.flyingMob>
    public void aiStep() {
		super.aiStep();

		this.setNoGravity(true);
	}
    </#if>

	public static void init() {
		<#if data.spawnThisMob>
			<#if data.mobSpawningType == "creature">
			SpawnPlacements.register(${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}.get(),
					SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				<#if hasProcedure(data.spawningCondition)>
					(entityType, world, reason, pos, random) -> {
						int x = pos.getX();
						int y = pos.getY();
						int z = pos.getZ();
						return <@procedureOBJToConditionCode data.spawningCondition/>;
					}
				<#else>
					(entityType, world, reason, pos, random) ->
							(world.getBlockState(pos.below()).getMaterial() == Material.GRASS && world.getRawBrightness(pos, 0) > 8)
				</#if>
			);
			<#elseif data.mobSpawningType == "ambient" || data.mobSpawningType == "misc">
			SpawnPlacements.register(${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}.get(),
					SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
					<#if hasProcedure(data.spawningCondition)>
					(entityType, world, reason, pos, random) -> {
						int x = pos.getX();
						int y = pos.getY();
						int z = pos.getZ();
						return <@procedureOBJToConditionCode data.spawningCondition/>;
					}
					<#else>
					Mob::checkMobSpawnRules
					</#if>
			);
			<#elseif data.mobSpawningType == "waterCreature" || data.mobSpawningType == "waterAmbient">
			SpawnPlacements.register(${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}.get(),
					SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
					<#if hasProcedure(data.spawningCondition)>
					(entityType, world, reason, pos, random) -> {
						int x = pos.getX();
						int y = pos.getY();
						int z = pos.getZ();
						return <@procedureOBJToConditionCode data.spawningCondition/>;
					}
					<#else>
					(entityType, world, reason, pos, random) ->
							(world.getBlockState(pos).is(Blocks.WATER) && world.getBlockState(pos.above()).is(Blocks.WATER))
					</#if>
			);
			<#elseif data.mobSpawningType == "undergroundWaterCreature">
			SpawnPlacements.register(${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}.get(),
					SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
					<#if hasProcedure(data.spawningCondition)>
					(entityType, world, reason, pos, random) -> {
						int x = pos.getX();
						int y = pos.getY();
						int z = pos.getZ();
						return <@procedureOBJToConditionCode data.spawningCondition/>;
					}
					<#else>
					(entityType, world, reason, pos, random) -> {
					    return world.getFluidState(pos.below()).is(FluidTags.WATER) && world.getBlockState(pos.above()).is(Blocks.WATER) && pos.getY() >= (world.getSeaLevel() - 13) && pos.getY() <= world.getSeaLevel();
                    }
					</#if>
			);
			<#else>
			SpawnPlacements.register(${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}.get(),
					SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
					<#if hasProcedure(data.spawningCondition)>
					(entityType, world, reason, pos, random) -> {
						int x = pos.getX();
						int y = pos.getY();
						int z = pos.getZ();
						return <@procedureOBJToConditionCode data.spawningCondition/>;
					}
					<#else>
						(entityType, world, reason, pos, random) ->
								(world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random)
										&& Mob.checkMobSpawnRules(entityType, world, reason, pos, random))
					</#if>
			);
			</#if>
		</#if>

		<#if data.spawnInDungeons>
			DungeonHooks.addDungeonMob(${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}.get(), 180);
		</#if>
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, ${data.movementSpeed});
		builder = builder.add(Attributes.MAX_HEALTH, ${data.health});
		builder = builder.add(Attributes.ARMOR, ${data.armorBaseValue});
		builder = builder.add(Attributes.ATTACK_DAMAGE, ${data.attackStrength});
		builder = builder.add(Attributes.FOLLOW_RANGE, ${data.followRange});

		<#if (data.knockbackResistance > 0)>
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, ${data.knockbackResistance});
		</#if>

		<#if (data.attackKnockback > 0)>
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, ${data.attackKnockback});
		</#if>

		<#if data.flyingMob>
		builder = builder.add(Attributes.FLYING_SPEED, ${data.movementSpeed});
		</#if>

		<#if data.waterMob>
		builder = builder.add(ForgeMod.SWIM_SPEED.get(), ${data.movementSpeed});
		</#if>

		<#if data.aiBase == "Zombie">
		builder = builder.add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
		</#if>

		return builder;
	}

}
<#-- @formatter:on -->