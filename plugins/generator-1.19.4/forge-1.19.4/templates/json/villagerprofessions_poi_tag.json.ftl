{
    "replace": false,
    "values": [
        <#list villagerprofessions as villager>
          "${modid}:${villager.getModElement().getRegistryName()}"<#if villager?has_next>,</#if>
        </#list>
    ]
}