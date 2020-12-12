{
  "scale": ${data.heightVariation},
  "effects": {
    "mood_sound": {
      "sound": "minecraft:ambient.cave",
      "tick_delay": 6000,
      "block_search_extent": 8,
      "offset": 2.0
    },
    <#if data.foliafeColor?has_content>
    "foliage_color": ${data.foliageColor.getRGB()},
    <#else>
    "foliage_color": 10387789,
    </#if>
    <#if data.grassColor?has_content>
    "grass_color": ${data.grassColor.getRGB()},
    <#else>
    "grass_color": 9470285,
    </#if>
    <#if data.airColor?has_content>
    "sky_color": ${data.airColor.getRGB()},
    "fog_color": ${data.airColor.getRGB()},
    <#else>
    "sky_color": 7972607,
    "fog_color": 12638463,
    </#if>
    <#if data.waterColor?has_content>
    "water_color": ${data.waterColor.getRGB()},
    <#else>
    "water_color": 4159204,
    </#if>
    <#if data.waterFogColorColor?has_content>
    "water_fog_color": ${data.waterColor.getRGB()}
    <#else>
    "water_fog_color": 329011
    </#if>
  },
  "surface_builder": "${modid}:${registryname}",
  "carvers": {
    "air": [
      "minecraft:cave",
      "minecraft:canyon"
    ]
  },
  "features": [
    [],
    [
    <#if data.generateLakes>
    "minecraft:lake_water",
    "minecraft:lake_lava"
    </#if>
    ],
    [],
    [
      "minecraft:monster_room"
    ],
    [],
    [],
    [
      "minecraft:ore_dirt",
      "minecraft:ore_gravel",
      "minecraft:ore_granite",
      "minecraft:ore_diorite",
      "minecraft:ore_andesite",
      "minecraft:ore_coal",
      "minecraft:ore_iron",
      "minecraft:ore_gold",
      "minecraft:ore_redstone",
      "minecraft:ore_diamond",
      "minecraft:ore_lapis",
      <#if (data.sandPathcesPerChunk > 0)>
      "minecraft:disk_sand",
      </#if>
      <#if (data.gravelPatchesPerChunk > 0)>
      "minecraft:disk_gravel",
      </#if>
      "minecraft:disk_clay"
    ],
    [],
    [
      <#if (data.treesPerChunk > 0)>
      <#if data.vanillaTreeType == "Big trees">
      "minecraft:fancy_oak",
      "minecraft:fancy_oak_bees_0002",
      "minecraft:fancy_oak_bees_002",
      "minecraft:fancy_oak_bees_005" <#if (data.flowersPerChunk > 0) || (data.grassPerChunk > 0) || (data.mushroomsPerChunk > 0) || (data.reedsPerChunk > 0) || (data.cactiPerChunk > 0)>,</#if>
      <#elseif data.vanillaTreeType == "Mega pine trees">
      "minecraft mega_spruce",
      "minecraft:mega_pine"<#if (data.flowersPerChunk > 0) || (data.grassPerChunk > 0) || (data.mushroomsPerChunk > 0) || (data.reedsPerChunk > 0) || (data.cactiPerChunk > 0)>,</#if>
      <#elseif data.vanillaTreeType == "Savanna trees">
      "minecarft acacia"<#if (data.flowersPerChunk > 0) || (data.grassPerChunk > 0) || (data.mushroomsPerChunk > 0) || (data.reedsPerChunk > 0) || (data.cactiPerChunk > 0)>,</#if>
      <#elseif data.vanillaTreeType == "Birch trees">
      "minecraft:birch",
      "minecraft:birch_0002",
      "minecraft:birch_002",
      "minecraft:birch_005",
      "minecraft:birch_other"<#if (data.flowersPerChunk > 0) || (data.grassPerChunk > 0) || (data.mushroomsPerChunk > 0) || (data.reedsPerChunk > 0) || (data.cactiPerChunk > 0)>,</#if>
      <#else>
      "minecraft:oak",
      "minecraft:oak_bees_0002",
      "minecraft:oak_bees_002",
      "minecraft:oak_bees_005" <#if (data.flowersPerChunk > 0) || (data.grassPerChunk > 0) || (data.mushroomsPerChunk > 0) || (data.reedsPerChunk > 0) || (data.cactiPerChunk > 0)>,</#if>
      </#if>
      </#if>
      <#if (data.flowersPerChunk > 0)>
      "minecraft:flower_default"<#if (data.grassPerChunk > 0) || (data.mushroomsPerChunk > 0) || (data.reedsPerChunk > 0) || (data.cactiPerChunk > 0)>,</#if>
      </#if>
      <#if (data.grassPerChunk > 0)>
      "minecraft:patch_grass_forest"<#if (data.mushroomsPerChunk > 0) || (data.reedsPerChunk > 0) || (data.cactiPerChunk > 0)>,</#if>
      </#if>
      <#if (data.mushroomsPerChunk > 0)>
      "minecraft:brown_mushroom_normal",
      "minecraft:red_mushroom_normal"<#if (data.reedsPerChunk > 0) || (data.cactiPerChunk > 0)>,</#if>
      </#if>
      <#if (data.reedsPerChunk > 0)>
      "minecraft:patch_sugar_cane"<#if (data.cactiPerChunk > 0)>,</#if>
      </#if>
      <#if (data.cactiPerChunk > 0)>
      "minecraft:patch_cactus_decorated"
      </#if>
    ],
    [
      "minecraft:freeze_top_layer"
    ]
  ],
  "starts": [
    <#if data.spawnWoodlandMansion>
    "minecraft:mansion",
    </#if>
    <#if data.spawnMineshaft>
    "minecraft:mineshaft" <#if (data.spawnStronghold) || (data.spawnPillagerOutpost) || (data.spawnShipwreck) || (data.oceanRuinType != "NONE") || (data.spawnOceanMonument) || (data.spawnDesertPyramid) || (data.spawnJungleTemple) || (data.spawnIgloo) || (data.villageType != "none")> , </#if>
    </#if>
    <#if data.spawnStronghold>
    "minecraft:stronghold" <#if (data.spawnPillagerOutpost) || (data.spawnShipwreck) || (data.oceanRuinType != "NONE") || (data.spawnOceanMonument) || (data.spawnDesertPyramid) || (data.spawnJungleTemple) || (data.spawnIgloo) || (data.villageType != "none")> , </#if>
    </#if>
    <#if data.spawnPillagerOutpost>
    "minecraft:pillager_outpost" <#if (data.spawnShipwreck) || (data.oceanRuinType != "NONE") || (data.spawnOceanMonument) || (data.spawnDesertPyramid) || (data.spawnJungleTemple) || (data.spawnIgloo) || (data.villageType != "none")> , </#if>
    </#if>
    <#if data.spawnShipwreck>
    "minecraft:shipwreck" <#if (data.oceanRuinType != "NONE") || (data.spawnOceanMonument) || (data.spawnDesertPyramid) || (data.spawnJungleTemple) || (data.spawnIgloo) || (data.villageType != "none")> , </#if>
    </#if>
    <#if data.oceanRuinType != "NONE">
    "minecraft:ocean_ruin_${Pdata.oceanRuinType?lower_case}" <#if (data.spawnOceanMonument) || (data.spawnDesertPyramid) || (data.spawnJungleTemple) || (data.spawnIgloo) || (data.villageType != "none")> , </#if>
    </#if>
    <#if data.spawnOceanMonument>
    "minecraft:monument" <#if (data.spawnDesertPyramid) || (data.spawnJungleTemple) || (data.spawnIgloo) || (data.villageType != "none")> , </#if>
    </#if>
    <#if data.spawnDesertPyramid>
    "minecraft:desert_pyramid" <#if (data.spawnJungleTemple) || (data.spawnIgloo) || (data.villageType != "none")> , </#if>
    </#if>
    <#if data.spawnJungleTenmple>
    "minecraft:jungle_pyramid" <#if (data.spawnIgloo) || (data.villageType != "none")> , </#if>
    </#if>
    <#if data.spawnIgloo>
    "minecraft:igloo" <#if (data.villageType != "none")> , </#if>
    </#if>
    <#if data.villageType != "none">
    "minecraft:village_${data.villageType}"
    </#if>
  ],
  "spawners": {
    "monster": [
    ],
    "creature": [
    ],
    "ambient": [
    ],
    "water_creature": [
    ],
    "water_ambient": [],
    "misc": []
  },
  "spawn_costs": {},
  "player_spawn_friendly": true,
  "precipitation": <#if (data.rainingPossibility > 0)><#if (data.temperature > 0.15)>"rain"<#else>"snow"</#if><#else>"none"</#if>,
  "temperature": ${data.temperature},
  "downfall": ${data.rainingPossibility},
  "category": "${data.biomeCategory?lower_case}",
  "depth": ${data.baseHeight}
}
