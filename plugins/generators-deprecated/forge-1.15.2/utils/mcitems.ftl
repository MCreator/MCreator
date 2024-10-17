<#function mappedBlockToBlockStateCode mappedBlock>
    <#if mappedBlock?starts_with("/*@BlockState*/")>
        <#return mappedBlock?replace("/*@BlockState*/","")>
    <#else>
        <#return mappedBlockToBlock(mappedBlock) + ".getDefaultState()">
    </#if>
</#function>

<#function mappedBlockToBlock mappedBlock>
    <#if mappedBlock?starts_with("/*@BlockState*/")>
        <#return mappedBlock?replace("/*@BlockState*/","") + ".getBlock()">
    <#elseif mappedBlock?starts_with("CUSTOM:")>
        <#if !mappedBlock?contains(".")>
            <#return mappedElementToClassName(mappedBlock) + ".block">
        <#else>
            <#return mappedElementToClassName(mappedBlock) + "." + generator.getElementExtension(mappedBlock)>
        </#if>
    <#else>
        <#return mappedBlock>
    </#if>
</#function>

<#function mappedMCItemToItemStackCode mappedBlock amount>
    <#if mappedBlock?starts_with("/*@ItemStack*/")>
        <#return mappedBlock?replace("/*@ItemStack*/", "")>
    <#elseif mappedBlock?starts_with("CUSTOM:")>
        <#if !mappedBlock?contains(".")>
            <#return "new ItemStack("+ mappedElementToClassName(mappedBlock) + ".block"
            + (amount == 1)?then(")",", (int)(" + amount + "))")>
        <#else>
            <#return "new ItemStack("+ mappedElementToClassName(mappedBlock) + "."
            + generator.getElementExtension(mappedBlock) + (amount == 1)?then(")",", (int)(" + amount + "))")>
        </#if>
    <#else>
        <#return "new ItemStack(" + mappedBlock + (amount == 1)?then(")",", (int)(" + amount + "))")>
    </#if>
</#function>

<#function mappedMCItemToItem mappedBlock>
    <#if mappedBlock?starts_with("/*@ItemStack*/")>
        <#return mappedBlock?replace("/*@ItemStack*/", "") + ".getItem()">
    <#elseif mappedBlock?starts_with("CUSTOM:")>
        <#if !mappedBlock?contains(".")>
            <#return mappedElementToClassName(mappedBlock) + ".block"
            + generator.isRecipeTypeBlockOrBucket(mappedBlock)?then(".asItem()","")>
        <#else>
            <#return mappedElementToClassName(mappedBlock) + "." + generator.getElementExtension(mappedBlock)>
        </#if>
    <#else>
        <#return mappedBlock + mappedBlock?contains("Blocks.")?then(".asItem()","")>
    </#if>
</#function>

<#function mappedElementToClassName mappedElement>
    <#return generator.getElementPlainName(mappedElement) + generator.isRecipeTypeBlockOrBucket(mappedElement)?then("Block", "Item")>
</#function>

<#function mappedMCItemToIngameItemName mappedBlock>
    <#if mappedBlock.getUnmappedValue().startsWith("CUSTOM:")>
        <#assign customelement = generator.getRegistryNameForModElement(mappedBlock.getUnmappedValue().replace("CUSTOM:", "")
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
    <#elseif mappedBlock.getUnmappedValue().startsWith("TAG:")>
        <#return "\"tag\": \"" + mappedBlock.getUnmappedValue().replace("TAG:", "")?lower_case + "\"">
    <#else>
        <#assign mapped = generator.map(mappedBlock.getUnmappedValue(), "blocksitems", 1) />
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
        <#assign mapped = generator.map(mappedBlock.getUnmappedValue(), "blocksitems", 1) />
        <#if mapped.startsWith("#")>
            <#return "minecraft:air">
        <#elseif mapped.contains(":")>
            <#return mapped>
        <#else>
            <#return "minecraft:" + mapped>
        </#if>
    </#if>
</#function>