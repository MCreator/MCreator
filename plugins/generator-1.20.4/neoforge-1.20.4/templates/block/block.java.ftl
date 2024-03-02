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
<#include "../boundingboxes.java.ftl">
<#include "../mcitems.ftl">
<#include "../procedures.java.ftl">
<#include "../triggers.java.ftl">

package ${package}.block;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

<#compress>
public class ${name}Block extends
	<#if data.hasGravity>
		FallingBlock
	<#elseif data.blockBase?has_content>
		${data.blockBase?replace("Stairs", "Stair")?replace("Pane", "IronBars")}Block
	<#else>
		Block
	</#if>

	<#assign interfaces = []>
	<#if data.isWaterloggable>
		<#assign interfaces += ["SimpleWaterloggedBlock"]>
	</#if>
	<#if data.hasInventory>
		<#assign interfaces += ["EntityBlock"]>
	</#if>
	<#if data.isBonemealable>
		<#assign interfaces += ["BonemealableBlock"]>
	</#if>
	<#if interfaces?size gt 0>
		implements ${interfaces?join(",")}
	</#if>
{

	<#if data.rotationMode == 1 || data.rotationMode == 3>
		public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
		<#if data.enablePitch>
		public static final EnumProperty<AttachFace> FACE = FaceAttachedHorizontalDirectionalBlock.FACE;
		</#if>
	<#elseif data.rotationMode == 2 || data.rotationMode == 4>
		public static final DirectionProperty FACING = DirectionalBlock.FACING;
	<#elseif data.rotationMode == 5>
		public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
	</#if>
	<#if data.isWaterloggable>
		public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	</#if>

	<#if data.hasGravity>
	public static final MapCodec<${name}Block> CODEC = simpleCodec(${name}Block::new);

	public MapCodec<${name}Block> codec() {
		return CODEC;
	}

	public ${name}Block(BlockBehaviour.Properties ignored) {
		this();
	}
	</#if>

	<#macro blockProperties>
		BlockBehaviour.Properties.of()
		${data.material}
		<#if generator.map(data.colorOnMap, "mapcolors") != "DEFAULT">
			.mapColor(MapColor.${generator.map(data.colorOnMap, "mapcolors")})
		</#if>
		<#if data.isCustomSoundType>
			.sound(new DeferredSoundType(1.0f, 1.0f,
				() -> BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("${data.breakSound}")),
				() -> BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("${data.stepSound}")),
				() -> BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("${data.placeSound}")),
				() -> BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("${data.hitSound}")),
				() -> BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("${data.fallSound}"))
			))
		<#else>
			.sound(SoundType.${data.soundOnStep})
		</#if>
		<#if data.unbreakable>
			.strength(-1, 3600000)
		<#elseif (data.hardness == 0) && (data.resistance == 0)>
			.instabreak()
		<#elseif data.hardness == data.resistance>
			.strength(${data.hardness}f)
		<#else>
			.strength(${data.hardness}f, ${data.resistance}f)
		</#if>
		<#if data.luminance != 0>
			.lightLevel(s -> ${data.luminance})
		</#if>
		<#if data.requiresCorrectTool>
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
		<#if data.reactionToPushing != "NORMAL">
			.pushReaction(PushReaction.${data.reactionToPushing})
		</#if>
		<#if data.emissiveRendering>
			.hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true)
		</#if>
		<#if data.hasTransparency>
			.isRedstoneConductor((bs, br, bp) -> false)
		</#if>
		<#if (data.boundingBoxes?? && !data.blockBase?? && !data.isFullCube() && data.offsetType != "NONE")
				|| (data.blockBase?has_content && !data.isFullCube())>
			.dynamicShape()
		</#if>
		<#if data.offsetType != "NONE">
			.offsetType(Block.OffsetType.${data.offsetType})
		</#if>
		<#if data.blockBase?has_content && (
				data.blockBase == "FenceGate" ||
				data.blockBase == "PressurePlate" ||
				data.blockBase == "Fence" ||
				data.blockBase == "Wall")>
			.forceSolidOn()
		</#if>
		<#if data.blockBase?has_content && data.blockBase == "EndRod">
			.forceSolidOff()
		</#if>
	</#macro>

	public ${name}Block() {
		<#if data.blockBase?has_content && data.blockBase == "Stairs">
			super(() -> Blocks.AIR.defaultBlockState(), <@blockProperties/>);
		<#elseif data.blockBase?has_content && data.blockBase == "PressurePlate">
		    <#if data.material.getUnmappedValue() == "WOOD">
		        super(BlockSetType.OAK, <@blockProperties/>);
		    <#else>
		        super(BlockSetType.IRON, <@blockProperties/>);
		    </#if>
		<#elseif data.blockBase?has_content && data.blockBase == "Button">
			<#if data.material.getUnmappedValue() == "WOOD">
		        super(BlockSetType.OAK, 30, <@blockProperties/>);
			<#else>
		        super(BlockSetType.STONE, 20, <@blockProperties/>);
			</#if>
		<#elseif data.blockBase?has_content && (data.blockBase == "TrapDoor" || data.blockBase == "Door")>
			<#if data.material.getUnmappedValue() == "IRON">
				super(BlockSetType.IRON, <@blockProperties/>);
			<#elseif data.material.getUnmappedValue() == "WOOD">
				super(BlockSetType.OAK, <@blockProperties/>);
			<#else>
				super(BlockSetType.STONE, <@blockProperties/>);
			</#if>
		<#elseif data.blockBase?has_content && data.blockBase == "FenceGate">
			super(WoodType.OAK, <@blockProperties/>);
		<#else>
			super(<@blockProperties/>);
		</#if>

	    <#if data.rotationMode != 0 || data.isWaterloggable>
	    this.registerDefaultState(this.stateDefinition.any()
	    	<#if data.rotationMode == 1 || data.rotationMode == 3>
	    	.setValue(FACING, Direction.NORTH)
	    	    <#if data.enablePitch>
	    	    .setValue(FACE, AttachFace.WALL)
	    	    </#if>
	    	<#elseif data.rotationMode == 2 || data.rotationMode == 4>
	    	.setValue(FACING, Direction.NORTH)
	    	<#elseif data.rotationMode == 5>
	    	.setValue(AXIS, Direction.Axis.Y)
	    	</#if>
	    	<#if data.isWaterloggable>
	    	.setValue(WATERLOGGED, false)
	    	</#if>
	    );
		</#if>
	}

	<#if data.blockBase?has_content && data.blockBase == "Stairs">
   	@Override public float getExplosionResistance() {
		return ${data.resistance}f;
   	}

   	@Override public boolean isRandomlyTicking(BlockState state) {
		return ${data.tickRandomly?c};
   	}
	</#if>

	<@addSpecialInformation data.specialInformation, true/>

	<#if data.displayFluidOverlay>
	@Override public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidstate) {
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
		return adjacentBlockState.getBlock() == this ? true : super.skipRendering(state, adjacentBlockState, side);
	}
	</#if>

	<#if (!data.blockBase?has_content || data.blockBase == "Leaves") && data.lightOpacity == 0>
	@Override public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return <#if data.isWaterloggable>state.getFluidState().isEmpty()<#else>true</#if>;
	}
	</#if>

	<#if !data.blockBase?has_content || data.blockBase == "Leaves" || data.lightOpacity != 15>
	@Override public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return ${data.lightOpacity};
	}
	</#if>

	<#if data.hasTransparency && !data.blockBase?has_content>
	@Override public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}
	</#if>

	<#if data.boundingBoxes?? && !data.blockBase?? && !data.isFullCube()>
	@Override public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		<#if data.isBoundingBoxEmpty()>
			return Shapes.empty();
		<#else>
			<#if !data.shouldDisableOffset()>Vec3 offset = state.getOffset(world, pos);</#if>
			<@boundingBoxWithRotation data.positiveBoundingBoxes() data.negativeBoundingBoxes() data.shouldDisableOffset() data.rotationMode data.enablePitch/>
		</#if>
	}
	</#if>

	<#if data.rotationMode != 0 || data.isWaterloggable>
	@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		<#assign props = []>
		<#if data.rotationMode == 5>
			<#assign props += ["AXIS"]>
		<#elseif data.rotationMode != 0>
			<#assign props += ["FACING"]>
			<#if (data.rotationMode == 1 || data.rotationMode == 3) && data.enablePitch>
				<#assign props += ["FACE"]>
			</#if>
		</#if>
		<#if data.isWaterloggable>
			<#assign props += ["WATERLOGGED"]>
		</#if>
		builder.add(${props?join(", ")});
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		<#if data.isWaterloggable>
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		</#if>
		<#if data.rotationMode != 3>
		return this.defaultBlockState()
			<#if data.rotationMode == 1>
			    <#if data.enablePitch>
			    .setValue(FACE, faceForDirection(context.getNearestLookingDirection()))
			    </#if>
			.setValue(FACING, context.getHorizontalDirection().getOpposite())
			<#elseif data.rotationMode == 2>
			.setValue(FACING, context.getNearestLookingDirection().getOpposite())
			<#elseif data.rotationMode == 4>
			.setValue(FACING, context.getClickedFace())
			<#elseif data.rotationMode == 5>
			.setValue(AXIS, context.getClickedFace().getAxis())
			</#if>
			<#if data.isWaterloggable>
			.setValue(WATERLOGGED, flag)
			</#if>;
		<#elseif data.rotationMode == 3>
	    if (context.getClickedFace().getAxis() == Direction.Axis.Y)
	        return this.defaultBlockState()
	    		<#if data.enablePitch>
	    		    .setValue(FACE, context.getClickedFace().getOpposite() == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR)
	    		    .setValue(FACING, context.getHorizontalDirection())
	    		<#else>
	    		    .setValue(FACING, Direction.NORTH)
	    		</#if>
	    		<#if data.isWaterloggable>
	    		.setValue(WATERLOGGED, flag)
	    		</#if>;

	    return this.defaultBlockState()
	    	<#if data.enablePitch>
	    	    .setValue(FACE, AttachFace.WALL)
	    	</#if>
	    	.setValue(FACING, context.getClickedFace())
	    	<#if data.isWaterloggable>
	    	.setValue(WATERLOGGED, flag)
	    	</#if>;
		</#if>
	}
	</#if>

	<#if data.rotationMode != 0>
		<#if data.rotationMode != 5>
		public BlockState rotate(BlockState state, Rotation rot) {
			return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
		}

		public BlockState mirror(BlockState state, Mirror mirrorIn) {
			return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
		}
		<#else>
		@Override public BlockState rotate(BlockState state, Rotation rot) {
			if(rot == Rotation.CLOCKWISE_90 || rot == Rotation.COUNTERCLOCKWISE_90) {
				if (state.getValue(AXIS) == Direction.Axis.X) {
					return state.setValue(AXIS, Direction.Axis.Z);
				} else if (state.getValue(AXIS) == Direction.Axis.Z) {
					return state.setValue(AXIS, Direction.Axis.X);
				}
			}
			return state;
		}
		</#if>

		<#if data.rotationMode == 1 && data.enablePitch>
		private AttachFace faceForDirection(Direction direction) {
			if (direction.getAxis() == Direction.Axis.Y)
				return direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR;
			else
				return AttachFace.WALL;
		}
		</#if>
	</#if>

	<#if hasProcedure(data.placingCondition)>
	@Override public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
		if (worldIn instanceof LevelAccessor world) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return <@procedureOBJToConditionCode data.placingCondition/>;
		}
		return super.canSurvive(blockstate, worldIn, pos);
	}
	</#if>

	<#if data.isWaterloggable>
	@Override public FluidState getFluidState(BlockState state) {
	    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	</#if>

	<#if data.isWaterloggable || hasProcedure(data.placingCondition)>
	@Override public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
	    <#if data.isWaterloggable>
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		</#if>
		return <#if hasProcedure(data.placingCondition)>
		!state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState() :
		</#if> super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}
	</#if>

	<#if data.enchantPowerBonus != 0>
	@Override public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
		return ${data.enchantPowerBonus}f;
	}
	</#if>

	<#if data.isReplaceable>
	@Override public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return context.getItemInHand().getItem() != this.asItem();
	}
	</#if>

	<#if data.canProvidePower && data.emittedRedstonePower??>
	@Override public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override public int getSignal(BlockState blockstate, BlockGetter blockAccess, BlockPos pos, Direction direction) {
		<#if hasProcedure(data.emittedRedstonePower)>
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			Level world = (Level) blockAccess;
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
	@Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		return ${mappedMCItemToItemStackCode(data.creativePickItem, 1)};
	}
	</#if>

	<#if generator.map(data.aiPathNodeType, "pathnodetypes") != "DEFAULT">
	@Override public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return BlockPathTypes.${generator.map(data.aiPathNodeType, "pathnodetypes")};
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

	<#if data.canRedstoneConnect>
	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		return true;
	}
	</#if>

	<#if data.requiresCorrectTool>
	@Override public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
		if(player.getInventory().getSelected().getItem() instanceof
				<#if data.destroyTool == "pickaxe">PickaxeItem
				<#elseif data.destroyTool == "axe">AxeItem
				<#elseif data.destroyTool == "shovel">ShovelItem
				<#elseif data.destroyTool == "hoe">HoeItem
				<#else>TieredItem</#if> tieredItem)
			return tieredItem.getTier().getLevel() >= ${data.breakHarvestLevel};
		return false;
	}
	</#if>

	<@onBlockAdded data.onBlockAdded, hasProcedure(data.onTickUpdate) && data.shouldScheduleTick(), data.tickRate/>

	<@onRedstoneOrNeighborChanged data.onRedstoneOn, data.onRedstoneOff, data.onNeighbourBlockChanges/>

	<#if hasProcedure(data.onTickUpdate)>
	@Override public void <#if data.tickRandomly && (data.blockBase?has_content && data.blockBase == "Stairs")>randomTick<#else>tick</#if>
			(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.<#if data.tickRandomly && (data.blockBase?has_content && data.blockBase == "Stairs")>randomTick<#else>tick</#if>(blockstate, world, pos, random);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		<@procedureOBJToCode data.onTickUpdate/>

		<#if data.shouldScheduleTick()>
		world.scheduleTick(pos, this, ${data.tickRate});
		</#if>
	}
	</#if>

	<#if hasProcedure(data.onRandomUpdateEvent)>
	@OnlyIn(Dist.CLIENT) @Override public void animateTick(BlockState blockstate, Level world, BlockPos pos, RandomSource random) {
		super.animateTick(blockstate, world, pos, random);
		Player entity = Minecraft.getInstance().player;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		<@procedureOBJToCode data.onRandomUpdateEvent/>
	}
	</#if>

	<@onDestroyedByPlayer data.onDestroyedByPlayer/>

	<@onDestroyedByExplosion data.onDestroyedByExplosion/>

	<@onStartToDestroy data.onStartToDestroy/>

	<@onEntityCollides data.onEntityCollides/>

	<@onEntityWalksOn data.onEntityWalksOn/>

	<@onHitByProjectile data.onHitByProjectile/>

	<@onBlockPlacedBy data.onBlockPlayedBy/>

	<#if hasProcedure(data.onRightClicked) || data.shouldOpenGUIOnRightClick()>
	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.use(blockstate, world, pos, entity, hand, hit);
		<#if data.shouldOpenGUIOnRightClick()>
		if(entity instanceof ServerPlayer player) {
			player.openMenu(new MenuProvider() {
				@Override public Component getDisplayName() {
					return Component.literal("${data.name}");
				}
				@Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					return new ${data.guiBoundTo}Menu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
				}
			}, pos);
		}
		</#if>

		<#if hasProcedure(data.onRightClicked)>
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			double hitX = hit.getLocation().x;
			double hitY = hit.getLocation().y;
			double hitZ = hit.getLocation().z;
			Direction direction = hit.getDirection();
			<#if hasReturnValueOf(data.onRightClicked, "actionresulttype")>
			InteractionResult result = <@procedureOBJToInteractionResultCode data.onRightClicked/>;
			<#else>
			<@procedureOBJToCode data.onRightClicked/>
			</#if>
		</#if>

		<#if data.shouldOpenGUIOnRightClick() || !hasReturnValueOf(data.onRightClicked, "actionresulttype")>
		return InteractionResult.SUCCESS;
		<#else>
		return result;
		</#if>
	}
	</#if>

	<#if data.isBonemealable>
	<@bonemealEvents data.isBonemealTargetCondition, data.bonemealSuccessCondition, data.onBonemealSuccess/>
	</#if>

	<#if data.hasInventory>
		@Override public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
			BlockEntity tileEntity = worldIn.getBlockEntity(pos);
			return tileEntity instanceof MenuProvider menuProvider ? menuProvider : null;
		}

		@Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		    return new ${name}BlockEntity(pos, state);
		}

	    @Override
		public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
			super.triggerEvent(state, world, pos, eventID, eventParam);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			return blockEntity == null ? false : blockEntity.triggerEvent(eventID, eventParam);
		}

	    <#if data.inventoryDropWhenDestroyed>
		@Override public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
			if (state.getBlock() != newState.getBlock()) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof ${name}BlockEntity be) {
					Containers.dropContents(world, pos, be);
					world.updateNeighbourForOutputSignal(pos, this);
				}

				super.onRemove(state, world, pos, newState, isMoving);
			}
		}
	    </#if>

	    <#if data.inventoryComparatorPower>
	    @Override public boolean hasAnalogOutputSignal(BlockState state) {
			return true;
		}

	    @Override public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
			BlockEntity tileentity = world.getBlockEntity(pos);
			if (tileentity instanceof ${name}BlockEntity be)
				return AbstractContainerMenu.getRedstoneSignalFromContainer(be);
			else
				return 0;
		}
	    </#if>
	</#if>

	<#if data.tintType != "No tint">
		@OnlyIn(Dist.CLIENT) public static void blockColorLoad(RegisterColorHandlersEvent.Block event) {
			event.getBlockColors().register((bs, world, pos, index) -> {
				<#if data.tintType == "Default foliage">
					return FoliageColor.getDefaultColor();
				<#elseif data.tintType == "Birch foliage">
					return FoliageColor.getBirchColor();
				<#elseif data.tintType == "Spruce foliage">
					return FoliageColor.getEvergreenColor();
				<#else>
					return world != null && pos != null ?
					<#if data.tintType == "Grass">
						BiomeColors.getAverageGrassColor(world, pos) : GrassColor.get(0.5D, 1.0D);
					<#elseif data.tintType == "Foliage">
						BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.getDefaultColor();
					<#elseif data.tintType == "Water">
						BiomeColors.getAverageWaterColor(world, pos) : -1;
					<#elseif data.tintType == "Sky">
						Minecraft.getInstance().level.getBiome(pos).value().getSkyColor() : 8562943;
					<#elseif data.tintType == "Fog">
						Minecraft.getInstance().level.getBiome(pos).value().getFogColor() : 12638463;
					<#else>
						Minecraft.getInstance().level.getBiome(pos).value().getWaterFogColor() : 329011;
					</#if>
				</#if>
			}, ${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}.get());
		}

		<#if data.isItemTinted>
		@OnlyIn(Dist.CLIENT) public static void itemColorLoad(RegisterColorHandlersEvent.Item event) {
			event.getItemColors().register((stack, index) -> {
				<#if data.tintType == "Grass">
					return GrassColor.get(0.5D, 1.0D);
				<#elseif data.tintType == "Foliage" || data.tintType == "Default foliage">
					return FoliageColor.getDefaultColor();
				<#elseif data.tintType == "Birch foliage">
					return FoliageColor.getBirchColor();
				<#elseif data.tintType == "Spruce foliage">
					return FoliageColor.getEvergreenColor();
				<#elseif data.tintType == "Water">
					return 3694022;
				<#elseif data.tintType == "Sky">
					return 8562943;
				<#elseif data.tintType == "Fog">
					return 12638463;
				<#else>
					return 329011;
				</#if>
			}, ${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}.get());
		}
		</#if>
	</#if>

}
</#compress>
<#-- @formatter:on -->
