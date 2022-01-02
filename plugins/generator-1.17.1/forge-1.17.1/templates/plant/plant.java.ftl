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
<#include "../boundingboxes.java.ftl">
<#include "../procedures.java.ftl">
<#include "../triggers.java.ftl">
<#include "../mcitems.ftl">

package ${package}.block;

import net.minecraft.world.level.material.Material;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ${name}Block extends <#if data.plantType == "normal">Flower<#elseif data.plantType == "growapable">SugarCane<#elseif data.plantType == "double">DoublePlant</#if>Block<#if data.hasTileEntity> implements EntityBlock</#if>{
	public ${name}Block() {
		super(<#if data.plantType == "normal">${generator.map(data.suspiciousStewEffect, "effects")}, ${data.suspiciousStewDuration},</#if>
		<#if generator.map(data.colorOnMap, "mapcolors") != "DEFAULT">
		BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.${generator.map(data.colorOnMap, "mapcolors")})
		<#else>
		BlockBehaviour.Properties.of(Material.PLANT)
		</#if>
		<#if data.plantType == "growapable" || data.forceTicking>
		.randomTicks()
		</#if>
		.noCollission()
		<#if data.isCustomSoundType>
			.sound(new ForgeSoundType(1.0f, 1.0f, () -> new SoundEvent(new ResourceLocation("${data.breakSound}")),
			() -> new SoundEvent(new ResourceLocation("${data.stepSound}")),
			() -> new SoundEvent(new ResourceLocation("${data.placeSound}")),
			() -> new SoundEvent(new ResourceLocation("${data.hitSound}")),
			() -> new SoundEvent(new ResourceLocation("${data.fallSound}"))))
		<#else>
			.sound(SoundType.${data.soundOnStep})
		</#if>
		<#if data.unbreakable>
		.strength(-1, 3600000)
		<#elseif (data.hardness == 0) && (data.resistance == 0)>
		.instabreak()
		<#else>
		.strength(${data.hardness}f, ${data.resistance}f)
		</#if>
		<#if data.emissiveRendering>
		.hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true)
		</#if>
		<#if data.speedFactor != 1.0>
		.speedFactor(${data.speedFactor}f)
		</#if>
		<#if data.jumpFactor != 1.0>
		.jumpFactor(${data.jumpFactor}f)
		</#if>
		<#if data.luminance != 0>
		.lightLevel(s -> ${data.luminance})
		</#if>
		<#if !data.useLootTableForDrops && (data.dropAmount == 0)>
		.noDrops()
		</#if>
	);
	setRegistryName("${registryname}");
	}

	<#if data.customBoundingBox && data.boundingBoxes??>
	@Override public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		<#if data.isBoundingBoxEmpty()>
			return Shapes.empty();
		<#else>
			<#if !data.disableOffset> Vec3 offset = state.getOffset(world, pos); </#if>
			<@makeBoundingBox data.positiveBoundingBoxes() data.negativeBoundingBoxes() data.disableOffset "north"/>
		</#if>
	}
	</#if>

	<#if (data.plantType == "normal") && (data.suspiciousStewDuration > 0)>
	@Override public int getEffectDuration() {
		return ${data.suspiciousStewDuration};
	}
	</#if>

	<#if data.isReplaceable>
	@Override public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		return useContext.getItemInHand().getItem() != this.asItem();
	}
	</#if>

	<#if data.flammability != 0>
	@Override public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return ${data.flammability};
	}
	</#if>

	<#if generator.map(data.aiPathNodeType, "pathnodetypes") != "DEFAULT">
	@Override public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return BlockPathTypes.${generator.map(data.aiPathNodeType, "pathnodetypes")};
	}
	</#if>

	<#if data.offsetType != "XZ">
	@Override public BlockBehaviour.OffsetType getOffsetType() {
		return BlockBehaviour.OffsetType.${data.offsetType};
	}
	</#if>

	<#if data.specialInfo?has_content>
	@Override public void appendHoverText(ItemStack itemstack, BlockGetter world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		<#list data.specialInfo as entry>
		list.add(new TextComponent("${JavaConventions.escapeStringForJava(entry)}"));
		</#list>
	}
	</#if>

	<#if data.fireSpreadSpeed != 0>
	@Override public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return ${data.fireSpreadSpeed};
	}
	</#if>

	<#if data.creativePickItem?? && !data.creativePickItem.isEmpty()>
	@Override public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		return ${mappedMCItemToItemStackCode(data.creativePickItem, 1)};
	}
	</#if>

	<#if !(data.useLootTableForDrops || (data.dropAmount == 0))>
		<#if data.dropAmount != 1 && !(data.customDrop?? && !data.customDrop.isEmpty())>
		@Override public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
			<#if data.plantType == "double">
			if(state.getValue(HALF) != DoubleBlockHalf.LOWER)
				return Collections.emptyList();
			</#if>

			List<ItemStack> dropsOriginal = super.getDrops(state, builder);
			if(!dropsOriginal.isEmpty())
				return dropsOriginal;
			return Collections.singletonList(new ItemStack(this, ${data.dropAmount}));
		}
		<#elseif data.customDrop?? && !data.customDrop.isEmpty()>
		@Override public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
			<#if data.plantType == "double">
			if(state.getValue(HALF) != DoubleBlockHalf.LOWER)
				return Collections.emptyList();
			</#if>

			List<ItemStack> dropsOriginal = super.getDrops(state, builder);
			if(!dropsOriginal.isEmpty())
				return dropsOriginal;
			return Collections.singletonList(${mappedMCItemToItemStackCode(data.customDrop, data.dropAmount)});
		}
		<#else>
		@Override public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
			<#if data.plantType == "double">
			if(state.getValue(HALF) != DoubleBlockHalf.LOWER)
				return Collections.emptyList();
			</#if>

			List<ItemStack> dropsOriginal = super.getDrops(state, builder);
			if(!dropsOriginal.isEmpty())
				return dropsOriginal;
			return Collections.singletonList(new ItemStack(this, 1));
		}
		</#if>
	</#if>

	<#if (data.canBePlacedOn?size > 0) || hasProcedure(data.placingCondition)>
		<#if data.plantType != "growapable">
		@Override public boolean mayPlaceOn(BlockState groundState, BlockGetter worldIn, BlockPos pos) {
			<#if hasProcedure(data.placingCondition)>
			boolean additionalCondition = true;
			if (worldIn instanceof LevelAccessor world) {
				int x = pos.getX();
				int y = pos.getY() + 1;
				int z = pos.getZ();
				BlockState blockstate = world.getBlockState(pos.above());
				additionalCondition = <@procedureOBJToConditionCode data.placingCondition/>;
			}
			</#if>

			return
			<#if (data.canBePlacedOn?size > 0)>
				<@canPlaceOnList data.canBePlacedOn hasProcedure(data.placingCondition)/>
			</#if>
			<#if (data.canBePlacedOn?size > 0) && hasProcedure(data.placingCondition)> && </#if>
			<#if hasProcedure(data.placingCondition)> additionalCondition </#if>;
		}
		</#if>

		@Override public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
			BlockPos blockpos = pos.below();
			BlockState groundState = worldIn.getBlockState(blockpos);

			<#if data.plantType = "normal">
				return this.mayPlaceOn(groundState, worldIn, blockpos)
			<#elseif data.plantType == "growapable">
				<#if hasProcedure(data.placingCondition)>
				boolean additionalCondition = true;
				if (worldIn instanceof LevelAccessor world) {
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();
					additionalCondition = <@procedureOBJToConditionCode data.placingCondition/>;
				}
				</#if>

				return groundState.is(this) ||
				<#if (data.canBePlacedOn?size > 0)>
					<@canPlaceOnList data.canBePlacedOn hasProcedure(data.placingCondition)/>
				</#if>
				<#if (data.canBePlacedOn?size > 0) && hasProcedure(data.placingCondition)> && </#if>
				<#if hasProcedure(data.placingCondition)> additionalCondition </#if>
			<#else>
				if (blockstate.getValue(HALF) == DoubleBlockHalf.UPPER)
					return groundState.is(this) && groundState.getValue(HALF) == DoubleBlockHalf.LOWER;
				else
					return this.mayPlaceOn(groundState, worldIn, blockpos)
			</#if>;
		}
	</#if>

	<#if !(data.growapableSpawnType == "Plains" && data.plantType == "normal")>
	@Override public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.${generator.map(data.growapableSpawnType, "planttypes")};
	}
	</#if>

	<@onBlockAdded data.onBlockAdded, false, 0/>

	<@onBlockTick data.onTickUpdate, false, 0/>

	<#if data.plantType == "growapable">
	@Override public void randomTick(BlockState blockstate, ServerLevel world, BlockPos blockpos, Random random) {
		if (world.isEmptyBlock(blockpos.above())) {
			int i = 1;
			for(;world.getBlockState(blockpos.below(i)).is(this); ++i);
			if (i < ${data.growapableMaxHeight}) {
				int j = blockstate.getValue(AGE);
				if (ForgeHooks.onCropsGrowPre(world, blockpos, blockstate, true)) {
					if (j == 15) {
						world.setBlockAndUpdate(blockpos.above(), defaultBlockState());
						world.setBlock(blockpos, blockstate.setValue(AGE, 0), 4);
					} else
						world.setBlock(blockpos, blockstate.setValue(AGE, j + 1), 4);
				}
			}
		}
	}
	</#if>

	<@onAnimateTick data.onRandomUpdateEvent/>

	<@onRedstoneOrNeighborChanged "", "", data.onNeighbourBlockChanges/>

	<@onEntityCollides data.onEntityCollides/>

	<@onDestroyedByPlayer data.onDestroyedByPlayer/>

	<@onDestroyedByExplosion data.onDestroyedByExplosion/>

	<@onStartToDestroy data.onStartToDestroy/>

	<@onBlockPlacedBy data.onBlockPlacedBy/>

	<@onBlockRightClicked data.onRightClicked/>

	<#if data.hasTileEntity>
	@Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ${name}BlockEntity(pos, state);
	}

	@Override public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
		super.triggerEvent(state, world, pos, eventID, eventParam);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity == null ? false : blockEntity.triggerEvent(eventID, eventParam);
	}
	</#if>

	@OnlyIn(Dist.CLIENT) public static void registerRenderLayer() {
		ItemBlockRenderTypes.setRenderLayer(${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}, renderType -> renderType == RenderType.cutout());
	}

	<#if data.tintType != "No tint">
		@OnlyIn(Dist.CLIENT) public static void blockColorLoad(ColorHandlerEvent.Block event) {
			event.getBlockColors().register((bs, world, pos, index) -> {
					return world != null && pos != null ?
					<#if data.tintType == "Grass">
						BiomeColors.getAverageGrassColor(world, pos) : GrassColor.get(0.5D, 1.0D);
					<#elseif data.tintType == "Foliage">
						BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.getDefaultColor();
					<#elseif data.tintType == "Water">
						BiomeColors.getAverageWaterColor(world, pos) : -1;
					<#elseif data.tintType == "Sky">
						Minecraft.getInstance().level.getBiome(pos).getSkyColor() : 8562943;
					<#elseif data.tintType == "Fog">
						Minecraft.getInstance().level.getBiome(pos).getFogColor() : 12638463;
					<#else>
						Minecraft.getInstance().level.getBiome(pos).getWaterFogColor() : 329011;
					</#if>
			}, ${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()});
		}

		<#if data.isItemTinted>
		@OnlyIn(Dist.CLIENT) public static void itemColorLoad(ColorHandlerEvent.Item event) {
			event.getItemColors().register((stack, index) -> {
				<#if data.tintType == "Grass">
					return GrassColor.get(0.5D, 1.0D);
				<#elseif data.tintType == "Foliage">
					return FoliageColor.getDefaultColor();
				<#elseif data.tintType == "Water">
					return 3694022;
				<#elseif data.tintType == "Sky">
					return 8562943;
				<#elseif data.tintType == "Fog">
					return 12638463;
				<#else>
					return 329011;
				</#if>
			}, ${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()});
		}
		</#if>
	</#if>
}
<#-- @formatter:on -->

<#macro canPlaceOnList blockList condition>
<#if (blockList?size > 1) && condition>(</#if>
<#list blockList as canBePlacedOn>
groundState.is(${mappedBlockToBlock(canBePlacedOn)})<#if canBePlacedOn?has_next>||</#if>
</#list><#if (blockList?size > 1) && condition>)</#if>
</#macro>