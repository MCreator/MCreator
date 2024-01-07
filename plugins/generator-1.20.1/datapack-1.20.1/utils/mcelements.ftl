<#function toPlacedFeature featureType featureConfig placement="">
	<#if featureType == "placed_feature_inline">
		<#return featureConfig>
	<#else>
		<#return '{"feature": {"type": "' + generator.map(featureType, "features", 2)?replace("@modid",modid) + '", "config": '
			+ featureConfig + '}, "placement": [' + placement?remove_ending(",") + ']}'>
	</#if>
</#function>