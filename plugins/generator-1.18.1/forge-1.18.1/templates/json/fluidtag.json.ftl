{
    "replace": false,
    "values": [
        <#list w.getElementsOfType("fluid") as mod>
            <#if mod.getGeneratableElement().type.toString()?lower_case == var_type>
                "${modid}:${mod.getRegistryName()?lower_case}",
                "${modid}:flowing_${mod.getRegistryName()?lower_case}"<#sep>,
            </#if>
        </#list>
    ]
}