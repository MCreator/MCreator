{
    "replace": false,
    "values": [
        <#list bannerpatterns?filter(e -> !e.requireItem) as pattern>
            "${modid}:${pattern.getModElement().getRegistryName()}"<#sep>,
        </#list>
    ]
}