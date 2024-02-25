<#assign spawnBiomes = w.filterBrokenReferences(data.restrictionBiomes)>
{
  "type": "neoforge:add_spawns",
  <#if spawnBiomes?size == 1>
  "biomes": "${spawnBiomes?first}",
  <#elseif spawnBiomes?size gt 1>
  "biomes": [
    <#list spawnBiomes as spawnBiome>"${spawnBiome}"<#sep>,</#list>
  ],
  <#else>
  "biomes": {
    "type": "neoforge:any"
  },
  </#if>
  "spawners": {
    "type": "${modid}:${registryname}",
    "weight": ${data.spawningProbability},
    "minCount": ${data.minNumberOfMobsPerGroup},
    "maxCount": ${data.maxNumberOfMobsPerGroup}
  }
}