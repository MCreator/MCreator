<#function toResourceLocation string>
    <#if string?matches('"[^+]*"')>
        <#return "ResourceLocation.parse(" + string?lower_case + ")">
    <#else>
        <#return "ResourceLocation.parse((" + string + ").toLowerCase(java.util.Locale.ENGLISH))">
    </#if>
</#function>

<#function toArmorSlot slot>
    <#if slot == "/*@int*/0">
        <#return "EquipmentSlot.FEET">
    <#elseif slot == "/*@int*/1">
        <#return "EquipmentSlot.LEGS">
    <#elseif slot == "/*@int*/2">
        <#return "EquipmentSlot.CHEST">
    <#elseif slot == "/*@int*/3">
        <#return "EquipmentSlot.HEAD">
    <#else>
        <#return "new Object(){
            public static EquipmentSlot armorSlotByIndex(int _slotindex) {
                for (EquipmentSlot _slot : EquipmentSlot.values()) {
                    if (_slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && _slot.getIndex() == _slotindex) {
                        return _slot;
                    }
                }
                throw new IllegalArgumentException(\"Invalid slot index: \" + _slotindex);
            }
        }.armorSlotByIndex(${opt.toInt(slot)})">
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

<#function toBlockPos x y z>
    <#if x?starts_with("/*@int*/") && y?starts_with("/*@int*/") && z?starts_with("/*@int*/")>
        <#return "new BlockPos(" + opt.removeParentheses(x) + "," + opt.removeParentheses(y) + "," + opt.removeParentheses(z) +")">
    <#else>
        <#return "BlockPos.containing(" + opt.removeParentheses(x) + "," + opt.removeParentheses(y) + "," + opt.removeParentheses(z) +")">
    </#if>
</#function>

<#function toPlacedFeature featureType featureConfig placement="">
	<#if featureType == "placed_feature_inline">
		<#return featureConfig>
	<#elseif featureType == "configured_feature_reference">
		<#return '{"feature": ' + featureConfig + ', "placement": [' + placement?remove_ending(",") + ']}'>
	<#else>
		<#return '{"feature": {"type": "' + generator.map(featureType, "features", 2)?replace("@modid",modid) + '", "config": '
			+ featureConfig + '}, "placement": [' + placement?remove_ending(",") + ']}'>
	</#if>
</#function>