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
<#include "procedures.java.ftl">

package ${package}.particle;

@${JavaModName}Elements.ModElement.Tag
public class ${name}Particle extends ${JavaModName}Elements.ModElement{

	public static final BasicParticleType particle = new BasicParticleType(${data.alwaysShow});

	public ${name}Particle (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});

		MinecraftForge.EVENT_BUS.register(this);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent public void registerParticleType(RegistryEvent.Register<ParticleType<?>> event) {
		event.getRegistry().register(particle.setRegistryName("${registryname}"));
	}

	@OnlyIn(Dist.CLIENT) @SubscribeEvent public void registerParticle(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particles.registerFactory(particle, CustomParticleFactory::new);
	}

	@OnlyIn(Dist.CLIENT) private static class CustomParticle extends SpriteTexturedParticle {

		private final IAnimatedSprite spriteSet;

		protected CustomParticle(World world, double x, double y, double z, double vx, double vy, double vz, IAnimatedSprite spriteSet) {
			super(world, x, y, z);
			this.spriteSet = spriteSet;

			this.setSize((float) ${data.width}, (float) ${data.height});
			this.particleScale *= (float) ${data.scale};

			<#if (data.maxAgeDiff > 0)>
			this.maxAge = (int) Math.max(1, ${data.maxAge} + (this.rand.nextInt(${data.maxAgeDiff * 2}) - ${data.maxAgeDiff}));
			<#else>
			this.maxAge = ${data.maxAge};
			</#if>

			this.particleGravity = (float) ${data.gravity};
			this.canCollide = ${data.canCollide};

			this.motionX = vx * ${data.speedFactor};
			this.motionY = vy * ${data.speedFactor};
			this.motionZ = vz * ${data.speedFactor};

			<#if data.animate>
			this.selectSpriteWithAge(spriteSet);
			<#else>
			this.selectSpriteRandomly(spriteSet);
			</#if>
		}

		<#if data.renderType == "LIT">
   		@Override public int getBrightnessForRender(float partialTick) {
			return 15728880;
   		}
		</#if>

		@Override public IParticleRenderType getRenderType() {
			return IParticleRenderType.PARTICLE_SHEET_${data.renderType};
		}

		@Override public void tick() {
			super.tick();

			<#if data.animate>
			if(!this.isExpired) {
				<#assign frameCount = data.getTextureTileCount()>
				this.setSprite(this.spriteSet.get((this.age / ${data.frameDuration}) % ${frameCount} + 1, ${frameCount}));
			}
			</#if>

			<#if hasCondition(data.additionalExpiryCondition)>
			double x = this.posX;
			double y = this.posY;
			double z = this.posZ;
			if (<@procedureOBJToConditionCode data.additionalExpiryCondition/>)
				this.setExpired();
			</#if>
		}

	}

	@OnlyIn(Dist.CLIENT) private static class CustomParticleFactory implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;

		public CustomParticleFactory(IAnimatedSprite spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new CustomParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

}
<#-- @formatter:on -->