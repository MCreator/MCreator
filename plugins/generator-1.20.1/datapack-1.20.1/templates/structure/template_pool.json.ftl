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
        "projection": "${data.projection}",
        "processors": {
          "processors": [
            <#if data.ignoredBlocks?has_content>
            {
              "processor_type": "minecraft:block_ignore",
              "blocks": [
                <#list data.ignoredBlocks as block>
                {
                  "Name": "${mappedMCItemToRegistryName(block)}"
                }<#sep>,
                </#list>
              ]
            }
            </#if>
          ]
        }
      }
    }
  ]
}