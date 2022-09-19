<#if w.hasGameRulesOfType("Number")>
public net.minecraft.world.level.GameRules$IntegerValue m_46312_(I)Lnet/minecraft/world/level/GameRules$Type; #create
</#if>

<#if w.hasGameRulesOfType("Logic")>
public net.minecraft.world.level.GameRules$BooleanValue m_46250_(Z)Lnet/minecraft/world/level/GameRules$Type; #create
</#if>

<#if w.hasElementsOfType("villagerprofession")>
    public net.minecraft.world.entity.ai.village.poi.PoiType m_27367_(Lnet/minecraft/world/entity/ai/village/poi/PoiType;)Lnet/minecraft/world/entity/ai/village/poi/PoiType; # registerBlockStates
</#if>