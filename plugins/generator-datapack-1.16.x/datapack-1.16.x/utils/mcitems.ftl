<#function mappedMCItemToIngameItemName mappedBlock>
    <#if mappedBlock.getUnmappedValue().startsWith("TAG:")>
        <#return "\"tag\": \"" + mappedBlock.getUnmappedValue().replace("TAG:", "") + "\"">
    <#else>
        <#assign mapped = mappedBlock.toString() />
        <#if mapped.startsWith("#")>
            <#return "\"tag\": \"" + mapped.replace("#", "") + "\"">
        <#elseif mapped.contains(":")>
            <#return "\"item\": \"" + mapped + "\"">
        <#else>
            <#return "\"item\": \"minecraft:" + mapped + "\"">
        </#if>
    </#if>
</#function>

<#function mappedMCItemToIngameNameNoTags mappedBlock>
    <#if mappedBlock.getUnmappedValue().startsWith("TAG:")>
        <#return "minecraft:air">
    <#else>
        <#assign mapped = mappedBlock.toString() />
        <#if mapped.startsWith("#")>
            <#return "minecraft:air">
        <#elseif mapped.contains(":")>
            <#return mapped>
        <#else>
            <#return "minecraft:" + mapped>
        </#if>
    </#if>
</#function>

<#function mappedMCItemToBlockStateJSON mappedBlock>
    <#if !mappedBlock.getUnmappedValue().startsWith("TAG:")>
        <#assign mapped = generator.map(mappedBlock.getUnmappedValue(), "blocksitems") />
        <#if !mapped.startsWith("#")>
            <#if !mapped.contains(":")>
                <#assign mapped = "minecraft:" + mapped />
            </#if>
            <#assign propertymap = fp.file("utils/defaultstates.json")?eval/>
            <#if propertymap[mapped]?has_content>
                <#assign retval='{ "Name": "' + mapped + '", "Properties" : {'/>
                <#list propertymap[mapped] as property>
                    <#assign retval = retval + '"' + property.name + '": "' + property.value + '"'/>
                    <#if property?has_next>
                        <#assign  retval = retval + ","/>
                    </#if>
                </#list>
                <#return retval + "} }">
            <#else>
                <#return '{ "Name": "' + mapped + '" }'>
            </#if>
        </#if>
    </#if>
    <#return '{ "Name": "minecraft:air" }'>
</#function>