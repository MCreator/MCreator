<#-- @formatter:off -->
<#include "../mcitems.ftl">

<#macro vanilla biome>
  <#if biome?contains("frozen_ocean") || biome?contains("frozen_river")>
    <@default biome w.itemBlock("Blocks.ICE") w.itemBlock("Blocks.WATER") w.itemBlock("Blocks.GRAVEL")/>
  <#elseif biome?contains("ocean") || biome?contains("ocean")>
    <@default biome w.itemBlock("Blocks.WATER") w.itemBlock("Blocks.WATER") w.itemBlock("Blocks.GRAVEL")/>
  <#elseif biome?contains("badlands")>
    <@default biome w.itemBlock("Blocks.SAND#1") w.itemBlock("Blocks.STAINED_HARDENED_CLAY") w.itemBlock("Blocks.GRAVEL")/>
  <#elseif biome == "basalt_deltas">
    <@default biome w.itemBlock("Blocks.BLACKSTONE") w.itemBlock("Blocks.BASALT") w.itemBlock("Blocks.MAGMA")/>
  <#elseif biome == "crimson_forest">
    <@default biome w.itemBlock("Blocks.CRIMSON_NYLIUM") w.itemBlock("Blocks.NETHERRACK") w.itemBlock("Blocks.NETHER_WART_BLOCK")/>
  <#elseif biome?contains("desert") || biome?contains("beaches")>
    <@default biome w.itemBlock("Blocks.SAND") w.itemBlock("Blocks.SAND") w.itemBlock("Blocks.GRAVEL")/>
  <#elseif biome?contains("end")>
    <@default biome w.itemBlock("Blocks.END_STONE") w.itemBlock("Blocks.END_STONE") w.itemBlock("Blocks.END_STONE")/>
  <#elseif biome?contains("mushroom_field")>
    <@default biome w.itemBlock("Blocks.MYCELIUM") w.itemBlock("Blocks.DIRT") w.itemBlock("Blocks.GRAVEL")/>
  <#elseif biome == "nether">
    <@default biome w.itemBlock("Blocks.NETHERRACK") w.itemBlock("Blocks.NETHERRACK") w.itemBlock("Blocks.NETHERRACK")/>
  <#elseif biome == "soul_sand_valley">
    <@default biome w.itemBlock("Blocks.SOUL_SAND") w.itemBlock("Blocks.SOUL_SAND") w.itemBlock("Blocks.SOUL_SAND")/>
  <#elseif biome == "stone_beach">
    <@default biome w.itemBlock("Blocks.STONE") w.itemBlock("Blocks.STONE") w.itemBlock("Blocks.GRAVEL")/>
  <#elseif biome == "warped_forest">
    <@default biome w.itemBlock("Blocks.WARPED_NYLIUM") w.itemBlock("Blocks.NETHERRACK") w.itemBlock("Blocks.WARPED_WART_BLOCK")/>
  <#elseif biome == "warped_forest">
    <@default biome w.itemBlock("Blocks.WARPED_NYLIUM") w.itemBlock("Blocks.NETHERRACK") w.itemBlock("Blocks.WARPED_WART_BLOCK")/>
  <#else>
    <@default biome w.itemBlock("Blocks.GRASS") w.itemBlock("Blocks.DIRT") w.itemBlock("Blocks.GRAVEL")/>
  </#if>
</#macro>

<#macro default biome groundBlockWithProperties undergroundBlockWithProperties underwaterBlockWithProperties=undergroundBlockWithProperties>
{
  "type": "minecraft:condition",
  "if_true": {
    "type": "minecraft:biome",
    "biome_is": [
      "${biome}"
    ]
  },
  "then_run": {
    "type": "minecraft:condition",
    "if_true": {
      "type": "minecraft:above_preliminary_surface"
    },
    "then_run": {
      "type": "minecraft:sequence",
      "sequence": [
        {
          "type": "minecraft:condition",
          "if_true": {
            "type": "minecraft:stone_depth",
            "surface_type": "floor",
            "add_surface_depth": false,
            "add_surface_secondary_depth": false,
            "offset": 0
          },
          "then_run": {
            "type": "minecraft:sequence",
            "sequence": [
              {
                "type": "minecraft:condition",
                "if_true": {
                  "type": "minecraft:water",
                  "offset": -1,
                  "surface_depth_multiplier": 0,
                  "add_stone_depth": false
                },
                "then_run": {
                  "type": "minecraft:block",
                  "result_state": ${mappedMCItemToBlockStateJSON(groundBlockWithProperties)}
                }
              },
              {
                "type": "minecraft:block",
                "result_state": ${mappedMCItemToBlockStateJSON(underwaterBlockWithProperties)}
              }
            ]
          }
        },
        {
          "type": "minecraft:condition",
          "if_true": {
            "type": "minecraft:stone_depth",
            "surface_type": "floor",
            "add_surface_depth": true,
            "add_surface_secondary_depth": false,
            "offset": 0
          },
          "then_run": {
            "type": "minecraft:block",
            "result_state": ${mappedMCItemToBlockStateJSON(undergroundBlockWithProperties)}
          }
        }
      ]
    }
  }
}
</#macro>
<#-- @formatter:on -->