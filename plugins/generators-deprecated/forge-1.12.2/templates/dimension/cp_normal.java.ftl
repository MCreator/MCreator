public static class ChunkProviderModded implements IChunkGenerator
{
	private static final IBlockState STONE = ${mappedBlockToBlockStateCode(data.mainFillerBlock)};
	private static final IBlockState STONE2 = ${mappedBlockToBlockStateCode(data.secondaryFillerBlock)};
	private static final IBlockState FLUID = ${mappedBlockToBlockStateCode(data.fluidBlock)};
	private static final IBlockState AIR = Blocks.AIR.getDefaultState();
	private static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

	private static final int SEALEVEL = 63;

	private final Random random;
	private final NoiseGeneratorOctaves perlin1;
	private final NoiseGeneratorOctaves perlin2;
	private final NoiseGeneratorOctaves perlin;
	private final NoiseGeneratorPerlin height;
	private final NoiseGeneratorOctaves depth;
	private final World world;
	private final WorldType terrainType;
	private final MapGenBase caveGenerator;
	private final MapGenBase ravineGenerator;

	private Biome[] biomesForGeneration;
	private double[] heightMap;
	private double[] depthbuff = new double[256];
	private double[] noiseRegMain;
	private double[] limitRegMin;
	private double[] limitRegMax;
	private double[] depthReg;
	private float[] biomeWeights;

	public ChunkProviderModded(World worldIn, long seed) {
		worldIn.setSeaLevel(SEALEVEL);

		caveGenerator = new MapGenCaves() {
			@Override protected boolean canReplaceBlock(IBlockState a, IBlockState b) {
				if (a.getBlock() == STONE.getBlock())
					return true;
				return super.canReplaceBlock(a, b);
			}
		};

		ravineGenerator = new MapGenRavine() {
			@Override
			protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
				net.minecraft.world.biome.Biome biome = world
						.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
				IBlockState state = data.getBlockState(x, y, z);
				if (state.getBlock() == STONE.getBlock() || state.getBlock() == biome.topBlock.getBlock()
						|| state.getBlock() == biome.fillerBlock.getBlock()) {
					if (y - 1 < 10) {
						data.setBlockState(x, y, z, FLOWING_LAVA);
					} else {
						data.setBlockState(x, y, z, AIR);

						if (foundTop && data.getBlockState(x, y - 1, z).getBlock() == biome.fillerBlock.getBlock()) {
							data.setBlockState(x, y - 1, z, biome.topBlock.getBlock().getDefaultState());
						}
					}
				}
			}
		};

		this.world = worldIn;
		this.terrainType = worldIn.getWorldInfo().getTerrainType();
		this.random = new Random(seed);
		this.perlin1 = new NoiseGeneratorOctaves(this.random, 16);
		this.perlin2 = new NoiseGeneratorOctaves(this.random, 16);
		this.perlin = new NoiseGeneratorOctaves(this.random, 8);
		this.height = new NoiseGeneratorPerlin(this.random, 4);
		this.depth = new NoiseGeneratorOctaves(this.random, 16);
		this.heightMap = new double[825];
		this.biomeWeights = new float[25];

		for (int i = -2; i <= 2; i++)
			for (int j = -2; j <= 2; j++)
				this.biomeWeights[i + 2 + (j + 2) * 5] = 10 / MathHelper.sqrt((float) (i * i + j * j) + 0.2f);
	}

	@Override public Chunk generateChunk(int x, int z) {
		this.random.setSeed((long) x * 535358712L + (long) z * 347539041L);
		ChunkPrimer chunkprimer = new ChunkPrimer();
		this.setBlocksInChunk(x, z, chunkprimer);

		this.biomesForGeneration = this.world.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 16, z * 16, 16, 16);
		this.replaceBiomeBlocks(x, z, chunkprimer, this.biomesForGeneration);

		this.caveGenerator.generate(this.world, x, z, chunkprimer);
		this.ravineGenerator.generate(this.world, x, z, chunkprimer);

		Chunk chunk = new Chunk(this.world, chunkprimer, x, z);
		byte[] abyte = chunk.getBiomeArray();

		for (int i = 0; i < abyte.length; ++i)
			abyte[i] = (byte) Biome.getIdForBiome(this.biomesForGeneration[i]);

		chunk.generateSkylightMap();
		return chunk;
	}

	@Override public void populate(int x, int z) {
		BlockFalling.fallInstantly = true;

		int i = x * 16;
		int j = z * 16;

		BlockPos blockpos = new BlockPos(i, 0, j);
		Biome biome = this.world.getBiome(blockpos.add(16, 0, 16));
		this.random.setSeed(this.world.getSeed());
		long k = this.random.nextLong() / 2 * 2 + 1;
		long l = this.random.nextLong() / 2 * 2 + 1;
		this.random.setSeed((long) x * k + (long) z * l ^ this.world.getSeed());

		net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, this.random, x, z, false);

		if (this.random.nextInt(4) == 0)
			if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.random, x, z, false,
					net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE)) {
				int i1 = this.random.nextInt(16) + 8;
				int j1 = this.random.nextInt(256);
				int k1 = this.random.nextInt(16) + 8;
				(new WorldGenLakes(FLUID.getBlock())).generate(this.world, this.random, blockpos.add(i1, j1, k1));
			}

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

    ${mcc.getMethod("net.minecraft.world.gen.ChunkGeneratorOverworld", "setBlocksInChunk", "int", "int", "ChunkPrimer")
         .replace("this.oceanBlock", "FLUID")
         .replace("this.settings.seaLevel", "SEALEVEL")}

    ${mcc.getMethod("net.minecraft.world.gen.ChunkGeneratorOverworld", "generateHeightmap", "int", "int", "int")
         .replace("this.settings.depthNoiseScaleExponent", "0.5f")
		 .replace("this.settings.depthNoiseScaleX", "200")
		 .replace("this.settings.depthNoiseScaleZ", "200")
		 .replace("this.settings.biomeScaleWeight", "1")
		 .replace("this.settings.biomeDepthWeight", "1")
		 .replace("this.settings.upperLimitScale", "512")
		 .replace("this.settings.biomeDepthOffSet", "0")
		 .replace("this.settings.biomeScaleOffset", "0")
		 .replace("this.settings.stretchY", "12")
         .replace("depthRegion", "depthReg")
         .replace("depthNoise", "depth")
         .replace("mainPerlinNoise", "perlin")
         .replace("minLimitPerlinNoise", "perlin1")
         .replace("maxLimitPerlinNoise", "perlin2")
         .replace("mainNoiseRegion", "noiseRegMain")
         .replace("minLimitRegion", "limitRegMin")
         .replace("maxLimitRegion", "limitRegMax")
         .replace("this.settings.depthScaleExponent", "8.5f")
         .replace("this.settings.baseSize", "8.5f")
         .replace("this.settings.coordinateScale", "684.412f")
         .replace("this.settings.heightScale", "684.412f")
         .replace("this.settings.mainNoiseScaleY", "160")
         .replace("this.settings.mainNoiseScaleX", "80")
         .replace("this.settings.mainNoiseScaleZ", "80")
         .replace("this.settings.lowerLimitScale", "512")}

	private void replaceBiomeBlocks(int x, int z, ChunkPrimer primer, Biome[] biomesIn) {
		this.depthbuff = this.height.getRegion(this.depthbuff, (double) (x * 16), (double) (z * 16), 16, 16, 0.0625, 0.0625, 1);
		for (int i = 0; i < 16; i++)
			for (int j = 0; j < 16; j++)
				generateBiomeTerrain(this.world, this.random, primer, x * 16 + i, z * 16 + j, this.depthbuff[j + i * 16], biomesIn[j + i * 16]);
	}

	${mcc.getMethod("net.minecraft.world.biome.Biome", "generateBiomeTerrain", "World", "Random", "ChunkPrimer", "int", "int", "double")
		 .replace("double noiseVal", "double noiseVal, Biome biome")
		 .replace("worldIn.getSeaLevel()", "SEALEVEL")
		 .replace("Blocks.STONE", "STONE.getBlock()")
		 .replace("ICE", "FLUID")
		 .replace("WATER", "FLUID")
		 .replace("GRAVEL", "STONE2")
		 .replace("RED_SANDSTONE", "STONE2")
		 .replace("SANDSTONE", "STONE2")
		 .replace("SANDSTONE", "STONE2")
		 .replace("this.getTemperature", "biome.getTemperature")
		 .replace("this.topBlock", "biome.topBlock")
		 .replace("this.fillerBlock", "biome.fillerBlock")}

}