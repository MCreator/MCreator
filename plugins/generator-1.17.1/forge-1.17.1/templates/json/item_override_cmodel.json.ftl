{
  "parent": "${modid}:custom/${element.modelName.split(":")[0]}",
  "textures": {
    <#if element.getTextureMap()??>
        <#list element.getTextureMap().entrySet() as texture>
            "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}",
        </#list>
    </#if>
    "particle": "${modid}:items/${element.modelTexture}"
  }
}