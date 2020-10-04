<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "format_version": "1.13.0",
    "minecraft:ore_feature": {
      "description": {
        "identifier": "${modid}:${modid}_${registryname}_ore_feature"
      },
      "count": ${data.frequencyOnChunk},
      "places_block": "${modid}:${registryname}",
      "may_replace": [
        <#list data.blocksToReplace as block>
        "${mappedMCItemToIngameNameNoTags(block)}"<#if block?has_next>,</#if>
        </#list>
      ]
    }
}
<#-- @formatter:on -->