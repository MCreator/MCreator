<#macro toTreeConfiguration dirt_provider foliage_provider trunk_provider foliage_config trunk_config size_config force_dirt ignore_vines decorators>
{
  "dirt_provider": ${dirt_provider},
  "foliage_placer": <@toFoliagePlacer foliage_config/>,
  "foliage_provider": ${foliage_provider},
  "trunk_placer": <@toTrunkPlacer trunk_config/>,
  "trunk_provider": ${trunk_provider},
  "force_dirt": ${force_dirt},
  "ignore_vines": ${ignore_vines},
  "minimum_size": <@toSizeConfiguration size_config/>,
  "decorators": [
    <#list decorators as decorator>
      ${decorator}
    <#sep>,</#list>
  ]
}
</#macro>

<#macro toFoliagePlacer foliage_config>
{
  "type": "${foliage_config[0]}",
  "radius": ${foliage_config[1]},
  "offset": ${foliage_config[2]}
  <#if foliage_config?size == 4>,
    "height": ${foliage_config[3]}
  </#if>
}
</#macro>

<#macro toTrunkPlacer trunk_config>
{
  "type": "${trunk_config[0]}",
  "base_height": ${trunk_config[1]},
  "height_rand_a": ${trunk_config[2]},
  "height_rand_b": ${trunk_config[3]}
}
</#macro>

<#-- Passing 5 parameters means using the "three layers" feature size -->
<#macro toSizeConfiguration size_config>
{
  "type": <#if size_config?size == 5>"minecraft:three_layers_feature_size"<#else>"minecraft:two_layers_feature_size"</#if>,
  "limit": ${size_config[0]},
  "lower_size": ${size_config[1]},
  "upper_size": ${size_config[2]}
  <#if size_config?size == 5>,
    "middle_size": ${size_config[3]},
    "upper_limit": ${size_config[4]}
  </#if>
}
</#macro>