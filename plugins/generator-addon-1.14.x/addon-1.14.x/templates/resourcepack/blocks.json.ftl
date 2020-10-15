{
  "format_version": [1, 1, 0]

  <#list w.getElementsOfType("BLOCK") as mod>
    <#assign ge = mod.getGeneratableElement()>
    ,"${modid}:${mod.getRegistryName()}": {
        "textures": {
          "up": "${modid}_${mod.getRegistryName()}_up",
          "down": "${modid}_${mod.getRegistryName()}_down",
          "south": "${modid}_${mod.getRegistryName()}_south",
          "north": "${modid}_${mod.getRegistryName()}_north",
          "west": "${modid}_${mod.getRegistryName()}_west",
          "east": "${modid}_${mod.getRegistryName()}_east"
        },
      "sound": "${ge.soundOnStep}"
    }
  </#list>

}