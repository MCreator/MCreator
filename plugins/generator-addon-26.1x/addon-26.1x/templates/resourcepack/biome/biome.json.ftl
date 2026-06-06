<#-- @formatter:off -->
{
  "format_version": "1.26.0",
  "minecraft:client_biome": {
    "description": {
      "identifier": "${modid}:${registryname}"
    },
    "components": {
      <#assign hasGrassColor = data.grassColor?has_content>
      <#assign hasFoliageColor = data.foliageColor?has_content>
      <#assign hasAirColor = data.airColor?has_content>
      <#assign hasWaterColor = data.waterColor?has_content>
      <#assign hasColor = hasGrassColor || hasFoliageColor || hasAirColor || hasWaterColor>
      <#if hasGrassColor>
      "minecraft:grass_appearance": {
        "color": ${"[" + data.grassColor.getRed() + "," + data.grassColor.getGreen() + "," + data.grassColor.getBlue() + "]"}
      },
      </#if>
      <#if hasFoliageColor>
      "minecraft:foliage_appearance": {
        "color": ${"[" + data.foliageColor.getRed() + "," + data.foliageColor.getGreen() + "," + data.foliageColor.getBlue() + "]"}
      },
      </#if>
      <#if hasAirColor>
      "minecraft:sky_color": {
        "sky_color": ${"[" + data.airColor.getRed() + "," + data.airColor.getGreen() + "," + data.airColor.getBlue() + "]"}
      },
      </#if>
      <#if hasWaterColor>
      "minecraft:water_appearance": {
        "surface_color": ${"[" + data.waterColor.getRed() + "," + data.waterColor.getGreen() + "," + data.waterColor.getBlue() + "]"}
      }
      </#if>
      <#if data.hasFog()><#if hasColor>,</#if>
        "minecraft:fog_appearance": {
          "fog_identifier": "${modid}:${registryname}_fog"
        }
      </#if>
      <#if data.spawnParticles><#if hasColor || data.hasFog()>,</#if>
        "minecraft:precipitation": {
          "${data.particleToSpawn}": ${data.particleDensity}
        }
      </#if>
      <#assign hasMusic = data.music?has_content && data.music.getMappedValue()?has_content>
      <#if hasMusic><#if hasColor || data.hasFog() || data.spawnParticles>,</#if>
        "minecraft:biome_music": {
          "music_definition": "${data.music}"
        }
      </#if>
      <#assign ambientSound = data.ambientSound?has_content && data.ambientSound.getMappedValue()?has_content>
      <#assign additionsSound = data.additionsSound?has_content && data.additionsSound.getMappedValue()?has_content>
      <#assign moodSound = data.moodSound?has_content && data.moodSound.getMappedValue()?has_content>
      <#if ambientSound || additionsSound || moodSound><#if hasColor || data.hasFog() || data.spawnParticles || hasMusic>,</#if>
        "minecraft:ambient_sounds": {
          <#if ambientSound>
            "loop": "${data.ambientSound}"
          </#if>
          <#if additionsSound><#if ambientSound>,</#if>
            "addition": "${data.additionsSound}"
          </#if>
          <#if moodSound><#if ambientSound || additionsSound>,</#if>
            "mood": "${data.moodSound}"
          </#if>
        }
      </#if>
    }
  }
}
<#-- @formatter:on -->