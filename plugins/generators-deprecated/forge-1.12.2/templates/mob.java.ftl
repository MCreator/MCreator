<#-- @formatter:off -->
<#include "mcitems.ftl">
<#include "procedures.java.ftl">
<#include "particles.java.ftl">

package ${package}.entity;

@Elements${JavaModName}.ModElement.Tag public class Entity${name} extends Elements${JavaModName}.ModElement{

	public static final int ENTITYID = ${data.getModElement().getID(0)};
	public static final int ENTITYID_RANGED = ${data.getModElement().getID(1)};

	public Entity${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
								.id(new ResourceLocation("${modid}", "${registryname}"), ENTITYID).name("${registryname}").tracker(${data.trackingRange}, 3, true)
								<#if data.hasSpawnEgg>.egg(${data.spawnEggBaseColor.getRGB()}, ${data.spawnEggDotColor.getRGB()})</#if>
								.build());
		<#if data.ranged>
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityArrowCustom.class)
								.id(new ResourceLocation("${modid}", "entitybullet${registryname}"), ENTITYID_RANGED).name("entitybullet${registryname}").tracker(64, 1, true).build());
		</#if>
	}

	<#if data.spawnThisMob>
	@Override public void init(FMLInitializationEvent event) {
		<#if data.restrictionBiomes?has_content>
			Biome[] spawnBiomes = {
				<#list data.restrictionBiomes as restrictionBiome>
					<#if restrictionBiome.canProperlyMap()>
					Biome.REGISTRY.getObject(new ResourceLocation("${restrictionBiome}")),
					</#if>
				</#list>
			};
		<#else>
			Biome[] spawnBiomes = allbiomes(Biome.REGISTRY);
		</#if>
		EntityRegistry.addSpawn(EntityCustom.class, ${data.spawningProbability}, ${data.minNumberOfMobsPerGroup}, ${data.maxNumberOfMobsPerGroup},
			${generator.map(data.mobSpawningType, "mobspawntypes")}, spawnBiomes);

		<#if data.spawnInDungeons>
		DungeonHooks.addDungeonMob(new ResourceLocation("${modid}:${registryname}" ), 180);
		</#if>
	}
	</#if>

	<#if !data.restrictionBiomes?has_content>
	private Biome[] allbiomes(net.minecraft.util.registry.RegistryNamespaced<ResourceLocation, Biome> in) {
		Iterator<Biome> itr = in.iterator();
		ArrayList<Biome> ls = new ArrayList<Biome>();
		while(itr.hasNext())
			ls.add(itr.next());
		return ls.toArray(new Biome[ls.size()]);
	}
	</#if>

	@SideOnly(Side.CLIENT) @Override public void preInit(FMLPreInitializationEvent event) {
		<#if data.mobModelName == "Chicken">
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new RenderLiving(renderManager, new ModelChicken(), ${data.modelShadowSize}f) {
						protected ResourceLocation getEntityTexture(Entity entity) {
							return new ResourceLocation("${modid}:textures/${data.mobModelTexture}");
						}
					};
			});
		<#elseif data.mobModelName == "Cow">
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new RenderLiving(renderManager, new ModelCow(), ${data.modelShadowSize}f) {
						protected ResourceLocation getEntityTexture(Entity entity) {
							return new ResourceLocation("${modid}:textures/${data.mobModelTexture}");
						}
					};
			});
		<#elseif data.mobModelName == "Creeper">
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new RenderLiving(renderManager, new ModelCreeper(), ${data.modelShadowSize}f) {
						protected ResourceLocation getEntityTexture(Entity entity) {
		    				return new ResourceLocation("${modid}:textures/${data.mobModelTexture}");
						}
					};
			});
		<#elseif data.mobModelName == "Ghast">
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new RenderLiving(renderManager, new ModelGhast(), ${data.modelShadowSize}f) {
						protected ResourceLocation getEntityTexture(Entity entity) {
		    				return new ResourceLocation("${modid}:textures/${data.mobModelTexture}");
						}
					};
			});
		<#elseif data.mobModelName == "Pig">
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new RenderLiving(renderManager, new ModelPig(), ${data.modelShadowSize}f) {
						protected ResourceLocation getEntityTexture(Entity entity) {
							return new ResourceLocation("${modid}:textures/${data.mobModelTexture}");
						}
					};
			});
		<#elseif data.mobModelName == "Slime">
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new RenderLiving(renderManager, new ModelSlime(0), ${data.modelShadowSize}f) {
						protected ResourceLocation getEntityTexture(Entity entity) {
							return new ResourceLocation("${modid}:textures/${data.mobModelTexture}");
						}
					};
			});
		<#elseif data.mobModelName == "Spider">
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new RenderLiving(renderManager, new ModelSpider(), ${data.modelShadowSize}f) {
						protected ResourceLocation getEntityTexture(Entity entity) {
							return new ResourceLocation("${modid}:textures/${data.mobModelTexture}");
						}
					};
			});
		<#elseif data.mobModelName == "Villager">
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				RenderLiving customRender = new RenderLiving(renderManager, new ModelVillager(0.0F), ${data.modelShadowSize}f) {
					protected ResourceLocation getEntityTexture(Entity entity) {
		    			return new ResourceLocation("${modid}:textures/${data.mobModelTexture}");
					}
				};
				customRender.addLayer(new net.minecraft.client.renderer.entity.layers.LayerHeldItem(customRender));
				return customRender;
			});
		<#elseif data.mobModelName == "Silverfish">
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				RenderLiving customRender = new RenderLiving(renderManager, new ModelSilverfish(), ${data.modelShadowSize}f) {
					protected ResourceLocation getEntityTexture(Entity entity) {
		    			return new ResourceLocation("${modid}:textures/${data.mobModelTexture}");
					}
				};
				customRender.addLayer(new net.minecraft.client.renderer.entity.layers.LayerHeldItem(customRender));
				return customRender;
			});
		<#elseif !data.isBuiltInModel()>
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new RenderLiving(renderManager, new ${data.mobModelName}(), ${data.modelShadowSize}f) {
						protected ResourceLocation getEntityTexture(Entity entity) {
		    				return new ResourceLocation("${modid}:textures/${data.mobModelTexture}");
						}
					};
			});
		<#else>
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				RenderBiped customRender = new RenderBiped(renderManager, new ModelBiped(), ${data.modelShadowSize}f) {
					protected ResourceLocation getEntityTexture(Entity entity) {
						return new ResourceLocation("${modid}:textures/${data.mobModelTexture}");
					}
				};
				customRender.addLayer(new net.minecraft.client.renderer.entity.layers.LayerBipedArmor(customRender){
					protected void initArmor() {
						this.modelLeggings = new ModelBiped(0.5f);
						this.modelArmor = new ModelBiped(1);
					}
				});
				return customRender;
			});
		</#if>


		<#if data.ranged >
		RenderingRegistry.registerEntityRenderingHandler(EntityArrowCustom.class, renderManager -> {
			return new RenderSnowball<EntityArrowCustom>(renderManager, null, Minecraft.getMinecraft().getRenderItem()){
				public ItemStack getStackToRender(EntityArrowCustom entity) {
		    	<#if !data.rangedAttackItem.isEmpty()>
		    		return ${mappedMCItemToItemStackCode(data.rangedAttackItem, 1)};
		    	<#else>
		    		return new ItemStack(Items.ARROW);
		    	</#if>
				}
			};
		});
		</#if>
	}

	<#assign extendsClass = "Mob">
	<#if data.aiBase != "(none)" >
	    <#assign extendsClass = data.aiBase>
	<#else>
	    <#assign extendsClass = data.mobBehaviourType>
	</#if>
	<#if data.breedable >
	    <#assign extendsClass = "Animal">
	</#if>

	public static class EntityCustom extends Entity${extendsClass}<#if data.ranged > implements IRangedAttackMob</#if> {

		public EntityCustom(World world) {
			super(world);
			setSize(${data.modelWidth}f, ${data.modelHeight}f);
			experienceValue = ${data.xpAmount};
			this.isImmuneToFire = ${data.immuneToFire};
			setNoAI(!${data.hasAI});

			<#if data.mobLabel?has_content >
                setCustomNameTag("${data.mobLabel}" );
                setAlwaysRenderNameTag(true);
            </#if>

			<#if !data.doesDespawnWhenIdle>
				enablePersistence();
            </#if>

			<#if !data.equipmentMainHand.isEmpty()>
            this.setItemStackToSlot(
					EntityEquipmentSlot.MAINHAND, ${mappedMCItemToItemStackCode(data.equipmentMainHand, 1)});
            </#if>
            <#if !data.equipmentOffHand.isEmpty()>
            this.setItemStackToSlot(
					EntityEquipmentSlot.OFFHAND, ${mappedMCItemToItemStackCode(data.equipmentOffHand, 1)});
            </#if>
            <#if !data.equipmentHelmet.isEmpty()>
            this.setItemStackToSlot(EntityEquipmentSlot.HEAD, ${mappedMCItemToItemStackCode(data.equipmentHelmet, 1)});
            </#if>
            <#if !data.equipmentBody.isEmpty()>
            this.setItemStackToSlot(EntityEquipmentSlot.CHEST, ${mappedMCItemToItemStackCode(data.equipmentBody, 1)});
            </#if>
            <#if !data.equipmentLeggings.isEmpty()>
            this.setItemStackToSlot(
					EntityEquipmentSlot.LEGS, ${mappedMCItemToItemStackCode(data.equipmentLeggings, 1)});
            </#if>
            <#if !data.equipmentBoots.isEmpty()>
            this.setItemStackToSlot(EntityEquipmentSlot.FEET, ${mappedMCItemToItemStackCode(data.equipmentBoots, 1)});
            </#if>

			<#if data.flyingMob>
			this.navigator = new PathNavigateFlying(this, this.world);
			this.moveHelper = new EntityFlyHelper(this);
			</#if>
		}

		<#if data.hasAI>
		@Override protected void initEntityAI() {
			super.initEntityAI();

			<#if aicode??>
				${aicode}
			</#if>

            <#if data.breedable >
                this.tasks.addTask(3, new EntityAIMate(this, 1.0D));
			</#if>

            <#if data.ranged >
                this.tasks.addTask(1, new EntityAIAttackRanged(this, 1.25D, 20, 10.0F));
			</#if>
		}
		</#if>

		@Override public EnumCreatureAttribute getCreatureAttribute() {
			return EnumCreatureAttribute.${data.mobCreatureType};
		}

		<#if !data.doesDespawnWhenIdle>
		@Override protected boolean canDespawn() {
			return false;
		}
        </#if>

		@Override protected Item getDropItem() {
			<#if !data.mobDrop.isEmpty()>
                return ${mappedMCItemToItem(data.mobDrop)};
            <#else>
                return null;
            </#if>
		}

		@Override public net.minecraft.util.SoundEvent getAmbientSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
					.getObject(new ResourceLocation("${data.livingSound}"));
		}

		@Override public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
					.getObject(new ResourceLocation("${data.hurtSound}"));
		}

		@Override public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
					.getObject(new ResourceLocation("${data.deathSound}"));
		}

		@Override protected float getSoundVolume() {
			return 1.0F;
		}

		<#if hasProcedure(data.onStruckByLightning)>
		@Override public void onStruckByLightning(EntityLightningBolt entityLightningBolt) {
			super.onStruckByLightning(entityLightningBolt);
			int x = (int) this.posX;
			int y = (int) this.posY;
			int z = (int) this.posZ;
			Entity entity = this;
			<@procedureOBJToCode data.onStruckByLightning/>
		}
        </#if>

		<#if hasProcedure(data.whenMobFalls) || data.flyingMob>
		@Override public void fall(float l, float d) {
			<#if !data.flyingMob >
			super.fall(l, d);
			</#if>
			<#if hasProcedure(data.whenMobFalls) >
				int x = (int) this.posX;
				int y = (int) this.posY;
				int z = (int) this.posZ;
				Entity entity = this;
				<@procedureOBJToCode data.whenMobFalls/>
			</#if>
		}
        </#if>

		<#if hasProcedure(data.whenMobIsHurt) || data.immuneToArrows || data.immuneToFallDamage
			|| data.immuneToCactus || data.immuneToDrowning || data.immuneToLightning || data.immuneToPotions || data.immuneToPlayer>
		@Override public boolean attackEntityFrom(DamageSource source, float amount) {
			<#if hasProcedure(data.whenMobIsHurt)>
				int x = (int) this.posX;
				int y = (int) this.posY;
				int z = (int) this.posZ;
				Entity entity = this;
				<@procedureOBJToCode data.whenMobIsHurt/>
			</#if>
			<#if data.immuneToArrows>
				if (source.getImmediateSource() instanceof EntityArrow)
					return false;
			</#if>
			<#if data.immuneToPlayer>
				if (source.getImmediateSource() instanceof EntityPlayer)
					return false;
			</#if>
			<#if data.immuneToPotions>
				if (source.getImmediateSource() instanceof EntityPotion)
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
			return super.attackEntityFrom(source, amount);
		}
        </#if>

		<#if hasProcedure(data.whenMobDies)>
		@Override public void onDeath(DamageSource source) {
			super.onDeath(source);
			int x = (int) this.posX;
			int y = (int) this.posY;
			int z = (int) this.posZ;
			Entity entity = this;
			<@procedureOBJToCode data.whenMobDies/>
		}
        </#if>

		<#if hasProcedure(data.onInitialSpawn)>
		@Override public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
			IEntityLivingData retval = super.onInitialSpawn(difficulty, livingdata);
			int x = (int) this.posX;
			int y = (int) this.posY;
			int z = (int) this.posZ;
			Entity entity = this;
			<@procedureOBJToCode data.onInitialSpawn/>
			return retval;
		}
        </#if>

		<#if hasProcedure(data.onRightClickedOn) || data.ridable>
		@Override public boolean processInteract(EntityPlayer entity, EnumHand hand) {
			super.processInteract(entity, hand);
			<#if data.ridable >
            entity.startRiding(this);
            </#if>
			int x = (int) this.posX;
			int y = (int) this.posY;
			int z = (int) this.posZ;
			ItemStack itemstack = entity.getHeldItem(hand);
			<@procedureOBJToCode data.onRightClickedOn/>
			return true;
		}
        </#if>

		<#if hasProcedure(data.whenThisMobKillsAnother)>
		@Override public void onKillEntity(EntityLivingBase entity) {
			super.onKillEntity(entity);
			int x = (int) this.posX;
			int y = (int) this.posY;
			int z = (int) this.posZ;
			<@procedureOBJToCode data.whenThisMobKillsAnother/>
		}
        </#if>

		<#if hasProcedure(data.onMobTickUpdate)>
		@Override public void onEntityUpdate() {
			super.onEntityUpdate();
			int x = (int) this.posX;
			int y = (int) this.posY;
			int z = (int) this.posZ;
			Entity entity = this;
			<@procedureOBJToCode data.onMobTickUpdate/>
		}
        </#if>

		<#if hasProcedure(data.onPlayerCollidesWith)>
		@Override public void onCollideWithPlayer(EntityPlayer entity) {
			super.onCollideWithPlayer(entity);
			int x = (int) this.posX;
			int y = (int) this.posY;
			int z = (int) this.posZ;
			<@procedureOBJToCode data.onPlayerCollidesWith/>
		}
        </#if>

        @Override protected void applyEntityAttributes() {
			super.applyEntityAttributes();

			if (this.getEntityAttribute(SharedMonsterAttributes.ARMOR) != null)
				this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(${data.armorBaseValue}D);
			if (this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null)
				this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(${data.movementSpeed}D);
			if (this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH) != null)
				this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(${data.health}D);
			if (this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null)
				this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(${data.attackStrength}D);

			<#if data.flyingMob>
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
			this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(${data.movementSpeed});
			</#if>
		}

        <#if data.ranged >
            @Override public void setSwingingArms(boolean swingingArms) {
			}

		    public void attackEntityWithRangedAttack(EntityLivingBase target, float flval) {
				EntityArrowCustom entityarrow = new EntityArrowCustom(this.world, this);
				double d0 = target.posY + (double) target.getEyeHeight() - 1.1;
				double d1 = target.posX - this.posX;
				double d3 = target.posZ - this.posZ;
				entityarrow.shoot(d1, d0 - entityarrow.posY + (double) MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F, d3, 1.6F, 12.0F);
				this.world.spawnEntity(entityarrow);
			}
        </#if>

		<#if data.breedable >
            @Override public EntityCustom createChild(EntityAgeable ageable) {
				return new EntityCustom(world);
			}

			@Override public float getEyeHeight() {
				return this.isChild() ? this.height : 1.3F;
			}

			@Override public boolean isBreedingItem(ItemStack stack) {
				if (stack == null)
					return false;

            	<#list data.breedTriggerItems as breedTriggerItem>
                    <#if hasMetadata(breedTriggerItem)>
			        if (${mappedMCItemToItemStackCode(breedTriggerItem,1)}.
						getItem() == stack.getItem() && ${mappedMCItemToItemStackCode(breedTriggerItem,1)}.
						getMetadata() == stack.getMetadata())
			        	return true;
                    <#else>
                    if (${mappedMCItemToItemStackCode(breedTriggerItem,1)}.getItem() == stack.getItem())
						return true;
                    </#if>
                </#list>

				return false;
			}
        </#if>

		<#if data.waterMob>
		@Override public boolean canBreatheUnderwater() {
        	return true;
    	}

    	@Override public boolean getCanSpawnHere() {
        	return true;
    	}

    	@Override public boolean isNotColliding() {
        	return this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this);
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

		   private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(),
					BossInfo.Color.${data.bossBarColor}, BossInfo.Overlay.${data.bossBarType});

		   @Override public void addTrackingPlayer(EntityPlayerMP player) {
				super.addTrackingPlayer(player);
				this.bossInfo.addPlayer(player);
			}

		   @Override public void removeTrackingPlayer(EntityPlayerMP player) {
				super.removeTrackingPlayer(player);
				this.bossInfo.removePlayer(player);
			}
		</#if>


		<#if data.flyingMob || data.isBoss>
		@Override public void onUpdate() {
			super.onUpdate();

			<#if data.flyingMob>
			this.setNoGravity(true);
			</#if>

			<#if data.isBoss>
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
			</#if>
		}
		</#if>

        <#if data.canControlForward || data.canControlStrafe>
            @Override public void travel(float ti, float tj, float tk) {
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

					if (entity instanceof EntityLivingBase) {
						this.setAIMoveSpeed((float) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());

						<#if data.canControlForward>
							float forward = ((EntityLivingBase) entity).moveForward;
						<#else>
							float forward = 0;
						</#if>

						<#if data.canControlStrafe>
							float strafe = ((EntityLivingBase) entity).moveStrafing;
						<#else>
							float strafe = 0;
						</#if>

						super.travel(strafe, 0, forward);
					}

					this.prevLimbSwingAmount = this.limbSwingAmount;
					double d1 = this.posX - this.prevPosX;
					double d0 = this.posZ - this.prevPosZ;
					float f1 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
					if (f1 > 1.0F) f1 = 1.0F;
					this.limbSwingAmount += (f1 - this.limbSwingAmount) * 0.4F;
					this.limbSwing += this.limbSwingAmount;
					return;
				}
				this.stepHeight = 0.5F;
				this.jumpMovementFactor = 0.02F;
				</#if>

				super.travel(ti, tj, tk);
			}
        </#if>

		<#if data.flyingMob>
		@Override protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
   		}

   		@Override public void setNoGravity(boolean ignored) {
			super.setNoGravity(true);
		}
        </#if>

        <#if data.spawnParticles>
        public void onLivingUpdate() {
			super.onLivingUpdate();
			int i = (int) this.posX;
			int j = (int) this.posY;
			int k = (int) this.posZ;
			Random random = this.rand;
            <@particles data.particleSpawningShape data.particleToSpawn data.particleSpawningRadious data.particleAmount data.particleSpawningCondition/>
		}
        </#if>

	}

	<#if data.ranged >
    public static class EntityArrowCustom extends EntityTippedArrow {

		public EntityArrowCustom(World a) {
			super(a);
		}

		public EntityArrowCustom(World worldIn, double x, double y, double z) {
			super(worldIn, x, y, z);
		}

		public EntityArrowCustom(World worldIn, EntityLivingBase shooter) {
			super(worldIn, shooter);
		}
	}
	</#if>

	<#if data.getModelCode()?? && !data.isBuiltInModel() >
    	${data.getModelCode()}
	</#if>

}
<#-- @formatter:on -->