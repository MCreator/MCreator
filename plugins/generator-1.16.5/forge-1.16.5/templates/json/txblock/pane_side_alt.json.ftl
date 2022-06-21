<#include "../textures.ftl">
{
  "parent": "block/template_glass_pane_side_alt",
  "textures": {
    <#if data.particleTexture?has_content>"particle": "${mappedSingleTexture(data.particleTexture, "blocks", modid)}",</#if>
    "edge": "${mappedElseTexture(data.textureTop, data.texture, "blocks", modid)}",
    "pane": "${mappedSingleTexture(data.texture, "blocks", modid)}"
  }
}