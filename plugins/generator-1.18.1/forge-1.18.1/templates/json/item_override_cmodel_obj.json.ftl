{
  "forge_marker": 1,
  "parent": "forge:item/default",
  "loader": "forge:obj",
  "model": "${modid}:models/item/${element.modelName.split(":")[0]}.obj",
  "textures": {
    <#if element.getTextureMap(w.getWorkspace())?has_content>
        <#list element.getTextureMap(w.getWorkspace()).entrySet() as texture>
            "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}",
        </#list>
    </#if>
    "particle": "${modid}:items/${element.modelTexture}"
  }
}