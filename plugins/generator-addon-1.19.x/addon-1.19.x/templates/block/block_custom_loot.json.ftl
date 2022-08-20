<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "pools": [
      {
        "rolls": 1,
        "entries": [
          {
            "type": "item",
            "name": "${mappedMCItemToIngameNameNoTags(data.customDrop)}",
            "weight": 1,
            "functions": [
              {
                "function": "set_count",
                "count": {
                  "min": ${data.dropAmount},
                  "max": ${data.dropAmount}
                }
              }
              <#if hasMetadata(data.customDrop)>
              ,{
                "function": "set_data",
                "data": ${getMappedMCItemMetadata(data.customDrop)}
              }
              </#if>
            ]
          }
        ]
      }
    ]
}
<#-- @formatter:on -->