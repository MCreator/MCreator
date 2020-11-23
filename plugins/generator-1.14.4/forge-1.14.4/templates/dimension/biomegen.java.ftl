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
public static class BiomeLayerCustom implements IC0Transformer {

	@Override public int apply(INoiseRandom context, int value) {
		return Registry.BIOME.getId(dimensionBiomes[context.random(dimensionBiomes.length)]);
	}

}

public static class BiomeProviderCustom extends BiomeProvider {

	private final Layer genBiomes;
	private final Layer biomeFactoryLayer;
	private final Biome[] biomes;
	<#if data.worldGenType == "End like gen">
	private final SimplexNoiseGenerator generator;
	</#if>

	<#if data.worldGenType == "Normal world gen">
	private static boolean biomesPatched = false;
	</#if>

	public BiomeProviderCustom(World world) {
		Layer[] aLayer = makeTheWorld(world.getSeed());
		this.genBiomes = aLayer[0];
		this.biomeFactoryLayer = aLayer[1];
		this.biomes = dimensionBiomes;

		<#if data.worldGenType == "End like gen">
		this.generator = new SimplexNoiseGenerator(new SharedSeedRandom(world.getSeed()));
		</#if>

		<#if data.worldGenType == "Normal world gen">
		if(!biomesPatched) {
			for (Biome biome : this.biomes) {
				biome.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(new CaveWorldCarver(ProbabilityConfig::deserialize, 256) {

					{
						carvableBlocks = ImmutableSet.of(
							${mappedBlockToBlockStateCode(data.mainFillerBlock)}.getBlock(),
							biome.getSurfaceBuilder().getConfig().getTop().getBlock(),
							biome.getSurfaceBuilder().getConfig().getUnder().getBlock()
						);
					}

				}, new ProbabilityConfig(0.14285715f)));
			}
			biomesPatched = true;
		}
		</#if>
	}

	private Layer[] makeTheWorld(long seed) {
		LongFunction<IExtendedNoiseRandom<LazyArea>> contextFactory = l -> new LazyAreaLayerContext(25, seed, l);

		IAreaFactory<LazyArea> parentLayer = IslandLayer.INSTANCE.apply(contextFactory.apply(1));
		IAreaFactory<LazyArea> biomeLayer = (new BiomeLayerCustom()).apply(contextFactory.apply(200), parentLayer);

		biomeLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1000), biomeLayer);
		biomeLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1001), biomeLayer);
		biomeLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1002), biomeLayer);
		biomeLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1003), biomeLayer);
		biomeLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1004), biomeLayer);
		biomeLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1005), biomeLayer);

		IAreaFactory<LazyArea> voronoizoom = VoroniZoomLayer.INSTANCE.apply(contextFactory.apply(10), biomeLayer);

		return new Layer[] { new Layer(biomeLayer), new Layer(voronoizoom) };
	}

	@Override ${mcc.getMethod("net.minecraft.world.biome.provider.OverworldBiomeProvider", "getBiome", "int", "int")}
	@Override ${mcc.getMethod("net.minecraft.world.biome.provider.OverworldBiomeProvider", "func_222366_b", "int", "int")}
	@Override ${mcc.getMethod("net.minecraft.world.biome.provider.OverworldBiomeProvider", "getBiomes", "int", "int", "int", "int", "boolean")}
	@Override ${mcc.getMethod("net.minecraft.world.biome.provider.OverworldBiomeProvider", "getBiomesInSquare", "int", "int", "int")}
	@Override ${mcc.getMethod("net.minecraft.world.biome.provider.OverworldBiomeProvider", "findBiomePosition", "int", "int", "int", "List", "Random")}
	@Override ${mcc.getMethod("net.minecraft.world.biome.provider.OverworldBiomeProvider", "hasStructure", "Structure")}
	@Override ${mcc.getMethod("net.minecraft.world.biome.provider.OverworldBiomeProvider", "getSurfaceBlocks")}

	<#if data.worldGenType == "End like gen">
	@Override ${mcc.getMethod("net.minecraft.world.biome.provider.EndBiomeProvider", "func_222365_c", "int", "int")}
    </#if>

}
<#-- @formatter:on -->