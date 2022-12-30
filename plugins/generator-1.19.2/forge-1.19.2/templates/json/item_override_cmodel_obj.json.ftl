{
  "forge_marker": 1,
  "parent": "forge:item/default",
  "loader": "forge:obj",
  "model": "${modid}:models/item/${item.modelName.split(":")[0]}.obj",
  "textures": {
    <#if item.getTextureMap(w.getWorkspace())?has_content>
        <#list item.getTextureMap(w.getWorkspace()).entrySet() as texture>
            "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}",
        </#list>
    </#if>
    "particle": "${modid}:items/${item.modelTexture}"
  }
}