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

package ${package}.entity;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public static class ${name}Entity extends AbstractArrowEntity implements IRendersAsItem {

	public ${name}Entity(FMLPlayMessages.SpawnEntity packet, World world) {
		super(arrow, world);
	}

	public ${name}Entity(EntityType<? extends ${name}Entity> type, World world) {
		super(type, world);
	}

	public ${name}Entity(EntityType<? extends ${name}Entity> type, double x, double y, double z, World world) {
		super(type, x, y, z, world);
	}

	public ${name}Entity(EntityType<? extends ${name}Entity> type, LivingEntity entity, World world) {
		super(type, entity, world);
	}

	@Override public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override @OnlyIn(Dist.CLIENT) public ItemStack getItem() {
		<#if !data.bulletItemTexture.isEmpty()>
		return ${mappedMCItemToItemStackCode(data.bulletItemTexture, 1)};
    	<#else>
		return null;
    	</#if>
	}

	@Override protected ItemStack getArrowStack() {
		<#if !data.ammoItem.isEmpty()>
		return ${mappedMCItemToItemStackCode(data.ammoItem, 1)};
    	<#else>
		return null;
    	</#if>
	}

	<#if hasProcedure(data.onBulletHitsPlayer)>
	@Override public void onCollideWithPlayer(PlayerEntity entity) {
		super.onCollideWithPlayer(entity);
		Entity sourceentity = this.func_234616_v_();
		double x = this.getPosX();
		double y = this.getPosY();
		double z = this.getPosZ();
		World world = this.world;
		Entity imediatesourceentity = this;
		<@procedureOBJToCode data.onBulletHitsPlayer/>
	}
    </#if>

	@Override protected void arrowHit(LivingEntity entity) {
		super.arrowHit(entity);
		entity.setArrowCountInEntity(entity.getArrowCountInEntity() - 1); <#-- #53957 -->
		<#if hasProcedure(data.onBulletHitsEntity)>
			Entity sourceentity = this.func_234616_v_();
			double x = this.getPosX();
			double y = this.getPosY();
			double z = this.getPosZ();
			World world = this.world;
			Entity imediatesourceentity = this;
            <@procedureOBJToCode data.onBulletHitsEntity/>
        </#if>
	}

	@Override public void tick() {
		super.tick();
		double x = this.getPosX();
		double y = this.getPosY();
		double z = this.getPosZ();
		World world = this.world;
		Entity entity = this.func_234616_v_();
		Entity imediatesourceentity = this;
		<@procedureOBJToCode data.onBulletFlyingTick/>
		if (this.inGround) {
			<@procedureOBJToCode data.onBulletHitsBlock/>
			this.remove();
		}
	}

	public static ${name}Entity shoot(World world, LivingEntity entity, Random random, float power, double damage, int knockback) {
		${name}Entity entityarrow = new ${name}Entity(arrow, entity, world);
		entityarrow.shoot(entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power * 2, 0);
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

	public static ${name}Entity shoot(LivingEntity entity, LivingEntity target) {
		${name}Entity entityarrow = new ${name}Entity(arrow, entity, entity.world);
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

<#-- @formatter:on -->