<#if w.hasToolsOfType("Fishing rod")>
public net.minecraft.world.entity.projectile.FishingHook m_37136_(Lnet/minecraft/world/entity/player/Player;)Z #shouldStopFishing
</#if>
<#if w.hasElementsOfType("dimension")>
public net.minecraft.world.level.levelgen.carver.WorldCarver f_64983_ #replaceableBlocks
public net.minecraft.client.renderer.DimensionSpecialEffects f_108857_ #EFFECTS
</#if>
<#if w.hasGameRulesOfType("Number")>
public net.minecraft.world.level.GameRules$IntegerValue m_46312_(I)Lnet/minecraft/world/level/GameRules$Type; #create
</#if>
<#if w.hasGameRulesOfType("Logic")>
public net.minecraft.world.level.GameRules$BooleanValue m_46250_(Z)Lnet/minecraft/world/level/GameRules$Type; #create
</#if>