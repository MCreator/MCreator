<#if w.hasToolsOfType("Fishing rod")>
public net.minecraft.world.entity.projectile.FishingHook m_37136_(Lnet/minecraft/world/entity/player/Player;)Z #shouldStopFishing
</#if>

<#if w.hasElementsOfType("dimension")>
public net.minecraft.world.level.levelgen.carver.WorldCarver f_64983_ #replaceableBlocks
public net.minecraft.client.renderer.DimensionSpecialEffects f_108857_ #EFFECTS
</#if>

<#if w.hasElementsOfType("biome")>
public net.minecraft.world.level.biome.MultiNoiseBiomeSource <init>(Lnet/minecraft/world/level/biome/Climate$ParameterList;Ljava/util/Optional;)V #constructor
public-f net.minecraft.world.level.biome.MultiNoiseBiomeSource f_48438_ #preset
public-f net.minecraft.world.level.biome.MultiNoiseBiomeSource f_48435_ #parameters
public-f net.minecraft.world.level.chunk.ChunkGenerator f_62137_ #biomeSource
public-f net.minecraft.world.level.chunk.ChunkGenerator f_62138_ #runtimeBiomeSource
public-f net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator f_64318_ #settings
public net.minecraft.world.level.levelgen.SurfaceRules$SequenceRuleSource
</#if>

<#if w.hasGameRulesOfType("Number")>
public net.minecraft.world.level.GameRules$IntegerValue m_46312_(I)Lnet/minecraft/world/level/GameRules$Type; #create
</#if>

<#if w.hasGameRulesOfType("Logic")>
public net.minecraft.world.level.GameRules$BooleanValue m_46250_(Z)Lnet/minecraft/world/level/GameRules$Type; #create
</#if>

<#if w.hasElementsOfType("feature")>
public net.minecraft.world.level.levelgen.feature.ScatteredOreFeature <init>(Lcom/mojang/serialization/Codec;)V #constructor
</#if>