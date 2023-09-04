<#assign spawnBiomes = w.filterBrokenReferences(data.restrictionBiomes)>
{
  "type": "minecraft:jigsaw",
  "start_pool": "${modid}:${registryname}",
  "size": 1,
  "max_distance_from_center": 64,
  "spawn_overrides": {},
  <#if spawnBiomes?size == 1>
  "biomes": "${spawnBiomes?first}",
  <#else>
  "biomes": [
    <#list spawnBiomes as spawnBiome>"${spawnBiome}"<#sep>,</#list>
  ],
  </#if>
  "use_expansion_hack": false,
  <#if data.spawnLocation == "Ground">
    "step": "surface_structures",
    "terrain_adaptation": "beard_thin",
    "start_height": {
      "absolute": 0
    },
    "project_start_to_heightmap": "<#if data.surfaceDetectionType == "First block">WORLD_SURFACE_WG<#else>OCEAN_FLOOR_WG</#if>"
  <#elseif data.spawnLocation == "Air">
  <#elseif data.spawnLocation == "Underground">
  </#if>
}