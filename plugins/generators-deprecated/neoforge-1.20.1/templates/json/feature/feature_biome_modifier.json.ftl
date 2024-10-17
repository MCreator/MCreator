<#assign spawnBiomes = w.filterBrokenReferences(data.restrictionBiomes)>
{
  "type": "forge:add_features",
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
  "features": "${modid}:${registryname}",
  "step": "${generator.map(data.generationStep, "generationsteps")}"
}