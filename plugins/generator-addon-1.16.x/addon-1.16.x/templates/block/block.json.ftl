<#-- @formatter:off -->
{
  "format_version": "1.16.0",
  "minecraft:block": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "is_experimental": false
      <#--"register_to_creative_menu ": true-->
      <#--MC bug: https://bugs.mojang.com/browse/MCPE-63121 -->
    },
    "components": {
      <#if generator.map(data.colorOnMap, "mapcolors") != "DEFAULT">
      "minecraft:map_color": "${generator.map(data.colorOnMap, "mapcolors")}",
      </#if>
      <#if data.hasCustomDrop()>
      "minecraft:loot": "loot_tables/blocks/${modid}_${registryname}.json",
      </#if>
      "minecraft:block_light_emission": ${data.luminance},
      "minecraft:destroy_time": ${data.hardness},
      "minecraft:explosion_resistance":${data.resistance},
      "minecraft:friction": ${data.slipperiness},
      "minecraft:flammable": {
        "flame_odds": ${data.flammability},
        "burn_odds": ${data.fireSpreadSpeed}
      }
    }
  }
}
<#-- @formatter:on -->