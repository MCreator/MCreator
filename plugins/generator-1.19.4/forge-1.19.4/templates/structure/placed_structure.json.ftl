{
  "feature": "${modid}:${registryname}",
  "placement": [
    {
      "type": "minecraft:heightmap",
      "heightmap": "<#if data.surfaceDetectionType == "First block">WORLD_SURFACE_WG<#else>OCEAN_FLOOR_WG</#if>"
    }
  ]
}