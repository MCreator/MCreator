{
  "parent": "${modid}:custom/${data.customModelName.split(":")[0]}",
  "textures": {
    <#if data.getTextureMap()??>
        <#list data.getTextureMap().entrySet() as texture>
            "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}",
        </#list>
    </#if>
    "particle": "${modid}:items/${data.texture}"
  }
}