<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2021, Pylo, opensource contributors
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

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class ${name}Entity extends AbstractArrow implements ItemSupplier {

	public ${name}Entity(PlayMessages.SpawnEntity packet, Level world) {
		super(${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}.get(), world);
	}

	public ${name}Entity(EntityType<? extends ${name}Entity> type, Level world) {
		super(type, world);
	}

	public ${name}Entity(EntityType<? extends ${name}Entity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
	}

	public ${name}Entity(EntityType<? extends ${name}Entity> type, LivingEntity entity, Level world) {
		super(type, entity, world);
	}

	@Override public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override @OnlyIn(Dist.CLIENT) public ItemStack getItem() {
		<#if !data.bulletItemTexture.isEmpty()>
		return ${mappedMCItemToItemStackCode(data.bulletItemTexture, 1)};
		<#else>
		return ItemStack.EMPTY;
		</#if>
	}

	@Override protected ItemStack getPickupItem() {
		<#if !data.ammoItem.isEmpty()>
		return ${mappedMCItemToItemStackCode(data.ammoItem, 1)};
		<#else>
		return ItemStack.EMPTY;
		</#if>
	}

	@Override protected void doPostHurtEffects(LivingEntity entity) {
		super.doPostHurtEffects(entity);
		entity.setArrowCount(entity.getArrowCount() - 1); <#-- #53957 -->
	}

	<#if hasProcedure(data.onBulletHitsPlayer)>
	@Override public void playerTouch(Player entity) {
		super.playerTouch(entity);
		<@procedureCode data.onBulletHitsPlayer, {
			"x": "this.getX()",
			"y": "this.getY()",
			"z": "this.getZ()",
			"entity": "entity",
			"sourceentity": "this.getOwner()",
			"immediatesourceentity": "this",
			"world": "this.level"
		}/>
	}
	</#if>

	<#if hasProcedure(data.onBulletHitsEntity)>
	@Override public void onHitEntity(EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
		<@procedureCode data.onBulletHitsEntity, {
			"x": "this.getX()",
			"y": "this.getY()",
			"z": "this.getZ()",
			"entity": "entityHitResult.getEntity()",
			"sourceentity": "this.getOwner()",
			"immediatesourceentity": "this",
			"world": "this.level"
		}/>
	}
	</#if>

	<#if hasProcedure(data.onBulletHitsBlock)>
	@Override public void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);
		<@procedureCode data.onBulletHitsBlock, {
			"x": "blockHitResult.getBlockPos().getX()",
			"y": "blockHitResult.getBlockPos().getY()",
			"z": "blockHitResult.getBlockPos().getZ()",
			"entity": "this.getOwner()",
			"immediatesourceentity": "this",
			"world": "this.level"
		}/>
	}
	</#if>

	<#if hasProcedure(data.onBulletFlyingTick) || !data.preserveProjectiles>
	@Override public void tick() {
		super.tick();

		<#if hasProcedure(data.onBulletFlyingTick)>
			<@procedureCode data.onBulletFlyingTick, {
				"x": "this.getX()",
				"y": "this.getY()",
				"z": "this.getZ()",
				"world": "this.level",
				"entity": "this.getOwner()",
				"immediatesourceentity": "this"
			}/>
		</#if>

		<#if !data.preserveProjectiles>
		if (this.inGround)
			this.discard();
		</#if>
	}
	</#if>

	public static ${name}Entity shoot(Level world, LivingEntity entity, Random random, float power, double damage, int knockback) {
		${name}Entity entityarrow = new ${name}Entity(${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}.get(), entity, world);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setCritArrow(${data.bulletParticles});
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(knockback);
		<#if data.bulletIgnitesFire>
			entityarrow.setSecondsOnFire(100);
		</#if>
		world.addFreshEntity(entityarrow);

		world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ForgeRegistries.SOUND_EVENTS
				.getValue(new ResourceLocation("${data.actionSound}")), SoundSource.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));

		return entityarrow;
	}

	public static ${name}Entity shoot(LivingEntity entity, LivingEntity target) {
		${name}Entity entityarrow = new ${name}Entity(${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}.get(), entity, entity.level);
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, ${data.bulletPower}f * 2, 12.0F);

		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(${data.bulletDamage});
		entityarrow.setKnockback(${data.bulletKnockback});
		entityarrow.setCritArrow(${data.bulletParticles});
		<#if data.bulletIgnitesFire>
			entityarrow.setSecondsOnFire(100);
		</#if>
		entity.level.addFreshEntity(entityarrow);
		entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ForgeRegistries.SOUND_EVENTS
				.getValue(new ResourceLocation("${data.actionSound}")), SoundSource.PLAYERS, 1, 1f / (new Random().nextFloat() * 0.5f + 1));

		return entityarrow;
	}

	<#if data.ammoItem.isEmpty() && data.preserveProjectiles && data.pickupProjectiles>
	@Override
	protected boolean tryPickup(Player entity) {
		if (this.pickup.equals(AbstractArrow.Pickup.ALLOWED))
			return true;
			    return super.tryPickup(entity);
	}
	</#if>

}

<#-- @formatter:on -->