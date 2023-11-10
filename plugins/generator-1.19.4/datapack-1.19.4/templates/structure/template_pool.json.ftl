<#include "../mcitems.ftl">
{
  "name": "${modid}:${registryname}",
  "fallback": "minecraft:empty",
  "elements": [
    {
      "weight": 1,
      "element": {
        "element_type": "minecraft:single_pool_element",
        "location": "${modid}:${data.structure}",
        "projection": "${data.projection}"
        <#if data.ignoredBlocks?has_content>,
        "processors": {
          "processors": [
            {
              "processor_type": "minecraft:block_ignore",
              "blocks": [
                <#list data.ignoredBlocks as block>
                ${mappedMCItemToBlockStateJSON(block)}<#sep>,
                </#list>
              ]
            }
          ]
        }
        </#if>
      }
    }
  ]
}