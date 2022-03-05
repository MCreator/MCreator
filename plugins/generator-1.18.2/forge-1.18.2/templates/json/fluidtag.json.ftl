{
    "replace": false,
    "values": [
        <#assign elements = []>
        <#list w.getElementsOfType("fluid") as mod>
            <#if mod.getGeneratableElement().type.toString()?lower_case == var_type>
                <#assign elements += ["${modid}:${mod.getRegistryName()?lower_case}"]>
                <#assign elements += ["${modid}:flowing_${mod.getRegistryName()?lower_case}"]>
            </#if>
        </#list>

        <#list elements as e>
            "${e}"<#if e?has_next>,</#if>
        </#list>
    ]
}