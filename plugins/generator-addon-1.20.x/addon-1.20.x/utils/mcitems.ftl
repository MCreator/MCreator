<#function hasMetadata mappedBlock>
    <#return mappedBlock.toString().contains("#")>
</#function>

<#function getMappedMCItemMetadata mappedBlock>
    <#if !mappedBlock.toString().contains("#")>
        <#return "-1">
    <#else>
        <#return mappedBlock.toString().split("#")[1]>
    </#if>
</#function>

<#function transformExtension mappedBlock>
    <#assign extension = mappedBlock?keep_after_last(".")?replace("body", "chestplate")?replace("legs", "leggings")>
    <#return (extension?has_content)?then("_" + extension, "")>
</#function>

<#function mappedMCItemToItemObjectJSON mappedBlock skipDefaultMetadata=false>
    <#if mappedBlock.toString().startsWith("CUSTOM:")>
        <#assign customelement = generator.getRegistryNameFromFullName(mappedBlock.getUnmappedValue())!""/>
        <#if customelement?has_content>
            <#return "\"item\": \"" + "${modid}:" + customelement
            + transformExtension(mappedBlock.getUnmappedValue())
            + "\"">
        <#else>
            <#return "\"item\": \"minecraft:air\"">
        </#if>
    <#elseif mappedBlock.toString().startsWith("TAG:")>
        <#return "\"type\": \"forge:ore_dict\", \"ore\": \"" + mappedBlock.toString().replace("TAG:", "").replace(":", "").replace("/", "") + "\"">
    <#else>
        <#assign mapped = mappedBlock.toString()>
        <#if mapped.toString().contains("#")>
            <#return "\"item\": \"minecraft:" + mapped.toString().split("#")[0] + "\", \"data\": " + mapped.toString().split("#")[1]>
        <#elseif mapped.contains(":")>
            <#return "\"item\": \"" + mapped + "\"">
        <#else>
            <#return "\"item\": \"minecraft:" + mapped.toString().split("#")[0] + "\"">
        </#if>
    </#if>
</#function>


<#function mappedMCItemToRegistryNameNoTags mappedBlock>
    <#if mappedBlock.getUnmappedValue().startsWith("CUSTOM:")>
        <#assign customelement = generator.getRegistryNameFromFullName(mappedBlock.getUnmappedValue())!""/>
        <#if customelement?has_content>
            <#return "${modid}:" + customelement + transformExtension(mappedBlock.getUnmappedValue())>
        <#else>
            <#return "minecraft:air">
        </#if>
    <#elseif mappedBlock.getUnmappedValue().startsWith("TAG:")>
        <#return "minecraft:air">
    <#else>
        <#assign mapped = mappedBlock.toString() />
        <#if mapped.startsWith("#")>
            <#return "minecraft:air">
        <#elseif mapped.contains(":")>
            <#return mapped>
        <#else>
            <#return "minecraft:" + mapped.toString().split("#")[0]>
        </#if>
    </#if>
</#function>