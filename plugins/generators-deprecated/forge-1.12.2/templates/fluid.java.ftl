<#-- @formatter:off -->
<#include "procedures.java.ftl">

package ${package}.block;

@Elements${JavaModName}.ModElement.Tag public class Block${name} extends Elements${JavaModName}.ModElement{

	@GameRegistry.ObjectHolder("${modid}:${registryname}")
	public static final Block block = null;

	@GameRegistry.ObjectHolder("${modid}:${registryname}")
	public static final Item item = null;

	private Fluid fluid;

	public Block${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
		fluid = new Fluid("${registryname}", new ResourceLocation("${modid}:blocks/${data.textureStill}" ),new ResourceLocation("${modid}:blocks/${data.textureFlowing}"))
					.setLuminosity(${data.luminosity}).setDensity(${data.density}).setViscosity(${data.viscosity}).setGaseous(${data.isGas});
	}

	@Override public void initElements() {
		elements.blocks.add(() -> new BlockFluidClassic(fluid, Material.${data.type}){
			<#if hasProcedure(data.onBlockAdded)>
			@Override public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
				super.onBlockAdded(world, pos, state);
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				<@procedureOBJToCode data.onBlockAdded/>
			}
            </#if>

			<#if hasProcedure(data.onNeighbourChanges)>
			@Override public void neighborChanged(IBlockState state,World world,BlockPos pos,Block neighborBlock,BlockPos fromPos){
				super.neighborChanged(state,world,pos,neighborBlock,fromPos);
				int x=pos.getX();
				int y=pos.getY();
				int z=pos.getZ();
				<@procedureOBJToCode data.onNeighbourChanges/>
			}
			</#if>

			<#if hasProcedure(data.onTickUpdate)>
			@Override public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
				super.updateTick(world, pos, state, random);
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				<@procedureOBJToCode data.onTickUpdate/>
				world.scheduleUpdate(new BlockPos(x, y, z), this, this.tickRate(world));
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
		}.setUnlocalizedName("${registryname}").setRegistryName("${registryname}"));
		elements.items.add(() -> new ItemBlock(block).setRegistryName("${registryname}"));
	}

	@Override public void preInit(FMLPreInitializationEvent event) {
		FluidRegistry.registerFluid(fluid);
		<#if data.generateBucket>
		FluidRegistry.addBucketForFluid(fluid);
        </#if>
	}

	@Override @SideOnly(Side.CLIENT) public void registerModels(ModelRegistryEvent event) {
		ModelBakery.registerItemVariants(item);
		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
			@Override public ModelResourceLocation getModelLocation(ItemStack stack) {
				return new ModelResourceLocation("${modid}:${registryname}","${registryname}" );
			}
		});
		ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
			@Override protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation("${modid}:${registryname}","${registryname}" );
			}
		});
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
				if(dimID== World${(worldType.toString().replace("CUSTOM:", ""))}.DIMID)
					dimensionCriteria=true;
    	    </#if>
    	</#list>

		if(!dimensionCriteria)
			return;

		int i = chunkX + random.nextInt(16) + 8;
		int j = random.nextInt(256);
		int k = chunkZ + random.nextInt(16) + 8;

		new WorldGenLakes(block).generate(world, random, new BlockPos(i, j, k));
	}
	</#if>

}
<#-- @formatter:on -->