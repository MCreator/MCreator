public static class GenLayerBiomesCustom extends GenLayer {

	private Biome[] allowedBiomes = {
    	<#list data.biomesInDimension as biome>
			Biome.REGISTRY.getObject(new ResourceLocation("${biome}")),
		</#list>
	};

	public GenLayerBiomesCustom(long seed) {
		super(seed);
	}

	@Override public int[] getInts(int x, int z, int width, int depth) {
		int[] dest = IntCache.getIntCache(width * depth);
		for (int dz = 0; dz < depth; dz++) {
			for (int dx = 0; dx < width; dx++) {
				this.initChunkSeed(dx + x, dz + z);
				dest[(dx + dz * width)] = Biome.getIdForBiome(this.allowedBiomes[nextInt(this.allowedBiomes.length)]);
			}
		}
		return dest;
	}
}

public static class BiomeProviderCustom extends BiomeProvider {

	private GenLayer genBiomes;
	private GenLayer biomeIndexLayer;
	private BiomeCache biomeCache;

	public BiomeProviderCustom() {
		this.biomeCache = new BiomeCache(this);
	}

	public BiomeProviderCustom(long seed) {
		this.biomeCache = new BiomeCache(this);
		GenLayer[] agenlayer = makeTheWorld(seed);
		this.genBiomes = agenlayer[0];
		this.biomeIndexLayer = agenlayer[1];
	}

	private GenLayer[] makeTheWorld(long seed) {
		GenLayer biomes = new GenLayerBiomesCustom(1);
		biomes = new GenLayerZoom(1000, biomes);
		biomes = new GenLayerZoom(1001, biomes);
		biomes = new GenLayerZoom(1002, biomes);
		biomes = new GenLayerZoom(1003, biomes);
		biomes = new GenLayerZoom(1004, biomes);
		biomes = new GenLayerZoom(1005, biomes);
		GenLayer genlayervoronoizoom = new GenLayerVoronoiZoom(10, biomes);
		biomes.initWorldGenSeed(seed);
		genlayervoronoizoom.initWorldGenSeed(seed);
		return new GenLayer[] { biomes, genlayervoronoizoom };
	}

	public BiomeProviderCustom(World world) {
		this(world.getSeed());
	}

	@Override public void cleanupCache() {
		this.biomeCache.cleanupCache();
	}

	@Override public Biome getBiome(BlockPos pos) {
		return this.getBiome(pos, null);
	}

	@Override public Biome getBiome(BlockPos pos, Biome defaultBiome) {
		return this.biomeCache.getBiome(pos.getX(), pos.getZ(), defaultBiome);
	}

	@Override public Biome[] getBiomes(Biome[] oldBiomeList, int x, int z, int width, int depth) {
		return this.getBiomes(oldBiomeList, x, z, width, depth, true);
	}

	@Override ${mcc.getMethod("net.minecraft.world.biome.BiomeProvider", "getBiomesForGeneration", "Biome[]", "int", "int", "int", "int")}

	@Override ${mcc.getMethod("net.minecraft.world.biome.BiomeProvider", "getBiomes", "Biome[]", "int", "int", "int", "int", "boolean")}

	@Override ${mcc.getMethod("net.minecraft.world.biome.BiomeProvider", "areBiomesViable", "int", "int", "int", "List")}

	@Override ${mcc.getMethod("net.minecraft.world.biome.BiomeProvider", "findBiomePosition", "int", "int", "int", "List", "Random")}

}