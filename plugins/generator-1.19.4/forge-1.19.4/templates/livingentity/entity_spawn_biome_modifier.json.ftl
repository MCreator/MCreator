<#assign spawnBiomes = w.filterBrokenReferences(data.restrictionBiomes)>
{
  "type": "forge:add_spawns",
  <#if spawnBiomes?size == 1>
  "biomes": "${spawnBiomes?first}",
  <#elseif spawnBiomes?size gt 1>
  "biomes": [
    <#list spawnBiomes as spawnBiome>"${spawnBiome}"<#sep>,</#list>
  ],
  <#else>
  "biomes": {
    "type": "forge:any"
  },
  </#if>
  "spawners": {
    "type": "${modid}:${registryname}",
    "weight": ${data.spawningProbability},
    "minCount": ${data.minNumberOfMobsPerGroup},
    "maxCount": ${data.maxNumberOfMobsPerGroup}
  }
}