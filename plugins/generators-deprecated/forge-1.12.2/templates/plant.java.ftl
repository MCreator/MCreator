<#-- @formatter:off -->
<#include "procedures.java.ftl">
<#include "mcitems.ftl">

package ${package}.block;

@Elements${JavaModName}.ModElement.Tag public class Block${name} extends Elements${JavaModName}.ModElement {

	@GameRegistry.ObjectHolder("${modid}:${registryname}")
	public static final Block block = null;

	public Block${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		elements.blocks.add(() -> new BlockCustomFlower());
		elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	@SideOnly(Side.CLIENT) @Override public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),0,new ModelResourceLocation("${modid}:${registryname}" ,"inventory"));
	}

	<#if (data.spawnWorldTypes?size > 0)>
	@Override public void generateWorld(Random random, int chunkX, int chunkZ, World world, int dimID, IChunkGenerator cg, IChunkProvider cp){
		boolean dimensionCriteria = false;

    	<#list data.spawnWorldTypes as worldType>
			<#if worldType=="Surface">
				if(dimID==0)
					dimensionCriteria = true;
			<#elseif worldType=="Nether">
				if(dimID==-1)
					dimensionCriteria = true;
			<#elseif worldType=="End">
				if(dimID==1)
					dimensionCriteria = true;
			<#else>
				if(dimID== World${(worldType.toString().replace("CUSTOM:", ""))}.DIMID)
					dimensionCriteria = true;
			</#if>
		</#list>
		if(!dimensionCriteria)
			return;

		<#if data.restrictionBiomes?has_content>
			boolean biomeCriteria=false;
			Biome biome=world.getBiome(new BlockPos(chunkX,128,chunkZ));
            <#list data.restrictionBiomes as restrictionBiome>
				<#if restrictionBiome.canProperlyMap()>
				if(Biome.REGISTRY.getNameForObject(biome).equals(new ResourceLocation("${restrictionBiome}")))
					biomeCriteria=true;
				</#if>
            </#list>
			if(!biomeCriteria)
				return;
        </#if>

		for(int i = 0; i < ${data.frequencyOnChunks}; i++) {
			int l6 = chunkX + random.nextInt(16) + 8;
			int i11 = random.nextInt(128);
			int l14 = chunkZ + random.nextInt(16) + 8;
			<#if data.plantType == "normal">
				(new WorldGenFlowers(((BlockFlower)block), BlockFlower.EnumFlowerType.DANDELION))
					.generate(world, random, new BlockPos(l6, i11, l14));
				<#elseif data.plantType == "growapable">
				(new WorldGenReed(){
					@Override public boolean generate(World world,Random random,BlockPos pos){
						for(int i=0; i < 20; ++i){
							BlockPos blockpos1 = pos.add(random.nextInt(4)-random.nextInt(4),0,random.nextInt(4)-random.nextInt(4));
							if(world.isAirBlock(blockpos1)){
								BlockPos blockpos2=blockpos1.down();
								int j = 1 + random.nextInt(random.nextInt(${data.growapableMaxHeight})+1);
								j = Math.min(${data.growapableMaxHeight},j);
								for(int k=0; k < j; ++k)
									if (((BlockReed) block).canBlockStay(world, blockpos1))
										world.setBlockState(blockpos1.up(k), block.getDefaultState(), 2);
							}
						}
						return true;
					}
				}).generate(world, random, new BlockPos(l6, i11, l14));
			</#if>
			}

		}
	</#if>

	public static class BlockCustomFlower extends Block<#if data.plantType == "normal">Flower<#elseif data.plantType == "growapable">Reed</#if> {

		public BlockCustomFlower() {
			setSoundType(SoundType.${data.soundOnStep});
			setCreativeTab(${data.creativeTab});
			setHardness(${data.hardness}F);
			setResistance(${data.resistance}F);
			setLightLevel(${data.luminance}F);
			setUnlocalizedName("${registryname}");
			setRegistryName("${registryname}");
			<#if data.unbreakable>setBlockUnbreakable();</#if>
		}

        <#if data.isReplaceable>
        @Override public boolean isReplaceable(IBlockAccess blockAccess, BlockPos pos) {
			return true;
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

		<#if data.dropAmount != 1 && !(data.customDrop?? && !data.customDrop.isEmpty())>
		@Override public int quantityDropped(Random random) {
			return ${data.dropAmount};
		}
        </#if>

		@Override public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
			<#if data.customDrop?? && !data.customDrop.isEmpty()>
			drops.add(${mappedMCItemToItemStackCode(data.customDrop, data.dropAmount)});
			<#else>
			drops.add(new ItemStack(this));
			</#if>
		}

		<#if data.creativePickItem?? && !data.creativePickItem.isEmpty()>
		@Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        	return ${mappedMCItemToItemStackCode(data.creativePickItem, 1)};
    	}
    	<#else>
		@Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
			return new ItemStack(Item.getItemFromBlock(this), 1, this.damageDropped(state));
		}
        </#if>

		<#if data.colorOnMap?has_content && data.colorOnMap != "DEFAULT">
		@Override public MapColor getMapColor(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
        	return MapColor.${data.colorOnMap};
    	}
		</#if>

		@Override public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
			return EnumPlantType.${data.growapableSpawnType};
		}

		<#if data.plantType == "normal">

        @Override public BlockFlower.EnumFlowerColor getBlockType() {
			return BlockFlower.EnumFlowerColor.YELLOW;
		}

		@SideOnly(Side.CLIENT) @Override
		public void getSubBlocks(CreativeTabs tab, net.minecraft.util.NonNullList<ItemStack> list) {
			for (BlockFlower.EnumFlowerType blockflower$enumflowertype : BlockFlower.EnumFlowerType
					.getTypes(this.getBlockType())) {
				list.add(new ItemStack(this, 1, blockflower$enumflowertype.getMeta()));
			}
		}

        <#elseif data.plantType == "growapable">

		@Override public boolean canPlaceBlockAt(World world, BlockPos pos) {
			Block block2 = world.getBlockState(pos.down()).getBlock();
			return (block2.canSustainPlant(world.getBlockState(pos.down()), world, pos.down(), EnumFacing.UP, this)
					|| block2 == block);
		}

		@SideOnly(Side.CLIENT) public int colorMultiplier(IBlockAccess p_149720_1_, BlockPos pos, int pass) {
			return 16777215;
		}

        </#if>

        <#if hasProcedure(data.onTickUpdate) || data.plantType == "growapable">
		@Override public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
			<#if hasProcedure(data.onTickUpdate)>
                int x = pos.getX();
			    int y = pos.getY();
			    int z = pos.getZ();
                <@procedureOBJToCode data.onTickUpdate/>
            </#if>

            <#if data.plantType == "growapable">
			if (world.getBlockState(pos.down()).getBlock() == block || this.checkForDrop(world, pos, state)) {
				if (world.isAirBlock(pos.up())) {
					int l;
					for (l = 1; world.getBlockState(pos.down(l)).getBlock() == this; ++l)
						;
					if (l < ${data.growapableMaxHeight}) {
						int i1 = (Integer) state.getValue(AGE);
						if (i1 == 15) {
							world.setBlockState(pos.up(), this.getDefaultState());
							world.setBlockState(pos, state.withProperty(AGE, 0), 4);
						} else {
							world.setBlockState(pos, state.withProperty(AGE, i1 + 1), 4);
						}
					}
				}
			}
            </#if>
		}
        </#if>

        <#if hasProcedure(data.onNeighbourBlockChanges)>
		@Override
		public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos) {
			super.neighborChanged(state, world, pos, neighborBlock, fromPos);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<@procedureOBJToCode data.onNeighbourBlockChanges/>
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

        <#if hasProcedure(data.onRightClicked)>
		public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entity,
				EnumHand hand, EnumFacing direction, float hitX, float hitY, float hitZ) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<@procedureOBJToCode data.onRightClicked/>
			return true;
		}
        </#if>

	}
}
<#-- @formatter:on -->