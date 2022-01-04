<#function toResourceLocation string>
    <#if string?matches('"[^+]*"')>
        <#return "new ResourceLocation(" + string?lower_case + ")">
    <#else>
        <#return "new ResourceLocation((" + string + ").toLowerCase(java.util.Locale.ENGLISH))">
    </#if>
</#function>

<#function toArmorSlot slot>
    <#if slot == "/*@int*/0">
        <#return "EquipmentSlotType.FEET">
    <#elseif slot == "/*@int*/1">
        <#return "EquipmentSlotType.LEGS">
    <#elseif slot == "/*@int*/2">
        <#return "EquipmentSlotType.CHEST">
    <#elseif slot == "/*@int*/3">
        <#return "EquipmentSlotType.HEAD">
    <#else>
        <#return "EquipmentSlotType.fromSlotTypeAndIndex(EquipmentSlotType.Group.ARMOR, ${opt.toInt(slot)})">
    </#if>
</#function>

<#function toAxis direction>
    <#if (direction == "Direction.EAST") || (direction == "Direction.WEST")>
        <#return "Direction.Axis.X">
    <#elseif (direction == "Direction.UP") || (direction == "Direction.DOWN")>
        <#return "Direction.Axis.Y">
    <#elseif (direction == "Direction.NORTH") || (direction == "Direction.SOUTH")>
        <#return "Direction.Axis.Z">
    <#else>
        <#return direction + ".getAxis()">
    </#if>
</#function>