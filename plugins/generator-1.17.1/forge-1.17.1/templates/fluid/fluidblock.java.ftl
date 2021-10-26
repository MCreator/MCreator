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
<#include "../procedures.java.ftl">

package ${package}.block;

import net.minecraft.world.level.material.Material;

public class ${name}Block extends LiquidBlock {
	public ${name}Block() {
		super(() -> ${JavaModName}Fluids.${data.getModElement().getRegistryNameUpper()},
			<#if generator.map(data.colorOnMap, "mapcolors") != "DEFAULT">
			BlockBehaviour.Properties.of(Material.${data.type}, MaterialColor.${generator.map(data.colorOnMap, "mapcolors")})
			<#else>
			BlockBehaviour.Properties.of(Material.${data.type})
			</#if>
			.strength(${data.resistance}f)
			<#if data.emissiveRendering>
			.hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true)
			</#if>
			.lightLevel(s -> ${data.luminance})
		);
		setRegistryName("${registryname}");
	}

	<#if data.flammability != 0>
	@Override public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return ${data.flammability};
	}
	</#if>

	<#if data.fireSpreadSpeed != 0>
	@Override public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return ${data.fireSpreadSpeed};
	}
	</#if>

	<#if data.lightOpacity == 0>
	@Override public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}
	<#elseif data.lightOpacity != 1>
	@Override public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return ${data.lightOpacity};
	}
	</#if>

	<#if hasProcedure(data.onBlockAdded) || hasProcedure(data.onTickUpdate)>
	@Override public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
		<#if hasProcedure(data.onTickUpdate)>
		world.getPendingBlockTicks().scheduleTick(pos, this, ${data.tickRate});
		</#if>
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<@procedureOBJToCode data.onBlockAdded/>
	}
	</#if>

	<#if hasProcedure(data.onNeighbourChanges)>
	@Override public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, isMoving);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<@procedureOBJToCode data.onNeighbourBlockChanges/>
	}
	</#if>

	<#if hasProcedure(data.onTickUpdate)>
	@Override public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, Random random) {
		super.tick(blockstate, world, pos, random);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<@procedureOBJToCode data.onTickUpdate/>
		world.getPendingBlockTicks().scheduleTick(pos, this, ${data.tickRate});
	}
	</#if>

	<#if hasProcedure(data.onEntityCollides)>
	@Override public void entityInside(BlockState blockstate, Level world, BlockPos pos, Entity entity) {
		super.entityInside(blockstate, world, pos, entity);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<@procedureOBJToCode data.onEntityCollides/>
	}
	</#if>

	<#if hasProcedure(data.onRandomUpdateEvent)>
	@Override public void animateTick(BlockState blockstate, Level world, BlockPos pos, Random random) {
		super.animateTick(blockstate, world, pos, random);
		Player entity = Minecraft.getInstance().player;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<@procedureOBJToCode data.onRandomUpdateEvent/>
	}
	</#if>

	<#if hasProcedure(data.onDestroyedByExplosion)>
	@Override public void wasExploded(Level world, BlockPos pos, Explosion e) {
		super.wasExploded(world, pos, e);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<@procedureOBJToCode data.onDestroyedByExplosion/>
	}
	</#if>
}