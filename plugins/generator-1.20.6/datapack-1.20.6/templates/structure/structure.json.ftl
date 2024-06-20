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
      "absolute": 0
  },
  "project_start_to_heightmap": "${data.surfaceDetectionType}",
  <#if spawnBiomes?size == 1>
  "biomes": "${spawnBiomes?first}",
  <#else>
  "biomes": [
    <#list spawnBiomes as spawnBiome>"${spawnBiome}"<#sep>,</#list>
  ],
  </#if>
  "use_expansion_hack": false
}