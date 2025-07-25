<#-- Macro for trunk placers that only use height parameters -->
<#macro simpleTrunkPlacer type height rand_a rand_b>
{
  "type": "${type}",
  "base_height": ${height},
  "height_rand_a": ${rand_a},
  "height_rand_b": ${rand_b}
}
</#macro>

<#-- Macro for foliage placers that only use radius, offset, and optional height parameters -->
<#macro simpleFoliagePlacer type radius offset height=-1>
{
  "type": "${type}",
  "radius": ${radius},
  "offset": ${offset}
  <#if height != -1>,
    "height": ${height}
  </#if>
}
</#macro>

<#-- Macro for random spread foliage placer, used by mangrove and azalea trees -->
<#macro randomSpreadFoliagePlacer radius offset foliage_height density>
{
  "type": "minecraft:random_spread_foliage_placer",
  "radius": ${radius},
  "offset": ${offset},
  "foliage_height": ${foliage_height},
  "leaf_placement_attempts": ${density}
}
</#macro>

<#macro twoLayersFeatureSize limit lower_size upper_size min_clipped_height=-1>
{
  "type": "minecraft:two_layers_feature_size",
  "limit": ${limit},
  "lower_size": ${lower_size},
  "upper_size": ${upper_size}
  <#if min_clipped_height != -1>,
    "min_clipped_height": ${min_clipped_height}
  </#if>
}
</#macro>

<#macro threeLayersFeatureSize limit upper_limit lower_size middle_size upper_size>
{
  "type": "minecraft:three_layers_feature_size",
  "limit": ${limit},
  "upper_limit": ${upper_limit},
  "lower_size": ${lower_size},
  "middle_size": ${middle_size},
  "upper_size": ${upper_size}
}
</#macro>