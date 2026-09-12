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

<#function levelValueToNumProvider blockId blockCode>
<#if blockId?starts_with("level_based_value")><#return '
{
  "type": "minecraft:enchantment_level",
  "amount": ${blockCode}
}'>
<#else>
	<#return blockCode>
</#if>
</#function>