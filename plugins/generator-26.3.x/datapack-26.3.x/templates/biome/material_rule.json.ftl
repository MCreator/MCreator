<#-- @formatter:off -->
<#import "../dimension/surface_builder.json.ftl" as sb>
<#if var_any??>
<#-- This rule is injected in front of the vanilla dimension rules, so the bedrock layers need to be excluded explicitly -->
{
  "type": "minecraft:condition",
  "if_true": {
    "type": "minecraft:y_above",
    "anchor": {
      "above_bottom": 5
    },
    "surface_depth_multiplier": 0,
    "add_stone_depth": false
  },
  "then_run": {
    "type": "minecraft:condition",
    "if_true": {
      "type": "minecraft:not",
      "invert": {
        "type": "minecraft:y_above",
        "anchor": {
          "below_top": 5
        },
        "surface_depth_multiplier": 0,
        "add_stone_depth": false
      }
    },
    "then_run": <@sb.defaultAny "${modid}:${registryname}" data.groundBlock data.undergroundBlock data.getUnderwaterBlock()/>
  }
}
<#else>
<@sb.default "${modid}:${registryname}" data.groundBlock data.undergroundBlock data.getUnderwaterBlock()/>
</#if>
<#-- @formatter:on -->
