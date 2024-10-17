public static class ChunkProviderModded implements IChunkGenerator
{

	private static final IBlockState STONE = ${mappedBlockToBlockStateCode(data.mainFillerBlock)};
	private static final IBlockState AIR = Blocks.AIR.getDefaultState();

	private static final int SEALEVEL = 63;

	private final World world;
	private Random random;
	
	private final NoiseGeneratorSimplex islandNoise;
	private final NoiseGeneratorOctaves perlinnoise1;
	private final NoiseGeneratorOctaves perlinnoise2;
	private final NoiseGeneratorOctaves perlinnoise3;
	private final NoiseGeneratorPerlin height;
	
	private Biome[] biomesForGeneration;
	private double[] buffer;
	private double[] pnr;
	private double[] ar;
	private double[] br;

	private double[] depthbuff = new double[256];

	private WorldGenerator islandGen;

	public ChunkProviderModded(World worldIn, long seed) {
		worldIn.setSeaLevel(SEALEVEL);

		this.world = worldIn;
		this.random = new Random(seed);
		this.perlinnoise1 = new NoiseGeneratorOctaves(this.random, 16);
		this.perlinnoise2 = new NoiseGeneratorOctaves(this.random, 16);
		this.perlinnoise3 = new NoiseGeneratorOctaves(this.random, 8);
		this.height = new NoiseGeneratorPerlin(this.random, 4);
		this.islandNoise = new NoiseGeneratorSimplex(this.random);

		this.islandGen = new WorldGenerator(){
			public boolean generate(World worldIn, Random rand, BlockPos position) {
				float f = (float)(rand.nextInt(4) + 5);
				for (int i = 0; f > 1.5F; --i) {
					for (int j = MathHelper.floor(-f); j <= MathHelper.ceil(f); ++j) {
						for (int k = MathHelper.floor(-f); k <= MathHelper.ceil(f); ++k) {
							if ((float)(j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
								this.setBlockAndNotifyAdequately(worldIn, position.add(j, i, k), STONE);
							}
						}
					}
					f = (float)((double)f - ((double)rand.nextInt(2) + 0.5D));
				}

				return true;
			}
		};
	}

	@Override public Chunk generateChunk(int x, int z) {
		this.random.setSeed((long) x * 535358712L + (long) z * 347539041L);
		ChunkPrimer chunkprimer = new ChunkPrimer();
		this.setBlocksInChunk(x, z, chunkprimer);

		this.biomesForGeneration = this.world.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 16, z * 16, 16, 16);
		this.replaceBiomeBlocks(x, z, chunkprimer, this.biomesForGeneration);

		Chunk chunk = new Chunk(this.world, chunkprimer, x, z);
		byte[] abyte = chunk.getBiomeArray();

		for (int i = 0; i < abyte.length; ++i)
			abyte[i] = (byte) Biome.getIdForBiome(this.biomesForGeneration[i]);

		chunk.generateSkylightMap();
		return chunk;
	}

	@Override public void populate(int x, int z) {
		BlockFalling.fallInstantly = true;

		net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, this.random, x, z, false);

		int i = x * 16;
		int j = z * 16;
		BlockPos blockpos = new BlockPos(i, 0, j);

		float f = this.getIslandHeightValue(x, z, 1, 1);
		if (f < -10.0F && this.random.nextInt(6) == 0) {
			this.islandGen.generate(this.world, this.random, blockpos.add(this.random.nextInt(16) + 8, 55 + this.random
					.nextInt(16), this.random.nextInt(16) + 8));
			if (this.random.nextInt(4) == 0)
				this.islandGen.generate(this.world, this.random, blockpos.add(this.random
						.nextInt(16) + 8, 55 + this.random.nextInt(16), this.random.nextInt(16) + 8));
		}

		Biome biome = this.world.getBiome(blockpos.add(16, 0, 16));

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
	public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position,
			boolean findUnexplored) {
		return null;
	}

	@Override public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}

    ${mcc.getMethod("net.minecraft.world.gen.ChunkGeneratorEnd", "getHeights", "double[]", "int", "int", "int", "int", "int", "int")
		 .replace("lperlinNoise1", "perlinnoise1")
		 .replace("lperlinNoise2", "perlinnoise2")
		 .replace("perlinNoise1", "perlinnoise3")}

	${mcc.getMethod("net.minecraft.world.gen.ChunkGeneratorEnd", "getIslandHeightValue", "int", "int", "int", "int")}

	${mcc.getMethod("net.minecraft.world.gen.ChunkGeneratorEnd", "setBlocksInChunk", "int", "int", "ChunkPrimer")
		 .replace("END_STONE", "STONE")}

	private void replaceBiomeBlocks(int x, int z, ChunkPrimer primer, Biome[] biomesIn) {
		this.depthbuff = this.height.getRegion(this.depthbuff, (double) (x * 16), (double) (z * 16), 16, 16, 0.0625, 0.0625, 1.0);
		for (int i = 0; i < 16; i++)
			for (int j = 0; j < 16; j++)
				generateBiomeTerrain(this.world, this.random, primer, x * 16 + i, z * 16 + j, this.depthbuff[j + i * 16], biomesIn[j + i * 16]);
	}

	private void generateBiomeTerrain(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z,
			double noiseVal, Biome biome) {
		int i = SEALEVEL;
		IBlockState iblockstate = biome.topBlock;
		IBlockState iblockstate1 = biome.fillerBlock;
		int j = -1;
		int k = (int) (noiseVal / 3.0 + 3 + rand.nextDouble() / 4f);
		int l = x & 15;
		int i1 = z & 15;

		for (int j1 = 255; j1 >= 0; --j1) {
			IBlockState iblockstate2 = chunkPrimerIn.getBlockState(i1, j1, l);

			if (iblockstate2.getMaterial() == Material.AIR) {
				j = -1;
			} else if (iblockstate2.getBlock() == STONE.getBlock()) {
				if (j == -1) {
					if (k <= 0) {
						iblockstate = AIR;
						iblockstate1 = STONE;
					}

					j = k;

					if (j1 >= i - 1) {
						chunkPrimerIn.setBlockState(i1, j1, l, iblockstate);
					} else if (j1 < i - 7 - k) {
						iblockstate1 = STONE;
					} else {
						chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);
					}
				} else if (j > 0) {
					j--;
					chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);
				}
			}
		}
	}

}