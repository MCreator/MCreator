<#include "mcitems_json.ftl">

<#function mappedBlockToBlockStateCode mappedBlock>
    <#if mappedBlock?starts_with("/*@BlockState*/")>
        <#return mappedBlock?replace("/*@BlockState*/","")>
    <#elseif mappedBlock?contains("/*@?*/")>
        <#assign outputs = mappedBlock?keep_after("/*@?*/")?keep_before_last(")")>
        <#return mappedBlock?keep_before("/*@?*/") + "?" + mappedBlockToBlockStateCode(outputs?keep_before("/*@:*/"))
            + ":" + mappedBlockToBlockStateCode(outputs?keep_after("/*@:*/")) + ")">
    <#else>
        <#return mappedBlockToBlock(mappedBlock) + ".defaultBlockState()">
    </#if>
</#function>

<#function mappedBlockToBlock mappedBlock>
    <#if mappedBlock?starts_with("/*@BlockState*/")>
        <#return mappedBlock?replace("/*@BlockState*/","") + ".getBlock()">
    <#elseif mappedBlock?contains("/*@?*/")>
        <#assign outputs = mappedBlock?keep_after("/*@?*/")?keep_before_last(")")>
        <#return mappedBlock?keep_before("/*@?*/") + "?" + mappedBlockToBlock(outputs?keep_before("/*@:*/"))
            + ":" + mappedBlockToBlock(outputs?keep_after("/*@:*/")) + ")">
    <#elseif mappedBlock?starts_with("CUSTOM:")>
        <#return mappedElementToRegistryEntry(mappedBlock)>
    <#else>
        <#return mappedBlock>
    </#if>
</#function>

<#function mappedMCItemToItemStackCode mappedBlock amount=1>
    <#if mappedBlock?starts_with("/*@ItemStack*/")>
        <#return mappedBlock?replace("/*@ItemStack*/", "")>
    <#elseif mappedBlock?contains("/*@?*/")>
        <#assign outputs = mappedBlock?keep_after("/*@?*/")?keep_before_last(")")>
        <#return mappedBlock?keep_before("/*@?*/") + "?" + mappedMCItemToItemStackCode(outputs?keep_before("/*@:*/"), amount)
            + ":" + mappedMCItemToItemStackCode(outputs?keep_after("/*@:*/"), amount) + ")">
    <#elseif mappedBlock?starts_with("CUSTOM:")>
        <#return toItemStack(mappedElementToRegistryEntry(mappedBlock), amount)>
    <#else>
        <#return toItemStack(mappedBlock, amount)>
    </#if>
</#function>

<#function toItemStack item amount>
    <#if amount == 1>
        <#return "new ItemStack(" + item + ")">
    <#else>
        <#return "new ItemStack(" + item + "," + (amount == amount?floor)?then(amount + ")","(int)(" + amount + "))")>
    </#if>
</#function>

<#function mappedMCItemToItem mappedBlock>
    <#if mappedBlock?starts_with("/*@ItemStack*/")>
        <#return mappedBlock?replace("/*@ItemStack*/", "") + ".getItem()">
    <#elseif mappedBlock?contains("/*@?*/")>
        <#assign outputs = mappedBlock?keep_after("/*@?*/")?keep_before_last(")")>
        <#return mappedBlock?keep_before("/*@?*/") + "?" + mappedMCItemToItem(outputs?keep_before("/*@:*/"))
            + ":" + mappedMCItemToItem(outputs?keep_after("/*@:*/")) + ")">
    <#elseif mappedBlock?starts_with("CUSTOM:")>
        <#return mappedElementToRegistryEntry(mappedBlock) + generator.isBlock(mappedBlock)?then(".asItem()", "")>
    <#else>
        <#return mappedBlock + mappedBlock?contains("Blocks.")?then(".asItem()","")>
    </#if>
</#function>

<#function mappedMCItemToIngredient mappedBlock>
    <#if mappedBlock.getUnmappedValue().startsWith("TAG:")>
        <#return "Ingredient.of(HolderSet.emptyNamed(BuiltInRegistries.ITEM, ItemTags.create(ResourceLocation.parse(\"" + mappedBlock.getUnmappedValue().replace("TAG:", "").replace("mod:", modid + ":") + "\"))))">
    <#elseif mappedBlock.getMappedValue(1).startsWith("#")>
        <#return "Ingredient.of(HolderSet.emptyNamed(BuiltInRegistries.ITEM, ItemTags.create(ResourceLocation.parse(\"" + mappedBlock.getMappedValue(1).replace("#", "") + "\"))))">
    <#else>
        <#return "Ingredient.of(" + mappedMCItemToItem(mappedBlock) + ")">
    </#if>
</#function>

<#function mappedMCItemsToIngredient mappedBlocks=[]>
    <#if !mappedBlocks??>
        <#return "Ingredient.EMPTY">
    <#elseif mappedBlocks?size == 1>
        <#return mappedMCItemToIngredient(mappedBlocks?first)>
    <#else>
        <#assign itemsOnly = true>

        <#list mappedBlocks as mappedBlock>
            <#if mappedBlock.getUnmappedValue().startsWith("TAG:") || mappedBlock.getMappedValue(1).startsWith("#")>
                <#assign itemsOnly = false>
                <#break>
            </#if>
        </#list>

        <#if itemsOnly>
            <#assign retval = "Ingredient.of(">
            <#list mappedBlocks as mappedBlock>
                <#assign retval += mappedMCItemToItem(mappedBlock)>

                <#if mappedBlock?has_next>
                    <#assign retval += ",">
                </#if>
            </#list>
            <#return retval + ")">
        <#else>
            <#assign retval = "CompoundIngredient.of(">
            <#list mappedBlocks as mappedBlock>
                <#assign retval += mappedMCItemToIngredient(mappedBlock)>

                <#if mappedBlock?has_next>
                    <#assign retval += ",">
                </#if>
            </#list>
            <#return retval + ")">
        </#if>
    </#if>
</#function>

<#function containsAnyOfBlocks elements blockToCheck>
    <#assign blocks = []>
    <#assign tags = []>
    <#assign retval = "">

    <#list elements as block>
        <#if block.getUnmappedValue().startsWith("TAG:")>
            <#assign tags += [block.getUnmappedValue().replace("TAG:", "").replace("mod:", modid + ":")]>
        <#elseif block.getMappedValue(1).startsWith("#")>
            <#assign tags += [block.getMappedValue(1).replace("#", "")]>
        <#else>
            <#assign blocks += [mappedBlockToBlock(block)]>
        </#if>
    </#list>

    <#if !blocks?has_content && !tags?has_content>
        <#return "false">
    <#elseif blocks?has_content>
    	<#assign retval += "List.of(">
        <#list blocks as block>
        	<#assign retval += block>
			<#if block?has_next><#assign retval += ","></#if>
        </#list>
        <#assign retval += ").contains("+ blockToCheck + ".getBlock())">

        <#if tags?has_content>
        	<#assign retval += "||">
        </#if>
    </#if>

    <#if tags?has_content>
    	<#assign retval += "Stream.of(">
        <#list tags as tag>
        	<#assign retval += "BlockTags.create(ResourceLocation.parse(\"" + tag + "\"))">
            <#if tag?has_next><#assign retval += ","></#if>
        </#list>
        <#assign retval += ").anyMatch(" + blockToCheck + "::is)">
    </#if>

    <#return retval>
</#function>

<#function mappedElementToRegistryEntry mappedElement>
    <#return JavaModName + generator.isBlock(mappedElement)?then("Blocks", "Items") + "."
    + handleExtension(mappedElement, generator.getRegistryNameFromFullName(mappedElement))?upper_case + ".get()">
</#function>