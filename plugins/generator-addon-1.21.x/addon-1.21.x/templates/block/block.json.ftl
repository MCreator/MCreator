<#-- @formatter:off -->
{
  "format_version": "1.21.40",
  "minecraft:block": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "menu_category": {
          "category": "construction"
      }
    },
    "components": {
      <#if generator.map(data.colorOnMap, "mapcolors") != "DEFAULT">
      "minecraft:map_color": "${generator.map(data.colorOnMap, "mapcolors")}",
      </#if>
      <#if data.hasCustomDrop()>
      "minecraft:loot": "loot_tables/blocks/${modid}_${registryname}.json",
      </#if>
      "minecraft:light_emission": ${data.luminance / 15},
      "minecraft:destructible_by_mining": {
          "seconds_to_destroy": ${data.hardness}
      },
      "minecraft:destructible_by_explosion": {
          "explosion_resistance": ${data.resistance}
      },
      "minecraft:friction": ${data.slipperiness},
      "minecraft:flammable": {
        "flame_odds": ${data.flammability},
        "burn_odds": ${data.fireSpreadSpeed}
      }
    }
  }
}
<#-- @formatter:on -->