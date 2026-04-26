{
  "attributes": {
    <#if data.imitateOverworldBehaviour>
    "minecraft:gameplay/eyeblossom_open": true,
    "minecraft:gameplay/creaking_active": true,
    </#if>
    <#if data.piglinSafe>
    "minecraft:gameplay/piglins_zombify": false,
    </#if>
    <#if data.doesWaterVaporize>
    "minecraft:gameplay/water_evaporates": true,
    "minecraft:gameplay/fast_lava": true,
    </#if>
    <#if data.bedWorks>
    "minecraft:gameplay/bed_rule": {
      "can_sleep": "when_dark",
      "can_set_spawn": "always",
      "error_message": {
        "translate": "block.minecraft.bed.no_sleep"
      }
    },
    </#if>
    <#if data.hasFixedTime>
    "minecraft:visual/sun_angle": ${data.fixedTimeValue * 0.015},
    </#if>
    <#if data.hasClouds>
    "minecraft:visual/cloud_height": ${[[(((data.cloudHeight + 8) / 16)?floor * 16), 2031]?min, -2032]?max},
    "minecraft:visual/cloud_color": "#ccffffff",
    </#if>
    <#if data.useCustomEffects>
      <#if data.hasFog>
      "minecraft:visual/fog_start_distance": 1,
      "minecraft:visual/fog_end_distance": 10,
      </#if>
      <#if data.airColor?has_content>
      "minecraft:visual/fog_color": "${thelper.colorToHexString(data.airColor)}",
      "minecraft:visual/sky_color": "${thelper.colorToHexString(data.airColor)}",
      </#if>
      <#if !data.sunHeightAffectsFog>
      "minecraft:visual/sunrise_sunset_color": {
        "modifier": "override",
        "argument": 0
      },
      </#if>
    <#elseif data.defaultEffects == "overworld">
      "minecraft:visual/fog_color": "#c0d8ff",
      "minecraft:visual/sky_color": "#78a7ff",
    <#elseif data.defaultEffects == "the_nether">
      "minecraft:visual/fog_start_distance": 10,
      "minecraft:visual/fog_end_distance": 96,
      "minecraft:visual/sky_light_color": "#7a7aff",
      "minecraft:visual/sky_light_factor": 0,
    <#elseif data.defaultEffects == "the_end">
      "minecraft:visual/ambient_light_color": "#3f473f",
      "minecraft:visual/fog_color": "#181318",
      "minecraft:visual/sky_color": "#000000",
      "minecraft:visual/sky_light_color": "#ac60cd",
      "minecraft:visual/sky_light_factor": 0,
    </#if>
    "minecraft:gameplay/can_start_raid": ${data.hasRaids},
    "minecraft:gameplay/respawn_anchor_works": ${data.canRespawnHere}
  },
  "has_skylight": ${data.hasSkyLight},
  "has_ceiling": ${data.worldGenType == "Nether like gen"},
  "has_ender_dragon_fight": false,
  "coordinate_scale": ${data.coordinateScale},
  "ambient_light": ${data.ambientLight},
  "infiniburn": "#${data.infiniburnTag}",
  <#if data.hasFixedTime>
  "has_fixed_time": true,
  <#else>
  "timelines": "#minecraft:in_overworld",
  "default_clock": "minecraft:overworld",
  </#if>
  <#if data.worldGenType == "Normal world gen">
  "min_y": -64,
  "height": 384,
  "logical_height": 384,
  <#else>
  "min_y": 0,
  "height": 256,
  "logical_height": 256,
  </#if>
  <#if data.minMonsterSpawnLightLimit == data.maxMonsterSpawnLightLimit>
  "monster_spawn_light_level": ${data.minMonsterSpawnLightLimit},
  <#else>
  "monster_spawn_light_level": {
    "type": "minecraft:uniform",
    "min_inclusive": ${data.minMonsterSpawnLightLimit},
    "max_inclusive": ${data.maxMonsterSpawnLightLimit}
  },
  </#if>
  "monster_spawn_block_light_limit": ${data.monsterSpawnBlockLightLimit},
  "skybox": "${data.skyType?lower_case?replace("normal", "overworld")}"
}