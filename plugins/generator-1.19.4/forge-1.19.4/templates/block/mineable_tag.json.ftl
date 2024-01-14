{
    "replace": false,
    "values": [
        <#list blocks?filter(block -> block.destroyTool == var_type) as block>
            "${generator.getResourceLocationForModElement(block.getModElement())}"<#sep>,
        </#list>
    ]
}