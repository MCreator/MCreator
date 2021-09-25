<#function toResourceLocation string>
    <#if string?matches('"[^+]*"')>
        <#return "new ResourceLocation(" + string?lower_case + ")">
    <#else>
        <#return "new ResourceLocation((" + string + ").toLowerCase(java.util.Locale.ENGLISH))">
    </#if>
</#function>

<#function toArmorSlot slot>
    <#if slot == "0">
        <#return "EquipmentSlotType.FEET">
    <#elseif slot == "1">
        <#return "EquipmentSlotType.LEGS">
    <#elseif slot == "2">
        <#return "EquipmentSlotType.CHEST">
    <#elseif slot == "3">
        <#return "EquipmentSlotType.HEAD">
    <#else>
        <#return "EquipmentSlotType.fromSlotTypeAndIndex(EquipmentSlotType.Group.ARMOR, (int) " + slot + ")">
    </#if>
</#function>