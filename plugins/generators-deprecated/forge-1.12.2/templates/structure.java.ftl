<#-- @formatter:off -->
<#include "mcitems.ftl">
<#include "procedures.java.ftl">

package ${package}.world.structure;

@Elements${JavaModName}.ModElement.Tag public class Structure${name} extends Elements${JavaModName}.ModElement{

	public Structure${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

<#if data.structure??>
	@Override public void generateWorld(Random random,int i2,int k2,World world,int dimID,IChunkGenerator cg,
		IChunkProvider cp){

		boolean dimensionCriteria = false;
		boolean isNetherType = false;
	<#list data.spawnWorldTypes as worldType>
		<#if worldType=="Surface">
				if (dimID == 0)
					dimensionCriteria = true;
		<#elseif worldType=="Nether">
				if (dimID == -1) {
					dimensionCriteria = true;
					isNetherType = true;
				}
		<#elseif worldType=="End">
				if (dimID == 1)
					dimensionCriteria = true;
		<#else>
				if (dimID == World${(worldType.toString().replace("CUSTOM:", ""))}.DIMID) {
					dimensionCriteria = true;
					isNetherType = World${(worldType.toString().replace("CUSTOM:", ""))}.NETHER_TYPE;
				}
		</#if>
	</#list>
		if (!dimensionCriteria)
			return;

			if ((random.nextInt(1000000) + 1) <= ${data.spawnProbability}) {

				int count = random.nextInt(${data.maxCountPerChunk - data.minCountPerChunk + 1}) + ${data.minCountPerChunk};
				for(int a = 0; a < count; a++) {

					int i = i2 + random.nextInt(16) + 8;
					int k = k2 + random.nextInt(16) + 8;
					int height = 255;

					<#if data.surfaceDetectionType=="First block">
					if(isNetherType) {
						boolean notpassed = true;
						while (height > 0) {
							if (notpassed && world.isAirBlock(new BlockPos(i, height, k)))
								notpassed = false;
							else if (!notpassed && !world.isAirBlock(new BlockPos(i, height, k)))
								break;
							height--;
						}
					} else {
						while (height > 0) {
							if (!world.isAirBlock(new BlockPos(i, height, k)))
								break;
							height--;
						}
					}
					<#elseif data.surfaceDetectionType=="First motion blocking block">
					if(isNetherType) {
						boolean notpassed = true;
						while (height > 0) {
							if (notpassed &&(world.isAirBlock(new BlockPos(i, height, k)) ||
									!world.getBlockState(new BlockPos(i, height, k)).getBlock().getMaterial(world.getBlockState(new BlockPos(i, height, k))).blocksMovement()))
								notpassed = false;
							else if (!notpassed && !world.isAirBlock(new BlockPos(i, height, k))
									&& world.getBlockState(new BlockPos(i, height, k)).getBlock().getMaterial(world.getBlockState(new BlockPos(i, height, k))).blocksMovement())
								break;
							height--;
						}
					} else {
							while (height > 0) {
								if (!world.isAirBlock(new BlockPos(i, height, k))
										&& world.getBlockState(new BlockPos(i, height, k)).getBlock().getMaterial(world.getBlockState(new BlockPos(i, height, k))).blocksMovement())
									break;
								height--;
							}
					}
					</#if>

		<#if data.spawnLocation=="Ground">
					int j = height - 1;
		<#elseif data.spawnLocation=="Air">
					int j = height + random.nextInt(50) + 16;
		<#elseif data.spawnLocation=="Underground">
					int j = Math.abs(random.nextInt(Math.max(1, height)) - 24);
		</#if>

		<#if data.restrictionBlocks?has_content>
			IBlockState blockAt = world.getBlockState(new BlockPos(i, j + 1, k));
			boolean blockCriteria = false;
			IBlockState require;
			<#list data.restrictionBlocks as restrictionBlock>
					require = ${mappedBlockToBlockStateCode(restrictionBlock)};
				<#if hasMetadata(restrictionBlock)>try {
					if ((blockAt.getBlock() == require.getBlock()) && (blockAt.getBlock().getMetaFromState(blockAt)
						== require.getBlock().getMetaFromState(require)))
						blockCriteria = true;
				} catch (Exception e) {
					if (blockAt.getBlock() == require.getBlock())
						blockCriteria = true;
				}
				<#else>if (blockAt.getBlock() == require.getBlock())
					blockCriteria = true;
				</#if>
			</#list>if (!blockCriteria)
				continue;
		</#if>

		<#if data.restrictionBiomes?has_content>
			boolean biomeCriteria = false;
			Biome biome = world.getBiome(new BlockPos(i, j, k));
			<#list data.restrictionBiomes as restrictionBiome>
				<#if restrictionBiome.canProperlyMap()>
				if (Biome.REGISTRY.getNameForObject(biome).equals(new ResourceLocation("${restrictionBiome}")))
					biomeCriteria = true;
				</#if>
			</#list>
			if (!biomeCriteria)
				continue;
		</#if>

			if (world.isRemote)
				return;

			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),new ResourceLocation("${modid}" ,"${data.structure}"));
			if (template == null)
				return;

			<#if data.randomlyRotateStructure>
				Rotation rotation = Rotation.values()[random.nextInt(3)];
				Mirror mirror = Mirror.values()[random.nextInt(2)];
			<#else>
				Rotation rotation = Rotation.NONE;
				Mirror mirror = Mirror.NONE;
			</#if>

			BlockPos spawnTo = new BlockPos(i, j + ${data.spawnHeightOffset}, k);
			IBlockState iblockstate = world.getBlockState(spawnTo);
			world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
			template.addBlocksToWorldChunk(world, spawnTo,new PlacementSettings().setRotation(rotation).setMirror(mirror).setChunk((ChunkPos)null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));

			<#if hasProcedure(data.onStructureGenerated)>
					int x = spawnTo.getX();
					int y = spawnTo.getY();
					int z = spawnTo.getZ();
				<@procedureOBJToCode data.onStructureGenerated/>
			</#if>

		}

		}
	}
	</#if>

}
<#-- @formatter:on -->