<#-- @formatter:off -->
{
  "format_version": "1.21.110",
  "minecraft:client_biome": {
    "description": {
      "identifier": "${modid}:${registryname}"
    },
    "components": {
      "minecraft:grass_appearance": {
        "color": ${data.grassColor?has_content?then("[" + data.grassColor.getRed() + "," + data.grassColor.getGreen() + "," + data.grassColor.getBlue() + "]", "\"#90A04D\"")}
      },
      "minecraft:foliage_appearance": {
        "color": ${data.foliageColor?has_content?then("[" + data.foliageColor.getRed() + "," + data.foliageColor.getGreen() + "," + data.foliageColor.getBlue() + "]", "\"#9E9E4D\"")}
      },
      "minecraft:sky_color": {
        "sky_color": ${data.airColor?has_content?then("[" + data.airColor.getRed() + "," + data.airColor.getGreen() + "," + data.airColor.getBlue() + "]", "\"#79A0FF\"")}
      },
      "minecraft:water_appearance": {
        "surface_color": ${data.waterColor?has_content?then("[" + data.waterColor.getRed() + "," + data.waterColor.getGreen() + "," + data.waterColor.getBlue() + "]", "\"#3F76E4\"")}
      }
      <#if data.hasFog()>,
        "minecraft:fog_appearance": {
          "fog_identifier": "${modid}:${registryname}_fog"
        }
      </#if>
      <#if data.spawnParticles>,
        "minecraft:precipitation": {
          "${data.particleToSpawn}": ${data.particleDensity}
        }
      </#if>
      <#if data.music?has_content && data.music.getMappedValue()?has_content>,
        "minecraft:biome_music": {
          "music_definition": "${data.music}"
        }
      </#if>
      <#assign ambientSound = data.ambientSound?has_content && data.ambientSound.getMappedValue()?has_content>
      <#assign additionsSound = data.additionsSound?has_content && data.additionsSound.getMappedValue()?has_content>
      <#assign moodSound = data.moodSound?has_content && data.moodSound.getMappedValue()?has_content>
      <#if ambientSound || additionsSound || moodSound>,
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