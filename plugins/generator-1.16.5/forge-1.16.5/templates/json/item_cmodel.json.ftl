<#include "../textures.ftl">
{
  "parent": "${modid}:custom/${data.customModelName.split(":")[0]}",
  "textures": {
    <#if data.getTextureMap()??>
        <#list data.getTextureMap().entrySet() as texture>
            "${texture.getKey()}": "${mappedSingleTexture(texture.getValue(), "blocks", modid)}",
        </#list>
    </#if>
    "particle": "${mappedSingleTexture(data.texture, "items", modid)}"
  }
}