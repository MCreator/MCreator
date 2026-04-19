<#function toIdentifier string>
    <#if string?matches('"[^+]*"')>
        <#return "Identifier.parse(" + string?lower_case + ")">
    <#else>
        <#return "Identifier.parse((" + string + ").toLowerCase(java.util.Locale.ENGLISH))">
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
    <#if x?trim?starts_with("/*@int*/") && y?trim?starts_with("/*@int*/") && z?trim?starts_with("/*@int*/")>
        <#return "new BlockPos(" + opt.removeParentheses(x) + "," + opt.removeParentheses(y) + "," + opt.removeParentheses(z) +")">
    <#else>
        <#return "BlockPos.containing(" + opt.removeParentheses(x) + "," + opt.removeParentheses(y) + "," + opt.removeParentheses(z) +")">
    </#if>
</#function>

<#function toPlacedFeature featureType featureConfig placement="">
	<#if featureType == "placed_feature_inline"> <#-- Replace the /*@extra*/ marker with additional placement (if any) -->
		<#return featureConfig?replace("/*@extra*/", placement)>
	<#else> <#-- Treat as a placed feature with no placement of its own -->
		<#return '{"feature": ' + toConfiguredFeature(featureType, featureConfig) + ', "placement": [' + placement + ']}'>
	</#if>
</#function>

<#function toConfiguredFeature featureType featureConfig>
	<#if featureType == "configured_feature_reference"> <#-- No processing needed -->
		<#return featureConfig>
	<#else> <#-- Convert into an inlined configured feature object -->
		<#return '{"type": "' + generator.map(featureType, "features", 2)?replace("@modid",modid) + '", "config": ' + featureConfig + '}'>
	</#if>
</#function>

<#function patchFeaturePlacement tries xzSpread ySpread>
	<#return '{"type": "minecraft:count", "count": ' + tries + '},'
		+ '{"type": "minecraft:random_offset", "xz_spread": { "type": "minecraft:trapezoid", "plateau": 0, "min": -' + xzSpread
		+ ', "max": ' + xzSpread + '}, "y_spread": { "type": "minecraft:trapezoid", "plateau": 0, "min": -' + ySpread
		+ ', "max": ' + ySpread + '}}'>
</#function>