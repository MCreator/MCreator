<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "pools": [
      {
        "rolls": 1,
        "entries": [
          {
            "type": "item",
            "name": "${mappedMCItemToIngameNameNoTags(data.mobDrop)}",
            "weight": 1,
            "functions": [
              <#if hasMetadata(data.mobDrop)>
              {
                "function": "set_data",
                "data": ${getMappedMCItemMetadata(data.mobDrop)}
              }
              </#if>
            ]
          }
        ]
      }
    ]
}
<#-- @formatter:on -->