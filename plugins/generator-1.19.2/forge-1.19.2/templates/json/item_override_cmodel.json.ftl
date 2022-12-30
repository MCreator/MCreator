{
  "parent": "${modid}:custom/${item.modelName.split(":")[0]}",
  "textures": {
    <#if item.getTextureMap(w.getWorkspace())??>
        <#list item.getTextureMap(w.getWorkspace()).entrySet() as texture>
            "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}",
        </#list>
    </#if>
    "particle": "${modid}:items/${item.modelTexture}"
  }
}