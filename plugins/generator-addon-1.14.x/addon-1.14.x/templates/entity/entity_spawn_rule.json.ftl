<#-- @formatter:off -->
{
  "format_version": "1.8.0",
  "minecraft:spawn_rules": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "population_control": "<#if data.mobSpawningType == "monster">monster<#else>animal</#if>"
    },
    "conditions": [
      {
        <#if data.mobSpawningType == "monster">
        "minecraft:spawns_on_surface": {},
        "minecraft:spawns_underground": {},
        "minecraft:brightness_filter": {
          "min": 0,
          "max": 7,
          "adjust_for_weather": true
        },
        "minecraft:difficulty_filter": {
          "min": "easy",
          "max": "hard"
        },
        "minecraft:biome_filter": {
          "test": "has_biome_tag", "operator": "==", "value": "monster"
        },
        <#elseif data.mobSpawningType == "waterCreature">
        "minecraft:spawns_on_surface": {},
        "minecraft:spawns_underwater": {},
        "minecraft:biome_filter": {
          "any_of": [
            { "test": "has_biome_tag", "operator": "==", "value": "ocean" },
            { "test": "has_biome_tag", "operator": "==", "value": "river" }
          ]
        },
        <#else>
        "minecraft:spawns_on_surface": {},
        "minecraft:spawns_on_block_filter": "minecraft:grass",
        "minecraft:brightness_filter": {
          "min": 7,
          "max": 15,
          "adjust_for_weather": false
        },
        "minecraft:biome_filter": {
          "test": "has_biome_tag", "operator":"==", "value": "animal"
        },
        </#if>
        "minecraft:weight": {
          "default": ${data.spawningProbability}
        },
        "minecraft:herd": {
          "min_size": ${data.minNumberOfMobsPerGroup},
          "max_size": ${data.maxNumberOfMobsPerGroup}
        }
      }
    ]
  }
}
<#-- @formatter:on -->