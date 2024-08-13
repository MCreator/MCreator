<#if w.getGElementsOfType("biome")?filter(e -> e.spawnBiome || e.spawnInCaves || e.spawnBiomeNether)?size != 0>
public net.minecraft.world.level.biome.MultiNoiseBiomeSource parameters()Lnet/minecraft/world/level/biome/Climate$ParameterList;
public-f net.minecraft.world.level.chunk.ChunkGenerator biomeSource
public-f net.minecraft.world.level.chunk.ChunkGenerator featuresPerStep
public-f net.minecraft.world.level.chunk.ChunkGenerator generationSettingsGetter
public-f net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator settings
public net.minecraft.world.level.levelgen.SurfaceRules$SequenceRuleSource
public net.minecraft.world.level.levelgen.SurfaceRules$SequenceRuleSource <init>(Ljava/util/List;)V
</#if>

<#if w.getGElementsOfType("biome")?filter(e -> e.hasVines() || e.hasFruits())?size != 0>
public net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType <init>(Lcom/mojang/serialization/MapCodec;)V
</#if>

<#if w.hasElementsOfType("feature")>
public net.minecraft.world.level.levelgen.feature.ScatteredOreFeature <init>(Lcom/mojang/serialization/Codec;)V
public-f net.minecraft.world.level.levelgen.feature.TreeFeature place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z
</#if>
