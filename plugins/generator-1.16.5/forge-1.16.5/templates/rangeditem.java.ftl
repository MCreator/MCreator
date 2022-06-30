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

package ${package}.item;

import net.minecraft.entity.ai.attributes.Attributes;

@${JavaModName}Elements.ModElement.Tag
public class ${name}Item extends ${JavaModName}Elements.ModElement{

	@ObjectHolder("${modid}:${registryname}")
	public static final Item block = null;

	public static final EntityType arrow = (EntityType.Builder.<ArrowCustomEntity>create(ArrowCustomEntity::new, EntityClassification.MISC)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(ArrowCustomEntity::new)
			.size(0.5f, 0.5f)).build("projectile_${registryname}").setRegistryName("projectile_${registryname}");

	public ${name}Item(${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});

		FMLJavaModLoadingContext.get().getModEventBus().register(new ${name}Renderer.ModelRegisterHandler());
	}

	@Override public void initElements() {
		elements.items.add(() -> new ItemRanged());
		elements.entities.add(() -> arrow);
	}

	public static class ItemRanged extends Item {

		public ItemRanged() {
			super(new Item.Properties()
					.group(${data.creativeTab})
					<#if data.usageCount != 0>.maxDamage(${data.usageCount})
					<#else>.maxStackSize(${data.stackSize})</#if>);

			setRegistryName("${registryname}");
		}

		@Override public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity entity, Hand hand) {
			entity.setActiveHand(hand);
			return new ActionResult(ActionResultType.SUCCESS, entity.getHeldItem(hand));
		}

		<#if hasProcedure(data.onEntitySwing)>
		@Override public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
			boolean retval = super.onEntitySwing(itemstack, entity);
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();
			World world = entity.world;
			<@procedureOBJToCode data.onEntitySwing/>
			return retval;
		}
		</#if>

		<#if data.specialInfo?has_content>
		@Override public void addInformation(ItemStack itemstack, World world, List<ITextComponent> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			<#list data.specialInfo as entry>
			list.add(new StringTextComponent("${JavaConventions.escapeStringForJava(entry)}"));
			</#list>
		}
		</#if>

		@Override public UseAction getUseAction(ItemStack itemstack) {
			return UseAction.${data.animation?upper_case};
		}

		@Override public int getUseDuration(ItemStack itemstack) {
			return 72000;
		}

		<#if data.hasGlow>
		@Override @OnlyIn(Dist.CLIENT) public boolean hasEffect(ItemStack itemstack) {
		    <#if hasProcedure(data.glowCondition)>
			PlayerEntity entity = Minecraft.getInstance().player;
			World world = entity.world;
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();
        	if (!(<@procedureOBJToConditionCode data.glowCondition/>)) {
        	    return false;
        	}
        	</#if>
			return true;
		}
        </#if>

		<#if data.enableMeleeDamage>
			@Override public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot) {
				if (slot == EquipmentSlotType.MAINHAND) {
					ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
					builder.putAll(super.getAttributeModifiers(slot));
					builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", (double) ${data.damageVsEntity - 2}, AttributeModifier.Operation.ADDITION));
					builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Ranged item modifier", -2.4, AttributeModifier.Operation.ADDITION));
					return builder.build();
				}
				return super.getAttributeModifiers(slot);
			}
		</#if>

		<#if data.shootConstantly>
			@Override public void onUsingTick(ItemStack itemstack, LivingEntity entityLiving, int count) {
				World world = entityLiving.world;
				if (!world.isRemote && entityLiving instanceof ServerPlayerEntity) {
					ServerPlayerEntity entity = (ServerPlayerEntity) entityLiving;
					double x = entity.getPosX();
					double y = entity.getPosY();
					double z = entity.getPosZ();
					if (<@procedureOBJToConditionCode data.useCondition/>) {
						<@arrowShootCode/>
						entity.stopActiveHand();
					}
				}
			}
        <#else>
			@Override
			public void onPlayerStoppedUsing(ItemStack itemstack, World world, LivingEntity entityLiving, int timeLeft) {
				if (!world.isRemote && entityLiving instanceof ServerPlayerEntity) {
					ServerPlayerEntity entity = (ServerPlayerEntity) entityLiving;
					double x = entity.getPosX();
					double y = entity.getPosY();
					double z = entity.getPosZ();
					if (<@procedureOBJToConditionCode data.useCondition/>) {
						<@arrowShootCode/>
					}
				}
			}
        </#if>

	}

	@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
	public static class ArrowCustomEntity extends AbstractArrowEntity implements IRendersAsItem {

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
			<#if !data.bulletItemTexture.isEmpty()>
			return ${mappedMCItemToItemStackCode(data.bulletItemTexture, 1)};
			<#else>
			return ItemStack.EMPTY;
			</#if>
		}

		@Override protected ItemStack getArrowStack() {
			<#if !data.ammoItem.isEmpty()>
			return ${mappedMCItemToItemStackCode(data.ammoItem, 1)};
			<#else>
			return ItemStack.EMPTY;
			</#if>
		}

		@Override protected void arrowHit(LivingEntity entity) {
			super.arrowHit(entity);
			entity.setArrowCountInEntity(entity.getArrowCountInEntity() - 1); <#-- #53957 -->
		}

		<#if hasProcedure(data.onBulletHitsPlayer)>
		@Override public void onCollideWithPlayer(PlayerEntity entity) {
			super.onCollideWithPlayer(entity);
			Entity sourceentity = this.func_234616_v_();
			Entity immediatesourceentity = this;
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			World world = this.world;
			<@procedureOBJToCode data.onBulletHitsPlayer/>
		}
        </#if>

		<#if hasProcedure(data.onBulletHitsEntity)>
		@Override public void onEntityHit(EntityRayTraceResult entityRayTraceResult) {
			super.onEntityHit(entityRayTraceResult);
			Entity entity = entityRayTraceResult.getEntity();
			Entity sourceentity = this.func_234616_v_();
			Entity immediatesourceentity = this;
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			World world = this.world;
			<@procedureOBJToCode data.onBulletHitsEntity/>
		}
		</#if>

		<#if hasProcedure(data.onBulletHitsBlock)>
		@Override public void func_230299_a_(BlockRayTraceResult blockRayTraceResult) {
			super.func_230299_a_(blockRayTraceResult);
			double x = blockRayTraceResult.getPos().getX();
			double y = blockRayTraceResult.getPos().getY();
			double z = blockRayTraceResult.getPos().getZ();
			World world = this.world;
			Entity entity = this.func_234616_v_();
			Entity immediatesourceentity = this;
			<@procedureOBJToCode data.onBulletHitsBlock/>
		}
		</#if>

		@Override public void tick() {
			super.tick();

			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			World world = this.world;
			Entity entity = this.func_234616_v_();
			Entity immediatesourceentity = this;
			<@procedureOBJToCode data.onBulletFlyingTick/>

			if (this.inGround)
				this.remove();
		}

	}

	public static ArrowCustomEntity shoot(World world, LivingEntity entity, Random random, float power, double damage, int knockback) {
		ArrowCustomEntity entityarrow = new ArrowCustomEntity(arrow, entity, world);
		entityarrow.shoot(entity.getLook(1).x, entity.getLook(1).y, entity.getLook(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setIsCritical(${data.bulletParticles});
		entityarrow.setDamage(damage);
		entityarrow.setKnockbackStrength(knockback);
		<#if data.bulletIgnitesFire>
			entityarrow.setFire(100);
		</#if>
		world.addEntity(entityarrow);

		double x = entity.getPosX();
		double y = entity.getPosY();
		double z = entity.getPosZ();
		world.playSound((PlayerEntity) null, (double) x, (double) y, (double) z, (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS
				.getValue(new ResourceLocation("${data.actionSound}")), SoundCategory.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));

		return entityarrow;
	}

	public static ArrowCustomEntity shoot(LivingEntity entity, LivingEntity target) {
		ArrowCustomEntity entityarrow = new ArrowCustomEntity(arrow, entity, entity.world);
		double d0 = target.getPosY() + (double) target.getEyeHeight() - 1.1;
		double d1 = target.getPosX() - entity.getPosX();
		double d3 = target.getPosZ() - entity.getPosZ();
		entityarrow.shoot(d1, d0 - entityarrow.getPosY() + (double) MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F, d3, ${data.bulletPower}f * 2, 12.0F);

		entityarrow.setSilent(true);
		entityarrow.setDamage(${data.bulletDamage});
		entityarrow.setKnockbackStrength(${data.bulletKnockback});
		entityarrow.setIsCritical(${data.bulletParticles});
		<#if data.bulletIgnitesFire>
			entityarrow.setFire(100);
		</#if>
		entity.world.addEntity(entityarrow);

		double x = entity.getPosX();
		double y = entity.getPosY();
		double z = entity.getPosZ();
		entity.world.playSound((PlayerEntity) null, (double) x, (double) y, (double) z, (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS
				.getValue(new ResourceLocation("${data.actionSound}")), SoundCategory.PLAYERS, 1, 1f / (new Random().nextFloat() * 0.5f + 1));

		return entityarrow;
	}

}

<#macro arrowShootCode>
	<#if !data.ammoItem.isEmpty()>
	ItemStack stack = ShootableItem.getHeldAmmo(entity, e -> e.getItem() == ${mappedMCItemToItem(data.ammoItem)});

	if(stack == ItemStack.EMPTY) {
		for (int i = 0; i < entity.inventory.mainInventory.size(); i++) {
			ItemStack teststack = entity.inventory.mainInventory.get(i);
			if(teststack != null && teststack.getItem() == ${mappedMCItemToItem(data.ammoItem)}) {
				stack = teststack;
				break;
			}
		}
	}

	if (entity.abilities.isCreativeMode || stack != ItemStack.EMPTY) {
	</#if>

	ArrowCustomEntity entityarrow = shoot(world, entity, random, ${data.bulletPower}f, ${data.bulletDamage}, ${data.bulletKnockback});

	itemstack.damageItem(1, entity, e -> e.sendBreakAnimation(entity.getActiveHand()));

	<#if !data.ammoItem.isEmpty()>
	if (entity.abilities.isCreativeMode) {
		entityarrow.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
	} else {
		if (${mappedMCItemToItemStackCode(data.ammoItem, 1)}.isDamageable()){
			if (stack.attemptDamageItem(1, random, entity)) {
				stack.shrink(1);
				stack.setDamage(0);
            	if (stack.isEmpty())
               		entity.inventory.deleteStack(stack);
			}
		} else{
			stack.shrink(1);
            if (stack.isEmpty())
               entity.inventory.deleteStack(stack);
		}
	}
	<#else>
	entityarrow.pickupStatus = AbstractArrowEntity.PickupStatus.DISALLOWED;
	</#if>

	<#if hasProcedure(data.onRangedItemUsed)>
		<@procedureOBJToCode data.onRangedItemUsed/>
	</#if>

	<#if !data.ammoItem.isEmpty()>
	}
	</#if>
</#macro>

<#-- @formatter:on -->