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
      "minecraft:light_emission": ${data.lightEmission},
      "minecraft:destructible_by_mining": {
          "seconds_to_destroy": ${data.hardness}
      },
      "minecraft:destructible_by_explosion": {
          "explosion_resistance": ${data.resistance}
      },
      "minecraft:friction": ${data.friction},
      "minecraft:flammable": {
        "catch_chance_modifier": ${data.flammability},
        "destroy_chance_modifier": ${data.flammableDestroyChance}
      }
    }
  }
}
<#-- @formatter:on -->