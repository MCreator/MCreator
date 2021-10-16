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
<#include "../boundingboxes.java.ftl">
<#include "../mcitems.ftl">
<#include "../procedures.java.ftl">
<#include "../particles.java.ftl">

package ${package}.block;

import net.minecraft.world.level.material.Material;

<#if (data.spawnWorldTypes?size > 0)>
		MinecraftForge.EVENT_BUS.register(this);
		FMLJavaModLoadingContext.get().getModEventBus().register(new FeatureRegisterHandler());
</#if>

public class ${name}Block extends
			<#if data.hasGravity>
					FallingBlock
			<#elseif data.blockBase?has_content && data.blockBase == "Button">
				<#if (data.material.getUnmappedValue() == "WOOD") || (data.material.getUnmappedValue() == "NETHER_WOOD")>Wood<#else>Stone</#if>ButtonBlock
			<#elseif data.blockBase?has_content>
				${data.blockBase}Block
			<#else>
				Block
			</#if>
			<#if data.isWaterloggable>
            implements SimpleWaterloggedBlock
			</#if> {

	<#if data.rotationMode == 1 || data.rotationMode == 3>
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.HORIZONTAL_FACING;
	<#elseif data.rotationMode == 2 || data.rotationMode == 4>
	public static final DirectionProperty FACING = DirectionalBlock.FACING;
	<#elseif data.rotationMode == 5>
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
	</#if>
	<#if data.isWaterloggable>
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	</#if>

	<#macro blockProperties>
		Block.Properties.of(Material.${data.material})
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
			<#else>
				.strength(${data.hardness}f, ${data.resistance}f)
			</#if>
				.lightLevel(s -> ${data.luminance})
			<#if data.destroyTool != "Not specified">
				<#-- TODO: implement this properly: https://gist.github.com/gigaherz/691f528a61f631af90c9426c076a298a -->
				.harvestLevel(${data.breakHarvestLevel})
				.harvestTool(ToolType.${data.destroyTool?upper_case})
				.requiresCorrectToolForDrops()
			</#if>
			<#if data.isNotColidable>
				.noCollission()
			</#if>
			<#if data.slipperiness != 0.6>
				.friction(${data.slipperiness}f)
			</#if>
			<#if data.speedFactor != 1.0>
				.speedFactor(${data.speedFactor}f)
			</#if>
			<#if data.jumpFactor != 1.0>
				.jumpFactor(${data.jumpFactor}f)
			</#if>
			<#if data.hasTransparency || (data.blockBase?has_content && data.blockBase == "Leaves")>
				.noOcclusion()
			</#if>
			<#if data.tickRandomly>
				.randomTicks()
			</#if>
			<#if data.emissiveRendering>
				.hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true)
			</#if>
			<#if data.hasTransparency>
				.isRedstoneConductor((bs, br, bp) -> false)
			</#if>
			<#if data.boundingBoxes?? && !data.blockBase?? && !data.isFullCube() && data.offsetType != "NONE">
				.dynamicShape()
			</#if>
	</#macro>

	public ${name}Block() {
		<#if data.blockBase?has_content && data.blockBase == "Stairs">
		super(() -> new Block(<@blockProperties/>).defaultBlockState(),
		<#elseif data.blockBase?has_content && data.blockBase == "PressurePlate">
		    <#if (data.material.getUnmappedValue() == "WOOD") || (data.material.getUnmappedValue() == "NETHER_WOOD")>
		        super(Sensitivity.EVERYTHING,
		    <#else>
		        super(Sensitivity.MOBS,
		    </#if>
		<#else>
		super(
		</#if>
		<@blockProperties/>
		);

	    <#if data.rotationMode != 0 || data.isWaterloggable>
	    this.setDefaultState(this.stateContainer.getBaseState()
	                             <#if data.rotationMode == 1 || data.rotationMode == 3>
	                             .with(FACING, Direction.NORTH)
	                             <#elseif data.rotationMode == 2 || data.rotationMode == 4>
	                             .with(FACING, Direction.NORTH)
	                             <#elseif data.rotationMode == 5>
	                             .with(AXIS, Direction.Axis.Y)
	                             </#if>
	                             <#if data.isWaterloggable>
	                             .with(WATERLOGGED, false)
	                             </#if>
	    );
		</#if>

		setRegistryName("${registryname}");
	}

	<#if data.blockBase?has_content && data.blockBase == "Fence">
	@Override public boolean canConnect(BlockState state, boolean checkattach, Direction face) {
	  boolean flag = state.getBlock() instanceof FenceBlock && state.getMaterial() == this.material;
	  boolean flag1 = state.getBlock() instanceof FenceGateBlock && FenceGateBlock.isParallel(state, face);
	  return !cannotAttach(state.getBlock()) && checkattach || flag || flag1;
	}
	<#elseif data.blockBase?has_content && data.blockBase == "Wall">
	private static final VoxelShape CENTER_POLE_SHAPE = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
	private static final VoxelShape WALL_CONNECTION_NORTH_SIDE_SHAPE = Block.makeCuboidShape(7.0D, 0.0D, 0.0D, 9.0D, 16.0D, 9.0D);
	private static final VoxelShape WALL_CONNECTION_SOUTH_SIDE_SHAPE = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 16.0D);
	private static final VoxelShape WALL_CONNECTION_WEST_SIDE_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
	private static final VoxelShape WALL_CONNECTION_EAST_SIDE_SHAPE = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D);

	private boolean shouldConnect(BlockState state, boolean checkattach, Direction face) {
		boolean flag = state.getBlock() instanceof WallBlock || state.getBlock() instanceof FenceGateBlock && FenceGateBlock.isParallel(state, face);
		return !cannotAttach(state.getBlock()) && checkattach || flag;
	}
	@Override ${mcc.getMethod("net.minecraft.block.WallBlock", "getStateForPlacement", "BlockPlaceContext")}
	@Override ${mcc.getMethod("net.minecraft.block.WallBlock", "updatePostPlacement", "BlockState", "Direction", "BlockState", "LevelAccessor", "BlockPos", "BlockPos")}
	${mcc.getMethod("net.minecraft.block.WallBlock", "func_235625_a_", "LevelReader", "BlockState", "BlockPos", "BlockState")}
	${mcc.getMethod("net.minecraft.block.WallBlock", "func_235627_a_", "LevelReader", "BlockPos", "BlockState", "BlockPos", "BlockState", "Direction")}
	${mcc.getMethod("net.minecraft.block.WallBlock", "func_235626_a_", "LevelReader", "BlockState", "BlockPos", "BlockState", "boolean", "boolean", "boolean", "boolean")}
	${mcc.getMethod("net.minecraft.block.WallBlock", "func_235630_a_", "BlockState", "boolean", "boolean", "boolean", "boolean", "VoxelShape")}
	${mcc.getMethod("net.minecraft.block.WallBlock", "func_235633_a_", "boolean", "VoxelShape", "VoxelShape")}
	${mcc.getMethod("net.minecraft.block.WallBlock", "func_235628_a_", "BlockState", "BlockState", "VoxelShape")}

	private static boolean hasHeightForProperty(BlockState state, Property<WallHeight> heightProperty) {
		return state.get(heightProperty) != WallHeight.NONE;
	}

	private static boolean compareShapes(VoxelShape shape1, VoxelShape shape2) {
		return !VoxelShapes.compare(shape2, shape1, IBooleanFunction.ONLY_FIRST);
	}
	</#if>

	<#if data.specialInfo?has_content>
	@Override public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		<#list data.specialInfo as entry>
		list.add(new TextComponent("${JavaConventions.escapeStringForJava(entry)}"));
	    </#list>
	}
	</#if>

	<#if data.displayFluidOverlay>
	@Override public boolean shouldDisplayFluidOverlay(BlockState state, IBlockDisplayReader world, BlockPos pos, FluidState fluidstate) {
		return true;
	}
	</#if>

	<#if data.beaconColorModifier?has_content>
	@Override public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return new float[] { ${data.beaconColorModifier.getRed()/255}f, ${data.beaconColorModifier.getGreen()/255}f, ${data.beaconColorModifier.getBlue()/255}f };
	}
	</#if>

	<#if data.connectedSides>
	@Override public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		return adjacentBlockState.getBlock() == this ? true : super.isSideInvisible(state, adjacentBlockState, side);
	}
	</#if>

	<#if (!data.blockBase?? || data.blockBase == "Leaves") && data.lightOpacity == 0>
	@Override public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return <#if data.isWaterloggable>state.getFluidState().isEmpty()<#else>true</#if>;
	}
	</#if>

	<#if !data.blockBase?? || data.blockBase == "Leaves" || data.lightOpacity != 15>
	@Override public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return ${data.lightOpacity};
	}
	</#if>

	<#if data.boundingBoxes?? && !data.blockBase?? && !data.isFullCube()>
	@Override public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		<#if data.isBoundingBoxEmpty()>
			return VoxelShapes.empty();
		<#else>
			<#if !data.disableOffset>Vec3 offset = state.getOffset(world, pos);</#if>
			<@boundingBoxWithRotation data.positiveBoundingBoxes() data.negativeBoundingBoxes() data.disableOffset data.rotationMode/>
		</#if>
	}
	</#if>

	<#if data.rotationMode != 0>
	@Override protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		<#if data.isWaterloggable>
			<#if data.rotationMode == 5>
			builder.add(AXIS, WATERLOGGED);
			<#else>
			builder.add(FACING, WATERLOGGED);
			</#if>
		<#elseif data.rotationMode == 5>
			builder.add(AXIS);
		<#else>
			builder.add(FACING);
		</#if>
	}

		<#if data.rotationMode != 5>
		public BlockState rotate(BlockState state, Rotation rot) {
			return state.with(FACING, rot.rotate(state.get(FACING)));
		}

		public BlockState mirror(BlockState state, Mirror mirrorIn) {
			return state.rotate(mirrorIn.toRotation(state.get(FACING)));
		}
		<#else>
		@Override public BlockState rotate(BlockState state, Rotation rot) {
			if(rot == Rotation.CLOCKWISE_90 || rot == Rotation.COUNTERCLOCKWISE_90) {
				if ((Direction.Axis) state.get(AXIS) == Direction.Axis.X) {
					return state.with(AXIS, Direction.Axis.Z);
				} else if ((Direction.Axis) state.get(AXIS) == Direction.Axis.Z) {
					return state.with(AXIS, Direction.Axis.X);
				}
			}
			return state;
		}
		</#if>

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
	    <#if data.rotationMode == 4>
	    Direction facing = context.getFace();
	    </#if>
	    <#if data.rotationMode == 5>
	    Direction.Axis axis = context.getFace().getAxis();
	    </#if>
	    <#if data.isWaterloggable>
	    boolean flag = context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER;
	    </#if>;
		<#if data.rotationMode != 3>
		return this.defaultBlockState()
		        <#if data.rotationMode == 1>
		        .with(FACING, context.getPlacementHorizontalFacing().getOpposite())
		        <#elseif data.rotationMode == 2>
		        .with(FACING, context.getNearestLookingDirection().getOpposite())
	            <#elseif data.rotationMode == 4>
		        .with(FACING, facing)
	            <#elseif data.rotationMode == 5>
	            .with(AXIS, axis)
		        </#if>
		        <#if data.isWaterloggable>
		        .with(WATERLOGGED, flag)
		        </#if>
		<#elseif data.rotationMode == 3>
	    if (context.getFace() == Direction.UP || context.getFace() == Direction.DOWN)
	        return this.defaultBlockState()
	                .with(FACING, Direction.NORTH)
	                <#if data.isWaterloggable>
	                .with(WATERLOGGED, flag)
	                </#if>;
	    return this.defaultBlockState()
	            .with(FACING, context.getFace())
	            <#if data.isWaterloggable>
	            .with(WATERLOGGED, flag)
	            </#if>
		</#if>;
	}
	</#if>

	<#if hasProcedure(data.placingCondition)>
	@Override public boolean isValidPosition(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
		if (worldIn instanceof LevelAccessor) {
			LevelAccessor world = (LevelAccessor) worldIn;
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return <@procedureOBJToConditionCode data.placingCondition/>;
		}
		return super.isValidPosition(blockstate, worldIn, pos);
	}
	</#if>

	<#if data.isWaterloggable>
	    <#if data.rotationMode == 0>
	    @Override
	    public BlockState getStateForPlacement(BlockPlaceContext context) {
	    boolean flag = context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER;
	        return this.defaultBlockState().with(WATERLOGGED, flag);
	    }
	    @Override protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
	        builder.add(WATERLOGGED);
	    }
	    </#if>

	@Override public FluidState getFluidState(BlockState state) {
	    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}
	</#if>

	<#if data.isWaterloggable || hasProcedure(data.placingCondition)>
	@Override public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
	    <#if data.isWaterloggable>
		if (state.get(WATERLOGGED)) {
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		</#if>
		return <#if hasProcedure(data.placingCondition)>
		!state.isValidPosition(world, currentPos) ? Blocks.AIR.defaultBlockState() :
		</#if> super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}
	</#if>

	<#if data.enchantPowerBonus != 0>
	@Override public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
		return ${data.enchantPowerBonus}f;
	}
	</#if>

	<#if data.isReplaceable>
	@Override public boolean isReplaceable(BlockState state, BlockPlaceContext context) {
		return context.getItem().getItem() != this.asItem();
	}
	</#if>

	<#if data.canProvidePower && data.emittedRedstonePower??>
	@Override public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override public int getWeakPower(BlockState blockstate, BlockGetter blockAccess, BlockPos pos, Direction side) {
		<#if hasProcedure(data.emittedRedstonePower)>
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			World world = (World) blockAccess;
			return (int) <@procedureOBJToNumberCode data.emittedRedstonePower/>;
		<#else>
			return ${data.emittedRedstonePower.getFixedValue()};
		</#if>
	}
	</#if>

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

	<#if data.creativePickItem?? && !data.creativePickItem.isEmpty()>
	@Override public ItemStack getPickBlock(BlockState state, RayTraceResult target, BlockGetter world, BlockPos pos, PlayerEntity player) {
		return ${mappedMCItemToItemStackCode(data.creativePickItem, 1)};
	}
	</#if>

	<#if generator.map(data.colorOnMap, "mapcolors") != "DEFAULT">
	@Override public MaterialColor getMaterialColor() {
		return MaterialColor.${generator.map(data.colorOnMap, "mapcolors")};
	}
	</#if>

	<#if generator.map(data.aiPathNodeType, "pathnodetypes") != "DEFAULT">
	@Override public PathNodeType getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, MobEntity entity) {
		return PathNodeType.${generator.map(data.aiPathNodeType, "pathnodetypes")};
	}
	</#if>

	<#if data.offsetType != "NONE">
	@Override public Block.OffsetType getOffsetType() {
		return Block.OffsetType.${data.offsetType};
	}
	</#if>

	<#if data.plantsGrowOn>
	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction direction, IPlantable plantable) {
		return true;
	}
	</#if>

	<#if data.isLadder>
	@Override public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
		return true;
	}
	</#if>

	<#if data.reactionToPushing != "NORMAL">
	@Override public PushReaction getPushReaction(BlockState state) {
		return PushReaction.${data.reactionToPushing};
	}
	</#if>

	<#if data.canRedstoneConnect>
	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		return true;
	}
	</#if>

	<#if !data.useLootTableForDrops>
		<#if data.dropAmount != 1 && !(data.customDrop?? && !data.customDrop.isEmpty())>
		@Override public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
			<#if data.blockBase?has_content && data.blockBase == "Door">
			if(state.get(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.LOWER)
				return Collections.emptyList();
			</#if>

			List<ItemStack> dropsOriginal = super.getDrops(state, builder);
			if(!dropsOriginal.isEmpty())
				return dropsOriginal;
			return Collections.singletonList(new ItemStack(this, ${data.dropAmount}));
		}
		<#elseif data.customDrop?? && !data.customDrop.isEmpty()>
		@Override public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
			<#if data.blockBase?has_content && data.blockBase == "Door">
			if(state.get(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.LOWER)
				return Collections.emptyList();
			</#if>

			List<ItemStack> dropsOriginal = super.getDrops(state, builder);
			if(!dropsOriginal.isEmpty())
				return dropsOriginal;
			return Collections.singletonList(${mappedMCItemToItemStackCode(data.customDrop, data.dropAmount)});
		}
		<#elseif data.blockBase?has_content && data.blockBase == "Slab">
		@Override public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
			List<ItemStack> dropsOriginal = super.getDrops(state, builder);
			if(!dropsOriginal.isEmpty())
				return dropsOriginal;
			return Collections.singletonList(new ItemStack(this, state.get(TYPE) == SlabType.DOUBLE ? 2 : 1));
		}
		<#else>
		@Override public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
			<#if data.blockBase?has_content && data.blockBase == "Door">
			if(state.get(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.LOWER)
				return Collections.emptyList();
			</#if>

			List<ItemStack> dropsOriginal = super.getDrops(state, builder);
			if(!dropsOriginal.isEmpty())
				return dropsOriginal;
			return Collections.singletonList(new ItemStack(this, 1));
		}
		</#if>
	</#if>

	<#if (hasProcedure(data.onTickUpdate) && !data.tickRandomly) || hasProcedure(data.onBlockAdded) >
	@Override public void onBlockAdded(BlockState blockstate, World world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onBlockAdded(blockstate, world, pos, oldState, moving);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<#if hasProcedure(data.onTickUpdate) && !data.tickRandomly>
		world.getPendingBlockTicks().scheduleTick(new BlockPos(x, y, z), this, ${data.tickRate});
	    </#if>
		<@procedureOBJToCode data.onBlockAdded/>
	}
	</#if>

	<#if hasProcedure(data.onRedstoneOn) || hasProcedure(data.onRedstoneOff) || hasProcedure(data.onNeighbourBlockChanges)>
	@Override
	public void neighborChanged(BlockState blockstate, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if (world.getRedstonePowerFromNeighbors(new BlockPos(x, y, z)) > 0) {
			<@procedureOBJToCode data.onRedstoneOn/>
		} else {
			<@procedureOBJToCode data.onRedstoneOff/>
		}
		<@procedureOBJToCode data.onNeighbourBlockChanges/>
	}
	</#if>

	<#if hasProcedure(data.onTickUpdate)>
	@Override public void <#if data.tickRandomly && (data.blockBase?has_content && data.blockBase == "Stairs")>randomTick<#else>tick</#if>
			(BlockState blockstate, ServerWorld world, BlockPos pos, Random random) {
		super.<#if data.tickRandomly && (data.blockBase?has_content && data.blockBase == "Stairs")>randomTick<#else>tick</#if>(blockstate, world, pos, random);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		<@procedureOBJToCode data.onTickUpdate/>

		<#if !data.tickRandomly>
		world.getPendingBlockTicks().scheduleTick(new BlockPos(x, y, z), this, ${data.tickRate});
		</#if>
	}
	</#if>

	<#if hasProcedure(data.onRandomUpdateEvent) || data.spawnParticles>
	@OnlyIn(Dist.CLIENT) @Override
	public void animateTick(BlockState blockstate, World world, BlockPos pos, Random random) {
		super.animateTick(blockstate, world, pos, random);
		PlayerEntity entity = Minecraft.getInstance().player;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<#if data.spawnParticles>
	        <@particles data.particleSpawningShape data.particleToSpawn data.particleSpawningRadious
	        data.particleAmount data.particleCondition/>
	    </#if>
		<@procedureOBJToCode data.onRandomUpdateEvent/>
	}
	</#if>

	<#if hasProcedure(data.onDestroyedByPlayer)>
	@Override
	public boolean removedByPlayer(BlockState blockstate, World world, BlockPos pos, PlayerEntity entity, boolean willHarvest, FluidState fluid) {
		boolean retval = super.removedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<@procedureOBJToCode data.onDestroyedByPlayer/>
		return retval;
	}
	</#if>

	<#if hasProcedure(data.onDestroyedByExplosion)>
	@Override public void onExplosionDestroy(World world, BlockPos pos, Explosion e) {
		super.onExplosionDestroy(world, pos, e);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<@procedureOBJToCode data.onDestroyedByExplosion/>
	}
	</#if>

	<#if hasProcedure(data.onStartToDestroy)>
	@Override public void onBlockClicked(BlockState blockstate, World world, BlockPos pos, PlayerEntity entity) {
		super.onBlockClicked(blockstate, world, pos, entity);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<@procedureOBJToCode data.onStartToDestroy/>
	}
	</#if>

	<#if hasProcedure(data.onEntityCollides)>
	@Override public void onEntityCollision(BlockState blockstate, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(blockstate, world, pos, entity);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<@procedureOBJToCode data.onEntityCollides/>
	}
	</#if>

	<#if hasProcedure(data.onEntityWalksOn)>
	@Override public void onEntityWalk(World world, BlockPos pos, Entity entity) {
		super.onEntityWalk(world, pos, entity);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		BlockState blockstate = world.getBlockState(pos);
		<@procedureOBJToCode data.onEntityWalksOn/>
	}
	</#if>

	<#if hasProcedure(data.onBlockPlayedBy)>
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState blockstate, LivingEntity entity, ItemStack itemstack) {
		super.onBlockPlacedBy(world, pos, blockstate, entity, itemstack);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<@procedureOBJToCode data.onBlockPlayedBy/>
	}
	</#if>

	<#if hasProcedure(data.onRightClicked) || data.shouldOpenGUIOnRightClick()>
	@Override
	public ActionResultType onBlockActivated(BlockState blockstate, World world, BlockPos pos, PlayerEntity entity, Hand hand, BlockRayTraceResult hit) {
		super.onBlockActivated(blockstate, world, pos, entity, hand, hit);

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		<#if data.shouldOpenGUIOnRightClick()>
			if(entity instanceof ServerPlayerEntity) {
				NetworkHooks.openGui((ServerPlayerEntity) entity, new INamedContainerProvider() {
					@Override public ITextComponent getDisplayName() {
						return new StringTextComponent("${data.name}");
					}
					@Override public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
						return new ${(data.guiBoundTo)}Gui.GuiContainerMod(id, inventory, new PacketBuffer(Unpooled.buffer()).writeBlockPos(new BlockPos(x, y, z)));
					}
				}, new BlockPos(x, y, z));
			}
		</#if>

		<#if hasProcedure(data.onRightClicked)>
			double hitX = hit.getHitVec().x;
			double hitY = hit.getHitVec().y;
			double hitZ = hit.getHitVec().z;
			Direction direction = hit.getFace();
			<#if hasReturnValue(data.onRightClicked)>
			ActionResultType result = <@procedureOBJToActionResultTypeCode data.onRightClicked/>;
			<#else>
			<@procedureOBJToCode data.onRightClicked/>
			</#if>
		</#if>

		<#if data.shouldOpenGUIOnRightClick() || !hasReturnValue(data.onRightClicked)>
		return ActionResultType.SUCCESS;
		<#else>
		return result;
		</#if>
	}
	</#if>

	<#if data.hasInventory>
		@Override public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			return tileEntity instanceof INamedContainerProvider ? (INamedContainerProvider) tileEntity : null;
		}

		@Override public boolean hasTileEntity(BlockState state) {
			return true;
		}

		@Override public TileEntity createTileEntity(BlockState state, BlockGetter world) {
		    return new CustomTileEntity();
		}

	    @Override
		public boolean eventReceived(BlockState state, World world, BlockPos pos, int eventID, int eventParam) {
			super.eventReceived(state, world, pos, eventID, eventParam);
			TileEntity tileentity = world.getTileEntity(pos);
			return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
		}

	    <#if data.inventoryDropWhenDestroyed>
		@Override public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		   if (state.getBlock() != newState.getBlock()) {
		      TileEntity tileentity = world.getTileEntity(pos);
		      if (tileentity instanceof CustomTileEntity) {
		         InventoryHelper.dropInventoryItems(world, pos, (CustomTileEntity) tileentity);
		         world.updateComparatorOutputLevel(pos, this);
		      }

		      super.onReplaced(state, world, pos, newState, isMoving);
		   }
		}
	    </#if>

	    <#if data.inventoryComparatorPower>
	    @Override public boolean hasComparatorInputOverride(BlockState state) {
			return true;
		}

	    @Override public int getComparatorInputOverride(BlockState blockState, World world, BlockPos pos) {
			TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity instanceof CustomTileEntity)
				return Container.calcRedstoneFromInventory((CustomTileEntity) tileentity);
			else
				return 0;
		}
	    </#if>
	</#if>

	<#if (data.spawnWorldTypes?size > 0)>
	private static Feature<OreFeatureConfig> feature = null;
	private static ConfiguredFeature<?, ?> configuredFeature = null;

	private static IRuleTestType<CustomRuleTest> CUSTOM_MATCH = null;

	private static class CustomRuleTest extends RuleTest {

		static final CustomRuleTest INSTANCE = new CustomRuleTest();
		static final com.mojang.serialization.Codec<CustomRuleTest> codec = com.mojang.serialization.Codec.unit(() -> INSTANCE);

		public boolean test(BlockState blockAt, Random random) {
			boolean blockCriteria = false;

			<#list data.blocksToReplace as replacementBlock>
			if(blockAt.getBlock() == ${mappedBlockToBlock(replacementBlock)})
				blockCriteria = true;
			</#list>

			return blockCriteria;
		}

		protected IRuleTestType<?> getType() {
			return CUSTOM_MATCH;
		}

	}

	private static class FeatureRegisterHandler {

		@SubscribeEvent public void registerFeature(RegistryEvent.Register<Feature<?>> event) {
			CUSTOM_MATCH = Registry.register(Registry.RULE_TEST, new ResourceLocation("${modid}:${registryname}_match"), () -> CustomRuleTest.codec);

			feature = new OreFeature(OreFeatureConfig.CODEC) {
				@Override public boolean generate(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, OreFeatureConfig config) {
					RegistryKey<World> dimensionType = world.getWorld().getDimensionKey();
					boolean dimensionCriteria = false;

    				<#list data.spawnWorldTypes as worldType>
						<#if worldType=="Surface">
							if(dimensionType == World.OVERWORLD)
								dimensionCriteria = true;
						<#elseif worldType=="Nether">
							if(dimensionType == World.THE_NETHER)
								dimensionCriteria = true;
						<#elseif worldType=="End">
							if(dimensionType == World.THE_END)
								dimensionCriteria = true;
						<#else>
							if(dimensionType == RegistryKey.getOrCreateKey(Registry.WORLD_KEY,
									new ResourceLocation("${generator.getResourceLocationForModElement(worldType.toString().replace("CUSTOM:", ""))}")))
								dimensionCriteria = true;
						</#if>
					</#list>

					if(!dimensionCriteria)
						return false;

					<#if hasProcedure(data.generateCondition)>
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();
					if (!<@procedureOBJToConditionCode data.generateCondition/>)
						return false;
					</#if>

					return super.generate(world, generator, rand, pos, config);
				}
			};

			configuredFeature = feature
					.withConfiguration(new OreFeatureConfig(CustomRuleTest.INSTANCE, block.defaultBlockState(), ${data.frequencyOnChunk}))
					.range(${data.maxGenerateHeight})
					.square()
					.func_242731_b(${data.frequencyPerChunks});

			event.getRegistry().register(feature.setRegistryName("${registryname}"));
			Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation("${modid}:${registryname}"), configuredFeature);
		}

	}

	@SubscribeEvent public void addFeatureToBiomes(BiomeLoadingEvent event) {
		<#if data.restrictionBiomes?has_content>
				boolean biomeCriteria = false;
			<#list data.restrictionBiomes as restrictionBiome>
				<#if restrictionBiome.canProperlyMap()>
					if (new ResourceLocation("${restrictionBiome}").equals(event.getName()))
						biomeCriteria = true;
				</#if>
			</#list>
				if (!biomeCriteria)
					return;
		</#if>
		event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).add(() -> configuredFeature);
	}
	</#if>

	<#if data.transparencyType != "SOLID">
	@OnlyIn(Dist.CLIENT) public static void registerRenderLayer() {
		<#if data.transparencyType == "CUTOUT">
		ItemBlockRenderTypes.setRenderLayer(${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}, RenderType.cutout());
		<#elseif data.transparencyType == "CUTOUT_MIPPED">
		ItemBlockRenderTypes.setRenderLayer(${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}, RenderType.cutoutMipped());
		<#elseif data.transparencyType == "TRANSLUCENT">
		ItemBlockRenderTypes.setRenderLayer(${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}, RenderType.translucent());
		<#else>
		ItemBlockRenderTypes.setRenderLayer(${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}, RenderType.solid());
		</#if>
	}
	<#elseif data.hasTransparency> <#-- for cases when user selected SOLID but checked transparency -->
	@OnlyIn(Dist.CLIENT) public static void registerRenderLayer() {
		ItemBlockRenderTypes.setRenderLayer(${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}, RenderType.cutout());
	}
	</#if>

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
