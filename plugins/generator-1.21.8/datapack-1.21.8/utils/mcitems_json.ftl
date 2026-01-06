<#function mappedBlockToBlockStateProvider mappedBlock>
    <#if mappedBlock?starts_with("/*@BlockStateProvider*/")>
        <#return mappedBlock?replace("/*@BlockStateProvider*/", "")>
    <#else>
        <#return '{"type": "minecraft:simple_state_provider", "state": ' + mappedBlock + '}'>
    </#if>
</#function>

<#function handleExtension mappedBlock customelement>
    <#assign extension = mappedBlock?keep_after_last(".")?replace("body", "chestplate")?replace("legs", "leggings")>
    <#if extension == "wall"> <#-- Special handling for wall variant of signs -->
        <#if customelement?ends_with("sign")> <#-- If element name ends with "sign", we can use proper naming for wall sign -->
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
        <#assign customelement = generator.getRegistryNameFromFullName(mappedBlock.getUnmappedValue())!""/>
        <#if customelement?has_content>
            <#return "\"" + itemKey + "\": \"" + "${modid}:" + handleExtension(mappedBlock, customelement) + "\"">
        <#else>
            <#return "\"" + itemKey + "\": \"minecraft:air\"">
        </#if>
    <#elseif mappedBlock.getUnmappedValue().startsWith("TAG:")>
        <#return "\"tag\": \"" + mappedBlock.getUnmappedValue().replace("TAG:", "").replace("mod:", modid + ":")?lower_case + "\"">
    <#else>
        <#assign mapped = mappedBlock.getMappedValue(1) />
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
        <#assign customelement = generator.getRegistryNameFromFullName(mappedBlock.getUnmappedValue())!""/>
        <#if customelement?has_content>
            <#return "${modid}:" + handleExtension(mappedBlock, customelement)>
        <#else>
            <#return "minecraft:air">
        </#if>
    <#elseif mappedBlock.getUnmappedValue().startsWith("TAG:")>
        <#if acceptTags>
            <#return "#" + mappedBlock.getUnmappedValue().replace("TAG:", "").replace("mod:", modid + ":")?lower_case>
        <#else>
            <#return "minecraft:air">
        </#if>
    <#else>
        <#assign mapped = mappedBlock.getMappedValue(1) />
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

<#function mappedMCItemToBlockStateJSON mappedBlock>
    <#if mappedBlock.getUnmappedValue().startsWith("CUSTOM:")>
        <#assign mcitemresourcepath = mappedMCItemToRegistryName(mappedBlock)/>
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
                    <#list ge.customProperties as prop>
                        <#assign properties += [{"name": prop.property().getName().replace("CUSTOM:", ""), "value": prop.value()}] />
                    </#list>
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
        <#assign mapped = mappedBlock.getMappedValue(1) />
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