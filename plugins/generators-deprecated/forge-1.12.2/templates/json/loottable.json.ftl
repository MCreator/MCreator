<#include "../mcitems.ftl">
{
  "type": "minecraft:${data.type?lower_case?replace(" ", "_")}",
  "pools": [
    <#list data.pools as pool>
        {
          <#if pool.minrolls == pool.maxrolls>
          "rolls": ${pool.minrolls},
          <#else>
          "rolls": {
            "min": ${pool.minrolls},
            "max": ${pool.maxrolls}
          },
          </#if>
          <#if pool.hasbonusrolls>
            <#if pool.minbonusrolls == pool.maxbonusrolls>
            "bonus_rolls": ${pool.minbonusrolls},
            <#else>
            "bonus_rolls": {
              "min": ${pool.minbonusrolls},
              "max": ${pool.maxbonusrolls}
            },
            </#if>
          </#if>
          "entries": [
            <#list pool.entries as entry>
              {
                "type": "minecraft:${entry.type}",
                "name": "${mappedMCItemToIngameNameNoTags(entry.item)}",
                "weight": ${entry.weight}
              }
              <#if entry?has_next>,</#if>
            </#list>
          ]
        }
        <#if pool?has_next>,</#if>
    </#list>
  ]
}
