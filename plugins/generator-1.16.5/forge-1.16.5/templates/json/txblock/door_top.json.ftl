<#include "../textures.ftl">
{
    "parent": "block/door_top",
    "textures": {
      <#if data.particleTexture?has_content>"particle": "${mappedSingleTexture(data.particleTexture, "blocks", modid)}",</#if>
      "bottom": "${mappedSingleTexture(data.texture, "blocks", modid)}",
      "top": "${mappedElseTexture(data.textureTop, data.texture, "blocks", modid)}"
    }
}
