<#if w.hasGameRulesOfType("Number")>
public net.minecraft.world.level.GameRules$IntegerValue m_46312_(I)Lnet/minecraft/world/level/GameRules$Type; #create
</#if>

<#if w.hasGameRulesOfType("Logic")>
public net.minecraft.world.level.GameRules$BooleanValue m_46250_(Z)Lnet/minecraft/world/level/GameRules$Type; #create
</#if>

<#if w.hasBiomesInVanillaDimensions()>
public net.minecraft.world.level.biome.MultiNoiseBiomeSource m_274409_()Lnet/minecraft/world/level/biome/Climate$ParameterList; #parameters()
public-f net.minecraft.world.level.chunk.ChunkGenerator f_62137_ #biomeSource
public-f net.minecraft.world.level.chunk.ChunkGenerator f_223020_ #featuresPerStep
public-f net.minecraft.world.level.chunk.ChunkGenerator f_223021_ #generationSettingsGetter
public-f net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator f_64318_ #settings
public net.minecraft.world.level.levelgen.SurfaceRules$SequenceRuleSource
</#if>

<#if w.hasElementsOfType("livingentity")>
public-f net.minecraft.world.entity.npc.Villager f_35366_ #assignProfessionWhenSpawned
public-f net.minecraft.world.entity.npc.Villager f_35373_ #updateMerchantTimer
public-f net.minecraft.world.entity.npc.Villager f_35374_ #increaseProfessionLevelOnUpdate
public net.minecraft.world.entity.npc.Villager m_35528_()V #increaseMerchantCareer
public-f net.minecraft.world.entity.npc.Villager f_35375_ #lastTradedPlayer
</#if>