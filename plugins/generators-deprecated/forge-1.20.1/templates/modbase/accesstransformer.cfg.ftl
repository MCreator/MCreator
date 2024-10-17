<#if w.getGElementsOfType("biome")?filter(e -> e.spawnBiome || e.spawnInCaves || e.spawnBiomeNether)?size != 0>
public net.minecraft.world.level.biome.MultiNoiseBiomeSource m_274409_()Lnet/minecraft/world/level/biome/Climate$ParameterList; #parameters()
public-f net.minecraft.world.level.chunk.ChunkGenerator f_62137_ #biomeSource
public-f net.minecraft.world.level.chunk.ChunkGenerator f_223020_ #featuresPerStep
public-f net.minecraft.world.level.chunk.ChunkGenerator f_223021_ #generationSettingsGetter
public-f net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator f_64318_ #settings
public net.minecraft.world.level.levelgen.SurfaceRules$SequenceRuleSource
public net.minecraft.world.level.levelgen.SurfaceRules$SequenceRuleSource <init>(Ljava/util/List;)V
</#if>

<#if w.hasElementsOfType("feature")>
public net.minecraft.world.level.levelgen.feature.ScatteredOreFeature <init>(Lcom/mojang/serialization/Codec;)V #constructor
public-f net.minecraft.world.level.levelgen.feature.TreeFeature m_142674_(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z #place
</#if>
