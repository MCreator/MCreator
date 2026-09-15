<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "format_version": "1.13.0",
    "minecraft:ore_feature": {
      "description": {
        "identifier": "${modid}:${modid}_${registryname}_ore_feature"
      },
      "count": ${data.oreCount},
      "replace_rules": [
        {
          "places_block": "${modid}:${registryname}",
          "may_replace": [
            <#list data.blocksToReplace as block>
              <#-- Blocks.STONE was the default value of this field before it was removed from the mappings -->
              <#if block.getUnmappedValue() == "Blocks.STONE">
              "minecraft:stone"<#if block?has_next>,</#if>
              <#else>
              "${mappedMCItemToRegistryNameNoTags(block)}"<#if block?has_next>,</#if>
              </#if>
            </#list>
          ]
        }
      ]
    }
}
<#-- @formatter:on -->