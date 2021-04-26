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
<#include "boundingboxes.java.ftl">
<#include "mcitems.ftl">
<#include "procedures.java.ftl">
<#include "particles.java.ftl">

package ${package}.block;

import net.minecraft.block.material.Material;

@${JavaModName}Elements.ModElement.Tag
public class ${name}Block extends ${JavaModName}Elements.ModElement {

	@ObjectHolder("${modid}:${registryname}")
	public static final Block block = null;

	<#if data.hasInventory>
	@ObjectHolder("${modid}:${registryname}")
	public static final TileEntityType<CustomTileEntity> tileEntityType = null;
	</#if>

	public ${name}Block (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});

		<#if (data.spawnWorldTypes?size > 0)>
		MinecraftForge.EVENT_BUS.register(this);
		</#if>

		<#if data.hasInventory>
		FMLJavaModLoadingContext.get().getModEventBus().register(new TileEntityRegisterHandler());
		</#if>
		<#if data.tintType != "No tint">
			FMLJavaModLoadingContext.get().getModEventBus().register(new BlockColorRegisterHandler());
			<#if data.isItemTinted>
			FMLJavaModLoadingContext.get().getModEventBus().register(new ItemColorRegisterHandler());
			</#if>
		</#if>
	}

	@Override public void initElements() {
		elements.blocks.add(() -> new CustomBlock());
		elements.items.add(() -> new BlockItem(block, new Item.Properties()
		                             .group(${data.creativeTab})
		                             ).setRegistryName(block.getRegistryName()));
	}

	<#if data.hasInventory>
	private static class TileEntityRegisterHandler {
		@SubscribeEvent public void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> event) {
			event.getRegistry().register(TileEntityType.Builder.create(CustomTileEntity::new, block).build(null).setRegistryName("${registryname}"));
		}
	}
	</#if>

	<#if data.transparencyType != "SOLID">
	@Override @OnlyIn(Dist.CLIENT) public void clientLoad(FMLClientSetupEvent event) {
		<#if data.transparencyType == "CUTOUT">
		RenderTypeLookup.setRenderLayer(block, RenderType.getCutout());
		<#elseif data.transparencyType == "CUTOUT_MIPPED">
		RenderTypeLookup.setRenderLayer(block, RenderType.getCutoutMipped());
		<#elseif data.transparencyType == "TRANSLUCENT">
		RenderTypeLookup.setRenderLayer(block, RenderType.getTranslucent());
		<#else>
		RenderTypeLookup.setRenderLayer(block, RenderType.getSolid());
		</#if>
	}
	<#elseif data.hasTransparency> <#-- for cases when user selected SOLID but checked transparency -->
	@Override @OnlyIn(Dist.CLIENT) public void clientLoad(FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(block, RenderType.getCutout());
	}
	</#if>

	<#if data.tintType != "No tint">
	private static class BlockColorRegisterHandler {
		@OnlyIn(Dist.CLIENT) @SubscribeEvent public void blockColorLoad(ColorHandlerEvent.Block event) {
			event.getBlockColors().register((bs, world, pos, index) -> {
				return world != null && pos != null ?
				<#if data.tintType == "Grass">
					BiomeColors.getGrassColor(world, pos) : GrassColors.get(0.5D, 1.0D);
				<#elseif data.tintType == "Foliage">
					BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefault();
				<#elseif data.tintType == "Water">
					BiomeColors.getWaterColor(world, pos) : -1;
				<#elseif data.tintType == "Sky">
					Minecraft.getInstance().world.getBiome(pos).getSkyColor() : 8562943;
				<#elseif data.tintType == "Fog">
					Minecraft.getInstance().world.getBiome(pos).getFogColor() : 12638463;
				<#else>
					Minecraft.getInstance().world.getBiome(pos).getWaterFogColor() : 329011;
				</#if>
			}, block);
		}
	}

		<#if data.isItemTinted>
		private static class ItemColorRegisterHandler {
			@OnlyIn(Dist.CLIENT) @SubscribeEvent public void itemColorLoad(ColorHandlerEvent.Item event) {
				event.getItemColors().register((stack, index) -> {
					<#if data.tintType == "Grass">
						return GrassColors.get(0.5D, 1.0D);
					<#elseif data.tintType == "Foliage">
						return FoliageColors.getDefault();
					<#elseif data.tintType == "Water">
						return 3694022;
					<#elseif data.tintType == "Sky">
						return 8562943;
					<#elseif data.tintType == "Fog">
						return 12638463;
					<#else>
						return 329011;
					</#if>
				}, block);
			}
		}
		</#if>
	</#if>

	public static class CustomBlock extends
			<#if data.hasGravity>
				FallingBlock
			<#elseif data.blockBase?has_content>
				${data.blockBase}Block
			<#else>
				Block
			</#if>
			<#if data.isWaterloggable>
            implements IWaterLoggable
            </#if> {

		<#if data.rotationMode == 1 || data.rotationMode == 3>
		public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
		<#elseif data.rotationMode == 2 || data.rotationMode == 4 || data.rotationMode == 5>
		public static final DirectionProperty FACING = DirectionalBlock.FACING;
        </#if>
        <#if data.isWaterloggable>
        public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
        </#if>

		public CustomBlock() {
			<#if data.blockBase?has_content && data.blockBase == "Stairs">
			super(new Block(Block.Properties.create(Material.ROCK)
					<#if data.unbreakable>
					.hardnessAndResistance(-1, 3600000)
					<#else>
					.hardnessAndResistance(${data.hardness}f, ${data.resistance}f)
					</#if>
					).getDefaultState(),
			<#elseif data.blockBase?has_content && data.blockBase == "Wall">
			super(
			<#elseif data.blockBase?has_content && data.blockBase == "Fence">
			super(
			<#else>
			super(
			</#if>

			Block.Properties.create(Material.${data.material})
					.sound(SoundType.${data.soundOnStep})
					<#if data.unbreakable>
					.hardnessAndResistance(-1, 3600000)
					<#else>
					.hardnessAndResistance(${data.hardness}f, ${data.resistance}f)
					</#if>
					.setLightLevel(s -> ${data.luminance})
					<#if data.destroyTool != "Not specified">
					.harvestLevel(${data.breakHarvestLevel})
					.harvestTool(ToolType.${data.destroyTool?upper_case})
					.setRequiresTool()
					</#if>
					<#if data.isNotColidable>
					.doesNotBlockMovement()
					</#if>
					<#if data.slipperiness != 0.6>
					.slipperiness(${data.slipperiness}f)
					</#if>
					<#if data.speedFactor != 1.0>
					.speedFactor(${data.speedFactor}f)
					</#if>
					<#if data.jumpFactor != 1.0>
					.jumpFactor(${data.jumpFactor}f)
					</#if>
					<#if data.hasTransparency || (data.blockBase?has_content && data.blockBase == "Leaves")>
					.notSolid()
					</#if>
					<#if data.tickRandomly>
					.tickRandomly()
					</#if>
					<#if data.emissiveRendering>
					.setNeedsPostProcessing((bs, br, bp) -> true).setEmmisiveRendering((bs, br, bp) -> true)
					</#if>
					<#if data.hasTransparency>
					.setOpaque((bs, br, bp) -> false)
					</#if>
			);

            <#if data.rotationMode != 0 || data.isWaterloggable>
            this.setDefaultState(this.stateContainer.getBaseState()
                                     <#if data.rotationMode == 1 || data.rotationMode == 3>
                                     .with(FACING, Direction.NORTH)
                                     <#elseif data.rotationMode == 2 || data.rotationMode == 4>
                                     .with(FACING, Direction.NORTH)
                                     <#elseif data.rotationMode == 5>
                                     .with(FACING, Direction.SOUTH)
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
   		@Override ${mcc.getMethod("net.minecraft.block.WallBlock", "getStateForPlacement", "BlockItemUseContext")}
   		@Override ${mcc.getMethod("net.minecraft.block.WallBlock", "updatePostPlacement", "BlockState", "Direction", "BlockState", "IWorld", "BlockPos", "BlockPos")}
   		${mcc.getMethod("net.minecraft.block.WallBlock", "func_235625_a_", "IWorldReader", "BlockState", "BlockPos", "BlockState")}
   		${mcc.getMethod("net.minecraft.block.WallBlock", "func_235627_a_", "IWorldReader", "BlockPos", "BlockState", "BlockPos", "BlockState", "Direction")}
   		${mcc.getMethod("net.minecraft.block.WallBlock", "func_235626_a_", "IWorldReader", "BlockState", "BlockPos", "BlockState", "boolean", "boolean", "boolean", "boolean")}
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
		@Override @OnlyIn(Dist.CLIENT) public void addInformation(ItemStack itemstack, IBlockReader world, List<ITextComponent> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			<#list data.specialInfo as entry>
			list.add(new StringTextComponent("${JavaConventions.escapeStringForJava(entry)}"));
            </#list>
		}
        </#if>

		<#if data.displayFluidOverlay>
		@Override public boolean shouldDisplayFluidOverlay(BlockState state, IBlockDisplayReader world, BlockPos pos, FluidState fluidstate) {
			return true;
		}
		</#if>

		<#if data.beaconColorModifier?has_content>
		@Override public float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos) {
			return new float[] { ${data.beaconColorModifier.getRed()/255}f, ${data.beaconColorModifier.getGreen()/255}f, ${data.beaconColorModifier.getBlue()/255}f };
		}
		</#if>

		<#if data.connectedSides>
        @OnlyIn(Dist.CLIENT) public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
			return adjacentBlockState.getBlock() == this ? true : super.isSideInvisible(state, adjacentBlockState, side);
		}
		</#if>

		<#if data.lightOpacity == 0>
		@Override public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
			return true;
		}
		</#if>

		<#if data.boundingBoxes?? && !data.blockBase?? && !data.isFullCube()>
		@Override public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
			<#if data.isBoundingBoxEmpty()>
				return VoxelShapes.empty();
			<#else>
				<#if !data.disableOffset>Vector3d offset = state.getOffset(world, pos);</#if>
				<@boundingBoxWithRotation data.positiveBoundingBoxes() data.negativeBoundingBoxes() data.disableOffset data.rotationMode/>
			</#if>
		}
		</#if>

		<#if data.rotationMode != 0>
		@Override protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		    <#if data.isWaterloggable>
      		    builder.add(FACING, WATERLOGGED);
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
					if((Direction) state.get(FACING) == Direction.WEST || (Direction) state.get(FACING) == Direction.EAST) {
						return state.with(FACING, Direction.UP);
					} else if((Direction) state.get(FACING) == Direction.UP || (Direction) state.get(FACING) == Direction.DOWN) {
						return state.with(FACING, Direction.WEST);
					}
				}
				return state;
			}
			</#if>

		@Override
		public BlockState getStateForPlacement(BlockItemUseContext context) {
		    <#if data.rotationMode == 4>
		    Direction facing = context.getFace();
		    </#if>
		    <#if data.rotationMode == 5>
            Direction facing = context.getFace();
            if (facing == Direction.WEST || facing == Direction.EAST)
                facing = Direction.UP;
            else if (facing == Direction.NORTH || facing == Direction.SOUTH)
                facing = Direction.EAST;
            else
                facing = Direction.SOUTH;
            </#if>
            <#if data.isWaterloggable>
            boolean flag = context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER;
            </#if>;
			<#if data.rotationMode != 3>
			return this.getDefaultState()
			        <#if data.rotationMode == 1>
			        .with(FACING, context.getPlacementHorizontalFacing().getOpposite())
			        <#elseif data.rotationMode == 2>
			        .with(FACING, context.getNearestLookingDirection().getOpposite())
                    <#elseif data.rotationMode == 4 || data.rotationMode == 5>
			        .with(FACING, facing)
			        </#if>
			        <#if data.isWaterloggable>
			        .with(WATERLOGGED, flag)
			        </#if>
			<#elseif data.rotationMode == 3>
            if (context.getFace() == Direction.UP || context.getFace() == Direction.DOWN)
                return this.getDefaultState()
                        .with(FACING, Direction.NORTH)
                        <#if data.isWaterloggable>
                        .with(WATERLOGGED, flag)
                        </#if>;
            return this.getDefaultState()
                    .with(FACING, context.getFace())
                    <#if data.isWaterloggable>
                    .with(WATERLOGGED, flag)
                    </#if>
			</#if>;
		}
        </#if>

        <#if data.isWaterloggable>
            <#if data.rotationMode == 0>
            @Override
            public BlockState getStateForPlacement(BlockItemUseContext context) {
            boolean flag = context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER;
                return this.getDefaultState().with(WATERLOGGED, flag);
            }
            @Override protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
                builder.add(WATERLOGGED);
            }
            </#if>

        @Override public FluidState getFluidState(BlockState state) {
            return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
        }
	
		@Override public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
	        if (state.get(WATERLOGGED)) {
		        world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
	        }
	        return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
        }
        </#if>

		<#if data.enchantPowerBonus != 0>
		@Override public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
			return ${data.enchantPowerBonus}f;
		}
        </#if>

		<#if data.isReplaceable>
        @Override public boolean isReplaceable(BlockState state, BlockItemUseContext context) {
			return context.getItem().getItem() != this.asItem();
		}
        </#if>

		<#if data.flammability != 0>
		@Override public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
			return ${data.flammability};
		}
		</#if>

		<#if data.fireSpreadSpeed != 0>
		@Override public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
			return ${data.fireSpreadSpeed};
		}
		</#if>

		<#if data.creativePickItem?? && !data.creativePickItem.isEmpty()>
		@Override public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        	return ${mappedMCItemToItemStackCode(data.creativePickItem, 1)};
    	}
        </#if>

		<#if generator.map(data.colorOnMap, "mapcolors") != "DEFAULT">
		@Override public MaterialColor getMaterialColor() {
        	return MaterialColor.${generator.map(data.colorOnMap, "mapcolors")};
    	}
		</#if>

		<#if generator.map(data.aiPathNodeType, "pathnodetypes") != "DEFAULT">
		@Override public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, MobEntity entity) {
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
		public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, IPlantable plantable) {
			return true;
		}
        </#if>

		<#if data.isLadder>
		@Override public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
			return true;
		}
		</#if>

		<#if data.reactionToPushing != "NORMAL">
		@Override public PushReaction getPushReaction(BlockState state) {
			return PushReaction.${data.reactionToPushing};
		}
		</#if>

        <#if data.canProvidePower>
        @Override
		public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
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
		@Override public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moving) {
			super.onBlockAdded(state, world, pos, oldState, moving);
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
		public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
			super.neighborChanged(state, world, pos, neighborBlock, fromPos, moving);
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
		@Override public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
			super.tick(state, world, pos, random);
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
		public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
			super.animateTick(state, world, pos, random);
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
		public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity entity, boolean willHarvest, FluidState fluid) {
			boolean retval = super.removedByPlayer(state, world, pos, entity, willHarvest, fluid);
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
		@Override public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity entity) {
			super.onBlockClicked(state, world, pos, entity);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<@procedureOBJToCode data.onStartToDestroy/>
		}
        </#if>

        <#if hasProcedure(data.onEntityCollides)>
		@Override public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
			super.onEntityCollision(state, world, pos, entity);
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
			<@procedureOBJToCode data.onEntityWalksOn/>
		}
        </#if>

        <#if hasProcedure(data.onBlockPlayedBy)>
		@Override
		public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack itemstack) {
			super.onBlockPlacedBy(world, pos, state, entity, itemstack);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<@procedureOBJToCode data.onBlockPlayedBy/>
		}
        </#if>

        <#if hasProcedure(data.onRightClicked) || data.openGUIOnRightClick>
		@Override
		public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity entity, Hand hand, BlockRayTraceResult hit) {
			super.onBlockActivated(state, world, pos, entity, hand, hit);

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			<#if data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>" && data.openGUIOnRightClick && (data.guiBoundTo)?has_content>
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
				Direction direction = hit.getFace();
				<@procedureOBJToCode data.onRightClicked/>
			</#if>

			return ActionResultType.SUCCESS;
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
			
			@Override public TileEntity createTileEntity(BlockState state, IBlockReader world) {
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

	}

<#if data.hasInventory>
    public static class CustomTileEntity extends LockableLootTileEntity implements ISidedInventory {

		private NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(${data.inventorySize}, ItemStack.EMPTY);

		protected CustomTileEntity() {
			super(tileEntityType);
		}

		@Override public void read(BlockState blockState, CompoundNBT compound) {
			super.read(blockState, compound);

			if (!this.checkLootAndRead(compound)) {
			    this.stacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
			}

			ItemStackHelper.loadAllItems(compound, this.stacks);

			<#if data.hasEnergyStorage>
			if(compound.get("energyStorage") != null)
				CapabilityEnergy.ENERGY.readNBT(energyStorage, null, compound.get("energyStorage"));
			</#if>

			<#if data.isFluidTank>
			if(compound.get("fluidTank") != null)
				CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(fluidTank, null, compound.get("fluidTank"));
			</#if>
   		}

   		@Override public CompoundNBT write(CompoundNBT compound) {
   		   super.write(compound);

		   if (!this.checkLootAndWrite(compound)) {
   		       ItemStackHelper.saveAllItems(compound, this.stacks);
   		   }

           <#if data.hasEnergyStorage>
		   compound.put("energyStorage", CapabilityEnergy.ENERGY.writeNBT(energyStorage, null));
		   </#if>

           <#if data.isFluidTank>
		   compound.put("fluidTank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(fluidTank, null));
		   </#if>

   		   return compound;
   		}

		@Override public SUpdateTileEntityPacket getUpdatePacket() {
			return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
		}

		@Override public CompoundNBT getUpdateTag() {
			return this.write(new CompoundNBT());
		}

		@Override public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
			this.read(this.getBlockState(), pkt.getNbtCompound());
		}

		@Override public int getSizeInventory() {
			return stacks.size();
		}

		@Override public boolean isEmpty() {
			for (ItemStack itemstack : this.stacks)
				if (!itemstack.isEmpty())
					return false;
			return true;
		}

		@Override public ITextComponent getDefaultName() {
			return new StringTextComponent("${registryname}");
		}

		@Override public int getInventoryStackLimit() {
			return ${data.inventoryStackSize};
		}

		@Override public Container createMenu(int id, PlayerInventory player) {
			<#if !data.guiBoundTo?has_content || data.guiBoundTo == "<NONE>" || !(data.guiBoundTo)?has_content>
				return ChestContainer.createGeneric9X3(id, player, this);
			<#else>
				return new ${(data.guiBoundTo)}Gui.GuiContainerMod(id, player, new PacketBuffer(Unpooled.buffer()).writeBlockPos(this.getPos()));
			</#if>
		}

		@Override public ITextComponent getDisplayName() {
			return new StringTextComponent("${data.name}");
		}

		@Override protected NonNullList<ItemStack> getItems() {
			return this.stacks;
		}

		@Override protected void setItems(NonNullList<ItemStack> stacks) {
			this.stacks = stacks;
		}

		@Override public boolean isItemValidForSlot(int index, ItemStack stack) {
			<#list data.inventoryOutSlotIDs as id>
			    if (index == ${id})
					return false;
			</#list>
			return true;
		}

		<#-- START: ISidedInventory -->
		@Override public int[] getSlotsForFace(Direction side) {
			return IntStream.range(0, this.getSizeInventory()).toArray();
		}

		@Override public boolean canInsertItem(int index, ItemStack stack, @Nullable Direction direction) {
			return this.isItemValidForSlot(index, stack);
		}

		@Override public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
			<#list data.inventoryInSlotIDs as id>
			    if (index == ${id})
					return false;
			</#list>
			return true;
		}
		<#-- END: ISidedInventory -->

		private final LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.values());

		<#if data.hasEnergyStorage>
		private final EnergyStorage energyStorage = new EnergyStorage(${data.energyCapacity}, ${data.energyMaxReceive}, ${data.energyMaxExtract}, ${data.energyInitial}) {
			@Override public int receiveEnergy(int maxReceive, boolean simulate) {
				int retval = super.receiveEnergy(maxReceive, simulate);
				if(!simulate) {
					markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
				}
				return retval;
			}

			@Override public int extractEnergy(int maxExtract, boolean simulate) {
				int retval = super.extractEnergy(maxExtract, simulate);
				if(!simulate) {
					markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
				}
				return retval;
			}
		};
		</#if>

		<#if data.isFluidTank>
			<#if data.fluidRestrictions?has_content>
			private final FluidTank fluidTank = new FluidTank(${data.fluidCapacity}, fs -> {
				<#list data.fluidRestrictions as fluidRestriction>
					<#if fluidRestriction.getUnmappedValue().startsWith("CUSTOM:")>
						<#if fluidRestriction.getUnmappedValue().endsWith(":Flowing")>
						if(fs.getFluid() == ${(fluidRestriction.getUnmappedValue().replace("CUSTOM:", "").replace(":Flowing", ""))}Block.flowing) return true;
						<#else>
						if(fs.getFluid() == ${(fluidRestriction.getUnmappedValue().replace("CUSTOM:", ""))}Block.still) return true;
						</#if>
					<#else>
					if(fs.getFluid() == Fluids.${fluidRestriction}) return true;
					</#if>
				</#list>

				return false;
			}) {
				@Override protected void onContentsChanged() {
					super.onContentsChanged();
					markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
				}
			};
			<#else>
			private final FluidTank fluidTank = new FluidTank(${data.fluidCapacity}) {
				@Override protected void onContentsChanged() {
					super.onContentsChanged();
					markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
				}
			};
			</#if>
		</#if>

		@Override public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
			if (!this.removed && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				return handlers[facing.ordinal()].cast();

			<#if data.hasEnergyStorage>
			if (!this.removed && capability == CapabilityEnergy.ENERGY)
				return LazyOptional.of(() -> energyStorage).cast();
			</#if>

			<#if data.isFluidTank>
			if (!this.removed && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
				return LazyOptional.of(() -> fluidTank).cast();
			</#if>

			return super.getCapability(capability, facing);
		}

		@Override public void remove() {
			super.remove();
			for(LazyOptional<? extends IItemHandler> handler : handlers)
				handler.invalidate();
		}

	}
</#if>

<#if (data.spawnWorldTypes?size > 0)>
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
		event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).add(() -> new OreFeature(OreFeatureConfig.CODEC) {
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

				<#if hasCondition(data.generateCondition)>
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				if (!<@procedureOBJToConditionCode data.generateCondition/>)
					return false;
				</#if>

				return super.generate(world, generator, rand, pos, config);
			}}
			.withConfiguration(new OreFeatureConfig(new BlockMatchRuleTest(
				${data.blocksToReplace?has_content?then(mappedBlockToBlockStateCode(data.blocksToReplace[0]) + ".getBlock()", "Blocks.BARRIER")}
			) {
				public boolean test(BlockState blockAt, Random random) {
					boolean blockCriteria = false;
					<#list data.blocksToReplace as replacementBlock>
					if(blockAt.getBlock() == ${mappedBlockToBlockStateCode(replacementBlock)}.getBlock())
						blockCriteria = true;
					</#list>
					return blockCriteria;
				}

				protected IRuleTestType<?> getType() {
					return IRuleTestType.BLOCK_MATCH;
				}
			}, block.getDefaultState(), ${data.frequencyOnChunk}))
			.range(${data.maxGenerateHeight}).square().func_242731_b(${data.frequencyPerChunks}));
	}
</#if>

}
<#-- @formatter:on -->
