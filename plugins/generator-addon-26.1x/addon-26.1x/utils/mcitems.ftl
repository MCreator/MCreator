<#function mappedBlockToBlockPermutation mappedBlock>
    <#if mappedBlock?trim?starts_with("/*@BlockState*/")>
        <#return mappedBlock?replace("/*@BlockState*/","")>
    <#elseif mappedBlock?contains("/*@?*/")>
        <#assign outputs = mappedBlock?keep_after("/*@?*/")?keep_before_last(")")>
        <#return mappedBlock?keep_before("/*@?*/") + "?" + mappedBlockToBlockPermutation(outputs?keep_before("/*@:*/"))
        + ":" + mappedBlockToBlockPermutation(outputs?keep_after("/*@:*/")) + ")">
    <#elseif mappedBlock?starts_with("CUSTOM:")>
        <#return "BlockPermutation.resolve(\"" + modid + ":" + generator.getRegistryNameFromFullName(mappedBlock) + "\")">
    <#else>
        <#return "BlockPermutation.resolve(\"minecraft:" + mappedBlock + "\")">
    </#if>
</#function>

<#function mappedMCItemToItemStackCode mappedBlock amount=1>
    <#if mappedBlock?trim?starts_with("/*@ItemStack*/")>
        <#return mappedBlock?replace("/*@ItemStack*/", "")>
    <#elseif mappedBlock?contains("/*@?*/")>
        <#assign outputs = mappedBlock?keep_after("/*@?*/")?keep_before_last(")")>
        <#return mappedBlock?keep_before("/*@?*/") + "?" + mappedMCItemToItemStackCode(outputs?keep_before("/*@:*/"), amount)
        + ":" + mappedMCItemToItemStackCode(outputs?keep_after("/*@:*/"), amount) + ")">
    <#elseif mappedBlock?starts_with("CUSTOM:")>
        <#return toItemStack(modid + ":" + generator.getRegistryNameFromFullName(mappedBlock), amount)>
    <#else>
        <#return toItemStack("minecraft:" + mappedBlock, amount)>
    </#if>
</#function>

<#function toItemStack item amount>
    <#if amount == 1>
        <#return "new ItemStack(\"" + item + "\")">
    <#else>
        <#return "new ItemStack(\"" + item + "\", " + amount + ")">
    </#if>
</#function>

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

<#function mappedMCItemToItemObjectJSON mappedBlock acceptTags=false>
    <#if acceptTags>
        <#local tag = mappedMCItemToBedrockTag(mappedBlock)>
        <#if tag?has_content>
            <#return "\"tag\": \"" + tag + "\"">
        </#if>
    </#if>
    <#return "\"item\": \"" + mappedMCItemToRegistryNameNoTags(mappedBlock) + "\"">
</#function>

<#-- Returns Bedrock item tag name for the given element, or empty string if the element is not a tag -->
<#function mappedMCItemToBedrockTag mappedBlock>
    <#if mappedBlock.getUnmappedValue().startsWith("TAG:")>
        <#return mappedBlock.asTagEntry()>
    <#elseif !mappedBlock.getUnmappedValue().startsWith("CUSTOM:") && mappedBlock.toString().startsWith("#")>
        <#return mappedBlock.toString()?substring(1)>
    </#if>
    <#return "">
</#function>

<#function transformExtension mappedBlock>
    <#assign extension = mappedBlock?keep_after_last(".")>
    <#return (extension?has_content)?then("_" + extension, "")>
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
            <#return "minecraft:" + mapped>
        </#if>
    </#if>
</#function>

<#function mappedMCItemToRegistryNameOrTag mappedBlock>
    <#local tag = mappedMCItemToBedrockTag(mappedBlock)>
    <#if tag?has_content>
        <#return "{\"tags\": \"q.any_tag(\'" + tag + "\')\" }">
    <#else>
        <#return "\"" + mappedMCItemToRegistryNameNoTags(mappedBlock) + "\"">
    </#if>
</#function>

<#function recipeUnlockJSON unlockingItems>
    <#local entries = []>
    <#list unlockingItems as item>
        <#local tag = mappedMCItemToBedrockTag(item)>
        <#if tag?has_content>
            <#local entries += ["{ \"tag\": \"" + tag + "\" }"]>
        <#else>
            <#local name = mappedMCItemToRegistryNameNoTags(item)>
            <#if name != "minecraft:air">
                <#local entries += ["{ \"item\": \"" + name + "\" }"]>
            </#if>
        </#if>
    </#list>
    <#if entries?has_content>
        <#return "[ " + entries?join(", ") + " ]">
    <#else>
        <#return "{ \"context\": \"AlwaysUnlocked\" }">
    </#if>
</#function>