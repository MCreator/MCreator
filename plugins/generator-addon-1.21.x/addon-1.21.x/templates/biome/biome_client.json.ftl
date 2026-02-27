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
    }
  }
}
<#-- @formatter:on -->