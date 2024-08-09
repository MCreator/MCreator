<#-- @formatter:off -->
<#include "../mcitems.ftl">

<#macro vanilla biome>
  <#if biome?contains("badlands")>
    <@vanillaSB biome w.itemBlock("Blocks.SAND#1") w.itemBlock("Blocks.HARDENED_CLAY") w.itemBlock("Blocks.GRAVEL") data.worldGenType != "Normal world gen"/>
  <#elseif biome == "basalt_deltas">
    <@vanillaSB biome w.itemBlock("Blocks.BLACKSTONE") w.itemBlock("Blocks.BASALT") w.itemBlock("Blocks.MAGMA") data.worldGenType != "Normal world gen"/>
  <#elseif biome == "crimson_forest">
    <@vanillaSB biome w.itemBlock("Blocks.CRIMSON_NYLIUM") w.itemBlock("Blocks.NETHERRACK") w.itemBlock("Blocks.NETHER_WART_BLOCK") data.worldGenType != "Normal world gen"/>
  <#elseif biome?contains("desert") || biome?contains("beaches")>
    <@vanillaSB biome w.itemBlock("Blocks.SAND#0") w.itemBlock("Blocks.SAND#0") w.itemBlock("Blocks.GRAVEL") data.worldGenType != "Normal world gen"/>
  <#elseif biome?contains("end")>
    <@vanillaSB biome w.itemBlock("Blocks.END_STONE") w.itemBlock("Blocks.END_STONE") w.itemBlock("Blocks.END_STONE") data.worldGenType != "Normal world gen"/>
  <#elseif biome == "deep_dark">
    <@vanillaSB biome w.itemBlock("Blocks.SCULK") w.itemBlock("Blocks.DEEPSLATE") w.itemBlock("Blocks.DEEPSLATE") data.worldGenType != "Normal world gen"/>
  <#elseif biome?contains("mushroom_field")>
    <@vanillaSB biome w.itemBlock("Blocks.MYCELIUM") w.itemBlock("Blocks.DIRT#0") w.itemBlock("Blocks.GRAVEL") data.worldGenType != "Normal world gen"/>
  <#elseif biome == "nether">
    <@vanillaSB biome w.itemBlock("Blocks.NETHERRACK") w.itemBlock("Blocks.NETHERRACK") w.itemBlock("Blocks.NETHERRACK") data.worldGenType != "Normal world gen"/>
  <#elseif biome == "soul_sand_valley">
    <@vanillaSB biome w.itemBlock("Blocks.SOUL_SAND") w.itemBlock("Blocks.SOUL_SAND") w.itemBlock("Blocks.SOUL_SAND") data.worldGenType != "Normal world gen"/>
  <#elseif biome == "stone_beach">
    <@vanillaSB biome w.itemBlock("Blocks.STONE#0") w.itemBlock("Blocks.STONE#0") w.itemBlock("Blocks.GRAVEL") data.worldGenType != "Normal world gen"/>
  <#elseif biome == "warped_forest">
    <@vanillaSB biome w.itemBlock("Blocks.WARPED_NYLIUM") w.itemBlock("Blocks.NETHERRACK") w.itemBlock("Blocks.WARPED_WART_BLOCK") data.worldGenType != "Normal world gen"/>
  <#else>
    <@vanillaSB biome w.itemBlock("Blocks.GRASS") w.itemBlock("Blocks.DIRT#0") w.itemBlock("Blocks.GRAVEL") data.worldGenType != "Normal world gen"/>
  </#if>
</#macro>

<#macro vanillaSB biome groundBlockWithProperties undergroundBlockWithProperties underwaterBlockWithProperties=undergroundBlockWithProperties coverAny=false>
  <#if coverAny>
    <@defaultAny biome groundBlockWithProperties undergroundBlockWithProperties underwaterBlockWithProperties />
  <#else>
    <@default biome groundBlockWithProperties undergroundBlockWithProperties underwaterBlockWithProperties />
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
            "secondary_depth_range": 0,
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
            "secondary_depth_range": 0,
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

<#macro defaultAny biome groundBlockWithProperties undergroundBlockWithProperties underwaterBlockWithProperties=undergroundBlockWithProperties>
{
  "type": "minecraft:condition",
  "if_true": {
    "type": "minecraft:biome",
    "biome_is": [
      "${biome}"
    ]
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
          "secondary_depth_range": 0,
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
          "secondary_depth_range": 0,
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
</#macro>
<#-- @formatter:on -->