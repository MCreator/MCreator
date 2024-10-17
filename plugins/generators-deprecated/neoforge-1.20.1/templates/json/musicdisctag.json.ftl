{
    "replace": false,
    "values": [
        <#list musicdiscs as musicdisc>
            "${modid}:${musicdisc.getModElement().getRegistryName()}"<#sep>,
        </#list>
    ]
}