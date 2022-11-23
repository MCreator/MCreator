<#include "../mcitems.ftl">
{
    "replace": false,
    "values": [
          <#list villagerprofessions as villager>
            "${mappedMCItemToIngameNameNoTags(villager.pointOfInterest)}"<#if villager?has_next>,</#if>
          </#list>
    ]
}