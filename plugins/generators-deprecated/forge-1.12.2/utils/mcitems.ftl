<#function mappedBlockToBlockStateCode mappedBlock>
    <#if mappedBlock.toString().contains("(world.")>
        <#return mappedBlock>
    <#elseif mappedBlock.toString().startsWith("CUSTOM:")>
        <#if !mappedBlock.toString().contains(".")>
            <#return (mappedBlock.toString().replace("CUSTOM:", (generator.getType(mappedBlock.toString())
            .getRecipeElementType() == "BLOCK")?then("Block", "Item"))) + ".block.getDefaultState()">
        <#else>
            <#return (mappedBlock.toString().replace("CUSTOM:", (generator.getType(mappedBlock.toString())
            .getRecipeElementType() == "BLOCK")?then("Block", "Item"))) + ".getDefaultState()">
        </#if>
    <#elseif !mappedBlock.toString().contains("#")>
        <#return mappedBlock + ".getDefaultState()">
    <#else>
        <#return mappedBlock.toString().split("#")[0] + ".getStateFromMeta(" + mappedBlock.toString().split("#")[1] + ")">
    </#if>
</#function>

<#function mappedMCItemToItemStackCode mappedBlock amount>
    <#if mappedBlock.toString().contains("/*@ItemStack*/")>
        <#return mappedBlock?replace("/*@ItemStack*/", "")>
    <#elseif mappedBlock.toString().startsWith("CUSTOM:")>
        <#if !mappedBlock.toString().contains(".")>
            <#return "new ItemStack("+ (mappedBlock.toString().replace("CUSTOM:", (generator.getType(mappedBlock.toString())
            .getRecipeElementType() == "BLOCK")?then("Block", "Item"))) + ".block, (int)(" + amount + "))">
        <#else>
            <#return "new ItemStack("+ (mappedBlock.toString().replace("CUSTOM:", (generator.getType(mappedBlock.toString())
            .getRecipeElementType() == "BLOCK")?then("Block", "Item"))) + ", (int)(" + amount + "))">
        </#if>
    <#elseif !mappedBlock.toString().contains("#")>
        <#return "new ItemStack(" + mappedBlock.toString().split("#")[0] + ", (int)(" + amount + "))">
    <#else>
        <#return "new ItemStack(" + mappedBlock.toString().split("#")[0] + ", (int)(" + amount + "), " + mappedBlock.toString().split("#")[1] + ")">
    </#if>
</#function>

<#function mappedMCItemToItem mappedBlock>
    <#return mappedMCItemToItemStackCode(mappedBlock, 1)+".getItem()">
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

<#function mappedMCItemToIngameItemName mappedBlock skipDefaultMetadata=false>
    <#if mappedBlock.toString().startsWith("CUSTOM:")>
        <#assign customelement = generator.getRegistryNameForModElement(mappedBlock.toString().replace("CUSTOM:", "")
        .replace(".helmet", "").replace(".body", "").replace(".legs", "").replace(".boots", ""))!""/>
        <#if customelement?has_content>
            <#return "\"item\": \"" + "${modid}:" + customelement
            + (mappedBlock.toString().contains(".helmet"))?then("helmet", "")
            + (mappedBlock.toString().contains(".body"))?then("body", "")
            + (mappedBlock.toString().contains(".legs"))?then("legs", "")
            + (mappedBlock.toString().contains(".boots"))?then("boots", "")
            + "\"">
        <#else>
            <#return "\"item\": \"minecraft:air\"">
        </#if>
    <#elseif mappedBlock.toString().startsWith("TAG:")>
        <#return "\"type\": \"forge:ore_dict\", \"ore\": \"" + mappedBlock.toString().replace("TAG:", "").replace(":", "").replace("/", "") + "\"">
    <#else>
        <#assign mapped = generator.map(mappedBlock, "blocksitems_ingame")>
        <#if mapped.toString().contains("#") && !(skipDefaultMetadata && mapped.toString().endsWith("#32767"))>
            <#return "\"item\": \"minecraft:" + mapped.toString().split("#")[0] + "\", \"data\": " + mapped.toString().split("#")[1]>
        <#else>
            <#return "\"item\": \"minecraft:" + mapped.toString().split("#")[0] + "\"">
        </#if>
    </#if>
</#function>

<#function mappedMCItemToIngameNameNoTags mappedBlock>
    <#if mappedBlock.getUnmappedValue().startsWith("CUSTOM:")>
        <#assign customelement = generator.getRegistryNameForModElement(mappedBlock.getUnmappedValue().replace("CUSTOM:", "")
        .replace(".helmet", "").replace(".body", "").replace(".legs", "").replace(".boots", ""))!""/>
        <#if customelement?has_content>
            <#return "${modid}:" + customelement
            + (mappedBlock.getUnmappedValue().contains(".helmet"))?then("helmet", "")
            + (mappedBlock.getUnmappedValue().contains(".body"))?then("body", "")
            + (mappedBlock.getUnmappedValue().contains(".legs"))?then("legs", "")
            + (mappedBlock.getUnmappedValue().contains(".boots"))?then("boots", "")>
        <#else>
            <#return "minecraft:air">
        </#if>
    <#elseif mappedBlock.getUnmappedValue().startsWith("TAG:")>
        <#return "minecraft:air">
    <#else>
        <#assign mapped = generator.map(mappedBlock.getUnmappedValue(), "blocksitems_ingame") />
        <#if mapped.startsWith("#")>
            <#return "minecraft:air">
        <#else>
            <#return "minecraft:" + mapped>
        </#if>
    </#if>
</#function>