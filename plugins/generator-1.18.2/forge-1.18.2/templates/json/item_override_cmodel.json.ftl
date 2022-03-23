{
  "parent": "${modid}:custom/${element.modelName.split(":")[0]}",
  "textures": {
    <#if element.getTextureMap(w.getWorkspace())??>
        <#list element.getTextureMap(w.getWorkspace()).entrySet() as texture>
            "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}",
        </#list>
    </#if>
    "particle": "${modid}:items/${element.modelTexture}"
  }
}