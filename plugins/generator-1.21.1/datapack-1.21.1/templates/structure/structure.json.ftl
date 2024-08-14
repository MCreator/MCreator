<#assign spawnBiomes = w.filterBrokenReferences(data.restrictionBiomes)>
{
  "type": "minecraft:jigsaw",
  "start_pool": "${modid}:${registryname}",
  "size": ${data.size},
  "max_distance_from_center": ${data.maxDistanceFromCenter},
  "spawn_overrides": {},
  "step": "${generator.map(data.generationStep, "generationsteps")}",
  "terrain_adaptation": "${data.terrainAdaptation}",
  "start_height": {
    <#if data.useStartHeight>
    "type": "minecraft:${data.startHeightProviderType?lower_case}",
    "min_inclusive": {
      "absolute": ${data.startHeightMin}
    },
    "max_inclusive": {
      "absolute": ${data.startHeightMax}
    }
    <#else>
    "absolute": 0
    </#if>
  },
  <#if !data.useStartHeight>
  "project_start_to_heightmap": "${data.surfaceDetectionType}",
  </#if>
  <#if spawnBiomes?size == 1>
  "biomes": "${spawnBiomes?first}",
  <#else>
  "biomes": [
    <#list spawnBiomes as spawnBiome>"${spawnBiome}"<#sep>,</#list>
  ],
  </#if>
  "use_expansion_hack": false
}