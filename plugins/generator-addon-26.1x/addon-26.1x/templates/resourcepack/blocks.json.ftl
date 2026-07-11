{
  "format_version": "1.21.40"
  <#list w.getElementsOfType("beblock") as mod>
    <#assign ge = mod.getGeneratableElement()>
    ,"${modid}:${mod.getRegistryName()}": {
      "sound": "${ge.soundOnStep}"
    }
  </#list>

}