<#-- @formatter:off -->
{
  "format_version": "1.16.100",
  "minecraft:fog_settings": {
    "description": {
      "identifier": "${modid}:${registryname}_fog"
    },
    "distance": {
      "air": {
        "fog_start": 0.92,
        "fog_end": 1,
        "fog_color": ${data.fogColor?has_content?then("[" + data.fogColor.getRed() + "," + data.fogColor.getGreen() + "," + data.fogColor.getBlue() + "]", "\"#C0D8FF\"")},
        "render_distance_type": "render"
      },
      "water": {
        "fog_start": 0,
        "fog_end": 60,
        "fog_color": ${data.waterFogColor?has_content?then("[" + data.waterFogColor.getRed() + "," + data.waterFogColor.getGreen() + "," + data.waterFogColor.getBlue() + "]", "\"#050533\"")},
        "render_distance_type": "fixed"
      }
    }
  }
}
<#-- @formatter:on -->