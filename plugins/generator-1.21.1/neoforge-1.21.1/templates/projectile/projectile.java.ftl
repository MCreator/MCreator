<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2023, Pylo, opensource contributors
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

<#compress>
@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class ${name}Entity extends AbstractArrow implements ItemSupplier {

	public static final ItemStack PROJECTILE_ITEM = ${mappedMCItemToItemStackCode(data.projectileItem)};

	private int knockback = 0;

	public ${name}Entity(EntityType<? extends ${name}Entity> type, Level world) {
		super(type, world);
	}

	public ${name}Entity(EntityType<? extends ${name}Entity> type, double x, double y, double z, Level world, @Nullable ItemStack firedFromWeapon) {
		super(type, x, y, z, world, PROJECTILE_ITEM, firedFromWeapon);
	}

	public ${name}Entity(EntityType<? extends ${name}Entity> type, LivingEntity entity, Level world, @Nullable ItemStack firedFromWeapon) {
		super(type, entity, world, PROJECTILE_ITEM, firedFromWeapon);
	}

	@Override @OnlyIn(Dist.CLIENT) public ItemStack getItem() {
		return PROJECTILE_ITEM;
	}

	@Override protected ItemStack getDefaultPickupItem() {
		return ${mappedMCItemToItemStackCode(data.projectileItem)};
	}

	@Override protected void doPostHurtEffects(LivingEntity entity) {
		super.doPostHurtEffects(entity);
		entity.setArrowCount(entity.getArrowCount() - 1); <#-- #53957 -->
	}

	public void setKnockback(int knockback) {
		this.knockback = knockback;
	}

	@Override protected void doKnockback(LivingEntity livingEntity, DamageSource damageSource) {
		if (knockback > 0.0) {
			double d1 = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
			Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(knockback * 0.6 * d1);
			if (vec3.lengthSqr() > 0.0) {
				livingEntity.push(vec3.x, 0.1, vec3.z);
			}
		}
	}

	<#if (data.modelWidth > 0.5) || (data.modelHeight > 0.5)>
	@Nullable @Override protected EntityHitResult findHitEntity(Vec3 projectilePosition, Vec3 deltaPosition) {
		double d0 = Double.MAX_VALUE;
		Entity entity = null;
		AABB lookupBox = this.getBoundingBox().expandTowards(deltaPosition).inflate(1.0D);
		for (Entity entity1 : this.level().getEntities(this, lookupBox, this::canHitEntity)) {
			if (entity1 == this.getOwner()) continue;
			AABB aabb = entity1.getBoundingBox();
			if (aabb.intersects(lookupBox)) {
				double d1 = projectilePosition.distanceToSqr(projectilePosition);
				if (d1 < d0) {
					entity = entity1;
					d0 = d1;
				}
			}
		}
		return entity == null ? null : new EntityHitResult(entity);
	}
	</#if>

	<#if hasProcedure(data.onHitsPlayer)>
	@Override public void playerTouch(Player entity) {
		super.playerTouch(entity);
		<@procedureCode data.onHitsPlayer, {
			"x": "this.getX()",
			"y": "this.getY()",
			"z": "this.getZ()",
			"entity": "entity",
			"sourceentity": "this.getOwner()",
			"immediatesourceentity": "this",
			"world": "this.level()"
		}/>
	}
	</#if>

	<#if hasProcedure(data.onHitsEntity)>
	@Override public void onHitEntity(EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
		<@procedureCode data.onHitsEntity, {
			"x": "this.getX()",
			"y": "this.getY()",
			"z": "this.getZ()",
			"entity": "entityHitResult.getEntity()",
			"sourceentity": "this.getOwner()",
			"immediatesourceentity": "this",
			"world": "this.level()"
		}/>
	}
	</#if>

	<#if hasProcedure(data.onHitsBlock)>
	@Override public void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);
		<@procedureCode data.onHitsBlock, {
			"x": "blockHitResult.getBlockPos().getX()",
			"y": "blockHitResult.getBlockPos().getY()",
			"z": "blockHitResult.getBlockPos().getZ()",
			"entity": "this.getOwner()",
			"immediatesourceentity": "this",
			"world": "this.level()"
		}/>
	}
	</#if>

	@Override public void tick() {
		super.tick();

		<#if hasProcedure(data.onFlyingTick)>
			<@procedureCode data.onFlyingTick, {
				"x": "this.getX()",
				"y": "this.getY()",
				"z": "this.getZ()",
				"world": "this.level()",
				"entity": "this.getOwner()",
				"immediatesourceentity": "this"
			}/>
		</#if>

		if (this.inGround)
			this.discard();
	}

	public static ${name}Entity shoot(Level world, LivingEntity entity, RandomSource source) {
		return shoot(world, entity, source, ${data.power}f, ${data.damage}, ${data.knockback});
	}

	public static ${name}Entity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
		return shoot(world, entity, source, pullingPower * ${data.power}f, ${data.damage}, ${data.knockback});
	}

	public static ${name}Entity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
		${name}Entity entityarrow = new ${name}Entity(${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}.get(), entity, world, null);
		entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
		entityarrow.setSilent(true);
		entityarrow.setCritArrow(${data.showParticles});
		entityarrow.setBaseDamage(damage);
		entityarrow.setKnockback(knockback);
		<#if data.igniteFire>
			entityarrow.igniteForSeconds(100);
		</#if>
		world.addFreshEntity(entityarrow);

		<#if data.actionSound.toString()?has_content>
		world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), BuiltInRegistries.SOUND_EVENT
				.get(ResourceLocation.parse("${data.actionSound}")), SoundSource.PLAYERS, 1, 1f / (random.nextFloat() * 0.5f + 1) + (power / 2));
		</#if>

		return entityarrow;
	}

	public static ${name}Entity shoot(LivingEntity entity, LivingEntity target) {
		${name}Entity entityarrow = new ${name}Entity(${JavaModName}Entities.${data.getModElement().getRegistryNameUpper()}.get(), entity, entity.level(), null);
		double dx = target.getX() - entity.getX();
		double dy = target.getY() + target.getEyeHeight() - 1.1;
		double dz = target.getZ() - entity.getZ();
		entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, ${data.power}f * 2, 12.0F);

		entityarrow.setSilent(true);
		entityarrow.setBaseDamage(${data.damage});
		entityarrow.setKnockback(${data.knockback});
		entityarrow.setCritArrow(${data.showParticles});
		<#if data.igniteFire>
			entityarrow.igniteForSeconds(100);
		</#if>
		entity.level().addFreshEntity(entityarrow);

		<#if data.actionSound.toString()?has_content>
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), BuiltInRegistries.SOUND_EVENT
				.get(ResourceLocation.parse("${data.actionSound}")), SoundSource.PLAYERS, 1, 1f / (RandomSource.create().nextFloat() * 0.5f + 1));
		</#if>

		return entityarrow;
	}

}
</#compress>

<#-- @formatter:on -->