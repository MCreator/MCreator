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
<#include "procedures.java.ftl">

package ${package}.client.particle;

@OnlyIn(Dist.CLIENT) public class ${name}Particle extends TextureSheetParticle {

	public static ${name}ParticleProvider provider(SpriteSet spriteSet) {
		return new ${name}ParticleProvider(spriteSet);
	}

	public static class ${name}ParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public ${name}ParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ${name}Particle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;
	
	<#if data.angularVelocity != 0 || data.angularAcceleration != 0>
	private float angularVelocity;
	private float angularAcceleration;
	</#if>

	protected ${name}Particle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;

		this.setSize(${data.width}f, ${data.height}f);
		<#if data.scale != 1>this.quadSize *= ${data.scale}f;</#if>

		<#if (data.maxAgeDiff > 0)>
		this.lifetime = (int) Math.max(1, ${data.maxAge} + (this.random.nextInt(${data.maxAgeDiff * 2}) - ${data.maxAgeDiff}));
		<#else>
		this.lifetime = ${data.maxAge};
		</#if>

		this.gravity = ${data.gravity}f;
		this.hasPhysics = ${data.canCollide};

		this.xd = vx * ${data.speedFactor};
		this.yd = vy * ${data.speedFactor};
		this.zd = vz * ${data.speedFactor};

		<#if data.angularVelocity != 0 || data.angularAcceleration != 0>
		this.angularVelocity = ${data.angularVelocity}f;
		this.angularAcceleration = ${data.angularAcceleration}f;
		</#if>

		<#if data.animate>
		this.setSpriteFromAge(spriteSet);
		<#else>
		this.pickSprite(spriteSet);
		</#if>
	}

	<#if data.renderType == "LIT">
   	@Override public int getLightColor(float partialTick) {
		return 15728880;
   	}
	</#if>

	@Override public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_${data.renderType};
	}

	@Override public void tick() {
		super.tick();

		<#if data.angularVelocity != 0 || data.angularAcceleration != 0>
		this.oRoll = this.roll;
		this.roll += this.angularVelocity;
		this.angularVelocity += this.angularAcceleration;
		</#if>

		<#if data.animate>
		if(!this.removed) {
			<#assign frameCount = data.getTextureTileCount()>
			this.setSprite(this.spriteSet.get((this.age / ${data.frameDuration}) % ${frameCount} + 1, ${frameCount}));
		}
		</#if>

		<#if hasProcedure(data.additionalExpiryCondition)>
		double x = this.x;
		double y = this.y;
		double z = this.z;
		if (<@procedureOBJToConditionCode data.additionalExpiryCondition/>)
			this.remove();
		</#if>
	}

}
<#-- @formatter:on -->