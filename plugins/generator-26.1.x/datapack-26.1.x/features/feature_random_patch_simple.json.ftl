<#include "mcitems_json.ftl">
<#include "mcelements.ftl">
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
        ${patchFeaturePlacement(field$tries, field$xzSpread, field$ySpread)}
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