<#include "mcitems_json.ftl">
{
  "features": [
    {
      "feature": {
        "type": "minecraft:simple_block",
        "config": {
          "to_place": ${mappedBlockToBlockStateProvider(input$block)}
          <#if (field$schedule_tick!"FALSE") == "TRUE">, "schedule_tick": true</#if>
        }
      },
      "placement": [
        {
          "type": "minecraft:count",
          "count": ${field$tries}
        },
        {
          "type": "minecraft:random_offset",
          "xz_spread": {
            "type": "minecraft:trapezoid",
            "plateau": 0,
            "min": -${field$xzSpread},
            "max": ${field$xzSpread}
          },
          "y_spread": {
            "type": "minecraft:trapezoid",
            "plateau": 0,
            "min": -${field$ySpread},
            "max": ${field$ySpread}
          }
        }
        <#if input_id$condition != "block_predicate_true">,
        {
          "type": "minecraft:block_predicate_filter",
          "predicate": ${input$condition}
        }
        </#if>
      ]
    }
  ]
}