<#-- @formatter:off -->
{
  "format_version": "1.26.0",
  "minecraft:client_biome": {
    "description": {
      "identifier": "${modid}:${registryname}"
    },
    "components": {
      <#assign first = true>
      <#if data.grassColor?has_content>
      <#if !first>,</#if><#assign first = false>
      "minecraft:grass_appearance": {
        "color": "${thelper.colorToHexString(data.grassColor)}"
      }
      </#if>
      <#if data.foliageColor?has_content>
      <#if !first>,</#if><#assign first = false>
      "minecraft:foliage_appearance": {
        "color": "${thelper.colorToHexString(data.foliageColor)}"
      }
      </#if>
      <#if data.airColor?has_content>
      <#if !first>,</#if><#assign first = false>
      "minecraft:sky_color": {
        "sky_color": "${thelper.colorToHexString(data.airColor)}"
      }
      </#if>
      <#if data.waterColor?has_content>
      <#if !first>,</#if><#assign first = false>
      "minecraft:water_appearance": {
        "surface_color": "${thelper.colorToHexString(data.waterColor)}"
      }
      </#if>
      <#if data.hasFog()>
      <#if !first>,</#if><#assign first = false>
      "minecraft:fog_appearance": {
        "fog_identifier": "${modid}:${registryname}_fog"
      }
      </#if>
      <#if data.spawnParticles>
      <#if !first>,</#if><#assign first = false>
      "minecraft:precipitation": {
        "${data.particleToSpawn}": ${data.particleDensity}
      }
      </#if>
      <#if data.music?has_content && data.music.getMappedValue()?has_content>
      <#if !first>,</#if><#assign first = false>
      "minecraft:biome_music": {
        "music_definition": "${data.music}"
      }
      </#if>
      <#assign ambientSound = data.ambientSound?has_content && data.ambientSound.getMappedValue()?has_content>
      <#assign additionsSound = data.additionsSound?has_content && data.additionsSound.getMappedValue()?has_content>
      <#assign moodSound = data.moodSound?has_content && data.moodSound.getMappedValue()?has_content>
      <#if ambientSound || additionsSound || moodSound>
      <#if !first>,</#if><#assign first = false>
      "minecraft:ambient_sounds": {
        <#assign firstSound = true>
        <#if ambientSound>
        <#if !firstSound>,</#if><#assign firstSound = false>
        "loop": "${data.ambientSound}"
        </#if>
        <#if additionsSound>
        <#if !firstSound>,</#if><#assign firstSound = false>
        "addition": "${data.additionsSound}"
        </#if>
        <#if moodSound>
        <#if !firstSound>,</#if><#assign firstSound = false>
        "mood": "${data.moodSound}"
        </#if>
      }
      </#if>
    }
  }
}
<#-- @formatter:on -->
