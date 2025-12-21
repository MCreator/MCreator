<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "pools": [
      {
        "rolls": 1,
        "entries": [
          {
            "type": "item",
            "name": "${mappedMCItemToRegistryNameNoTags(data.loot)}",
            "weight": 1,
            "functions": [
              {
                "function": "set_count",
                "count": {
                  "min": ${data.dropAmount},
                  "max": ${data.dropAmount}
                }
              }
              <#if hasMetadata(data.loot)>
              ,{
                "function": "set_data",
                "data": ${getMappedMCItemMetadata(data.loot)}
              }
              </#if>
            ]
          }
        ]
      }
    ]
}
<#-- @formatter:on -->