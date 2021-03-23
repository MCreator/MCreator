<#function mappedBlockToBlockStateCode mappedBlock>
    <#if mappedBlock.toString().contains("(world.") || mappedBlock.toString().contains("/*@BlockState*/")>
        <#return mappedBlock>
    <#elseif mappedBlock.toString().startsWith("CUSTOM:")>
        <#if !mappedBlock.toString().contains(".")>
            <#return (generator.getElementPlainName(mappedBlock))
            + (generator.isRecipeTypeBlockOrBucket(mappedBlock.toString()))?then("Block", "Item") + ".block.getDefaultState()">
        <#else>
            <#return (generator.getElementPlainName(mappedBlock))
            + (generator.isRecipeTypeBlockOrBucket(mappedBlock.toString()))?then("Block", "Item") + "." + generator.getElementExtension(mappedBlock) + ".getDefaultState()">
        </#if>
    <#else>
        <#return mappedBlock + ".getDefaultState()">
    </#if>
</#function>

<#function mappedMCItemToItemStackCode mappedBlock amount>
    <#if mappedBlock.toString().contains("/*@ItemStack*/")>
        <#return mappedBlock?replace("/*@ItemStack*/", "")>
    <#elseif mappedBlock.toString().startsWith("CUSTOM:")>
        <#if !mappedBlock.toString().contains(".")>
            <#return "new ItemStack("+ (generator.getElementPlainName(mappedBlock))
            + (generator.isRecipeTypeBlockOrBucket(mappedBlock.toString()))?then("Block", "Item") + ".block, (int)(" + amount + "))">
        <#else>
            <#return "new ItemStack("+ (generator.getElementPlainName(mappedBlock))
            + (generator.isRecipeTypeBlockOrBucket(mappedBlock.toString()))?then("Block", "Item") + "."
            + generator.getElementExtension(mappedBlock) + ", (int)(" + amount + "))">
        </#if>
    <#else>
        <#return "new ItemStack(" + mappedBlock.toString().split("#")[0] + ", (int)(" + amount + "))">
    </#if>
</#function>

<#function mappedMCItemToItem mappedBlock>
    <#return mappedMCItemToItemStackCode(mappedBlock, 1)+".getItem()">
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