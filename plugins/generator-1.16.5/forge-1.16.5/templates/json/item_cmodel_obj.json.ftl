<#include "../textures.ftl">
{
  "forge_marker": 1,
  "parent": "forge:item/default",
  "loader": "forge:obj",
  "model": "${modid}:models/item/${data.customModelName.split(":")[0]}.obj",
  "textures": {
    <#if data.getTextureMap()?has_content>
        <#list data.getTextureMap().entrySet() as texture>
            "${texture.getKey()}": "${mappedSingleTexture(texture.getValue(), "blocks", modid)}",
        </#list>
    </#if>
    "particle": "${mappedSingleTexture(data.texture, "items", modid)}"
  }<#--,
  <#if var_type?? && var_type=="tool">
  "transform": "forge:default-tool"
  <#else>
  "transform": "forge:default-item"
  </#if>-->
}