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

package ${package}.fluid;

<#compress>
public abstract class ${name}Fluid extends ForgeFlowingFluid {

	public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
		() -> ${JavaModName}FluidTypes.${data.getModElement().getRegistryNameUpper()}_TYPE.get(),
		() -> ${JavaModName}Fluids.${data.getModElement().getRegistryNameUpper()}.get(),
		() -> ${JavaModName}Fluids.FLOWING_${data.getModElement().getRegistryNameUpper()}.get())
		.explosionResistance(${data.resistance}f)
		<#if data.flowRate != 5>.tickRate(${data.flowRate})</#if>
		<#if data.levelDecrease != 1>.levelDecreasePerBlock(${data.levelDecrease})</#if>
		<#if data.slopeFindDistance != 4>.slopeFindDistance(${data.slopeFindDistance})</#if>
		<#if data.generateBucket>.bucket(() -> ${JavaModName}Items.${data.getModElement().getRegistryNameUpper()}_BUCKET.get())</#if>
		.block(() -> (LiquidBlock) ${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}.get());

	private ${name}Fluid() {
		super(PROPERTIES);
	}

	<#if data.spawnParticles>
	@Override public ParticleOptions getDripParticle() {
		return ${data.dripParticle};
	}
	</#if>

	<#if hasProcedure(data.flowCondition)>
	@Override protected boolean canSpreadTo(BlockGetter worldIn, BlockPos fromPos, BlockState blockstate,
			Direction direction, BlockPos toPos, BlockState intostate, FluidState toFluidState, Fluid fluidIn) {
		boolean condition = true;
		if (worldIn instanceof LevelAccessor world) {
			int x = fromPos.getX();
			int y = fromPos.getY();
			int z = fromPos.getZ();
			condition = <@procedureOBJToConditionCode data.flowCondition/>;
		}
		return super.canSpreadTo(worldIn, fromPos, blockstate, direction, toPos, intostate, toFluidState, fluidIn) && condition;
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

}</#compress>
<#-- @formatter:on -->