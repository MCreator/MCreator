<#include "../textures.ftl">
{
  "parent": "forge:item/default",
  "loader": "forge:composite",
  "parts": {
    "part1": {
      "loader": "forge:obj",
      "model": "${modid}:models/item/${data.customModelName.split(":")[0]}.obj",
      "ambientToFullbright": true
      <#if data.getTextureMap()??>
          ,
          "textures": {
          <#list data.getTextureMap().entrySet() as texture>
                "#${texture.getKey()}": "${mappedSingleTexture(texture.getValue(), "blocks", modid)}"<#if texture?has_next>,</#if>
          </#list>
          }
      </#if>
    }
  },
  "textures": {
    <#if data.particleTexture?has_content>
    "particle": "${mappedSingleTexture(data.particleTexture, "blocks", modid)}
    <#else>
    "particle": "${mappedSingleTexture(data.texture, "blocks", modid)}
    </#if>
  }
}