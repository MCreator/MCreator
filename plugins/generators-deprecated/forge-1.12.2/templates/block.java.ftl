<#-- @formatter:off -->
<#include "mcitems.ftl">
<#include "procedures.java.ftl">
<#include "particles.java.ftl">

package ${package}.block;

@Elements${JavaModName}.ModElement.Tag public class Block${name} extends Elements${JavaModName}.ModElement {

	@GameRegistry.ObjectHolder("${modid}:${registryname}")
	public static final Block block = null;

	<#if data.blockBase?has_content && data.blockBase == "Slab">
	@GameRegistry.ObjectHolder("${modid}:${registryname}_double")
	public static final Block block_slab_double = null;
	</#if>

	public Block${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		<#if data.blockBase?has_content && data.blockBase == "Slab">
		elements.blocks.add(() -> new BlockCustom().setRegistryName("${registryname}"));
		elements.blocks.add(() -> new BlockCustom.Double().setRegistryName("${registryname}_double"));
		elements.items.add(() -> new ItemSlab(block, (BlockSlab) block, (BlockSlab) block_slab_double).setRegistryName(block.getRegistryName()));
		<#else>
		elements.blocks.add(() -> new BlockCustom().setRegistryName("${registryname}"));
		elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
		</#if>
	}

	<#if data.hasInventory>
	@Override public void init(FMLInitializationEvent event) {
		<#if data.hasInventory>
			GameRegistry.registerTileEntity(TileEntityCustom.class, "${modid}:tileentity${registryname}");
		</#if>
	}
	</#if>

	@SideOnly(Side.CLIENT) @Override public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation("${modid}:${registryname}","inventory"));

		<#if data.blockBase?has_content && data.blockBase == "Leaves">
		ModelLoader.setCustomStateMapper(block, (new StateMap.Builder()).ignore(BlockLeaves.DECAYABLE, BlockLeaves.CHECK_DECAY).build());
		</#if>
	}

	<#if (data.spawnWorldTypes?size > 0)>
	@Override public void generateWorld(Random random,int chunkX,int chunkZ,World world,int dimID,IChunkGenerator cg,IChunkProvider cp){
		boolean dimensionCriteria=false;

		<#list data.spawnWorldTypes as worldType>
    	    <#if worldType=="Surface">
					if(dimID==0)
						dimensionCriteria=true;
    	    <#elseif worldType=="Nether">
					if(dimID==-1)
						dimensionCriteria=true;
    	    <#elseif worldType=="End">
					if(dimID==1)
						dimensionCriteria=true;
    	    <#else>
					if(dimID== World${worldType.toString().replace("CUSTOM:", "")}.DIMID)
						dimensionCriteria=true;
    	    </#if>
    	</#list>
		if(!dimensionCriteria)
			return;

		<#if data.restrictionBiomes?has_content>
			boolean biomeCriteria = false;
			Biome biome=world.getBiome(new BlockPos(chunkX,128,chunkZ));
    	    <#list data.restrictionBiomes as restrictionBiome>
				<#if restrictionBiome.canProperlyMap()>
				if(Biome.REGISTRY.getNameForObject(biome).equals(new ResourceLocation("${restrictionBiome}")))
    	    		biomeCriteria = true;
				</#if>
    	    </#list>
			if(!biomeCriteria)
				return;
    	</#if>

		for(int i=0; i < ${data.frequencyPerChunks}; i++){
			int x=chunkX+random.nextInt(16);
			int y=random.nextInt(${data.maxGenerateHeight - data.minGenerateHeight})+ ${data.minGenerateHeight};
			int z=chunkZ+random.nextInt(16);
			(new WorldGenMinable(block.getDefaultState(), ${data.frequencyOnChunk}, new com.google.common.base.Predicate<IBlockState>(){
				public boolean apply(IBlockState blockAt){
					boolean blockCriteria=false;
					IBlockState require;
					<#list data.blocksToReplace as replacementBlock>
    		            <#if hasMetadata(replacementBlock)>
						require= ${mappedBlockToBlockStateCode(replacementBlock)};
						try{
							if((blockAt.getBlock()==require.getBlock())&&(blockAt.getBlock().getMetaFromState(blockAt)==require.getBlock().getMetaFromState(require)))
								blockCriteria=true;
						}catch(Exception e){
							if(blockAt.getBlock()==require.getBlock())
								blockCriteria=true;
						}
    		            <#else>
						if(blockAt.getBlock()== ${mappedBlockToBlockStateCode(replacementBlock)}.getBlock())
							blockCriteria=true;
    		            </#if>
    		        </#list>
					return blockCriteria;
				}
			})).generate(world,random,new BlockPos(x,y,z));
		}
	}
	</#if>

	public static class BlockCustom extends
			<#if data.hasGravity>
				BlockFalling
			<#elseif data.blockBase?has_content>
				Block${data.blockBase}
			<#else>
				Block
			</#if>
			<#if data.hasInventory> implements ITileEntityProvider</#if> {

		<#if data.rotationMode == 1 || data.rotationMode == 3>
		public static final PropertyDirection FACING = BlockHorizontal.FACING;
		<#elseif data.rotationMode == 2 || data.rotationMode == 4 || data.rotationMode == 5>
		public static final PropertyDirection FACING = BlockDirectional.FACING;
        </#if>

		public BlockCustom() {
			<#if data.blockBase?has_content && data.blockBase == "Stairs">
			super(new Block(Material.${data.material}).getDefaultState());
			<#elseif data.blockBase?has_content && data.blockBase == "Wall">
			super(new Block(Material.${data.material}));
			<#elseif data.blockBase?has_content && data.blockBase == "Fence">
			super(Material.${data.material}, Material.${data.material}.getMaterialMapColor());
			<#elseif data.blockBase?has_content && data.blockBase == "Leaves">
			super();
			<#else>
			super(Material.${data.material});
			</#if>

			setUnlocalizedName("${registryname}");
			setSoundType(SoundType.${data.soundOnStep});

			<#if data.destroyTool != "Not specified">
			setHarvestLevel("${data.destroyTool}", ${data.breakHarvestLevel});
			</#if>

			setHardness(${data.hardness}F);
			setResistance(${data.resistance}F);
			setLightLevel(${data.luminance}F);
			setLightOpacity(${data.lightOpacity});
			setCreativeTab(${data.creativeTab});
			<#if data.unbreakable>setBlockUnbreakable();</#if>

			<#if data.slipperiness != 0.6>
			setDefaultSlipperiness(${data.slipperiness}f);
			</#if>

			<#if data.rotationMode == 1 || data.rotationMode == 3>
			this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
			<#elseif data.rotationMode == 2 || data.rotationMode == 4>
			this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
			<#elseif data.rotationMode == 5>
			this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH));
        	</#if>

			<#if data.blockBase?has_content && data.blockBase == "Slab">
			IBlockState state = this.blockState.getBaseState().withProperty(VARIANT, BlockCustom.Variant.DEFAULT);
			if (!this.isDouble())
				state = state.withProperty(BlockSlab.HALF, EnumBlockHalf.BOTTOM);
			this.setDefaultState(state);
			this.useNeighborBrightness = !this.isDouble();
			<#elseif data.blockBase?has_content && data.blockBase == "Leaves">
			this.setDefaultState(this.blockState.getBaseState().withProperty(CHECK_DECAY, true).withProperty(DECAYABLE, true));
			</#if>

			<#if data.tickRandomly>
			setTickRandomly(true);
			</#if>
		}

		<#if data.blockBase?has_content && data.blockBase == "Wall">
		@Override public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
			items.add(new ItemStack(this));
		}
		</#if>

		<#if data.blockBase?has_content && data.blockBase == "Leaves">
		@Override public BlockPlanks.EnumType getWoodType(int meta) {
			return BlockPlanks.EnumType.OAK;
		}

		@Override
		public NonNullList<ItemStack> onSheared(ItemStack item, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
			return NonNullList.withSize(1, new ItemStack(this, 1));
		}
		</#if>

		<#if data.blockBase?has_content && data.blockBase == "Slab">
			public static final PropertyEnum<BlockCustom.Variant> VARIANT = PropertyEnum.<BlockCustom.Variant>create("variant", BlockCustom.Variant.class);

			@Override public Item getItemDropped(IBlockState state, Random rand, int fortune) {
				return Item.getItemFromBlock(block);
			}

    		@Override public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
				return new ItemStack(block);
			}

			@Override protected net.minecraft.block.state.BlockStateContainer createBlockState() {
				return this.isDouble() ? new net.minecraft.block.state.BlockStateContainer(this, new IProperty[] {VARIANT}) :
						new net.minecraft.block.state.BlockStateContainer(this, new IProperty[] {HALF, VARIANT});
			}

			@Override public IBlockState getStateFromMeta(int meta) {
				if (this.isDouble()) {
					return this.getDefaultState();
				} else {
					return this.getDefaultState().withProperty(HALF, BlockSlab.EnumBlockHalf.values()[meta % 2]);
				}
			}

			@Override public int getMetaFromState(IBlockState state) {
				if (this.isDouble()) {
					return 0;
				} else {
					return state.getValue(HALF).ordinal();
				}
			}

			@Override public String getUnlocalizedName(int meta) {
				return super.getUnlocalizedName();
			}

			@Override public IProperty<?> getVariantProperty() {
				return VARIANT;
			}

			@Override public Comparable<?> getTypeForItem(ItemStack stack) {
				return BlockCustom.Variant.DEFAULT;
			}

			@Override public boolean isDouble() {
				return false;
			}

			@Override
			public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
    			if(isDouble())
    				return true;
    			return super.doesSideBlockRendering(state, world, pos, face);
			}

			public enum Variant implements IStringSerializable {
				DEFAULT;

				public String getName() {
					return "default";
				}
			}

			public static class Double extends BlockCustom {

				@Override public boolean isDouble() {
					return true;
				}
			}
		<#elseif data.blockBase?has_content && data.blockBase == "Leaves">
			@Override protected net.minecraft.block.state.BlockStateContainer createBlockState() {
				return new net.minecraft.block.state.BlockStateContainer(this, new IProperty[] {CHECK_DECAY, DECAYABLE});
			}

			public IBlockState getStateFromMeta(int meta) {
				return this.getDefaultState().withProperty(DECAYABLE, (meta & 1) != 0).withProperty(CHECK_DECAY, (meta & 2) != 0);
			}

			public int getMetaFromState(IBlockState state) {
				int i = 0;
				if (!(Boolean)state.getValue(DECAYABLE))
					i |= 1;
				if ((Boolean)state.getValue(CHECK_DECAY))
					i |= 2;
				return i;
			}
		</#if>

		<#if data.specialInfo?has_content>
		@Override public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			<#list data.specialInfo as entry>
			list.add("${JavaConventions.escapeStringForJava(entry)}");
            </#list>
		}
        </#if>

		<#if data.transparencyType != "SOLID">
		@SideOnly(Side.CLIENT) @Override public BlockRenderLayer getBlockLayer() {
			return BlockRenderLayer.${data.transparencyType};
		}
		<#elseif data.blockBase?has_content && data.blockBase == "Leaves">
		@SideOnly(Side.CLIENT) @Override public BlockRenderLayer getBlockLayer() {
			return BlockRenderLayer.CUTOUT_MIPPED;
		}
		</#if>

		<#if data.isNotColidable>
        @Override @javax.annotation.Nullable
		public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
			return NULL_AABB;
		}

		@Override public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
			return true;
		}
        </#if>

		<#if data.isNotColidable || data.mx != 0 || data.my != 0 || data.mz != 0 || data.Mx != 1 || data.My != 1 || data.Mz != 1>
		@Override public boolean isFullCube(IBlockState state) {
			return false;
		}
        </#if>

		<#if data.mx != 0 || data.my != 0 || data.mz != 0 || data.Mx != 1 || data.My != 1 || data.Mz != 1>
		@Override public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
			<#if data.rotationMode == 1 || data.rotationMode == 3>
			switch ((EnumFacing) state.getValue(BlockHorizontal.FACING)) {
			case UP:
			case DOWN:
			case SOUTH:
			default:
				return new AxisAlignedBB(${1-data.mx}D, ${data.my}D, ${1-data.mz}D, ${1-data.Mx}D, ${data.My}D,
                            ${1-data.Mz}D);
			case NORTH:
				return new AxisAlignedBB(${data.mx}D, ${data.my}D, ${data.mz}D, ${data.Mx}D, ${data.My}D, ${data.Mz}D);
			case WEST:
				return new AxisAlignedBB(${data.mz}D, ${data.my}D, ${1-data.mx}D, ${data.Mz}D, ${data.My}D,
                            ${1-data.Mx}D);
			case EAST:
				return new AxisAlignedBB(${1-data.mz}D, ${data.my}D, ${data.mx}D, ${1-data.Mz}D, ${data.My}D,
                            ${data.Mx}D);
			}
			<#elseif data.rotationMode == 2 || data.rotationMode == 4>
			switch ((EnumFacing) state.getValue(BlockDirectional.FACING)) {
			case SOUTH:
			default:
				return new AxisAlignedBB(${1-data.mx}D, ${data.my}D, ${1-data.mz}D, ${1-data.Mx}D, ${data.My}D,
                            ${1-data.Mz}D);
			case NORTH:
				return new AxisAlignedBB(${data.mx}D, ${data.my}D, ${data.mz}D, ${data.Mx}D, ${data.My}D, ${data.Mz}D);
			case WEST:
				return new AxisAlignedBB(${data.mz}D, ${data.my}D, ${1-data.mx}D, ${data.Mz}D, ${data.My}D,
                            ${1-data.Mx}D);
			case EAST:
				return new AxisAlignedBB(${1-data.mz}D, ${data.my}D, ${data.mx}D, ${1-data.Mz}D, ${data.My}D,
                            ${data.Mx}D);
			case UP:
				return new AxisAlignedBB(${data.mx}D, ${1-data.mz}D, ${data.my}D, ${data.Mx}D, ${1-data.Mz}D,
                            ${data.My}D);
			case DOWN:
				return new AxisAlignedBB(${data.mx}D, ${data.mz}D, ${1-data.my}D, ${data.Mx}D, ${data.Mz}D,
                            ${1-data.My}D);
			}
			<#elseif data.rotationMode == 5>
			switch ((EnumFacing) state.getValue(BlockDirectional.FACING)) {
			case SOUTH:
			case NORTH:
			default:
				return new AxisAlignedBB(${data.mx}D, ${data.my}D, ${data.mz}D, ${data.Mx}D, ${data.My}D, ${data.Mz}D);
			case EAST:
			case WEST:
				return new AxisAlignedBB(${data.mx}D, ${1-data.mz}D, ${data.my}D, ${data.Mx}D, ${1-data.Mz}D,
							${data.My}D);
			case UP:
			case DOWN:
				return new AxisAlignedBB(${data.my}D, ${1-data.mx}D, ${1-data.mz}D, ${data.My}D, ${1-data.Mx}D,
							${1-data.Mz}D);
			}
            <#else>
			return new AxisAlignedBB(${data.mx}D, ${data.my}D, ${data.mz}D, ${data.Mx}D, ${data.My}D, ${data.Mz}D);
            </#if>
		}
        </#if>

		<#if data.tickRate != 10>
		@Override public int tickRate(World world) {
			return ${data.tickRate};
		}
        </#if>

		<#if data.rotationMode != 0>
		@Override protected net.minecraft.block.state.BlockStateContainer createBlockState() {
			return new net.minecraft.block.state.BlockStateContainer(this, new IProperty[] { FACING });
		}

			<#if data.rotationMode != 5>
			@Override public IBlockState withRotation(IBlockState state, Rotation rot) {
        		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
    		}

    		@Override public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
    		}
    		<#else>
			@Override public IBlockState withRotation(IBlockState state, Rotation rot) {
				if(rot == Rotation.CLOCKWISE_90 || rot == Rotation.COUNTERCLOCKWISE_90) {
					if((EnumFacing) state.getValue(FACING) == EnumFacing.WEST || (EnumFacing) state.getValue(FACING) == EnumFacing.EAST) {
						return state.withProperty(FACING, EnumFacing.UP);
					} else if((EnumFacing) state.getValue(FACING) == EnumFacing.UP || (EnumFacing) state.getValue(FACING) == EnumFacing.DOWN) {
						return state.withProperty(FACING, EnumFacing.WEST);
					}
				}
				return state;
			}
			</#if>

		@Override public IBlockState getStateFromMeta(int meta) {
			return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
		}

		@Override public int getMetaFromState(IBlockState state) {
			return ((EnumFacing) state.getValue(FACING)).getIndex();
		}

		@Override
		public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
				float hitZ, int meta, EntityLivingBase placer) {
			<#if data.rotationMode == 1>
			return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
            <#elseif data.rotationMode == 3>
			if (facing == EnumFacing.UP || facing == EnumFacing.DOWN)
				return this.getDefaultState().withProperty(FACING, EnumFacing.NORTH);
			return this.getDefaultState().withProperty(FACING, facing);
            <#elseif data.rotationMode == 2>
			return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
            <#elseif data.rotationMode == 4>
			return this.getDefaultState().withProperty(FACING, facing);
			<#elseif data.rotationMode == 5>
			if (facing == EnumFacing.WEST || facing == EnumFacing.EAST)
				facing = EnumFacing.UP;
			else if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
				facing = EnumFacing.EAST;
			else
				facing = EnumFacing.SOUTH;
			return this.getDefaultState().withProperty(FACING, facing);
			</#if>
		}
        </#if>

		<#if data.isBeaconBase>
		@Override public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
			return true;
		}
        </#if>

		<#if data.enchantPowerBonus != 0>
		@Override public float getEnchantPowerBonus(World world, BlockPos pos) {
			return ${data.enchantPowerBonus}f;
		}
        </#if>

		<#if data.hasTransparency || (data.blockBase?has_content && data.blockBase == "Leaves")>
        @Override public boolean isOpaqueCube(IBlockState state) {
			return false;
		}
        </#if>

		<#if data.isReplaceable>
        @Override public boolean isReplaceable(IBlockAccess blockAccess, BlockPos pos) {
			return true;
		}
        </#if>

		<#if data.isLadder>
		@Override public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
			return true;
		}
		</#if>

		<#if data.reactionToPushing != "NORMAL">
		@Override public EnumPushReaction getMobilityFlag(IBlockState state) {
			return EnumPushReaction.${data.reactionToPushing};
		}
		</#if>

		<#if data.flammability != 0>
		@Override public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
			return ${data.flammability};
		}
		</#if>

		<#if data.fireSpreadSpeed != 0>
		@Override public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
			return ${data.fireSpreadSpeed};
		}
		</#if>

		<#if data.creativePickItem?? && !data.creativePickItem.isEmpty()>
		@Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        	return ${mappedMCItemToItemStackCode(data.creativePickItem, 1)};
    	}
        </#if>

		<#if data.colorOnMap?has_content && generator.map(data.colorOnMap, "mapcolors") != "DEFAULT">
		@Override public MapColor getMapColor(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
        	return MapColor.${generator.map(data.colorOnMap, "mapcolors")};
    	}
		</#if>

		<#if data.faceShape != "SOLID">
		@Override public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        	return BlockFaceShape.${data.faceShape};
    	}
		</#if>

		<#if !data.affectedBySilkTouch>
        @Override public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
			return false;
		}
        </#if>

        <#if data.plantsGrowOn>
        @Override
		public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction,
				net.minecraftforge.common.IPlantable plantable) {
			return true;
		}
        </#if>

        <#if data.canProvidePower>
        @Override
		public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
			return true;
		}
        </#if>

		<#if data.dropAmount != 1 && !(data.customDrop?? && !data.customDrop.isEmpty())>
		@Override public int quantityDropped(Random random) {
			return ${data.dropAmount};
		}
        </#if>

        <#if (data.customDrop?? && !data.customDrop.isEmpty())>
		@Override public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
			drops.add(${mappedMCItemToItemStackCode(data.customDrop, data.dropAmount)});
		}
        </#if>

		<#if data.hasInventory>
            @Override public TileEntity createNewTileEntity(World worldIn, int meta) {
				return new TileEntityCustom();
			}

		    @Override
			public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
				super.eventReceived(state, worldIn, pos, eventID, eventParam);
				TileEntity tileentity = worldIn.getTileEntity(pos);
				return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
			}

		    @Override public EnumBlockRenderType getRenderType(IBlockState state) {
				return EnumBlockRenderType.MODEL;
			}

            <#if data.inventoryDropWhenDestroyed>
            @Override public void breakBlock(World world, BlockPos pos, IBlockState state) {
				TileEntity tileentity = world.getTileEntity(pos);
				if (tileentity instanceof TileEntityCustom)
					InventoryHelper.dropInventoryItems(world, pos, (TileEntityCustom) tileentity);
				world.removeTileEntity(pos);
				super.breakBlock(world, pos, state);
			}
            </#if>

            <#if data.inventoryComparatorPower>
            @Override public boolean hasComparatorInputOverride(IBlockState state) {
				return true;
			}

		    @Override public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
				TileEntity tileentity = worldIn.getTileEntity(pos);
				if (tileentity instanceof TileEntityCustom)
					return Container.calcRedstoneFromInventory((TileEntityCustom) tileentity);
				else
					return 0;
			}
            </#if>
        </#if>

        <#if (hasProcedure(data.onTickUpdate) && !data.tickRandomly) || hasProcedure(data.onBlockAdded) >
		@Override public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
			super.onBlockAdded(world, pos, state);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<#if hasProcedure(data.onTickUpdate) && !data.tickRandomly>
			world.scheduleUpdate(new BlockPos(x, y, z), this, this.tickRate(world));
            </#if>
			<@procedureOBJToCode data.onBlockAdded/>
		}
        </#if>

		<#if hasProcedure(data.onRedstoneOn) || hasProcedure(data.onRedstoneOff) || hasProcedure(data.onNeighbourBlockChanges)>
		@Override
		public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock,
				BlockPos fromPos) {
			super.neighborChanged(state, world, pos, neighborBlock, fromPos);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			if (world.isBlockIndirectlyGettingPowered(new BlockPos(x, y, z)) > 0) {
				<@procedureOBJToCode data.onRedstoneOn/>
			} else {
				<@procedureOBJToCode data.onRedstoneOff/>
			}
			<@procedureOBJToCode data.onNeighbourBlockChanges/>
		}
        </#if>

        <#if hasProcedure(data.onTickUpdate)>
		@Override public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
			super.updateTick(world, pos, state, random);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<@procedureOBJToCode data.onTickUpdate/>
			<#if !data.tickRandomly>
			world.scheduleUpdate(new BlockPos(x, y, z), this, this.tickRate(world));
			</#if>
		}
        </#if>

        <#if hasProcedure(data.onRandomUpdateEvent) || data.spawnParticles>
		@SideOnly(Side.CLIENT) @Override
		public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
			super.randomDisplayTick(state, world, pos, random);
			EntityPlayer entity = Minecraft.getMinecraft().player;
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<#if data.spawnParticles>
                int i = x;
                int j = y;
                int k = z;
                <@particles data.particleSpawningShape data.particleToSpawn data.particleSpawningRadious
                data.particleAmount data.particleSpawningCondition/>
            </#if>
			<@procedureOBJToCode data.onRandomUpdateEvent/>
		}
        </#if>

        <#if hasProcedure(data.onDestroyedByPlayer)>
		@Override
		public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer entity,
				boolean willHarvest) {
			boolean retval = super.removedByPlayer(state, world, pos, entity, willHarvest);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<@procedureOBJToCode data.onDestroyedByPlayer/>
			return retval;
		}
        </#if>

        <#if hasProcedure(data.onDestroyedByExplosion)>
		@Override public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion e) {
			super.onBlockDestroyedByExplosion(world, pos, e);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<@procedureOBJToCode data.onDestroyedByExplosion/>
		}
        </#if>

        <#if hasProcedure(data.onStartToDestroy)>
		@Override public void onBlockClicked(World world, BlockPos pos, EntityPlayer entity) {
			super.onBlockClicked(world, pos, entity);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<@procedureOBJToCode data.onStartToDestroy/>
		}
        </#if>

        <#if hasProcedure(data.onEntityCollides)>
		@Override public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
			super.onEntityCollidedWithBlock(world, pos, state, entity);
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
		public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity,
				ItemStack itemstack) {
			super.onBlockPlacedBy(world, pos, state, entity, itemstack);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<@procedureOBJToCode data.onBlockPlayedBy/>
		}
        </#if>

        <#if hasProcedure(data.onRightClicked) || data.openGUIOnRightClick>
		@Override
		public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entity,
				EnumHand hand, EnumFacing direction, float hitX, float hitY, float hitZ) {
			super.onBlockActivated(world, pos, state, entity, hand, direction, hitX, hitY, hitZ);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			<#if data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>" && data.openGUIOnRightClick && (data.guiBoundTo)?has_content>
				if(entity instanceof EntityPlayer) {
					((EntityPlayer)entity).openGui(${JavaModName}.instance, Gui${(data.guiBoundTo)}.GUIID,world,x,y,z);
				}
			</#if>

			<#if hasProcedure(data.onRightClicked)>
				<@procedureOBJToCode data.onRightClicked/>
			</#if>
			return true;
		}
        </#if>

	}

<#if data.hasInventory>
    public static class TileEntityCustom extends TileEntityLockableLoot {

		private NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(${data.inventorySize}, ItemStack.EMPTY);

		@Override public int getSizeInventory() {
			return ${data.inventorySize};
		}

		@Override public boolean isEmpty() {
			for (ItemStack itemstack : this.stacks)
				if (!itemstack.isEmpty())
					return false;
			return true;
		}

		@Override public boolean isItemValidForSlot(int index, ItemStack stack) {
			<#list data.inventoryOutSlotIDs as id>
			    if (index == ${id})
					return false;
            </#list>
			return true;
		}

		@Override public ItemStack getStackInSlot(int slot) {
			return stacks.get(slot);
		}

		@Override public String getName() {
			return "container.${registryname}";
		}

		@Override public void readFromNBT(NBTTagCompound compound) {
			super.readFromNBT(compound);
			this.stacks = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
			if (!this.checkLootAndRead(compound))
				ItemStackHelper.loadAllItems(compound, this.stacks);
		}

		@Override public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			super.writeToNBT(compound);
			if (!this.checkLootAndWrite(compound))
				ItemStackHelper.saveAllItems(compound, this.stacks);
			return compound;
		}

		@Override public SPacketUpdateTileEntity getUpdatePacket() {
			return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
		}

		@Override public NBTTagCompound getUpdateTag() {
			return this.writeToNBT(new NBTTagCompound());
		}

		@Override public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
			this.readFromNBT(pkt.getNbtCompound());
		}

		@Override public void handleUpdateTag(NBTTagCompound tag) {
			this.readFromNBT(tag);
		}

		@Override public int getInventoryStackLimit() {
			return ${data.inventoryStackSize};
		}

		@Override public String getGuiID() {
			return "${modid}:${registryname}";
		}

		@Override public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
			<#if !data.guiBoundTo?has_content || data.guiBoundTo == "<NONE>" || !(data.guiBoundTo)?has_content>
				this.fillWithLoot(playerIn);
				return new ContainerChest(playerInventory, this, playerIn);
			<#else>
				return new Gui${(data.guiBoundTo)}.GuiContainerMod(this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), playerIn);
			</#if>
		}

		@Override protected NonNullList<ItemStack> getItems() {
			return this.stacks;
		}

	}
</#if>

}
<#-- @formatter:on -->