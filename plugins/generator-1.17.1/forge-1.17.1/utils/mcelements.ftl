<#function toResourceLocation string>
    <#if string?matches('"[^+]*"')>
        <#return "new ResourceLocation(" + string?lower_case + ")">
    <#else>
        <#return "new ResourceLocation((" + string + ").toLowerCase(java.util.Locale.ENGLISH))">
    </#if>
</#function>

<#function toArmorSlot slot>
    <#if slot == "0">
        <#return "EquipmentSlot.FEET">
    <#elseif slot == "1">
        <#return "EquipmentSlot.LEGS">
    <#elseif slot == "2">
        <#return "EquipmentSlot.CHEST">
    <#elseif slot == "3">
        <#return "EquipmentSlot.HEAD">
    <#else>
        <#return "EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, (int) " + slot + ")">
    </#if>
</#function>