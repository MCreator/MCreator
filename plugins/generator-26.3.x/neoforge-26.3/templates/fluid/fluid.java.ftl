<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2024, Pylo, opensource contributors
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

package ${package}.fluid;

<@javacompress>
public abstract class ${name}Fluid extends BaseFlowingFluid {

	public static final BaseFlowingFluid.Properties PROPERTIES = new BaseFlowingFluid.Properties(
		() -> ${JavaModName}FluidTypes.${REGISTRYNAME}_TYPE.get(),
		() -> ${JavaModName}Fluids.${REGISTRYNAME}.get(),
		() -> ${JavaModName}Fluids.FLOWING_${REGISTRYNAME}.get())
		.explosionResistance(${data.resistance}f)
		<#if data.flowRate != 5>.tickRate(${data.flowRate})</#if>
		<#if data.levelDecrease != 1>.levelDecreasePerBlock(${data.levelDecrease})</#if>
		<#if data.slopeFindDistance != 4>.slopeFindDistance(${data.slopeFindDistance})</#if>
		<#if data.generateBucket>.bucket(() -> ${JavaModName}Items.${REGISTRYNAME}_BUCKET.get())</#if>
		.block(() -> (LiquidBlock) ${JavaModName}Blocks.${REGISTRYNAME}.get());

	private ${name}Fluid() {
		super(PROPERTIES);
	}

	<#if data.spawnParticles>
	@Override public ParticleOptions getDripParticle() {
		return ${data.dripParticle};
	}
	</#if>

	<#if hasProcedure(data.flowCondition)>
	@Override protected void spread(ServerLevel world, BlockPos pos, BlockState blockstate, FluidState fluidState) {
		if (!fluidState.isEmpty()) {
			BlockPos belowPos = pos.below();
			BlockState intostate = world.getBlockState(belowPos);
			FluidState belowFluid = intostate.getFluidState();
			Direction direction = Direction.DOWN;
			if (this.canMaybePassThrough(world, pos, blockstate, direction, belowPos, intostate, belowFluid)) {
				FluidState newBelowFluid = this.getNewLiquid(world, belowPos, intostate);
				Fluid newBelowFluidType = newBelowFluid.getType();
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				if (<@procedureOBJToConditionCode data.flowCondition/>
					&& belowFluid.canBeReplacedWith(world, belowPos, newBelowFluidType, direction)
					&& canHoldSpecificFluid(world, belowPos, intostate, newBelowFluidType)) {
					this.spreadTo(world, belowPos, intostate, direction, newBelowFluid);
					if (this.sourceNeighborCount(world, pos) >= 3) {
						this.spreadToSides(world, pos, fluidState, blockstate);
					}

					return;
				}
			}

			if (fluidState.isSource() || !this.isWaterHole(world, pos, blockstate, belowPos, intostate)) {
				this.spreadToSides(world, pos, fluidState, blockstate);
			}
		}
	}

	@Override public void spreadToSides(ServerLevel world, BlockPos pos, FluidState fluidState, BlockState blockstate) {
		int neighbor = fluidState.getAmount() - this.getDropOff(world);
		if (fluidState.getValue(FALLING)) {
			neighbor = 7;
		}

		if (neighbor > 0) {
			Map<Direction, FluidState> spreads = this.getSpread(world, pos, blockstate);

			for (Map.Entry<Direction, FluidState> entry : spreads.entrySet()) {
				Direction spread = entry.getKey();
				FluidState newNeighborFluid = entry.getValue();
				BlockPos neighborPos = pos.relative(spread);
				BlockState intostate = world.getBlockState(neighborPos);
				Direction direction = entry.getKey();
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				if (<@procedureOBJToConditionCode data.flowCondition/>)
					this.spreadTo(world, neighborPos, intostate, spread, newNeighborFluid);
			}
		}
	}
	</#if>

	<#if hasProcedure(data.beforeReplacingBlock)>
	@Override protected void beforeDestroyingBlock(LevelAccessor world, BlockPos pos, BlockState blockstate) {
		<@procedureCode data.beforeReplacingBlock, {
			"x": "pos.getX()",
			"y": "pos.getY()",
			"z": "pos.getZ()",
			"world": "world",
			"blockstate": "blockstate"
		}/>
	}
	</#if>

	public static class Source extends ${name}Fluid {
		public int getAmount(FluidState state) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Flowing extends ${name}Fluid {
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		public boolean isSource(FluidState state) {
			return false;
		}
	}

}</@javacompress>
<#-- @formatter:on -->