<#function mappedBlockToBlockStateProvider mappedBlock>
    <#if mappedBlock?trim?starts_with("/*@BlockStateProvider*/")>
        <#return mappedBlock?replace("/*@BlockStateProvider*/", "")>
    <#else>
        <#return '{"id": ' + mappedBlock + '}'>
    </#if>
</#function>

<#function handleExtension mappedBlock customelement>
    <#local extension = mappedBlock?keep_after_last(".")?replace("body", "chestplate")?replace("legs", "leggings")>
    <#if extension == "wall"> <#-- Special handling for wall variant of signs -->
        <#if customelement?ends_with("hanging_sign")> <#-- If element name ends with "hanging_sign", we can use proper naming for wall sign -->
            <#return customelement?remove_ending("hanging_sign") + "wall_hanging_sign">
        <#elseif customelement?ends_with("sign")> <#-- If element name ends with "sign", we can use proper naming for wall sign -->
            <#return customelement?remove_ending("sign") + "wall_sign">
        <#else>
            <#return "wall_" + customelement>
        </#if>
    <#else>
    	<#return (extension?has_content)?then(customelement + "_" + extension, customelement)>
    </#if>
</#function>

<#function mappedMCItemToItemObjectJSON mappedBlock itemKey="item">
    <#if mappedBlock.getUnmappedValue().startsWith("CUSTOM:")>
        <#local customelement = generator.getRegistryNameFromFullName(mappedBlock.getUnmappedValue())!""/>
        <#if customelement?has_content>
            <#return "\"" + itemKey + "\": \"" + "${modid}:" + handleExtension(mappedBlock, customelement) + "\"">
        <#else>
            <#return "\"" + itemKey + "\": \"minecraft:air\"">
        </#if>
    <#elseif mappedBlock.getUnmappedValue().startsWith("TAG:")>
        <#return "\"tag\": \"" + mappedBlock.asTagEntry()?lower_case + "\"">
    <#else>
        <#local mapped = mappedBlock.getMappedValue(1) />
        <#if mapped.startsWith("#")>
            <#return "\"tag\": \"" + mapped.replace("#", "") + "\"">
        <#elseif mapped.contains(":")>
            <#return "\"" + itemKey + "\": \"" + mapped + "\"">
        <#else>
            <#return "\"" + itemKey + "\": \"minecraft:" + mapped + "\"">
        </#if>
    </#if>
</#function>

<#function mappedMCItemToRegistryName mappedBlock acceptTags=false>
    <#if mappedBlock.getUnmappedValue().startsWith("CUSTOM:")>
        <#local customelement = generator.getRegistryNameFromFullName(mappedBlock.getUnmappedValue())!""/>
        <#if customelement?has_content>
            <#return "${modid}:" + handleExtension(mappedBlock, customelement)>
        <#else>
            <#return "minecraft:air">
        </#if>
    <#elseif mappedBlock.getUnmappedValue().startsWith("TAG:")>
        <#if acceptTags>
            <#return "#" + mappedBlock.asTagEntry()?lower_case>
        <#else>
            <#return "minecraft:air">
        </#if>
    <#else>
        <#local mapped = mappedBlock.getMappedValue(1) />
        <#if mapped.startsWith("#")>
            <#if acceptTags>
                <#return mapped>
            <#else>
                <#return "minecraft:air">
            </#if>
        <#elseif mapped.contains(":")>
            <#return mapped>
        <#else>
            <#return "minecraft:" + mapped>
        </#if>
    </#if>
</#function>