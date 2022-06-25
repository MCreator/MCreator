<#include "../textures.ftl">
{
    "parent": "block/button_pressed",
    "textures": {
      <#if data.particleTexture?has_content>"particle": "${mappedSingleTexture(data.particleTexture, "blocks", modid)}",</#if>
      "texture": "${mappedSingleTexture(data.texture, "blocks", modid)}"
    }
}