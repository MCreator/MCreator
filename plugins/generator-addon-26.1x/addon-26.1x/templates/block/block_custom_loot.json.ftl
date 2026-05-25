<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "pools": [
      {
        "rolls": 1,
        "entries": [
          {
            "type": "item",
            "name": "${mappedMCItemToRegistryNameNoTags(data.customDrop)}",
            "weight": 1,
            "functions": [
              {
                "function": "set_count",
                "count": {
                  "min": ${data.dropAmount},
                  "max": ${data.dropAmount}
                }
              }
            ]
          }
        ]
      }
    ]
}
<#-- @formatter:on -->