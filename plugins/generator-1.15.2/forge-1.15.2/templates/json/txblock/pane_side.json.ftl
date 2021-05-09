<#include "../textures.ftl">
{
  "parent": "block/template_glass_pane_side",
  "textures": {
    <#if data.particleTexture?has_content>
    "particle": "${mappedSingleTexture(data.particleTexture, "blocks", modid)}",
    </#if>
    <#if data.textureTop?has_content>
    "edge": "${mappedSingleTexture(data.textureTop, "blocks", modid)}",
    <#else>
    "edge": "${mappedSingleTexture(data.texture, "blocks", modid)}",
    </#if>
    "pane": "${mappedSingleTexture(data.texture, "blocks", modid)}"
  }
}