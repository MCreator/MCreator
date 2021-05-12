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

<#function mappedMCItemToIngameItemName mappedBlock skipDefaultMetadata=false>
    <#if mappedBlock.toString().startsWith("CUSTOM:")>
        <#assign customelement = generator.getRegistryNameForModElement(mappedBlock.toString().replace("CUSTOM:", "")
        .replace(".helmet", "").replace(".body", "").replace(".legs", "").replace(".boots", "").replace(".bucket", ""))!""/>
        <#if customelement?has_content>
            <#return "\"item\": \"" + "${modid}:" + customelement
            + (mappedBlock.getUnmappedValue().contains(".helmet"))?then("_helmet", "")
            + (mappedBlock.getUnmappedValue().contains(".body"))?then("_chestplate", "")
            + (mappedBlock.getUnmappedValue().contains(".legs"))?then("_leggings", "")
            + (mappedBlock.getUnmappedValue().contains(".boots"))?then("_boots", "")
            + (mappedBlock.getUnmappedValue().contains(".bucket"))?then("_bucket", "")
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


<#function mappedMCItemToIngameNameNoTags mappedBlock>
    <#if mappedBlock.getUnmappedValue().startsWith("CUSTOM:")>
        <#assign customelement = generator.getRegistryNameForModElement(mappedBlock.getUnmappedValue().replace("CUSTOM:", "")
        .replace(".helmet", "").replace(".body", "").replace(".legs", "").replace(".boots", "").replace(".bucket", ""))!""/>
        <#if customelement?has_content>
            <#return "${modid}:" + customelement
            + (mappedBlock.getUnmappedValue().contains(".helmet"))?then("_helmet", "")
            + (mappedBlock.getUnmappedValue().contains(".body"))?then("_chestplate", "")
            + (mappedBlock.getUnmappedValue().contains(".legs"))?then("_leggings", "")
            + (mappedBlock.getUnmappedValue().contains(".boots"))?then("_boots", "")
            + (mappedBlock.getUnmappedValue().contains(".bucket"))?then("_bucket", "")>
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