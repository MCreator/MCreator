<#include "../textures.ftl">
{
    "parent": "${modid}:custom/${data.customModelName.split(":")[0]}",
    "textures": {
      "all": "${mappedSingleTexture(data.texture, "blocks", modid)}",
      "particle": "${mappedElseTexture(data.particleTexture, data.texture, "blocks", modid)}"
        <#if data.getTextureMap()??>
          <#list data.getTextureMap().entrySet() as texture>
              ,
              "${texture.getKey()}": "${mappedSingleTexture(texture.getValue(), "blocks", modid)}"
          </#list>
        </#if>
    }
}