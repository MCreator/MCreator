<#-- @formatter:off -->
<#include "mcitems.ftl">
<#include "procedures.java.ftl">

package ${package}.item;

@Elements${JavaModName}.ModElement.Tag public class Item${name} extends Elements${JavaModName}.ModElement{

	@GameRegistry.ObjectHolder("${modid}:${registryname}")
	public static final Item block = null;

	public static final int ENTITYID = ${data.getModElement().getID(0, "entity")};

	public Item${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityArrowCustom.class)
								.id(new ResourceLocation("${modid}", "entitybullet${registryname}"), ENTITYID)
								.name("entitybullet${registryname}").tracker(64, 1, true).build());
	}

	@Override @SideOnly(Side.CLIENT) public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("${modid}:${registryname}" ,"inventory"));
	}

	@SideOnly(Side.CLIENT) @Override public void preInit(FMLPreInitializationEvent event) {
		<#if data.bulletModel != "Default">
		RenderingRegistry.registerEntityRenderingHandler(EntityArrowCustom.class, renderManager -> {
			return new RenderCustom(renderManager);
		});
		<#else>
		RenderingRegistry.registerEntityRenderingHandler(EntityArrowCustom.class, renderManager -> {
			return new RenderSnowball(renderManager, ${mappedMCItemToItem(data.bulletItemTexture)}, Minecraft.getMinecraft().getRenderItem());
		});
		</#if>
	}

	public static class RangedItem extends Item {

		public RangedItem() {
			super();
			setMaxDamage(${data.usageCount});
			setFull3D();
			setUnlocalizedName("${registryname}");
			setRegistryName("${registryname}");
			maxStackSize = ${data.stackSize};
			setCreativeTab(${data.creativeTab});

			<#macro arrowShootCode>
				<#if !data.ammoItem.isEmpty()>
				int slotID = -1;
				for (int i = 0; i < entity.inventory.mainInventory.size(); i++) {
					ItemStack stack = entity.inventory.mainInventory.get(i);
					if(stack != null && stack.getItem() == ${mappedMCItemToItem(data.ammoItem)}
							&& stack.getMetadata()== ${mappedMCItemToItemStackCode(data.ammoItem, 1)}.getMetadata()) {
						slotID = i;
						break;
					}
				}

				if (entity.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, itemstack) > 0 || slotID != -1) {
				</#if>
				float power = ${data.bulletPower}f;
				EntityArrowCustom entityarrow = new EntityArrowCustom(world, entity);
				entityarrow.shoot(entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power * 2, 0);
				entityarrow.setSilent(true);
				entityarrow.setIsCritical(${data.bulletParticles});
				entityarrow.setDamage(${data.bulletDamage});
				entityarrow.setKnockbackStrength(${data.bulletKnockback});
				<#if data.bulletIgnitesFire>
					entityarrow.setFire(100);
                </#if>

				itemstack.damageItem(1, entity);

				int x = (int) entity.posX;
				int y = (int) entity.posY;
				int z = (int) entity.posZ;
				world.playSound((EntityPlayer) null, (double) x, (double) y, (double) z,
						(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
								.getObject(new ResourceLocation(("${data.actionSound}"))), SoundCategory.NEUTRAL, 1,
						1f / (itemRand.nextFloat() * 0.5f + 1f) + (power / 2));

				<#if !data.ammoItem.isEmpty()>
				if (entity.capabilities.isCreativeMode) {
					entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
				} else {
					if (${mappedMCItemToItemStackCode(data.ammoItem, 1)}.isItemStackDamageable()){
						ItemStack stack = entity.inventory.getStackInSlot(slotID);
						if (stack.attemptDamageItem(1, itemRand, entity)) {
							stack.shrink(1);
							stack.setItemDamage(0);
						}
					} else{
						entity.inventory.clearMatchingItems(${mappedMCItemToItem(data.ammoItem)}, -1, 1, null);
					}
				}
				<#else>
				entityarrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
				</#if>

				if (!world.isRemote)
					world.spawnEntity(entityarrow);

				<@procedureOBJToCode data.onRangedItemUsed/>

				<#if !data.ammoItem.isEmpty()>
				}
				</#if>
            </#macro>

		}

		<#if data.enableMeleeDamage>
			@Override public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
				Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
				if (slot == EntityEquipmentSlot.MAINHAND) {
					multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", (double) ${data.damageVsEntity - 4}, 0));
					multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Ranged item modifier", -2.4, 0));
				}
				return multimap;
			}
		</#if>

		<#if data.shootConstantly>
			@Override public void onUsingTick(ItemStack itemstack, EntityLivingBase entityLivingBase, int count) {
				World world = entityLivingBase.world;
				if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP) {
					EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
					<@arrowShootCode/>
					entity.stopActiveHand();
				}
			}
        <#else>
			@Override
			public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase,
					int timeLeft) {
				if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP) {
					EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
					<@arrowShootCode/>
				}
			}
        </#if>

		@Override public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			entity.setActiveHand(hand);
			return new ActionResult(EnumActionResult.SUCCESS, entity.getHeldItem(hand));
		}

		@Override public EnumAction getItemUseAction(ItemStack itemstack) {
			return EnumAction.BOW;
		}

		@Override public int getMaxItemUseDuration(ItemStack itemstack) {
			return 72000;
		}

		<#if data.hasGlow>
		@Override @SideOnly(Side.CLIENT) public boolean hasEffect(ItemStack itemstack) {
			return true;
		}
        </#if>

	}

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

		<#if hasProcedure(data.onBulletHitsPlayer)>
		@Override public void onCollideWithPlayer(EntityPlayer entity) {
			super.onCollideWithPlayer(entity);
			int x = (int) this.posX;
			int y = (int) this.posY;
			int z = (int) this.posZ;
			World world = this.world;
			<@procedureOBJToCode data.onBulletHitsPlayer/>
		}
        </#if>

		@Override protected void arrowHit(EntityLivingBase entity) {
			super.arrowHit(entity);
			entity.setArrowCountInEntity(entity.getArrowCountInEntity() - 1); <#-- #53957 -->
			<#if hasProcedure(data.onBulletHitsEntity)>
				int x = (int) this.posX;
				int y = (int) this.posY;
				int z = (int) this.posZ;
				World world = this.world;
				<@procedureOBJToCode data.onBulletHitsEntity/>
			</#if>
		}

		@Override public void onUpdate() {
			super.onUpdate();
			int x = (int) this.posX;
			int y = (int) this.posY;
			int z = (int) this.posZ;
			World world = this.world;
			Entity entity = (Entity) shootingEntity;
			<@procedureOBJToCode data.onBulletFlyingTick/>
			if (this.inGround) {
			    <@procedureOBJToCode data.onBulletHitsBlock/>
				this.world.removeEntity(this);
			}
		}

	}

<#if data.bulletModel != "Default">
		public static class RenderCustom extends Render {
			private static final ResourceLocation texture = new ResourceLocation("${modid}:textures/${data.customBulletModelTexture}");

			public RenderCustom(RenderManager renderManager) {
				super(renderManager);
				shadowSize = 0.1f;
			}

			@Override public void doRender(Entity bullet, double d, double d1, double d2, float f, float f1) {
				bindEntityTexture(bullet);
				GL11.glPushMatrix();
				GL11.glTranslatef((float) d, (float) d1, (float) d2);
				GL11.glRotatef(f, 0, 1, 0);
				GL11.glRotatef(90f - bullet.prevRotationPitch - (bullet.rotationPitch - bullet.prevRotationPitch) * f1, 1, 0, 0);
				ModelBase model = new ${data.bulletModel}();
				model.render(bullet, 0, 0, 0, 0, 0, 0.0625f);
				GL11.glPopMatrix();
			}

			@Override protected ResourceLocation getEntityTexture(Entity entity) {
				return texture;
			}
		}

	${data.getModelCode()}

</#if>

}

<#-- @formatter:on -->