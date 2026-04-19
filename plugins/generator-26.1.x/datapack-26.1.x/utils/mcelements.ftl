<#function toPlacedFeature featureType featureConfig placement="">
	<#if featureType == "placed_feature_inline"> <#-- Replace the /*@extra*/ marker with additional placement (if any) -->
		<#return featureConfig?replace("/*@extra*/", placement)>
	<#else> <#-- Treat as a placed feature with no placement -->
		<#return '{"feature": ' + toConfiguredFeature(featureType, featureConfig) + ', "placement": []}'>
	</#if>
</#function>

<#function toConfiguredFeature featureType featureConfig>
	<#if featureType == "configured_feature_reference"> <#-- No processing needed -->
		<#return featureConfig>
	<#else> <#-- Convert into an inlined configured feature object -->
		<#return '{"type": "' + generator.map(featureType, "features", 2)?replace("@modid",modid) + '", "config": ' + featureConfig + '}'>
	</#if>
</#function>