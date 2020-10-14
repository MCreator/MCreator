<#-- @formatter:off -->
{
  "format_version": "1.14",
  "minecraft:block": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "register_to_creative_menu": true,
      "is_experimental": false
    },

    "components": {
      <#if generator.map(data.colorOnMap, "mapcolors") != "DEFAULT">
      "minecraft:map_color": {
        "color": "${generator.map(data.colorOnMap, "mapcolors")}"
      },
      </#if>
      <#if data.hasCustomDrop()>
      "minecraft:loot": {
        "table": "loot_tables/blocks/${modid}_${registryname}.json"
      },
      </#if>
      "minecraft:destroy_time": {
        "value": ${data.hardness}
      },
      "minecraft:explosion_resistance": {
        "value": ${data.resistance}
      },
      "minecraft:friction": {
        "value": ${data.slipperiness}
      },
      "minecraft:block_light_emission": {
        "emission": ${data.luminance}
      },
      "minecraft:flammable": {
        "flame_odds": ${data.flammability},
        "burn_odds": ${data.fireSpreadSpeed}
      }
    }
  }
}
<#-- @formatter:on -->