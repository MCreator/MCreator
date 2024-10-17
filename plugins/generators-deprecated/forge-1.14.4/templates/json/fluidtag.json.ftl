{
    "replace": false,
    "values": [
        <#assign elements = []>
        <#list w.getElementsOfType("FLUID") as mod>
            <#if mod.getGeneratableElement().type.toString()?lower_case == var_type>
                <#assign elements += ["${modid}:${mod.getRegistryName()?lower_case}"]>
                <#assign elements += ["${modid}:${mod.getRegistryName()?lower_case}_flowing"]>
            </#if>
        </#list>

        <#list elements as e>
            "${e}"<#if e?has_next>,</#if>
        </#list>
    ]
}