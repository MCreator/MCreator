<#assign entries=[]/>
<#list w.getElementsOfType("biome") as mod>
    <#if mod.getGeneratableElement().hasStructure(var_type)>
        <#assign entries+=[mod.getRegistryName()?lower_case]/>
    </#if>
</#list>
{
    "replace": false,
    "values": [
        <#list entries as entry>
            "${modid}:${entry}"<#sep>,
        </#list>
    ]
}