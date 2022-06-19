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
                "#${texture.getKeu()}": "${mappedSingleTexture(texture.getValue, "blocks", modid)}"<#if texture?has_next>,</#if>
          </#list>
          }
      </#if>
    }
  },
  "textures": {
    "particle": "${mappedElseTexture(data.particleTexture, data.texture, "blocks", modid)}"
  }
}