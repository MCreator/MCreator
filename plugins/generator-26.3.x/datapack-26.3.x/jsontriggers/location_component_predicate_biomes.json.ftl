<#if field_list$biome?size == 1>
"biomes": "${generator.map(field_list$biome?first, "biomes")}"
<#else>
"biomes": [
  <#list field_list$biome as biome>"${generator.map(biome, "biomes")}"<#sep>,</#list>
]
</#if>