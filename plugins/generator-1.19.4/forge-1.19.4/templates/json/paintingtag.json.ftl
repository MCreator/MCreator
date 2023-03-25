{
    "replace": false,
    "values": [
        <#list paintings as painting>
            "${modid}:${painting.getModElement().getRegistryName()}"<#sep>,
        </#list>
    ]
}