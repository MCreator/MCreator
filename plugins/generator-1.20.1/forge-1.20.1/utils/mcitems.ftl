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
        <#return "Ingredient.of(ItemTags.create(new ResourceLocation(\"" + mappedBlock.getUnmappedValue().replace("TAG:", "") + "\")))">
    <#elseif generator.map(mappedBlock.getUnmappedValue(), "blocksitems", 1).startsWith("#")>
        <#return "Ingredient.of(ItemTags.create(new ResourceLocation(\"" + generator.map(mappedBlock.getUnmappedValue(), "blocksitems", 1).replace("#", "") + "\")))">
    <#else>
        <#return "Ingredient.of(" + mappedMCItemToItemStackCode(mappedBlock, 1) + ")">
    </#if>
</#function>

<#function mappedMCItemsToIngredient mappedBlocks=[]>
    <#if !mappedBlocks??>
        <#return "Ingredient.EMPTY">
    <#elseif mappedBlocks?size == 1>
        <#return mappedMCItemToIngredient(mappedBlocks[0])>
    <#else>
        <#assign itemsOnly = true>

        <#list mappedBlocks as mappedBlock>
            <#if mappedBlock.getUnmappedValue().startsWith("TAG:") || generator.map(mappedBlock.getUnmappedValue(), "blocksitems", 1).startsWith("#")>
                <#assign itemsOnly = false>
                <#break>
            </#if>
        </#list>

        <#if itemsOnly>
            <#assign retval = "Ingredient.of(">
            <#list mappedBlocks as mappedBlock>
                <#assign retval += mappedMCItemToItemStackCode(mappedBlock, 1)>

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
            <#assign tags += [block.getUnmappedValue().replace("TAG:", "")]>
        <#elseif generator.map(block.getUnmappedValue(), "blocksitems", 1).startsWith("#")>
            <#assign tags += [generator.map(block.getUnmappedValue(), "blocksitems", 1).replace("#", "")]>
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
        	<#assign retval += "BlockTags.create(new ResourceLocation(\"" + tag + "\"))">
            <#if tag?has_next><#assign retval += ","></#if>
        </#list>
        <#assign retval += ").anyMatch(" + blockToCheck + "::is)">
    </#if>

    <#return retval>
</#function>

<#function mappedBlockToBlockStateProvider mappedBlock>
    <#if mappedBlock?starts_with("/*@BlockStateProvider*/")>
        <#return mappedBlock?replace("/*@BlockStateProvider*/", "")>
    <#else>
        <#return '{"type": "minecraft:simple_state_provider", "state": ' + mappedBlock + '}'>
    </#if>
</#function>

<#function mappedElementToRegistryEntry mappedElement>
    <#return JavaModName + generator.isBlock(mappedElement)?then("Blocks", "Items") + "."
    + generator.getRegistryNameFromFullName(mappedElement)?upper_case + transformExtension(mappedElement)?upper_case + ".get()">
</#function>

<#function transformExtension mappedBlock>
    <#assign extension = mappedBlock?keep_after_last(".")?replace("body", "chestplate")?replace("legs", "leggings")>
    <#return (extension?has_content)?then("_" + extension, "")>
</#function>

<#function mappedMCItemToIngameItemName mappedBlock>
    <#if mappedBlock.getUnmappedValue().startsWith("CUSTOM:")>
        <#assign customelement = generator.getRegistryNameFromFullName(mappedBlock.getUnmappedValue())!""/>
        <#if customelement?has_content>
            <#return "\"item\": \"" + "${modid}:" + customelement
            + transformExtension(mappedBlock)
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
        <#assign customelement = generator.getRegistryNameFromFullName(mappedBlock.getUnmappedValue())!""/>
        <#if customelement?has_content>
            <#return "${modid}:" + customelement + transformExtension(mappedBlock)>
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

<#function mappedMCItemToBlockStateJSON mappedBlock>
    <#if mappedBlock.getUnmappedValue().startsWith("CUSTOM:")>
        <#assign mcitemresourcepath = mappedMCItemToIngameNameNoTags(mappedBlock)/>
        <#assign me = w.getWorkspace().getModElementByName(generator.getElementPlainName(mappedBlock.getUnmappedValue()))/>
        <#if me??>
            <#assign ge = me.getGeneratableElement() />
            <#assign properties = []>

            <#if !ge.isUnknown()> <#-- We might still need to generate a valid blockstate JSON during element conversion -->
                <#if me.getType().getRegistryName() == "fluid">
                    <#assign properties += [{"name": "level", "value": 0}] />
                <#elseif me.getType().getRegistryName() == "plant">
                    <#if ge.plantType == "growapable">
                        <#assign properties += [{"name": "age", "value": 0}] />
                    <#elseif ge.plantType == "double">
                        <#assign properties += [{"name": "half", "value": "lower"}] />
                    </#if>
                <#elseif me.getType().getRegistryName() == "block">
                    <#if ge.blockBase?has_content && ge.blockBase == "Stairs">
                        <#assign properties += [
                        {"name": "facing", "value": "north"},
                        {"name": "half", "value": "bottom"},
                        {"name": "shape", "value": "straight"},
                        {"name": "waterlogged", "value": "false"}
                        ] />
                    <#elseif ge.blockBase?has_content && ge.blockBase == "Slab">
                        <#assign properties += [
                        {"name": "type", "value": "bottom"},
                        {"name": "waterlogged", "value": "false"}
                        ] />
                    <#elseif ge.blockBase?has_content && ge.blockBase == "Fence">
                        <#assign properties += [
                        {"name": "east", "value": "false"},
                        {"name": "north", "value": "false"},
                        {"name": "south", "value": "false"},
                        {"name": "waterlogged", "value": "false"},
                        {"name": "west", "value": "false"}
                        ] />
                    <#elseif ge.blockBase?has_content && ge.blockBase == "Wall">
                        <#assign properties += [
                        {"name": "east", "value": "none"},
                        {"name": "north", "value": "none"},
                        {"name": "south", "value": "none"},
                        {"name": "up", "value": "true"},
                        {"name": "waterlogged", "value": "false"},
                        {"name": "west", "value": "none"}
                        ] />
                    <#elseif ge.blockBase?has_content && ge.blockBase == "Leaves">
                        <#assign properties += [
                        {"name": "distance", "value": "7"},
                        {"name": "persistent", "value": "false"},
                        {"name": "waterlogged", "value": "false"}
                        ] />
                    <#elseif ge.blockBase?has_content && ge.blockBase == "TrapDoor">
                        <#assign properties += [
                        {"name": "facing", "value": "north"},
                        {"name": "half", "value": "bottom"},
                        {"name": "open", "value": "false"},
                        {"name": "powered", "value": "false"},
                        {"name": "waterlogged", "value": "false"}
                        ] />
                    <#elseif ge.blockBase?has_content && ge.blockBase == "Pane">
                        <#assign properties += [
                        {"name": "east", "value": "false"},
                        {"name": "north", "value": "false"},
                        {"name": "south", "value": "false"},
                        {"name": "waterlogged", "value": "false"},
                        {"name": "west", "value": "false"}
                        ] />
                    <#elseif ge.blockBase?has_content && ge.blockBase == "Door">
                        <#assign properties += [
                        {"name": "facing", "value": "north"},
                        {"name": "half", "value": "lower"},
                        {"name": "hinge", "value": "left"},
                        {"name": "open", "value": "false"},
                        {"name": "powered", "value": "false"}
                        ] />
                    <#elseif ge.blockBase?has_content && ge.blockBase == "FenceGate">
                        <#assign properties += [
                        {"name": "facing", "value": "north"},
                        {"name": "in_wall", "value": "false"},
                        {"name": "open", "value": "false"},
                        {"name": "powered", "value": "false"}
                        ] />
                    <#else>
                        <#if ge.isWaterloggable>
                            <#assign properties += [{"name": "waterlogged", "value": "false"}] />
                        </#if>

                        <#if ge.rotationMode != 0 && ge.rotationMode != 5>
                            <#assign properties += [{"name": "facing", "value": "north"}] />
                        <#elseif ge.rotationMode == 5>
                            <#assign properties += [{"name": "axis", "value": "y"}] />
                        </#if>
                    </#if>
                </#if>
            </#if>

            <#if properties?has_content>
                <#assign retval='{ "Name": "' + mcitemresourcepath + '", "Properties" : {'/>
                <#list properties as property>
                    <#assign retval = retval + '"' + property.name + '": "' + property.value + '"'/>
                    <#if property?has_next>
                        <#assign  retval = retval + ","/>
                    </#if>
                </#list>
                <#return retval + "} }">
            <#else>
                <#return '{ "Name": "' + mcitemresourcepath + '" }'>
            </#if>
        </#if>
    <#elseif !mappedBlock.getUnmappedValue().startsWith("TAG:")>
        <#assign mapped = generator.map(mappedBlock.getUnmappedValue(), "blocksitems", 1) />
        <#if !mapped.startsWith("#")>
            <#if !mapped.contains(":")>
                <#assign mapped = "minecraft:" + mapped />
            </#if>
            <#assign propertymap = fp.file("utils/defaultstates.json")?eval_json/>
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