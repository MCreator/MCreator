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

<#include "../procedures.java.ftl">

package ${package}.block;

import org.slf4j.Logger;

public class ${name}PortalBlock extends NetherPortalBlock {

	private static final Logger LOGGER = LogUtils.getLogger();

	public static void portalSpawn(Level world, BlockPos pos) {
		Optional<${name}PortalShape> optional = ${name}PortalShape.findEmptyPortalShape(world, pos, Direction.Axis.X);
		if (optional.isPresent()) {
			optional.get().createPortalBlocks(world);
		}
	}

	public ${name}PortalBlock(BlockBehaviour.Properties properties) {
		super(properties.noCollission().randomTicks().pushReaction(PushReaction.BLOCK)
				.strength(-1.0F).sound(SoundType.GLASS).lightLevel(s -> ${data.portalLuminance}).noLootTable());
	}

	private ${name}Teleporter getTeleporter(ServerLevel level) {
		return new ${name}Teleporter(level);
	}

	@Override ${mcc.getMethod("net.minecraft.world.level.block.NetherPortalBlock", "updateShape", "BlockState", "LevelReader", "ScheduledTickAccess", "BlockPos", "Direction", "BlockPos", "BlockState", "RandomSource")
				   .replace("PortalShape", name+"PortalShape")}

	@Override @Nullable ${mcc.getMethod("net.minecraft.world.level.block.NetherPortalBlock", "getPortalDestination", "ServerLevel", "Entity", "BlockPos")
							 .replace("Level.NETHER", "ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(\"${modid}:${registryname}\"))")}

	@Nullable ${mcc.getMethod("net.minecraft.world.level.block.NetherPortalBlock", "getExitPortal", "ServerLevel", "Entity", "BlockPos", "BlockPos", "boolean", "WorldBorder")
				   .replace("p_350564_.getPortalForcer()", "getTeleporter(p_350564_)")}

	${mcc.getMethod("net.minecraft.world.level.block.NetherPortalBlock", "getDimensionTransitionFromExit",
			"Entity", "BlockPos", "BlockUtil.FoundRectangle", "ServerLevel", "TeleportTransition.PostTeleportTransition")}

	${mcc.getMethod("net.minecraft.world.level.block.NetherPortalBlock", "createDimensionTransition",
			"ServerLevel", "BlockUtil.FoundRectangle", "Direction.Axis", "Vec3", "Entity", "TeleportTransition.PostTeleportTransition")
				.replace("PortalShape", name+"PortalShape")}

	@Override public int getPortalTransitionTime(ServerLevel world, Entity entity) {
		return 0;
	}

	@Override public Portal.Transition getLocalTransition() {
		return Portal.Transition.NONE;
	}

	@Override public void randomTick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
	<#-- Do not call super to prevent ZOMBIFIED_PIGLINs from spawning -->
		<#if hasProcedure(data.onPortalTickUpdate)>
			<@procedureCode data.onPortalTickUpdate, {
			"x": "pos.getX()",
			"y": "pos.getY()",
			"z": "pos.getZ()",
			"world": "world",
			"blockstate": "blockstate"
			}/>
		</#if>
	}

	@OnlyIn(Dist.CLIENT) @Override public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		for (int i = 0; i < 4; i++) {
			double px = pos.getX() + random.nextFloat();
			double py = pos.getY() + random.nextFloat();
			double pz = pos.getZ() + random.nextFloat();
			double vx = (random.nextFloat() - 0.5) / 2.;
			double vy = (random.nextFloat() - 0.5) / 2.;
			double vz = (random.nextFloat() - 0.5) / 2.;
			int j = random.nextInt(4) - 1;
			if (world.getBlockState(pos.west()).getBlock() != this
					&& world.getBlockState(pos.east()).getBlock() != this) {
				px = pos.getX() + 0.5 + 0.25 * j;
				vx = random.nextFloat() * 2 * j;
			} else {
				pz = pos.getZ() + 0.5 + 0.25 * j;
				vz = random.nextFloat() * 2 * j;
			}
			world.addParticle(${data.portalParticles}, px, py, pz, vx, vy, vz);
		}

		<#if data.portalSound.toString()?has_content>
		if (random.nextInt(110) == 0)
			world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					BuiltInRegistries.SOUND_EVENT.getValue(ResourceLocation.parse(("${data.portalSound}"))), SoundSource.BLOCKS, 0.5f,
					random.nextFloat() * 0.4f + 0.8f, false);
        </#if>
	}

}

<#-- @formatter:on -->