{
  "ultrawarm": ${data.doesWaterVaporize},
  "natural": ${data.imitateOverworldBehaviour},
  "piglin_safe": ${!data.imitateOverworldBehaviour},
  "respawn_anchor_works": ${data.canRespawnHere},
  "bed_works": ${data.sleepResult == "ALLOW"},
  "has_raids": ${data.imitateOverworldBehaviour},
  "has_skylight": ${data.hasSkyLight},
  "has_ceiling": ${data.worldGenType == "Nether like gen"},
  "coordinate_scale": 1,
  "ambient_light": <#if data.isDark>0<#else>0.5</#if>,
  "logical_height": ${data.logicalHeight},
  "infiniburn": "minecraft:infiniburn_overworld",
  "min_y": 0, <#-- Minecraft does not support custom heights in this version -->
  "height": 256, <#-- Minecraft does not support custom heights in this version -->
  "effects": "<#if data.hasFog>minecraft:the_nether<#else>minecraft:overworld</#if>"
}