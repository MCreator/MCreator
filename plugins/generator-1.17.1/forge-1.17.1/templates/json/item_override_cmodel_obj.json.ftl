{
  "forge_marker": 1,
  "parent": "forge:item/default",
  "loader": "forge:obj",
  "model": "${modid}:models/item/${element.modelName.split(":")[0]}.obj",
  "textures": {
    <#if element.getTextureMap()?has_content>
        <#list element.getTextureMap().entrySet() as texture>
            "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}",
        </#list>
    </#if>
    "particle": "${modid}:items/${element.modelTexture}"
  }<#--,
  <#if var_type?? && var_type=="tool">
  "transform": "forge:default-tool"
  <#else>
  "transform": "forge:default-item"
  </#if>-->
}