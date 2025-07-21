<#include "../mcitems.ftl">
{
  <#if data.poolName??>
  "name": "${modid}:${registryname}_${data.poolName}",
  "fallback": "${data.fallbackPool?has_content?then(data.fallbackPool, "minecraft:empty")}",
  <#else>
  "name": "${modid}:${registryname}",
  "fallback": "minecraft:empty",
  </#if>
  "elements": [
    <#list data.getPoolParts() as part>
    {
      "weight": ${part.weight},
      "element": {
        "element_type": "minecraft:single_pool_element",
        "location": "${modid}:${part.structure}",
        "projection": "${part.projection}",
        "processors": {
          "processors": [
            <#if part.ignoredBlocks?has_content>
            {
              "processor_type": "minecraft:block_ignore",
              "blocks": [
                <#list part.ignoredBlocks as block>
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
    }<#sep>,
    </#list>
  ]
}