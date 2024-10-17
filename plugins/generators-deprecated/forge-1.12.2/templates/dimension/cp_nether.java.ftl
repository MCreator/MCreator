public static class ChunkProviderModded implements IChunkGenerator
{
	private static final IBlockState STONE = ${mappedBlockToBlockStateCode(data.mainFillerBlock)};
	private static final IBlockState STONE2 = ${mappedBlockToBlockStateCode(data.secondaryFillerBlock)};
	private static final IBlockState STONE3 = ${mappedBlockToBlockStateCode(data.tertiaryFillerBlock)};
	private static final IBlockState FLUID = ${mappedBlockToBlockStateCode(data.fluidBlock)};
	private static final IBlockState AIR = Blocks.AIR.getDefaultState();
	private static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

	private static final int SEALEVEL = 63;

	private final World world;
	private final Random random;

	private final NoiseGeneratorOctaves lperlinNoise1;
	private final NoiseGeneratorOctaves lperlinNoise2;
	private final NoiseGeneratorOctaves perlinNoise1;
	private final NoiseGeneratorOctaves secondaryStoneNoiseGen;
	private final NoiseGeneratorOctaves depthNoiseGen;
	private final NoiseGeneratorOctaves depthNoise;
	private final MapGenBase genNetherCaves;

	private double[] stoneNoise3 = new double[256];
	private double[] stoneNoise2 = new double[256];
	private double[] depthBuffer = new double[256];
	private double[] buffer;
	private double[] pnr;
	private double[] ar;
	private double[] br;
	private double[] dr;
	double[] unused;

	public ChunkProviderModded(World worldIn, long seed) {
		worldIn.setSeaLevel(SEALEVEL);

		this.world = worldIn;
		this.random = new Random(seed);
		this.lperlinNoise1 = new NoiseGeneratorOctaves(this.random, 16);
		this.lperlinNoise2 = new NoiseGeneratorOctaves(this.random, 16);
		this.perlinNoise1 = new NoiseGeneratorOctaves(this.random, 8);
		this.secondaryStoneNoiseGen = new NoiseGeneratorOctaves(this.random, 4);
		this.depthNoiseGen = new NoiseGeneratorOctaves(this.random, 4);
		this.depthNoise = new NoiseGeneratorOctaves(this.random, 16);
		this.genNetherCaves = new MapGenCavesHell();
	}

	@Override public Chunk generateChunk(int x, int z) {
		this.random.setSeed((long) x * 347539041L + (long) z * 535358712L);

		ChunkPrimer chunkprimer = new ChunkPrimer();
		this.prepareHeights(x, z, chunkprimer);
		this.buildSurfaces(x, z, chunkprimer);
		this.genNetherCaves.generate(this.world, x, z, chunkprimer);

		Chunk chunk = new Chunk(this.world, chunkprimer, x, z);
		Biome[] abiome = this.world.getBiomeProvider().getBiomes((Biome[]) null, x * 16, z * 16, 16, 16);
		byte[] abyte = chunk.getBiomeArray();
		for (int i = 0; i < abyte.length; ++i)
			abyte[i] = (byte) Biome.getIdForBiome(abiome[i]);

		chunk.resetRelightChecks();
		return chunk;
	}

	@Override public void populate(int x, int z) {
		BlockFalling.fallInstantly = true;
		net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, this.random, x, z, false);
		int i = x * 16;
		int j = z * 16;
		BlockPos blockpos = new BlockPos(i, 0, j);
		Biome biome = this.world.getBiome(blockpos.add(16, 0, 16));
		ChunkPos chunkpos = new ChunkPos(x, z);

		net.minecraftforge.common.MinecraftForge.EVENT_BUS
				.post(new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Pre(this.world, this.random, blockpos));

		biome.decorate(this.world, this.random, new BlockPos(i, 0, j));

		net.minecraftforge.common.MinecraftForge.EVENT_BUS
				.post(new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Post(this.world, this.random, blockpos));

		if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.random, x, z, false,
				net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS))
			WorldEntitySpawner.performWorldGenSpawning(this.world, biome, i + 8, j + 8, 16, 16, this.random);

		net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.world, this.random, x, z, false);

		BlockFalling.fallInstantly = false;
	}

	@Override public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		return this.world.getBiome(pos).getSpawnableList(creatureType);
	}

	@Override public void recreateStructures(Chunk chunkIn, int x, int z) {
	}

	@Override public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
		return false;
	}

	@Override
	public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
		return null;
	}

	@Override public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}

	${mcc.getMethod("net.minecraft.world.gen.ChunkGeneratorHell", "prepareHeights", "int", "int", "ChunkPrimer")
		 .replace("LAVA", "FLUID")
		 .replace("NETHERRACK", "STONE")}

	${mcc.getMethod("net.minecraft.world.gen.ChunkGeneratorHell", "getHeights", "double[]", "int", "int", "int", "int", "int", "int")
		 .replace("noiseData4", "unused")
		 .replace("this.scaleNoise", "new NoiseGeneratorOctaves(this.random, 10)")}

	${mcc.getMethod("net.minecraft.world.gen.ChunkGeneratorHell", "buildSurfaces", "int", "int", "ChunkPrimer")
		 .replace("slowsandGravelNoiseGen", "secondaryStoneNoiseGen")
		 .replace("slowsandNoise", "stoneNoise2")
		 .replace("gravelNoise", "stoneNoise3")
		 .replace("netherrackExculsivityNoiseGen", "depthNoiseGen")
		 .replace("Blocks.NETHERRACK", "STONE.getBlock()")
		 .replace("iblockstate1 = SOUL_SAND;", "iblockstate1 = iblockstate;")
		 .replace("NETHERRACK", "STONE")
		 .replace("SOUL_SAND", "STONE2")
		 .replace("GRAVEL", "STONE3")
		 .replace("LAVA", "FLUID")
		 .replace("rand", "random")
		 .replace("this.scaleNoise", "new NoiseGeneratorOctaves(this.random, 10)")}

}